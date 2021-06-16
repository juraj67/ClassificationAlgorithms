/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import dataset.Value;

/**
 *
 * @author Juraj
 */
public class Tools {
    
    /**
     * Returns index of max element
     * @param array
     * @return 
     */
    public static int findIndexOfMax(double[] array) {
        double max = 0;
        int maxIndex = -1;
        for (int i = 0; i < array.length; i++) {
            if(array[i] > max) {
                max = array[i];
                maxIndex = i;
            }
        }
        
        return maxIndex;
    }
    
    public static double castToDouble(Value value, boolean itIsString) {
        if(!itIsString) {
            return (double)value.getValue();
        } else {
            return (int)value.getValue();
        }
    }
}
