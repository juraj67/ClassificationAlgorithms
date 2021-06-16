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
public final class MultiClassEvaluator extends Evaluator {

    public MultiClassEvaluator(Dataset dataset) {
        super(((LinguisticAttribute)dataset.getOutputAttribute()).getPossibleAttributes().size());
        this.checkDesiredOutputClassCount(((LinguisticAttribute)dataset.getOutputAttribute()).getPossibleAttributes().size());
    }

    @Override
    public double countAccuracy() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double accuracy = 0, tp = 0, tn = 0, fp = 0, fn = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if(i == j) {
                    tp = matrix[i][j];
                } else {
                    fp += matrix[j][i];
                    fn += matrix[i][j];
                    for (int k = 0; k < matrix.length; k++) {
                        if(i != k) {
                            tn += matrix[j][k];
                        }
                    }
                } 
            }
            accuracy += ((tp + tn) / (tp + tn + fp + fn));
            tn = 0;
            fp = 0;
            fn = 0;
        }
        
        accuracy /= matrix.length;
        return Double.isNaN(accuracy) ? 0d : accuracy;
    }

    @Override
    public double countPrecision() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double totalPrecision = 0d, tp = 0d, fp = 0d;
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if(i == j) {
                    tp = matrix[i][j];
                } else {
                    fp += matrix[j][i];
                }
            }
            totalPrecision += Double.isNaN(tp / (tp + fp)) ? 0d : (tp / (tp + fp));
            fp = 0;
        }
        
        totalPrecision /= matrix.length;
        return Double.isNaN(totalPrecision) ? 0d : totalPrecision;
    }

    @Override
    public double countRecall() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double totalRecall = 0d, tp = 0d, fn = 0d;
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if(i == j) {
                    tp = matrix[i][j];
                } else {
                    fn += matrix[i][j];
                }
            }
            totalRecall += Double.isNaN(tp / (tp + fn)) ? 0d : (tp / (tp + fn));
            fn = 0;
        }
        
        totalRecall /= matrix.length;
        return Double.isNaN(totalRecall) ? 0d : totalRecall;
    } 

    @Override
    public double countSpecificity() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double totalSpecificity = 0d, tn = 0d, fp = 0d;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if(i != j) {
                    fp += matrix[j][i];
                    for (int k = 0; k < matrix.length; k++) {
                        if(i != k) {
                            tn += matrix[j][k];
                        }
                    }
                } 
            }
            totalSpecificity += Double.isNaN(tn / (tn + fp)) ? 0d : (tn / (tn + fp));
            fp = 0;
            tn = 0;
        }
        
        totalSpecificity /= matrix.length;
        return Double.isNaN(totalSpecificity) ? 0d : totalSpecificity;
    }

    @Override
    public double countMCC() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double mcc = 0, tp = 0, tn = 0, fp = 0, fn = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if(i == j) {
                    tp = matrix[i][j];
                } else {
                    fp += matrix[j][i];
                    fn += matrix[i][j];
                    for (int k = 0; k < matrix.length; k++) {
                        if(i != k) {
                            tn += matrix[j][k];
                        }
                    }
                } 
            }
            mcc += ((tp * tn) - (fp * fn)) / Math.sqrt((tp + fp) * (tp + fn) * (tn + fp) * (tn + fn));
            tn = 0;
            fp = 0;
            fn = 0;
        }
        
        mcc /= matrix.length;
        return Double.isNaN(mcc) ? 0d : mcc;
    }
    
    @Override
    public double countErrorRate() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double errorRate = 0, tp = 0, tn = 0, fp = 0, fn = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if(i == j) {
                    tp = matrix[i][j];
                } else {
                    fp += matrix[j][i];
                    fn += matrix[i][j];
                    for (int k = 0; k < matrix.length; k++) {
                        if(i != k) {
                            tn += matrix[j][k];
                        }
                    }
                } 
            }
            errorRate += (fp + fn)/(tp + tn + fp + fn);
            tn = 0;
            fp = 0;
            fn = 0;
        }
        
        errorRate /= matrix.length;
        
        return Double.isNaN(errorRate) ? 0d : errorRate;
    }

    @Override
    public double countFPR() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double fpr = 0d, tn = 0d, fp = 0d;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if(i != j) {
                    fp += matrix[j][i];
                    for (int k = 0; k < matrix.length; k++) {
                        if(i != k) {
                            tn += matrix[j][k];
                        }
                    }
                } 
            }
            fpr += Double.isNaN(fp / (fp + tn)) ? 0d : (fp / (fp + tn));
            fp = 0;
            tn = 0;
        }
        
        fpr /= matrix.length;
        
        return Double.isNaN(fpr) ? 0d : fpr;
    }

    @Override
    public double countFNR() {
        double[][] matrix = this.confusionMatrix.getMatrix();
        double fnr = 0d, tp = 0d, fn = 0d;
        
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if(i == j) {
                    tp = matrix[i][j];
                } else {
                    fn += matrix[i][j];
                }
            }
            fnr += Double.isNaN(fn / (fn + tp)) ? 0d : (fn / (fn + tp));
            fn = 0;
        }
        
        fnr /= matrix.length;
        
        return Double.isNaN(fnr) ? 0d : fnr;
    }

    @Override
    public void checkDesiredOutputClassCount(int outputClassCount) {
        if(outputClassCount <= 2) {
            try {
                throw new NoSuchFieldException();
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace(System.out);
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Multi class dataset required.");
            } finally {
                System.exit(1);
            }
        }
    }
}
