/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisionTree;

/**
 *
 * @author Juraj
 */
public abstract class Prunning {
    
    public enum PrunningType {
        PessimisticErrorPrunning
    }
    
    public static Prunning getType(PrunningType prunningType) {
        switch(prunningType) {
            case PessimisticErrorPrunning:
                return new PessimisticPrunning();
            default:
                return new PessimisticPrunning();   
        }
    }
    
    public abstract void prune(Node treeRoot);
}
