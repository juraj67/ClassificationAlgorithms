/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knn;

/**
 *
 * @author Juraj
 * @param <T>
 */
public class KD_TreeNode<T extends Comparable<T>>  {
    
    private KD_TreeNode parent;
    private KD_TreeNode leftChild;
    private KD_TreeNode rightChild;
    private final T data;
    private final int level;
    
    /**
     * Constructor creates a new Node
     * @param data 
     * @param level 
     */
    public KD_TreeNode(T data, int level) {
        this.data = data;
        this.parent = null;
        this.leftChild = null;
        this.rightChild = null;
        this.level = level;
    }
    
    /**
     * Copy constructor makes a copy of Node
     * @param nodeToCopy 
     */
    public KD_TreeNode(KD_TreeNode<T> nodeToCopy) {
        this.data = nodeToCopy.getData();
        this.parent = nodeToCopy.getParent();
        this.leftChild = nodeToCopy.getLeftChild();
        this.rightChild = nodeToCopy.getRightChild();
        this.level = nodeToCopy.getLevel();
    }

    /**
     * Returns the level
     * @return 
     */
    public int getLevel() {
        return this.level;
    }
    
    /**
     * Returns the parent of the node
     * @return 
     */
    public KD_TreeNode getParent() {
        return this.parent;
    }
    
    /**
     * Returns the left child of the node
     * @return 
     */
    public KD_TreeNode getLeftChild() {
        return this.leftChild;
    }
    
    /**
     * Returns the right child of the node
     * @return 
     */
    public KD_TreeNode getRightChild() {
        return this.rightChild;
    }
    
    /**
     * Returns the another child of the node
     * @param child
     * @return 
     */
    public KD_TreeNode getAnotherChild(KD_TreeNode child) {
        if(this.leftChild == child) {
            return this.rightChild;
        } else {
            return this.leftChild;
        }
    }
    
    /**
     * Method returns the splay node data
     * @return 
     */
    public T getData() {
        return this.data;
    }
    
    /**
     * Method sets parent of the tree 
     * @param parent 
     */
    public void setParent(KD_TreeNode parent) {
        this.parent = parent;
    }
    
    /**
     * Method sets left child of the tree 
     * @param leftChild 
     */
    public void setLeftChild(KD_TreeNode leftChild) {
        this.leftChild = leftChild;
    }
    
    /**
     * Method sets right child of the tree 
     * @param rightChild 
     */
    public void setRightChild(KD_TreeNode rightChild) {
        this.rightChild = rightChild;
    }
    
    /**
     * Method returns true if the node has a left child
     * @return 
     */
    public boolean hasLeftChild() {
        return (this.leftChild != null);
    }
    
    /**
     * Method returns true if the node has a right child
     * @return 
     */
    public boolean hasRightChild() {
        return (this.rightChild != null);
    }
    
    /**
     * Method returns true, if the node has only one child
     * @return 
     */
    public boolean hasOnlyOneChild() {
        if(this.leftChild == null && this.rightChild != null) {
            return true;
        } else {
            return this.leftChild != null && this.rightChild == null;
        }
    }
    /**
     * Method returns true if the node is a leaf
     * @return 
     */
    public boolean isLeaf() { 
        return (!this.hasLeftChild() && !this.hasRightChild());
    }
    
    /**
     * Method returns true, if the node has got a parent
     * @return 
     */
    public boolean hasParent() {
        return (this.parent != null);
    }
}
