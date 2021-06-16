/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataset;

import java.util.ArrayList;

/**
 *
 * @author Juraj
 */
public class DatasetRow {
    
    private int rowNumber;
    private ArrayList<Value> row;
    private ArrayList<Attribute> columnNames;

    /**
     * Creates new DatasetRow
     * @param rowNumber
     * @param row
     * @param columnNames 
     */
    public DatasetRow(int rowNumber, ArrayList<Value> row, ArrayList<Attribute> columnNames) {
        this.rowNumber = rowNumber;
        this.row = row;
        this.columnNames = columnNames;
    }

    /**
     * Creates new DatasetRow for classification
     * @param row 
     */
    public DatasetRow(ArrayList<Value> row) {
        this.row = row;
    }

    /**
     * Add new Value object to dataset row
     * @param value 
     */
    public void addValue(Value value) {
        this.row.add(value);
    }
    
    /**
     * Returns row number
     * @return 
     */
    public int getRowNumber() {
        return this.rowNumber;
    }
    
    /**
     * Returns DatasetRow object
     * @return 
     */
    public DatasetRow getDatasetRow() {
        return this;
    }

    /**
     * Returns whole row
     * @return 
     */
    public ArrayList<Value> getRow() {
        return this.row;
    }

    /**
     * Returns column names
     * @return 
     */
    public ArrayList<Attribute> getColumnNames() {
        return this.columnNames;
    }
    
    /**
     * Returns output attribute value from row 
     * @return 
     */
    public Value getOutputValue() {
        for (int i = 0; i < this.columnNames.size(); i++) {
            if(this.columnNames.get(i).isOutputAttribute()) {
                return this.row.get(i);
            }
        }
        return null;
    }
    
    /**
     * Prints whole row
     */
    public void printRow() {
        System.out.println("Row number: " + this.rowNumber);
        int index = 0;
        for (Value value : this.row) {
            Attribute attribute = this.columnNames.get(index);
            System.out.println(attribute.getAttributeName() + ": " + value.getValue() + " " + (attribute.isAttributeString() ? "String" : "Double"));
            index++;
        }
    }
}
