/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knn;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

/**
 *
 * @author Juraj
 * @param <T>
 */
public class KD_Tree<T extends Comparable<T>> {
    
    private KD_TreeNode<T> root;
    private final int dimension;
    
    /**
     * Constructor creates an empty KD_Tree
     * @param dimension
     */
    public KD_Tree(int dimension) {
        this.dimension = dimension;
    }
    
    /**
     * Constructor creates new KD_Tree with root
     * @param root 
     * @param dimension 
     */
    public KD_Tree(KD_TreeNode<T> root, int dimension) {
        this.root = root;
        this.dimension = dimension;
    }
        
    /**
     * Copy constructor makes a copy of the KD_Tree
     * @param tree_toCopy 
     */
    public KD_Tree(KD_Tree<T> tree_toCopy) {
        this.root = tree_toCopy.getRoot();
        this.dimension = tree_toCopy.getDimension();
    }

    /**
     * Method returns the KD_Tree dimension
     * @return 
     */
    public int getDimension() {
        return this.dimension;
    }

    /**
     * Method returns the root of the KD_Tree
     * @return 
     */
    public KD_TreeNode<T> getRoot() {
        return this.root;
    }
    
    /**
     * Method inserts data as a new child into the parent
     * @param paData
     * @param parent
     * @param level
     * @return 
     */
    public KD_TreeNode<T> insertToSubtree(T paData, KD_TreeNode<T> parent, int level) {
        if(parent == null) {
            this.root = new KD_TreeNode<>(paData, level);
            return this.root;
        } else {    
            if (parent.getData().compareTo(paData) >= 0 && !parent.hasLeftChild()) {//go left - s1 >= s2, positive number  
                parent.setLeftChild(new KD_TreeNode<>(paData, level));
                parent.getLeftChild().setParent(parent);
                return parent.getLeftChild();
            } else {                                            //go right - s1 < s2, negative number  
                parent.setRightChild(new KD_TreeNode<>(paData, level));
                parent.getRightChild().setParent(parent);
                return parent.getRightChild();
            }
        }
    }
    
    /**
     * Method finds and returns nearest leaf node path
     * @param paCriterion 
     * @param subtreeRoot 
     * @return  
     */
    public Deque<KD_TreeNode<T>> getNearestLeafPath(T paCriterion, KD_TreeNode<T> subtreeRoot) {
        Deque<KD_TreeNode<T>> pathStack = new ArrayDeque<>();
        KD_TreeNode<T> actual_node;
        if(subtreeRoot != null) {
            actual_node = subtreeRoot;
            while (actual_node != null) {
                pathStack.push(actual_node);
                if (actual_node.getData().compareTo(paCriterion) >= 0) {    //go left - s1 > s2, positive number  
                    if(actual_node.hasLeftChild()) {
                        actual_node = actual_node.getLeftChild();
                    } else {
                        actual_node = actual_node.getRightChild();
                    }
                } else {                                                    //go right - s1 < s2, negative number  
                    if(actual_node.hasRightChild()) {
                        actual_node = actual_node.getRightChild();
                    } else {
                        actual_node = actual_node.getLeftChild();
                    }
                }
            }
        }
        return pathStack;
    }
    
    /**
     * InOrder traversal
     * @return 
     */
    public LinkedList inOrder() {
        LinkedList<T> linked_list = new LinkedList<>();
        
        if(this.root != null) {
            Stack<KD_TreeNode<T>> stack = new Stack<>();
            KD_TreeNode<T> actual_node = this.root;
            
            while(actual_node != null || !stack.empty()) {
                if (actual_node != null) {      //go to the left
                    stack.push(actual_node);
                    actual_node = actual_node.getLeftChild();
                } else {                        //go to the right
                    actual_node = stack.pop();
                    linked_list.add(actual_node.getData());
                    actual_node = actual_node.getRightChild();
                }
            }
        }
        return linked_list;
    }
}
