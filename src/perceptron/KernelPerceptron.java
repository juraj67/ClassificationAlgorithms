/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package perceptron;

import dataset.Attribute;
import dataset.Dataset;
import dataset.DatasetRow;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.Tools;

/**
 *
 * @author Juraj
 */
public class KernelPerceptron extends Perceptron {

    private final KernelFunctionType kernelFunctionType;
    private final double kernelFunctionParam;
    
    private TreeMap<Integer, MutableInt> missclassifiedTree;
    private List<Integer> missclassifiedList_Keys;
    private List<MutableInt> missclassifiedList_Values;
    
    private double[][] kernelMatrix;
    private RAFDoubleMatrix rafMatrix;
    
    public enum KernelFunctionType {
        Polynomial,
        Gaussian
    }
    
    /**
     * KernelPerceptron constructor
     * @param epochs
     * @param kernelFunctionType (Polynomial or Gaussian kernel function)
     * @param kernelFunctionParam (d for Polynomial kernel function or sigma for Gaussian kernel function)
     */
    public KernelPerceptron(int epochs, KernelFunctionType kernelFunctionType, double kernelFunctionParam) {
        super(epochs);
        this.kernelFunctionType = kernelFunctionType;
        this.kernelFunctionParam = kernelFunctionParam;
    }
    
    @Override
    public void buildModel(Dataset trainingDataset) {
        this.dataset = trainingDataset;
        if (this.dataset != null) {
            //check if dataset has binary output
            this.checkBinaryOutputClass();
            //prekernelize matrix
            DatasetRow tempInstance;
            try {
                //if there is enough memory capacity
                this.kernelMatrix = new double[this.dataset.getDatasetRows().size()][this.dataset.getDatasetRows().size()];
                for (int i = 0; i < this.dataset.getDatasetRows().size(); i++) {
                    tempInstance = this.dataset.getRow(i);
                    for (int j = 0; j < this.dataset.getDatasetRows().size(); j++) {
                        if(i <= j) {
                            this.kernelMatrix[i][j] = this.kernelFunction(tempInstance, this.dataset.getRow(j));
                        } else {
                            this.kernelMatrix[i][j] = this.kernelMatrix[j][i];
                        }
                    }
                }
            } catch(OutOfMemoryError e) {
                //instead use RAF
                this.rafMatrix = new RAFDoubleMatrix(this.dataset.getDatasetRows().size());
                double[] tempRow = new double[this.dataset.getDatasetRows().size()];
                for (int i = 0; i < this.dataset.getDatasetRows().size(); i++) {
                    tempInstance = this.dataset.getRow(i);
                    for (int j = 0; j < this.dataset.getDatasetRows().size(); j++) {
                        tempRow[j] = this.kernelFunction(tempInstance, this.dataset.getRow(j));
                    }
                    this.rafMatrix.writeLine(i, tempRow);
                }
            }
            
            this.missclassifiedTree = new TreeMap<>();
            this.missclassifiedList_Keys = new ArrayList<>(this.dataset.getDatasetRows().size());
            this.missclassifiedList_Values = new ArrayList<>(this.dataset.getDatasetRows().size());
            
            MutableInt missclassifiedCount;
            int predictedIndex, rowIndex, epoch = 0, incorrectlyClassifiedCount = Integer.MAX_VALUE;
            
            while(incorrectlyClassifiedCount != 0 && epoch < this.epochs) {
                epoch++;
                incorrectlyClassifiedCount = 0;
                rowIndex = 0;
                for (DatasetRow instance : this.dataset.getDatasetRows()) {
                    predictedIndex = this.signum(this.prekernelizedTrainingStep(rowIndex));
                    
                    //if instance is classified incorrectly
                    if(predictedIndex != this.transformClassValue((int)instance.getOutputValue().getValue())) {
                        if(epoch > 1) {
                            missclassifiedCount = this.missclassifiedTree.get(rowIndex);
                        } else {
                            missclassifiedCount = null;
                        }
                        //increment or add new missclassified record
                        if(missclassifiedCount != null) {
                            missclassifiedCount.increment();
                        } else {
                            missclassifiedCount = new MutableInt();
                            this.missclassifiedTree.put(rowIndex, missclassifiedCount);
                            //store references and keys in lists too
                            this.missclassifiedList_Keys.add(rowIndex);
                            this.missclassifiedList_Values.add(missclassifiedCount);
                        }
                        incorrectlyClassifiedCount++;
                    }
                    rowIndex++;
                }
                //System.out.println("Epoch: " + epoch + " Incorrect Classified " + incorrectlyClassifiedCount);
            }
            //close and delete RAF
            if(this.rafMatrix != null) {
                this.rafMatrix.closeAndDelete();
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
    
    private double prekernelizedTrainingStep(int instanceIndex) {
        double sum = 0;
        int datasetRowClass;
        double[] row;
        if(this.kernelMatrix != null) {
            row = this.kernelMatrix[instanceIndex];
        } else {
            row = this.rafMatrix.readRow(instanceIndex);
        }
        
        int key, value;
        for (int i = 0; i < this.missclassifiedList_Keys.size(); i++) {
            key = this.missclassifiedList_Keys.get(i);
            value = this.missclassifiedList_Values.get(i).getValue();
            
            datasetRowClass = this.transformClassValue((int)this.dataset.getRow(key).getOutputValue().getValue());
            sum += value * datasetRowClass * row[key];
        }
        
        return sum;
    }
    
    private double countPerceptronFormula(DatasetRow instance) {
        double sum = 0;
        int datasetRowClass;
        DatasetRow datasetRow;
        int key, value;
        for (int i = 0; i < this.missclassifiedList_Keys.size(); i++) {
            key = this.missclassifiedList_Keys.get(i);
            value = this.missclassifiedList_Values.get(i).getValue();
            datasetRow = this.dataset.getRow(key);
            
            datasetRowClass = this.transformClassValue((int)datasetRow.getOutputValue().getValue());
            sum += value * datasetRowClass * this.kernelFunction(instance, datasetRow);
        }
        
        return sum;
    }

    @Override
    public double[] classify(DatasetRow instance) {
        int sign = this.signum(this.countPerceptronFormula(instance));
        
        //if the sum is greater than 0, predict the first class, otherwise, predict the second class
        double[] classesResults = new double[2];

        if(sign > 0 ) {
            classesResults[0] = 1d;
        } else {
            classesResults[1] = 1d;
        }
        return classesResults;
    }
    
    private double kernelFunction(DatasetRow instance1, DatasetRow instance2) {
        switch(this.kernelFunctionType) {
            case Polynomial:
                return this.polynomialDotProduct(instance1, instance2);
            case Gaussian:
                return this.gaussianKernel(instance1, instance2);
            default:
                return this.polynomialDotProduct(instance1, instance2);
        }
    }
    
    /**
     * Dot product between two instances
     * @param instance1
     * @param instance2
     * @return 
     */
    private double dotProduct(DatasetRow instance1, DatasetRow instance2) {
        boolean isString;
        double dot = 0;
        double instance1Attribute, instance2Attribute;
        ArrayList<Attribute> attributes = instance1.getColumnNames();
        for (int i = 0; i < attributes.size(); i++) {
            if(!attributes.get(i).isOutputAttribute()) {
                isString = instance1.getColumnNames().get(i).isAttributeString();
                instance1Attribute = Tools.castToDouble(instance1.getRow().get(i), isString);
                instance2Attribute = Tools.castToDouble(instance2.getRow().get(i), isString);
                
                dot += instance1Attribute * instance2Attribute;
            }
        }
        
        return dot;
    }
    
    /**
     * Polynomial dot product between two instances
     * @param instance1
     * @param instance2
     * @return 
     */
    private double polynomialDotProduct(DatasetRow instance1, DatasetRow instance2) {
        double dot = this.dotProduct(instance1, instance2);
        return Math.pow(dot, this.kernelFunctionParam);
    }
    
    /**
     * exp(-L2norm||x - y||^2 / (2 * (sigma^2)))
     * @param instance1
     * @param instance2
     * @return 
     */
    private double gaussianKernel(DatasetRow instance1, DatasetRow instance2) {
        double l2_norm = 0;
        boolean isString;
        double instance1Attribute, instance2Attribute;
        ArrayList<Attribute> attributes = instance1.getColumnNames();
        for (int i = 0; i < attributes.size(); i++) {
            if(!attributes.get(i).isOutputAttribute()) {
                isString = instance1.getColumnNames().get(i).isAttributeString();
                instance1Attribute = Tools.castToDouble(instance1.getRow().get(i), isString);
                instance2Attribute = Tools.castToDouble(instance2.getRow().get(i), isString);
                
                l2_norm += Math.pow(instance1Attribute - instance2Attribute, 2);
            }
        }
        
        return Math.exp((-l2_norm) / (2 * Math.pow(this.kernelFunctionParam, 2)));
    }
    
    /**
     * Transforms binary class values (0,1) to (+1,-1)
     * @param classValue
     * @return 
     */
    private int transformClassValue(int classValue) {
        if(classValue == 0) {
            return 1;
        } else {
            return -1;
        }
    }
    
    /**
     * Signum function
     * @param value
     * @return 
     */
    private int signum(double value) {
        if(value > 0d) {
            return 1;
        } else if(value == 0d) {
            return 0;
        } else {
            return -1;
        }
    }
    
    /**
     * Nested class used for incrementing int value in TreeMap
     */
    private class MutableInt {
        
        private int value = 1;

        public int getValue() {
            return this.value;
        }
        
        public void increment() {
            this.value++;
        }
    }
    
    /**
     * Nested class for storing kernel function results in RAF
     */
    private final class RAFDoubleMatrix implements Closeable {

        private int sizeOfOneRow;
        private RandomAccessFile randomAccessFile;
        private String filename;

        public RAFDoubleMatrix(int numberOfRows)  {
            try {
                this.filename = "doubleMatrix.dat";
                this.sizeOfOneRow = numberOfRows * Double.BYTES;
                this.randomAccessFile = new RandomAccessFile(new File(this.filename), "rw");
                long size = this.sizeOfOneRow;
                size *=  numberOfRows;
                this.randomAccessFile.setLength(size);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }
        
        private double[] readRow(int rowNumber) {
            byte[] bytes = new byte[this.sizeOfOneRow];
            try {
                long pos = rowNumber;
                pos *= this.sizeOfOneRow;
                this.randomAccessFile.seek(pos);
                this.randomAccessFile.readFully(bytes);
            } catch(IOException ex) {
                ex.printStackTrace(System.out);
            }
            return this.toDoubleArray(bytes);
        }
        
        private void writeLine(int rowNumber, double[] array) {
            try {
                long pos = rowNumber;
                pos *= this.sizeOfOneRow;
                this.randomAccessFile.seek(pos);
                byte[] bytes = this.toByteArray(array);
                this.randomAccessFile.write(bytes);
            } catch(IOException ex) {
                ex.printStackTrace(System.out);
            }       
        }

        private void closeAndDelete() {
            try {
                this.randomAccessFile.close();
                (new File(this.filename)).delete();
            } catch (IOException ex) {
                ex.printStackTrace(System.out);
            }
        }

        @Override
        public void close() throws IOException {
            this.randomAccessFile.close();
        }
              
        private byte[] toByteArray(double[] doubleArray) {
            int times = Double.BYTES;
            byte[] bytes = new byte[doubleArray.length * times];
            for(int i = 0; i < doubleArray.length; i++){
                ByteBuffer.wrap(bytes, i*times, times).putDouble(doubleArray[i]);
            }
            return bytes;
        }
        
        private double[] toDoubleArray(byte[] byteArray) {
            int times = Double.BYTES;
            double[] doubles = new double[byteArray.length / times];
            for(int i = 0;i < doubles.length; i++){
                doubles[i] = ByteBuffer.wrap(byteArray, i*times, times).getDouble();
            }
            return doubles;
        }
    }
}
