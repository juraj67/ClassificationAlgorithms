/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelEvaluation;

import dataset.Dataset;
import dataset.LinguisticAttribute;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Juraj
 */
public final class BinaryClassEvaluator extends Evaluator {

    public BinaryClassEvaluator(Dataset dataset) {
        super(2);
        this.checkDesiredOutputClassCount(((LinguisticAttribute)dataset.getOutputAttribute()).getPossibleAttributes().size());
    }

    @Override
    public double countAccuracy() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double tp = matrix[0][0];
        double tn = matrix[1][1];
        double fp = matrix[1][0];
        double fn = matrix[0][1];
        
        double accuracy = (tp + tn)/(tp + tn + fp + fn);
        return Double.isNaN(accuracy) ? 0d : accuracy;
    }
    
    @Override
    public double countPrecision() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double tp = matrix[0][0];
        double fp = matrix[1][0];
        
        double precision = (tp / (tp + fp));
        return Double.isNaN(precision) ? 0d : precision;
    }

    @Override
    public double countRecall() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double tp = matrix[0][0];
        double fn = matrix[0][1];
        
        double recall = (tp / (tp + fn));
        return Double.isNaN(recall) ? 0d : recall;
    }

    @Override
    public double countSpecificity() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double tn = matrix[1][1];
        double fp = matrix[1][0];
        
        double specificity = (tn / (tn + fp));
        return Double.isNaN(specificity) ? 0d : specificity;
    }

    @Override
    public double countMCC() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double tp = matrix[0][0];
        double tn = matrix[1][1];
        double fp = matrix[1][0];
        double fn = matrix[0][1];
        
        double mcc = ((tp * tn) - (fp * fn)) / Math.sqrt((tp + fp) * (tp + fn) * (tn + fp) * (tn + fn));
        return Double.isNaN(mcc) ? 0d : mcc;
    }

    @Override
    public double countErrorRate() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double tp = matrix[0][0];
        double tn = matrix[1][1];
        double fp = matrix[1][0];
        double fn = matrix[0][1];
        
        double errorRate = (fp + fn)/(tp + tn + fp + fn);
        return Double.isNaN(errorRate) ? 0d : errorRate;
    }

    @Override
    public double countFPR() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double tn = matrix[1][1];
        double fp = matrix[1][0];
        
        double fpr = fp / (fp + tn);
        return Double.isNaN(fpr) ? 0d : fpr;
    }

    @Override
    public double countFNR() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double tp = matrix[0][0];
        double fn = matrix[0][1];
        
        double fnr = fn / (fn + tp);
        return Double.isNaN(fnr) ? 0d : fnr;
    }

    @Override
    public void checkDesiredOutputClassCount(int outputClassCount) {
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
