/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package informationMeasure;

import dataset.Attribute;
import dataset.DatasetRow;
import dataset.LinguisticAttribute;
import dataset.LinguisticValue;
import dataset.NumericValue;
import dataset.Value;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Juraj
 */
public abstract class InformationMeasure {
    
    protected final int NUMERIC_ATTRIBUTE_BREAKS = 2;
    protected ArrayList<SplitBorder> splitBorders;
    protected int[] frequencies;
    protected double[] probabilities;
    
    public enum CriterionType {
        InformationGain, 
        GiniIndex,
        GainRatio
    }
    
    public static InformationMeasure getCriterion(CriterionType criterionType) {
        switch(criterionType) {
            case InformationGain:
                return new InformationGain();
            case GiniIndex:
                return new GiniIndex();
            case GainRatio:
                return new GainRatio();
            default:
                return new InformationGain();   
        }
    }
    
    public abstract boolean minimal();
    
    protected abstract double countInformationMeasure(ArrayList<DatasetRow> rows, Attribute inputAttribute, Attribute outputAttribute);
    
    public double getValue(ArrayList<DatasetRow> rows, Attribute inputAttribute, Attribute outputAttribute) {
        double informationValue;
        SplitBorder splitBorder = new SplitBorder();
        this.splitBorders.add(splitBorder);
        int outputClassCount = ((LinguisticAttribute)outputAttribute).getPossibleAttributes().size();
        if(!inputAttribute.isAttributeString()) {
            //sort numeric elements
            SortedSet<ObjectToDiscretize> sortedSet = new TreeSet<>();
            Value inputValue, outputValue;
            DatasetRow row;
            for (int i = 0; i < rows.size(); i++) {
                row = rows.get(i);
                inputValue = row.getRow().get(inputAttribute.getAttributeIndex());
                outputValue = row.getOutputValue();
                //insert numeric attribute to treeSet
                if(!Double.isNaN((double)inputValue.getValue()) || sortedSet.isEmpty()) {
                    sortedSet.add(new ObjectToDiscretize(
                            (double)inputValue.getValue(), (int)outputValue.getValue(), outputClassCount));
                }
            }
            
            //add distinct succesive values to list
            ArrayList<ObjectToDiscretize> distinctSortedSetList = new ArrayList<>(sortedSet.size());
            distinctSortedSetList.addAll(sortedSet);
            ArrayList<ObjectToDiscretize> splitCandidates = new ArrayList<>(sortedSet.size());
            for (int i = 0; i < distinctSortedSetList.size() - 1; i++) {
                //add split candidates
                if(distinctSortedSetList.get(i).hasMoreOutputs() || distinctSortedSetList.get(i + 1).hasMoreOutputs() ||
                        distinctSortedSetList.get(i).getFirstOutputValue() != distinctSortedSetList.get(i + 1).getFirstOutputValue()) 
                {
                    //if last added element is not same
                    if(splitCandidates.isEmpty() || splitCandidates.get(splitCandidates.size() - 1) != distinctSortedSetList.get(i)) {
                        splitCandidates.add(distinctSortedSetList.get(i));
                    }
                    splitCandidates.add(distinctSortedSetList.get(i + 1));
                }
            }
            if(splitCandidates.isEmpty()) {
                splitCandidates.add(distinctSortedSetList.get(0));
            }
            
            //find best split point
            double bestInformationValue = -Double.MAX_VALUE, bestSplitPoint = 0d;
            //allocate Numeric value in split border
            NumericValue numValue = new NumericValue(0d);
            splitBorder.getSplitValues().add(numValue);
            double actualSplitPoint;
            if(splitCandidates.size() > 1) {
                //count num frequencies for all split candidates
                int[][] freqMatrix = this.countAllNumFrequencies(distinctSortedSetList, outputAttribute, splitCandidates);
                for (int i = 0; i < freqMatrix.length; i++) {
                    //count information measure for each split candidate
                    this.frequencies = freqMatrix[i];
                    informationValue = this.countInformationMeasure(rows, inputAttribute, outputAttribute);
                    if(bestInformationValue < informationValue) {
                        bestInformationValue = informationValue;
                        actualSplitPoint = (splitCandidates.get(i).getInputValue() + splitCandidates.get(i + 1).getInputValue()) / 2d;
                        bestSplitPoint = actualSplitPoint;
                    }
                }            
                numValue.setValue(bestSplitPoint);
                splitBorder.getSplitValues().set(0, numValue);

                return bestInformationValue;
            } else {
                //just one numeric split value
                numValue.setValue(splitCandidates.get(0).getInputValue());
                splitBorder.getSplitValues().set(0, numValue);
                this.countNumericFrequency(rows, inputAttribute, outputAttribute);
                
                return this.countInformationMeasure(rows, inputAttribute, outputAttribute);
            }
        } else {
            //string values
            this.countLinguisticFrequency(rows, inputAttribute, outputAttribute);
            
            return this.countInformationMeasure(rows, inputAttribute, outputAttribute);
        }
    }

    /**
     * Function is used to calculate frequencies for numeric values
     * @param rows
     * @param inputAttribute
     * @param outputAttribute 
     */
    protected void countNumericFrequency(ArrayList<DatasetRow> rows, Attribute inputAttribute, Attribute outputAttribute) {
        int outputClassCount = ((LinguisticAttribute)outputAttribute).getPossibleAttributes().size();
        this.frequencies = new int[this.NUMERIC_ATTRIBUTE_BREAKS * outputClassCount]; 
        
        Value inputValue, outputValue;
        DatasetRow row;
        int frequencyPosition;
        
        double boundary = (double)this.getSplitValues(this.splitBorders.size() - 1).get(0).getValue();
        int shiftIndex;
        for (int i = 0; i < rows.size(); i++) {
            row = rows.get(i);
            inputValue = row.getRow().get(inputAttribute.getAttributeIndex());
            outputValue = row.getOutputValue();
            if(!Double.isNaN((double)inputValue.getValue()) || Double.isNaN(boundary)) {
                if((double)inputValue.getValue() > boundary) {
                    shiftIndex = 1;
                } else {
                    shiftIndex = 0;
                }
                frequencyPosition = (outputClassCount * shiftIndex) + (int)outputValue.getValue();
                this.frequencies[frequencyPosition]++;
            }
        }
    }
    
    /**
     * Calculate frequencies for all candidates for numeric split
     * @param distinctValues
     * @param outputAttribute 
     * @param splitCanditates
     * @return 
     */
    protected int[][] countAllNumFrequencies(ArrayList<ObjectToDiscretize> distinctValues, Attribute outputAttribute, 
            ArrayList<ObjectToDiscretize> splitCanditates)
    {
        int outputClassCount = ((LinguisticAttribute)outputAttribute).getPossibleAttributes().size();
        int[][] freqMatrix = new int[splitCanditates.size() - 1][this.NUMERIC_ATTRIBUTE_BREAKS * outputClassCount]; 
        
        double inputValue;
        int outputClass;
        int frequencyPosition;
        double actualSplitPoint;
        int shiftIndex;
        for (ObjectToDiscretize distinctValue : distinctValues) {
            inputValue = distinctValue.getInputValue();
            for (int j = 0; j < splitCanditates.size() - 1; j++) {
                actualSplitPoint = splitCanditates.get(j).getInputValue();
                if(!Double.isNaN(inputValue) || Double.isNaN(actualSplitPoint)) {
                    if(!Double.isNaN(actualSplitPoint)) {
                        actualSplitPoint = (actualSplitPoint + splitCanditates.get(j + 1).getInputValue()) / 2d;
                    }
                    if(inputValue > actualSplitPoint) {
                        shiftIndex = 1;
                    } else {
                        shiftIndex = 0;
                    }
                    //count frequency for all output classes
                    if(distinctValue.hasMoreOutputs()) {
                        for (int i = 0; i <  distinctValue.getOutputClasses().length; i++) {
                            outputClass = distinctValue.getOutputClasses()[i];
                            if(outputClass > 0) {
                                frequencyPosition = (outputClassCount * shiftIndex) + i;
                                freqMatrix[j][frequencyPosition] += outputClass;
                            }
                        }
                    } else {
                        frequencyPosition = (outputClassCount * shiftIndex) + distinctValue.getFirstOutputValue();
                        freqMatrix[j][frequencyPosition]++;
                    }
                }
            }
        }
        
        return freqMatrix;
    }
    
    /**
     * Function is used to calculate frequencies for linguistic values
     * @param rows
     * @param inputAttribute
     * @param outputAttribute 
     */
    protected void countLinguisticFrequency(ArrayList<DatasetRow> rows, Attribute inputAttribute, Attribute outputAttribute) {
        int outputClassCount = ((LinguisticAttribute)outputAttribute).getPossibleAttributes().size();
        int inputGroupsCount = ((LinguisticAttribute)inputAttribute).getPossibleAttributes().size();
        this.frequencies = new int[inputGroupsCount * outputClassCount]; 
        
        int frequencyPosition;
        Value inputValue, outputValue;
        DatasetRow row;
        for (int i = 0; i < rows.size(); i++) {
            row = rows.get(i);
            inputValue = row.getRow().get(inputAttribute.getAttributeIndex());
            outputValue = row.getOutputValue();
            
            frequencyPosition = (outputClassCount * (int)inputValue.getValue()) + (int)outputValue.getValue();
            this.frequencies[frequencyPosition]++;
        }
        //store string split values
        for (int i = 0; i < inputGroupsCount; i++) {
            this.getSplitValues(this.splitBorders.size() - 1).add(new LinguisticValue((int)i));
        }
    }
    
    /**
     * Function used to count probabilities from frequencies
     * @param outputClassCount 
     */
    protected void countProbability(int outputClassCount) {
        int shiftIndex = 1;
        int breaksFrequency;
        this.probabilities = new double[this.frequencies.length];
        for (int i = 0; i < this.frequencies.length; i++) {
            if(i == outputClassCount * shiftIndex) {
                shiftIndex++;
            }
            breaksFrequency = 0;
            for (int j = 0; j < outputClassCount; j++) {
                breaksFrequency += this.frequencies[j + (outputClassCount * (shiftIndex-1))];
            }
            this.probabilities[i] = Double.isNaN((double)this.frequencies[i] / breaksFrequency) ? 0d : (double)this.frequencies[i] / breaksFrequency;
        }
    }
    
    public ArrayList<SplitBorder> getSplitBorders() {
        return this.splitBorders;
    }
    
    public ArrayList<Value> getSplitValues(int index) {
        return this.splitBorders.get(index).getSplitValues();
    }
    
    /**
     * Nested class
     * Used to sort numeric elements when discretizing
     */
    protected class ObjectToDiscretize implements Comparable<ObjectToDiscretize>{
        
        private final double inputValue;
        private final int outputValue;
        private int[] outputClasses;
        private boolean hasMoreOutputs;

        public ObjectToDiscretize(double inputValue, int outputValue, int outputClassesCount) {
            this.inputValue = inputValue;
            this.outputValue = outputValue;
            this.outputClasses = new int[outputClassesCount];
            this.outputClasses[outputValue]++;
        }

        public double getInputValue() {
            return this.inputValue;
        }

        public int getFirstOutputValue() {
            return this.outputValue;
        }

        public int[] getOutputClasses() {
            return this.outputClasses;
        }

        public boolean hasMoreOutputs() {
            return this.hasMoreOutputs;
        }

        public void setHasMoreOutputs(boolean hasMoreOutputs) {
            this.hasMoreOutputs = hasMoreOutputs;
        }
        
        @Override
        public int compareTo(ObjectToDiscretize other) {
            int inputCompare = Double.compare(this.getInputValue(), other.getInputValue());
            if(inputCompare == 0 && this != other) {
                other.getOutputClasses()[this.getFirstOutputValue()]++;
                other.setHasMoreOutputs(true);
            }
            return inputCompare;
        }
    }
    
    /**
     * Nested class
     * Used for storing split values for each attribute
     */
    protected class SplitBorder {
        
        private ArrayList<Value> splitValues;

        public SplitBorder() {
            this.splitValues = new ArrayList<>();
        }

        public ArrayList<Value> getSplitValues() {
            return this.splitValues;
        }
    }
}
