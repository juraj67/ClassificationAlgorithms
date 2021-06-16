/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisionTree;

import dataset.DatasetRow;
import java.util.Stack;
import tools.Tools;

/**
 *
 * @author Juraj
 */
public class PessimisticPrunning extends Prunning {

    @Override
    public void prune(Node treeRoot) {
        double z = 0.69, e = 0d;
        double f, N = 0d, N_child, combinedError;
        int E;
        Stack<Node> nodeStack = new Stack<>(); 
        nodeStack.push(treeRoot);
        Node node, previousNodeAncestor = null;
        while (!nodeStack.isEmpty()) {
            node = nodeStack.peek();
            //will be pruned?
            if(node.getCountOfLeaves() == node.getChildren().size()) {
                N = node.getNodeRows().size();
                E = this.countNumberOfCorrectClass(node);
                f = (double)E / N;
                e = countNodeError(z, f, N);
                nodeStack.pop();
            } else {
                if(previousNodeAncestor == node) {
                    previousNodeAncestor = node.getDirectAncestor();
                    nodeStack.pop();
                    continue;
                }
            }
            //System.out.println(node.toString());
            combinedError = 0;
            for (Node child : node.getChildren()) {
                if(!child.isLeaf()) {
                    nodeStack.push(child);
                } else {
                    if(node.getCountOfLeaves() == node.getChildren().size()) {
                        E = this.countNumberOfCorrectClass(child);
                        N_child = child.getNodeRows().size();
                        f = (double)E / N_child;
                        combinedError += (N_child / N) * countNodeError(z, f, N_child);
                    }
                }
            }
            //prune if the combined error is higher than the parent error
            if(node.getCountOfLeaves() == node.getChildren().size() && combinedError > e) {
                for (Node child : node.getChildren()) {
                    child.removeNodeFromAllAncestors();
                }
                node.getChildren().clear();
                
                //System.out.println("Prunning node with " + node.getNodeRows().size() + " rows, e: " + e + " combined e: " + combinedError);
            }
            if(node.getDirectAncestor() != null) {
                previousNodeAncestor = node.getDirectAncestor();
            }
        }
    }
            
    private double countNodeError(double z, double f, double N) {
        double e = f + (Math.pow(z, 2) / (2*N)) + (z * Math.sqrt((f / N) - (Math.pow(f, 2) / N) + (Math.pow(z, 2) / (4*(Math.pow(N, 2))))));
        e /= 1 + (Math.pow(z, 2) / N);
        
        return e;
    }
        
    private int countNumberOfCorrectClass(Node node) {
        int nodeClass = Tools.findIndexOfMax(node.getClassesConfidence());
        int correctClasses = 0;
        for (DatasetRow nodeRow : node.getNodeRows()) {
            if(nodeClass != (int)nodeRow.getOutputValue().getValue()) {
                correctClasses++;
            }
        }
        
        return correctClasses;
    }
}
