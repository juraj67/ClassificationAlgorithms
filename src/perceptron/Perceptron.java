/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perceptron;

import classification.Classification;
import dataset.LinguisticAttribute;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Juraj
 */
public abstract class Perceptron extends Classification {
    
    protected final int epochs;

    protected Perceptron(int epochs) {
        this.epochs = epochs;
    }
    
    /**
     * Check if dataset has binary output 
     */
    protected void checkBinaryOutputClass() {
        int outputClassCount = ((LinguisticAttribute)this.dataset.getOutputAttribute()).getPossibleAttributes().size();
        if(outputClassCount != 2) {
            try {
                throw new NoSuchFieldException();
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace(System.out);
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Binary class dataset required.");
            } finally {
                System.exit(1);
            }
        }
    }
}
