/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perceptron;

import dataset.Attribute;
import dataset.Dataset;
import dataset.DatasetRow;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.Tools;

/**
 *
 * @author Juraj
 */
public class LinearPerceptron extends Perceptron {

    private double[] weights;
    private final double learningRate;

    public LinearPerceptron(double learningRate, int epochs) {
        super(epochs);
        this.learningRate = learningRate;
    }
    
    @Override
    public void buildModel(Dataset trainingDataset) {
        this.dataset = trainingDataset;
        if (this.dataset != null) {
            //check if dataset has binary output
            this.checkBinaryOutputClass();
            
            int predictedIndex, epoch = 0, incorrectlyClassifiedCount = Integer.MAX_VALUE;
            //init weights
            this.weights = new double[this.dataset.getOnlyInputAttributes().size() + 1];
            //until all instances are classified correctly
            while(incorrectlyClassifiedCount != 0 && epoch < this.epochs) {
                epoch++;
                incorrectlyClassifiedCount = 0;
                for (DatasetRow instance : this.dataset.getDatasetRows()) {
                    predictedIndex = Tools.findIndexOfMax(this.classify(instance));
                    //if instance is classified incorrectly
                    if(predictedIndex != (int)instance.getOutputValue().getValue()) {
                        incorrectlyClassifiedCount++;
                        //update weights
                        this.updateWeights(instance, predictedIndex);
                    }
                }
                //System.out.println("Epoch: " + epoch + " Incorrect Classified " + incorrectlyClassifiedCount);
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
        boolean isString;
        ArrayList<Attribute> attributes = instance.getColumnNames();
        int weightIndex = 0;
        double sum = this.weights[weightIndex];
        for (int i = 0; i < attributes.size(); i++) {
            if(!attributes.get(i).isOutputAttribute()) {
                weightIndex++;
                isString = instance.getColumnNames().get(i).isAttributeString();
                sum += this.weights[weightIndex] * Tools.castToDouble(instance.getRow().get(i), isString);
            }
        }
        
        //if the sum is greater than 0, predict the second class, otherwise, predict the first class
        double[] classesResults = new double[2];
        if(sum > 0 ) {
            classesResults[1] = 1d;
        } else {
            classesResults[0] = 1d;
        }
        return classesResults;
    }
    
    /**
     * Updates weights
     * @param instance
     * @param predictedIndex 
     */
    private void updateWeights(DatasetRow instance, int predictedIndex) {
        boolean isString;
        double instanceAttribute;
        int desiredOutput = (int)instance.getOutputValue().getValue();
        ArrayList<Attribute> attributes = instance.getColumnNames();
        
        int weightIndex = 0;
        this.weights[weightIndex] += this.learningRate * (desiredOutput - predictedIndex);
        for (int i = 0; i < attributes.size(); i++) {
            if(!attributes.get(i).isOutputAttribute()) {
                weightIndex++;
                isString = instance.getColumnNames().get(i).isAttributeString();
                instanceAttribute = Tools.castToDouble(instance.getRow().get(i), isString);

                this.weights[weightIndex] += this.learningRate * (desiredOutput - predictedIndex) * instanceAttribute;
            }
        }
    }
}
