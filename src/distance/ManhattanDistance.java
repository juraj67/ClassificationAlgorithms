/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distance;

import dataset.Attribute;
import dataset.DatasetRow;
import java.util.ArrayList;

/**
 *
 * @author Juraj
 */
public class ManhattanDistance extends Distance {

    @Override
    public double getDistance(DatasetRow datasetRow1, DatasetRow datasetRow2) {
        double distance = 0;
        ArrayList<Attribute> attributes = datasetRow1.getColumnNames();
        for (int i = 0; i < attributes.size(); i++) {
            if(!attributes.get(i).isOutputAttribute()) {
                //numeric
                if(!attributes.get(i).isAttributeString()) {
                    distance += Math.abs((Double)datasetRow1.getRow().get(i).getValue() - (Double)datasetRow2.getRow().get(i).getValue());
                } else {
                    //string: 0 if equals, otherwise 1
                    if((int)datasetRow1.getRow().get(i).getValue() != (int)datasetRow2.getRow().get(i).getValue()) {
                       distance += 1d; 
                    }
                }
            }
        }
        
        return distance;
    }
}
