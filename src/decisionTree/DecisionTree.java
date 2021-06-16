/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisionTree;

import classification.Classification;
import dataset.Attribute;
import dataset.Dataset;
import dataset.DatasetRow;
import dataset.LinguisticAttribute;
import dataset.Value;
import informationMeasure.InformationGain;
import informationMeasure.InformationMeasure;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.Tools;

/**
 *
 * @author Juraj
 */
public class DecisionTree extends Classification{

    private Node root;
    private final InformationMeasure informationMeasure;
    private final Prunning prunning;
    private boolean randomFeaturesSubset;
    
    public DecisionTree() {
        this.informationMeasure = new InformationGain();
        this.prunning = null;
    }
        
    public DecisionTree(InformationMeasure.CriterionType criterionType) {
        this.informationMeasure = InformationMeasure.getCriterion(criterionType);
        this.prunning = null;
    }
    
    public DecisionTree(InformationMeasure.CriterionType criterionType, Prunning.PrunningType prunningType) {
        this.informationMeasure = InformationMeasure.getCriterion(criterionType);
        this.prunning = Prunning.getType(prunningType);
    }
    
    @Override
    public void buildModel(Dataset trainingDataset) {
        this.dataset = trainingDataset;
        if (this.dataset != null) {
            Deque<Node> nodeQueue = new ArrayDeque<>();
            this.root = new Node(this.dataset.getOnlyInputAttributes());
            this.root.setNodeRows(this.dataset.getDatasetRows());
            this.countClassesConfidence(this.root);
            this.countNodePercentage(this.root);
            nodeQueue.add(this.root);
            Node node;
            while (!nodeQueue.isEmpty()) {             
                node = nodeQueue.removeLast();
                this.split(node);
                for (Node child : node.getChildren()) {
                    if(!child.getPossibleAttributes().isEmpty()) {
                        nodeQueue.add(child);
                    }
                    this.countClassesConfidence(child);
                    this.countNodePercentage(child);
                } 
            }    
            //this.printTree();
            if(this.prunning != null) {
                this.prunning.prune(this.root);
            }
            //this.printTree();
        } else {
            try {
                throw new NoSuchFieldException();
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace(System.out);
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "No dataset found.");
            } finally {
                System.exit(1);
            }
        }
    }

    @Override
    public double[] classify(DatasetRow instance) {
        double instanceValue, splitValue;
        boolean isAttributeString;
        int attributeIndex, whichBranch;
        Node node = this.root;
        while(!node.isLeaf()) {
            whichBranch = 0;
            attributeIndex = node.getAssociativeAttribute().getAttributeIndex();
            isAttributeString = node.getAssociativeAttribute().isAttributeString();
            instanceValue = Tools.castToDouble(instance.getRow().get(attributeIndex), isAttributeString);
            
            for (Value value : node.getNodeSplitValue()) {
                //if numeric instanceValue is NaN, use most popular branch
                if(!isAttributeString && Double.isNaN(instanceValue)) {
                    double branchPopularity = -Double.MAX_VALUE;
                    for (int i = 0; i < node.getChildren().size(); i++) {
                        if(branchPopularity < node.getChildren().get(i).getNodePercentage()) {
                            branchPopularity = node.getChildren().get(i).getNodePercentage();
                            whichBranch = i;
                        }
                    }
                } else {
                    splitValue = Tools.castToDouble(value, node.getAssociativeAttribute().isAttributeString());
                    if(instanceValue <= splitValue) {
                        break;
                    } else {
                        whichBranch++;
                    }
                }
            }
            node = node.getChildren().get(whichBranch);
        }
        
        //check if node has some rows, if not return his parent confidence
        if(!node.getNodeRows().isEmpty()) {
            return node.getClassesConfidence();
        } else {
            return node.getDirectAncestor().getClassesConfidence();
        }
    }
    
    private void split(Node node) {
        double value;
        double bestValue = (this.informationMeasure.minimal()) ? Double.MAX_VALUE : -Double.MAX_VALUE;
        int index = 0, bestValueIndex = 0;
        if(!this.willBeLeaf(node)) {
            ArrayList<Attribute> splitAttributes = node.getPossibleAttributes();
            if(this.randomFeaturesSubset && node.getPossibleAttributes().size() > 1) {
                int m = (int)Math.sqrt(node.getPossibleAttributes().size());
                //int m = (int)(Math.log(decisionTree.getDataset().getOnlyInputAttributes().size() + 1) / Math.log(2));
                splitAttributes = this.getRandomFeatures(m, node.getPossibleAttributes());
            }
            
            Attribute bestValueAttribute = null;
            for (Attribute inputAttribute : splitAttributes) {
                value = this.informationMeasure.getValue(node.getNodeRows(), inputAttribute, this.dataset.getOutputAttribute());
                if(this.comparator(value, bestValue)) {
                    bestValue = value;
                    bestValueAttribute = inputAttribute;
                    bestValueIndex = index;
                }
                index++;
            }

            node.getNodeSplitValue().addAll(new ArrayList<>(this.informationMeasure.getSplitValues(bestValueIndex)));
            this.informationMeasure.getSplitBorders().clear();
           
            node.setAssociativeAttribute(bestValueAttribute);
            this.subset(node);
        }
    }
    
    private void subset(Node node) {
        int attributeIndex = node.getAssociativeAttribute().getAttributeIndex();
        int childrenCount;
        if(!node.getAssociativeAttribute().isAttributeString()) {
            childrenCount = node.getNodeSplitValue().size() + 1;
        } else {
            childrenCount = node.getNodeSplitValue().size();
        }
        Node child;
        for (int i = 0; i < childrenCount; i++) {
            child = new Node();
            node.getChildren().add(child);
            child.setDirectAncestor(node);
            child.addNodeToAllAncestors();
            if(node.getPossibleAttributes().size() > 1) {
                child.getPossibleAttributes().addAll(node.getPossibleAttributes());
                child.getPossibleAttributes().remove(node.getAssociativeAttribute());
            }
        }
        
        int whichBranch;
        double val1, val2;
        for (DatasetRow nodeRow : node.getNodeRows()) {
            val1 = Tools.castToDouble(nodeRow.getRow().get(attributeIndex), node.getAssociativeAttribute().isAttributeString());
            whichBranch = 0;
            for (Value value : node.getNodeSplitValue()) {
                val2 = Tools.castToDouble(value, node.getAssociativeAttribute().isAttributeString());
                if(val1 <= val2) {
                    break;
                } else {
                    whichBranch++;
                }
            }

            node.getChildren().get(whichBranch).getNodeRows().add(nodeRow);
        }
    }
    
    private boolean comparator(double value1, double value2) {
        if(this.informationMeasure.minimal()) {
            return value1 < value2;
        } else {
            return value1 > value2;
        }
    }
    
    private boolean willBeLeaf(Node node) {
        int credibleConfidence = 0;
        for (double d : node.getClassesConfidence()) {
            if(d > 0d) {
                credibleConfidence++;
            }
        }
        
        return (credibleConfidence <= 1 || (node.getPossibleAttributes().isEmpty()));
    }
    
    private void countClassesConfidence(Node node) {
        int outputClassCount = ((LinguisticAttribute)this.dataset.getOutputAttribute()).getPossibleAttributes().size(); 
        double[] classesConfidence = new double[outputClassCount];
        node.getNodeRows().forEach((nodeRow) -> {
            classesConfidence[(int)nodeRow.getOutputValue().getValue()] += 1d / node.getNodeRows().size();
        });
        node.setClassesConfidence(classesConfidence);
    }
    
    private void countNodePercentage(Node node) {
        node.setNodePercentage((double)node.getNodeRows().size() / this.dataset.getDatasetRows().size());
    }

    public void setRandomFeaturesSubset(boolean randomFeaturesSubset) {
        this.randomFeaturesSubset = randomFeaturesSubset;
    }
    
    /**
     * The function returns a subset of the attributes list (based on featuresCount param)
     * @param featuresCount
     * @param features
     * @return 
     */
    private ArrayList<Attribute> getRandomFeatures(int featuresCount, ArrayList<Attribute> features) {
        ArrayList<Attribute> featuresSubset = new ArrayList<>(featuresCount);
        //initialize list with indexes
        ArrayList<Integer> featuresIndexes = new ArrayList<>(features.size());
        for (int i = 0; i < features.size(); i++) {
            featuresIndexes.add(i);
        }
        
        //mix indexes and pick first m indexes
        Collections.shuffle(featuresIndexes, new Random());
        int featureIndex;
        for (int i = 0; i < featuresCount; i++) {
            featureIndex = featuresIndexes.get(i);
            featuresSubset.add(features.get(featureIndex));
        }
        
        return featuresSubset;
    }
    
    private void printTree() {
        System.out.print(this.root.toString());
        for (Node successor : this.root.getAllSuccessors()) {
            System.out.print(successor.toString());
        }
        System.out.println("\n");
    }
}
