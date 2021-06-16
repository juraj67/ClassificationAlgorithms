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
public class ConfusionMatrix {
    
    private double[][] matrix;

    public ConfusionMatrix(int numberOfClasses) {
        this.matrix = new double[numberOfClasses][numberOfClasses];
    }
    
    public void addRecord(int realIndex, int predictedIndex) {
        //increment element on i,j position
        this.matrix[realIndex][predictedIndex]++;
    }

    public double[][] getMatrix() {
        return this.matrix;
    }
    
    public void clearMatrix() {
        this.matrix = new double[this.matrix.length][this.matrix.length];
    }
    
    public void printMatrix() {
        System.out.println("\nConfusion matrix: ");
        for (double[] matrix1 : this.matrix) {
            for (int j = 0; j < this.matrix.length; j++) {
                System.out.print(matrix1[j] + " ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }
}
