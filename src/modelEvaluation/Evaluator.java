/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelEvaluation;

/**
 *
 * @author Juraj
 */
public abstract class Evaluator {
    
    protected ConfusionMatrix confusionMatrix;
    
    public Evaluator(int outputClassCount) {
        this.confusionMatrix = new ConfusionMatrix(outputClassCount);
    }
    
    public ConfusionMatrix getConfusionMatrix() {
        return this.confusionMatrix;
    }
    
    public void addMatrixRecord(int realIndex, int predictedIndex) {
        this.confusionMatrix.addRecord(realIndex, predictedIndex);
    }
        
    /**
     * Check if dataset has desired output 
     * @param outputClassCount
     */
    public abstract void checkDesiredOutputClassCount(int outputClassCount);
    
    /**
     * 2*recall*precision / (recall+precision)
     * @return 
     */
    public double countF1Score() {
        double precision = this.countPrecision();
        double recall = this.countRecall();
        
        double f1Score = (2*recall*precision / (recall + precision));
        return Double.isNaN(f1Score) ? 0d : f1Score;
    }
    
    /**
     * (sensitivity + specificity) / 2
     * @return 
     */
    public double countBalancedAccuracy() {
        double sensitivity = this.countRecall();
        double specificity = this.countSpecificity();
        
        double balancedAccuracy = (sensitivity + specificity) / 2d;
        return Double.isNaN(balancedAccuracy) ? 0d : balancedAccuracy;
    }
    
    /**
     * (TP + TN)/(TP + TN + FP + FN) = diagonal / all
     * @return accuracy
     */
    public abstract double countAccuracy();
    
    /**
     * TP/(TP+FP)
     * @return precision
     */
    public abstract double countPrecision();
    
    /**
     * TP/(TP+FN) = sensitivity or recall
     * @return sensitivity
     */
    public abstract double countRecall();
    
    /**
     * TN/(TN+FP) = specificity
     * @return specificity
     */
    public abstract double countSpecificity();
    
    /**
     * Matthews correlation coefficient - MCC
     * TP*TN - FP*FN / sqrt((TP+FP)(TP+FN)(TN+FP)(TN+FN))
     * @return mcc
     */    
    public abstract double countMCC();

    /**
     * (FP + FN)/(TP + TN + FP + FN)
     * @return errorRate
     */    
    public abstract double countErrorRate();
    
    /**
     * FP / (FP + TN)
     * @return fpr
     */    
    public abstract double countFPR();
    
    /**
     * FN / (FN + TP)
     * @return fnr
     */
    public abstract double countFNR();
   
    public void printEvaluationResults() {
        this.confusionMatrix.printMatrix();
        System.out.println("Accuracy: " + this.countAccuracy());
        System.out.println("Balanced accuracy: " + this.countBalancedAccuracy());
        System.out.println("Precision: " + this.countPrecision());
        System.out.println("Recall (sensitivity): " + this.countRecall());
        System.out.println("Specificity: " + this.countSpecificity());
        System.out.println("F1-Score: " + this.countF1Score());
        System.out.println("MCC: " + this.countMCC());
        System.out.println("Error rate: " + this.countErrorRate());
        System.out.println("False positive rate: " + this.countFPR());
        System.out.println("False negative rate: " + this.countFNR());
    }
}
