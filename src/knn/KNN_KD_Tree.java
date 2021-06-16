/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knn;

import dataset.Dataset;
import dataset.DatasetRow;
import dataset.LinguisticAttribute;
import distance.Distance;
import tools.Tools;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Juraj
 */
public class KNN_KD_Tree extends KNN {
    
    private KD_Tree<KNN_Data> kd_tree;
    private int inputAttributesIndexes[];
    
    public KNN_KD_Tree(int k) {
        super(k);
    }
    
    public KNN_KD_Tree(int k, Distance.DistanceType distanceType) {
        super(k, distanceType);
    }
    
    @Override
    public void buildModel(Dataset trainingDataset) {
        this.dataset = trainingDataset;
        if (this.dataset != null) {
            final int dimension = this.dataset.getOnlyInputAttributes().size(); 
            this.inputAttributesIndexes = new int[dimension];
            int inputAttributesIndex = 0;
            for (int i = 0; i < this.dataset.getAttributes().size(); i++) {
                if(!this.dataset.getAttributes().get(i).isOutputAttribute()) {
                    this.inputAttributesIndexes[inputAttributesIndex] = i;
                    inputAttributesIndex++;
                }
            }
            this.kd_tree = new KD_Tree<>(dimension);
            
            Deque<ArrayList<DatasetRow>> datasetRowsQueue = new ArrayDeque<>();
            Deque<KD_TreeNode<KNN_Data>> parents = new ArrayDeque<>();
            datasetRowsQueue.add(new ArrayList<>(this.dataset.getDatasetRows()));
            ArrayList<DatasetRow> tempDatasetRows, tempSubList;
            KD_TreeNode<KNN_Data> insertedNode, parent = null;
            int medianRowNumber, levelIndex;
            int fromIndex, toIndex;
            while(!datasetRowsQueue.isEmpty()) {
                if(!parents.isEmpty()) {
                    parent = parents.pop();
                }
                
                tempDatasetRows = datasetRowsQueue.pop();
                if(tempDatasetRows.size() > 1) {
                    levelIndex = this.getMaxVarianceAttributeIndex(tempDatasetRows);
                    if(levelIndex == -1) {
                        levelIndex = (parent != null) ? parent.getLevel() : 0;
                    }
                    medianRowNumber = this.getMedianIndex(tempDatasetRows, this.inputAttributesIndexes[levelIndex]);
                } else {
                    levelIndex = (parent != null) ? parent.getLevel() : 0;
                    medianRowNumber = 0;
                }
                insertedNode = this.kd_tree.insertToSubtree(new KNN_Data(tempDatasetRows.get(medianRowNumber)), parent, levelIndex);
                insertedNode.getData().setNode(insertedNode);

                //split rows by median
                fromIndex = 0;
                toIndex = medianRowNumber;
                for (int i = 0; i < 2; i++) {
                    tempSubList = new ArrayList<>(tempDatasetRows.subList(fromIndex, toIndex));
                    if(!tempSubList.isEmpty()) {
                        datasetRowsQueue.add(tempSubList);
                        parents.add(insertedNode);
                    }
                    fromIndex = medianRowNumber + 1;
                    toIndex = tempDatasetRows.size();
                }
            }
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
        int outputClassCount = ((LinguisticAttribute)this.dataset.getOutputAttribute()).getPossibleAttributes().size();
        double[] classesResults = new double[outputClassCount];
        double actualRowDistance;
        //KnnPair object - distanceCounter is key, outputValue is outputValue
        PriorityQueue<KNN_KD_Tree.KnnPair> distances = new PriorityQueue<>(this.k);
        
        Deque<KD_TreeNode<KNN_Data>> pathQueue = this.kd_tree.getNearestLeafPath(new KNN_Data(instance), this.kd_tree.getRoot());
        DatasetRow datasetRow;
        KD_TreeNode<KNN_Data> actualNode, otherChildNode, lastVisited = null;
        
        while(!pathQueue.isEmpty()) {
            actualNode = pathQueue.pop();
            datasetRow = actualNode.getData().getDatasetRow();
            actualRowDistance = this.distanceCounter.getDistance(datasetRow, instance);
            
            //store only k neighbors with best distance
            if(distances.size() < this.k || actualRowDistance < distances.peek().getDistance()) {
                if(distances.size() == this.k) {
                    distances.remove();
                }
                distances.offer(this.new KnnPair(actualRowDistance, datasetRow.getOutputValue()));
            }
            
            if(!actualNode.isLeaf() && !actualNode.hasOnlyOneChild()) {
                otherChildNode = actualNode.getAnotherChild(lastVisited);
                if(((KNN_Data)actualNode.getData()).diff(instance) <= distances.peek().getDistance()) {
                    pathQueue.addAll(this.kd_tree.getNearestLeafPath(new KNN_Data(instance), otherChildNode));
                }
            }
            lastVisited = actualNode;
        }
        
        int whichOutputClass;
        for (int i = 0; i < this.k; i++) {
            whichOutputClass = (int)distances.poll().getOutputValue().getValue();
            classesResults[whichOutputClass] += 1d / this.k;
        }
        
        return classesResults;
    }
    
    /**
     * Sorts and returns the median index from a given ArrayList of rows
     * @param datasetRows
     * @param splitByColumn
     * @return 
     */
    private int getMedianIndex(ArrayList<DatasetRow> datasetRows, int splitByColumn) {
        boolean isString = this.dataset.getAttributes().get(splitByColumn).isAttributeString();
        datasetRows.sort((DatasetRow r1, DatasetRow r2) -> Double.compare(
            Tools.castToDouble(r1.getDatasetRow().getRow().get(splitByColumn), isString),
            Tools.castToDouble(r2.getDatasetRow().getRow().get(splitByColumn), isString)
        ));
        
        return ((datasetRows.size() + 1) / 2) - 1;
    }
    
    private int getMaxVarianceAttributeIndex(ArrayList<DatasetRow> datasetRows) {
        boolean isString;
        int inputAttributeIndex;
        
        double mean[] = new double[this.inputAttributesIndexes.length];
        double value;
        for (DatasetRow datasetRow : datasetRows) {
            for (int i = 0; i < this.inputAttributesIndexes.length; i++) {
                inputAttributeIndex = this.getInputAttributeIndex(i);
                isString = this.dataset.getAttributes().get(inputAttributeIndex).isAttributeString();
                value = Tools.castToDouble(datasetRow.getRow().get(inputAttributeIndex), isString);
                mean[i] += value / datasetRows.size();
            }
        }
        
        double var[] = new double[this.inputAttributesIndexes.length];
        for (DatasetRow datasetRow : datasetRows) {
            for (int i = 0; i < this.inputAttributesIndexes.length; i++) {
                inputAttributeIndex = this.getInputAttributeIndex(i);
                isString = this.dataset.getAttributes().get(inputAttributeIndex).isAttributeString();
                value = Tools.castToDouble(datasetRow.getRow().get(inputAttributeIndex), isString);
                var[i] += Math.pow(value - mean[i], 2) / datasetRows.size();
            }
        }
        
        return Tools.findIndexOfMax(var);
    }

    private int getInputAttributeIndex(int index) {
        return this.inputAttributesIndexes[index];
    }
    
    private class KNN_Data implements Comparable<KNN_Data> {
    
        private KD_TreeNode node;
        private final DatasetRow datasetRow;

        public KNN_Data(DatasetRow datasetRow) {
            this.datasetRow = datasetRow;
        }  

        public void setNode(KD_TreeNode node) {
            this.node = node;
        }

        public DatasetRow getDatasetRow() {
            return this.datasetRow;
        }

        public KD_TreeNode getNode() {
            return this.node;
        }
        
        public double diff(DatasetRow other) {
            int inputAttributeIndex = getInputAttributeIndex(this.node.getLevel());
            boolean isString = other.getColumnNames().get(inputAttributeIndex).isAttributeString();
            
            double thisValue = Tools.castToDouble(this.getDatasetRow().getRow().get(inputAttributeIndex), isString);
            double otherValue = Tools.castToDouble(other.getDatasetRow().getRow().get(inputAttributeIndex), isString);
            
            return Math.abs(thisValue - otherValue);
        }

        @Override
        public int compareTo(KNN_Data other) {
            int inputAttributeIndex = getInputAttributeIndex(this.node.getLevel());
            boolean isString = other.getDatasetRow().getColumnNames().get(inputAttributeIndex).isAttributeString();
            double thisValue = Tools.castToDouble(this.getDatasetRow().getRow().get(inputAttributeIndex), isString);
            double otherValue = Tools.castToDouble(other.getDatasetRow().getRow().get(inputAttributeIndex), isString);
                
            return Double.compare(thisValue, otherValue);
        }
    }
}
