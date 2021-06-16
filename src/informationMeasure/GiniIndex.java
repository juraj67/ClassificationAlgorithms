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
public class GiniIndex extends InformationMeasure {

    public GiniIndex() {
        this.splitBorders = new ArrayList<>();
    }

    @Override
    public boolean minimal() {
        return true;
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
        
        //count gini index for each group
        double[] giniIndexes = new double[inputGroupsCount];
        int shiftIndex = 1;
        for (int i = 0; i < this.probabilities.length; i++) {
            giniIndexes[shiftIndex - 1] += Math.pow(this.probabilities[i], 2d);
            if(i + 1 == outputClassCount * shiftIndex) {
                giniIndexes[shiftIndex - 1] = 1 - giniIndexes[shiftIndex - 1];
                shiftIndex++;
            }
        }
        
        //count sum of gini indexes
        double sumGini = 0d, temp;
        int groupSum;
        for (int i = 0; i < giniIndexes.length; i++) {
            groupSum = 0;
            for (int j = 0; j < outputClassCount; j++) {
                groupSum += this.frequencies[j + (outputClassCount * (i))];       
            }
            temp = ((double)groupSum / rowsCount) * giniIndexes[i];
            sumGini += Double.isNaN(temp) ? 0d : temp;
        }
        
        return sumGini;
    }
}
