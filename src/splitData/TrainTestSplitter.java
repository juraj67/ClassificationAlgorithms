/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitData;

import dataset.Dataset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author Juraj
 */
public class TrainTestSplitter extends SplitData {
    
    private Dataset trainData;
    private Dataset testData;
    private final double test_size;
    
    /**
     * DatasetSplitter constructor with random seed.
     * @param dataset
     * @param test_size
     */
    public TrainTestSplitter(Dataset dataset, double test_size) {
        super(dataset);
        this.test_size = test_size;
        
        this.train_test_split();
    }
    
    /**
     * DatasetSplitter constructor with given seed.
     * @param dataset
     * @param test_size
     * @param seed
     */
    public TrainTestSplitter(Dataset dataset, double test_size, int seed) {
        super(dataset, seed);
        this.test_size = test_size;
        
        this.train_test_split();
    }
    
    /**
     * Function splits data to train and test set.
     * @param test_size 
     */
    private void train_test_split() {
        int testDataLength = (int)Math.round(this.dataset.getDatasetRows().size() * this.test_size);
        //initialize list with indexes
        ArrayList<Integer> rowIndexes = new ArrayList<>(this.dataset.getDatasetRows().size());
        for (int i = 0; i < this.dataset.getDatasetRows().size(); i++) {
            rowIndexes.add(i);
        }
        //mix indexes and split to test and train indexes
        Collections.shuffle(rowIndexes, this.random);
        ArrayList<Integer> testDataRowIndexes = new ArrayList<>(rowIndexes.subList(0, testDataLength));
        ArrayList<Integer> trainDataRowIndexes = new ArrayList<>(rowIndexes.subList(testDataLength, this.dataset.getDatasetRows().size()));

        //get data by given indexes for test data
        this.getDataByIndexes(this.dataset, testDataRowIndexes);
        this.testData = new Dataset(new HashMap<>(this.tempDataColumns), new ArrayList<>(this.tempDataRows), 
                                    this.dataset.getAttributes(), this.dataset.getRelation(), this.dataset.getOutputAttribute());
        //get data by given indexes for train data
        this.getDataByIndexes(this.dataset, trainDataRowIndexes);
        this.trainData = new Dataset(new HashMap<>(this.tempDataColumns), new ArrayList<>(this.tempDataRows), 
                                    this.dataset.getAttributes(), this.dataset.getRelation(), this.dataset.getOutputAttribute());
        
        this.tempDataColumns = null;
        this.tempDataRows = null;
    }
    
    /**
     * Returns splitted train data
     * @return 
     */
    public Dataset getTrainData() {
        return this.trainData;
    }

    /**
     * Returns splitted test data
     * @return 
     */
    public Dataset getTestData() {
        return this.testData;
    }  
}
