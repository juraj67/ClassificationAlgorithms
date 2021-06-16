/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classification;

import dataset.*;

/**
 *
 * @author Juraj
 */
public abstract class Classification {
    
    protected Dataset dataset;
    
    /**
     * Returns Dataset object
     * @return 
     */
    public Dataset getDataset() {
        return this.dataset;
    }
    
    /**
     * Building (training model)
     * @param trainingDataset
     */
    public abstract void buildModel(Dataset trainingDataset);
    
    /**
     * Classifies dataset row instance
     * @param instance
     * @return 
     */
    public abstract double[] classify(DatasetRow instance);
    
}
