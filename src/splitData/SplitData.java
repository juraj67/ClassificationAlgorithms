/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitData;

import dataset.Dataset;
import dataset.DatasetRow;
import dataset.Value;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Juraj
 */
public abstract class SplitData {
    
    protected final Dataset dataset;
    protected final Random random;
    protected ArrayList<DatasetRow>  tempDataRows;
    protected Map<String, ArrayList<Value>> tempDataColumns;

    /**
     * SplitData constructor with random seed.
     * @param dataset 
     */
    protected SplitData(Dataset dataset) {
        this.dataset = dataset;
        this.random = new Random(new Random().nextInt());
    }
    
    /**
     * SplitData constructor with given seed.
     * @param dataset
     * @param seed 
     */
    protected SplitData(Dataset dataset, int seed) {
        this.dataset = dataset;
        this.random = new Random(seed);
    }
        
    /**
     * Help function used in the sub classes functions. 
     * Takes rows and columns from original data by given indexes and sets temporary members tempDataRows and tempDataColumns.
     * @param originData
     * @param dataRowsIndexes 
     */
    protected void getDataByIndexes(Dataset originData, ArrayList<Integer> dataRowsIndexes) {
        this.tempDataRows = new ArrayList<>(dataRowsIndexes.size());
        this.tempDataColumns = new HashMap<>();
        DatasetRow tempRow;
        ArrayList<Value> tempColumn;
        for (Integer i : dataRowsIndexes) {
            tempRow = originData.getRow(i);
            this.tempDataRows.add(tempRow);
            for (int j = 0; j < tempRow.getRow().size(); j++) {
                if(this.tempDataColumns.size() < tempRow.getColumnNames().size()) {
                    tempColumn = new ArrayList<>();
                    this.tempDataColumns.put(tempRow.getColumnNames().get(j).getAttributeName(), tempColumn);
                } else {
                    tempColumn = this.tempDataColumns.get(tempRow.getColumnNames().get(j).getAttributeName());
                }
                tempColumn.add(tempRow.getRow().get(j));
            }
        }
    }
}
