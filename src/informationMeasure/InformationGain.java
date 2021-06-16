/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package informationMeasure;

import dataset.Attribute;
import dataset.DatasetRow;
import dataset.LinguisticAttribute;
import java.util.ArrayList;

/**
 *
 * @author Juraj
 */
public class InformationGain extends InformationMeasure  {

    private Double overallEntropy;
    
    public InformationGain() {
        this.splitBorders = new ArrayList<>();
    }
        
    @Override
    public boolean minimal() {
        return false;
    }

    @Override
    public double getValue(ArrayList<DatasetRow> rows, Attribute inputAttribute, Attribute outputAttribute) {
        this.overallEntropy = null;
        return super.getValue(rows, inputAttribute, outputAttribute);
    }
    
    @Override
    protected double countInformationMeasure(ArrayList<DatasetRow> rows, Attribute inputAttribute, Attribute outputAttribute) {
        final int rowsCount = rows.size();
        final int outputClassCount = ((LinguisticAttribute)outputAttribute).getPossibleAttributes().size();
        
        int inputGroupsCount;
        if(!inputAttribute.isAttributeString()) {
            inputGroupsCount = this.NUMERIC_ATTRIBUTE_BREAKS;
        } else {
            inputGroupsCount = ((LinguisticAttribute)inputAttribute).getPossibleAttributes().size();
        }
        
        //count probabilities from frequencies
        this.countProbability(outputClassCount);
        
        //count entropy for each group
        double[] entropies = new double[inputGroupsCount];
        int shiftIndex = 1;
        for (int i = 0; i < this.probabilities.length; i++) {
            if(this.probabilities[i] > 0) {
                entropies[shiftIndex - 1] += this.countEntropy(this.probabilities[i]);
            }
            if(i + 1 == outputClassCount * shiftIndex) {
                shiftIndex++;
            }
        }
        
        //count sum of entropies
        double sumEntropy = 0d, tempEntropy;
        int groupSum;
        for (int i = 0; i < entropies.length; i++) {
            groupSum = 0;
            for (int j = 0; j < outputClassCount; j++) {
                groupSum += this.frequencies[j + (outputClassCount * (i))];       
            }
            tempEntropy = ((double)groupSum / rowsCount) * entropies[i];
            sumEntropy += Double.isNaN(tempEntropy) ? 0d : tempEntropy;
        }
        
        //count overall entropy
        if(this.overallEntropy == null) {
            int classSum;
            this.overallEntropy = 0d;
            for (int i = 0; i < outputClassCount; i++) {
                classSum = 0;
                for (int j = 0; j < inputGroupsCount; j++) {
                    classSum += this.frequencies[i + (outputClassCount * j)];       
                }
                tempEntropy = this.countEntropy((double)classSum / rowsCount);
                this.overallEntropy += Double.isNaN(tempEntropy) ? 0d : tempEntropy;
            }
        }
        
        return this.overallEntropy - sumEntropy;
    }
    
    protected double countEntropy(double probability) {
        double entropy = 0d;
        if(probability > 0) {
            probability *= (Math.log(probability) / Math.log(2));
            entropy += Double.isNaN(probability) ? 0d : probability;
            entropy *= -1;
        }
        
        return entropy;
    }
}
