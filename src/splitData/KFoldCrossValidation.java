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
public class KFoldCrossValidation extends SplitData {
    
    private final int k;
    private final ArrayList<Dataset> trainData;
    private final ArrayList<Dataset> testData;
    
    /**
     * KFoldCrossValidation constructor with random seed.
     * @param dataset
     * @param k
     */
    public KFoldCrossValidation(Dataset dataset, int k) {
        super(dataset);
        this.k = k;
        this.trainData = new ArrayList<>(this.k);
        this.testData = new ArrayList<>(this.k);
        
        this.init();
    }
    
    /**
     * KFoldCrossValidation constructor with given seed.
     * @param dataset
     * @param k
     * @param seed
     */
    public KFoldCrossValidation(Dataset dataset, int k, int seed) {
        super(dataset, seed);
        this.k = k;
        this.trainData = new ArrayList<>(this.k);
        this.testData = new ArrayList<>(this.k);
        
        this.init();
    }
    
    /**
     * KFoldCrossValidation init - splits data k-times
     */
    private void init() {
        //initialize list with indexes
        ArrayList<Integer> rowIndexes = new ArrayList<>(this.dataset.getDatasetRows().size());
        for (int i = 0; i < this.dataset.getDatasetRows().size(); i++) {
            rowIndexes.add(i);
        }
        
        //mix indexes
        Collections.shuffle(rowIndexes, this.random);
        
        //The first n % k folds have size int((n / k) + 1), other folds have size int(n / k)
        ArrayList<Integer> testDataRowIndexes, trainDataRowIndexes;
        int foldStartIndex, foldEndIndex = 0;
        for (int i = 0; i < this.k; i++) {
            foldStartIndex = foldEndIndex;
            if(i < this.dataset.getDatasetRows().size() % this.k) {
                foldEndIndex += (this.dataset.getDatasetRows().size() / this.k) + 1;
            } else {
                foldEndIndex += this.dataset.getDatasetRows().size() / this.k;
            }
            
            testDataRowIndexes = new ArrayList<>(rowIndexes.subList(foldStartIndex, foldEndIndex));
            trainDataRowIndexes = new ArrayList<>(rowIndexes.subList(foldEndIndex, this.dataset.getDatasetRows().size()));
            if(i > 0) {
                trainDataRowIndexes.addAll(rowIndexes.subList(0, foldStartIndex));
            }

            //get data by given indexes for test data
            this.getDataByIndexes(this.dataset, testDataRowIndexes);
            this.testData.add(new Dataset(new HashMap<>(this.tempDataColumns), new ArrayList<>(this.tempDataRows), 
                                        this.dataset.getAttributes(), this.dataset.getRelation(), this.dataset.getOutputAttribute()));
            //get data by given indexes for train data
            this.getDataByIndexes(this.dataset, trainDataRowIndexes);
            this.trainData.add(new Dataset(new HashMap<>(this.tempDataColumns), new ArrayList<>(this.tempDataRows), 
                                        this.dataset.getAttributes(), this.dataset.getRelation(), this.dataset.getOutputAttribute()));

            this.tempDataColumns = null;
            this.tempDataRows = null;
        }
    }
    
    /**
     * Returns train data stored at given index
     * @param index
     * @return 
     */
    public Dataset getTrainData(int index) {
        return this.trainData.get(index);
    }

    /**
     * Returns test data stored at given index
     * @param index
     * @return 
     */
    public Dataset getTestData(int index) {
        return this.testData.get(index);
    }

    public int getK() {
        return this.k;
    }
}
