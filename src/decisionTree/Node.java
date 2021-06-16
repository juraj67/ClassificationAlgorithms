/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisionTree;

import dataset.Attribute;
import dataset.DatasetRow;
import dataset.Value;
import java.util.ArrayList;

/**
 *
 * @author Juraj
 */
public class Node {
    
    private ArrayList<Node> children;
    private ArrayList<Node> allSuccessors;
    private Node directAncestor;
    private ArrayList<Attribute> possibleAttributes;
    private ArrayList<DatasetRow> nodeRows;
    private ArrayList<Value> nodeSplitValues;
    private Attribute associativeAttribute;
    private double[] classesConfidence;
    private double nodePercentage;

    public Node() {
        this.possibleAttributes = new ArrayList<>();
        this.children = new ArrayList<>();
        this.nodeSplitValues = new ArrayList<>();
        this.nodeRows = new ArrayList<>();
        this.allSuccessors = new ArrayList<>();
    }

    public Node(ArrayList<Attribute> possibleAttributes) {
        this.possibleAttributes = possibleAttributes;
        this.children = new ArrayList<>();
        this.nodeSplitValues = new ArrayList<>();
        this.nodeRows = new ArrayList<>();
        this.allSuccessors = new ArrayList<>();
    }
    
    /**
     * Method returns true if the node is a leaf
     * @return 
     */
    public boolean isLeaf() {
        return (this.children.isEmpty());
    }
    
    public int getCountOfLeaves() {
        int countOfLeaves = 0;
        for (Node node : this.children) {
            if(node.isLeaf()) {
                countOfLeaves++;
            }
        }
        
        return countOfLeaves;
    }

    public ArrayList<Attribute> getPossibleAttributes() {
        return this.possibleAttributes;
    }
    
    public ArrayList<Node> getChildren() {
        return this.children;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public ArrayList<Value> getNodeSplitValue() {
        return this.nodeSplitValues;
    }

    public ArrayList<DatasetRow> getNodeRows() {
        return this.nodeRows;
    }

    public void setNodeRows(ArrayList<DatasetRow> nodeRows) {
        this.nodeRows = nodeRows;
    }

    public Attribute getAssociativeAttribute() {
        return this.associativeAttribute;
    }

    public void setAssociativeAttribute(Attribute associativeAttribute) {
        this.associativeAttribute = associativeAttribute;
    }

    public double[] getClassesConfidence() {
        return this.classesConfidence;
    }

    public void setClassesConfidence(double[] classesConfidence) {
        this.classesConfidence = classesConfidence;
    }

    public double getNodePercentage() {
        return this.nodePercentage;
    }

    public void setNodePercentage(double nodePercentage) {
        this.nodePercentage = nodePercentage;
    }    

    public Node getDirectAncestor() {
        return this.directAncestor;
    }

    public void setDirectAncestor(Node directAncestor) {
        this.directAncestor = directAncestor;
    }

    public ArrayList<Node> getAllSuccessors() {
        return this.allSuccessors;
    }

    public void addNodeToAllAncestors() {
        Node node = this.directAncestor;
        while(node != null) {
            node.getAllSuccessors().add(this);
            node = node.getDirectAncestor();
        }
    }

    public void removeNodeFromAllAncestors() {
        Node node = this.directAncestor;
        while(node != null) {
            node.getAllSuccessors().remove(this);
            node = node.getDirectAncestor();
        }
    }
    
    @Override
    public String toString() {
        String nodeInfo, assocAttrName = "null";
        int ancestorNodeRowsSize = 0;
        if(this.directAncestor != null) {
            ancestorNodeRowsSize = this.directAncestor.getNodeRows().size();
        }
        if(this.getAssociativeAttribute() != null) {
            assocAttrName = this.getAssociativeAttribute().getAttributeName();   
        }
        nodeInfo = "\nNode rows: " + this.getNodeRows().size() + " Children count: " + this.getChildren().size() 
                + " Direct ancestor rows size: " + ancestorNodeRowsSize + " Assoc.atr: " + assocAttrName
                + " Node instances percentage: " + this.getNodePercentage() + "% Classes confidence: ";
        for (double d : this.getClassesConfidence()) {
            nodeInfo += d + " ";
        }
        
        return nodeInfo;
    }
}
