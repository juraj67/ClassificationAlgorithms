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
public class GainRatio extends InformationGain {
    
    @Override
    protected double countInformationMeasure(ArrayList<DatasetRow> rows, Attribute inputAttribute, Attribute outputAttribute) {
        double informationGain = super.countInformationMeasure(rows, inputAttribute, outputAttribute);
        final int outputClassCount = ((LinguisticAttribute)outputAttribute).getPossibleAttributes().size();
        
        int index = 0;
        double splitInfo = 0, groupEntropy;
        int splitFrequency;
        while(index < this.frequencies.length - 1) {
            splitFrequency = 0;
            for (int i = index; i < index + outputClassCount; i++) {
                splitFrequency += this.frequencies[i];
            }
            groupEntropy = super.countEntropy((double)splitFrequency / rows.size());
            splitInfo += Double.isNaN(groupEntropy) ? 0d : groupEntropy;
            index += outputClassCount;
        }
        
        double gainRatio = informationGain / splitInfo;
        return (Double.isNaN(gainRatio) || Double.isInfinite(gainRatio)) ? 0d : gainRatio;
    }
}
