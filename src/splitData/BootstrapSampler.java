/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitData;

import dataset.Dataset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 *
 * @author Juraj
 */
public class BootstrapSampler extends SplitData {
    
    public BootstrapSampler(Dataset dataset) {
        super(dataset);
    }
    
    public BootstrapSampler(Dataset dataset, int seed) {
        super(dataset, seed);
    }
    
    /**
     * The function is used for dataset over/undersampling (based on recordsPercentage param), instances can be repeated
     * @param recordsPercentage
     * @return 
     */
    public Dataset getSamples(double recordsPercentage) {
        int newDatasetLength = (int)Math.round(this.dataset.getDatasetRows().size() * recordsPercentage);
        int trainDatasetLength = this.dataset.getDatasetRows().size();
        
        ArrayList<Integer> newDataRowIndexes = new ArrayList<>(this.random.ints(newDatasetLength, 0, trainDatasetLength)
                .boxed()
                .collect(Collectors.toList()));
        
        //get data by given indexes
        this.getDataByIndexes(this.dataset, newDataRowIndexes);
        Dataset newDataset = new Dataset(new HashMap<>(this.tempDataColumns), new ArrayList<>(this.tempDataRows), 
                                    this.dataset.getAttributes(), this.dataset.getRelation(), this.dataset.getOutputAttribute());
        
        this.tempDataColumns = null;
        this.tempDataRows = null;
        
        return newDataset;        
    }
}
