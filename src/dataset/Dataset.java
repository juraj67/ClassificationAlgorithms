/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataset;

import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Juraj
 */
public class Dataset {
    
    private final Map<String, ArrayList<Value>> datasetColumns;
    private final ArrayList<DatasetRow> datasetRows;
    private final ArrayList<Attribute> datasetAttributes;
    private String relation;
    private Attribute outputAttribute; 
    
    /**
     * Dataset constructor, creates new Dataset object from given file
     * @param filename 
     */
    public Dataset(String filename) {
        this.datasetColumns = new HashMap<>();
        this.datasetRows = new ArrayList<>();
        this.datasetAttributes = new ArrayList<>();
        this.readDataset(filename);
    }

    /**
     * Dataset constructor, creates new dataset object from existing data
     * @param datasetColumns
     * @param datasetRows
     * @param datasetAttributes
     * @param relation
     * @param outputAttribute 
     */
    public Dataset(Map<String, ArrayList<Value>> datasetColumns, ArrayList<DatasetRow> datasetRows, ArrayList<Attribute> datasetAttributes, String relation, Attribute outputAttribute) {
        this.datasetColumns = datasetColumns;
        this.datasetRows = datasetRows;
        this.datasetAttributes = datasetAttributes;
        this.relation = relation;
        this.outputAttribute = outputAttribute;
    }
    
    /**
     * Reads data from given .arff format file
     * @param filename 
     */
    private void readDataset(String filename) { 
        try { 
            FileReader filereader = new FileReader(filename); 
            CSVReader csvReader = new CSVReader(filereader); 
            this.skipCommentsAndWhiteSpace(csvReader);
        //@RELATION
            this.relation = csvReader.readNext()[0].substring(10);
            this.skipCommentsAndWhiteSpace(csvReader);
        //@ATTRIBUTE
            String attributeLine, attributeName;
            int nameStarting;
            while (!csvReader.peek()[0].toUpperCase().equals("@DATA") && !(attributeLine = csvReader.readNext()[0]).isEmpty()) {  
                nameStarting = attributeLine.indexOf("'") + 1;
                if(nameStarting == 0) {
                    nameStarting = attributeLine.indexOf(" ") + 1;
                    attributeName = attributeLine.substring(nameStarting, attributeLine.indexOf(" ", nameStarting + 1));
                } else {
                    attributeName = attributeLine.substring(nameStarting, attributeLine.indexOf("'", nameStarting + 1));
                }
                if(attributeLine.substring(attributeLine.length()-4).toUpperCase().equals("REAL")
                    || attributeLine.substring(attributeLine.length()-7).toUpperCase().equals("NUMERIC")
                    || attributeLine.substring(attributeLine.length()-7).toUpperCase().equals("INTEGER"))
                {
                    this.datasetAttributes.add(new Attribute(false, attributeName, this.datasetAttributes.size()));
                } else {
                    this.datasetAttributes.add(new LinguisticAttribute(true, attributeName, this.datasetAttributes.size()));
                }
            }
            this.skipCommentsAndWhiteSpace(csvReader);
        //@DATA
            if(csvReader.peek()[0].toUpperCase().equals("@DATA")) {
                csvReader.skip(1);
            }
            List<String[]> readAll = csvReader.readAll();
            ArrayList<Value> tempColumn;
            DatasetRow tempRow = null;
            int index = 0, stringIndex;
            Double doubleValue;
            NumericValue numericValue;
            LinguisticValue categoricalValue;
            for (String[] stringRow : readAll) {
                for (String stringElement : stringRow) {
                    if(stringElement.isEmpty() || !Character.toString(stringElement.charAt(0)).equals("%")) {
                        if(index % this.datasetAttributes.size() == 0) {
                            //new row
                            tempRow = new DatasetRow(this.datasetRows.size(), new ArrayList<>(this.datasetAttributes.size()), this.datasetAttributes);
                            this.datasetRows.add(tempRow);
                        }
                        if(!this.datasetAttributes.get(index % this.datasetAttributes.size()).isAttributeString()) {
                            //numeric
                            try {
                                doubleValue = Double.parseDouble(stringElement);
                            } catch (NumberFormatException e) {
                                doubleValue = Double.NaN;
                            }
                            numericValue = new NumericValue(doubleValue);
                            tempRow.addValue(numericValue);
                            if(index < this.datasetAttributes.size()) {
                                tempColumn = new ArrayList<>();
                                this.datasetColumns.put(this.datasetAttributes.get(index).getAttributeName(), tempColumn);
                            } else {
                                tempColumn = this.datasetColumns.get(this.datasetAttributes.get(index % this.datasetAttributes.size()).getAttributeName());
                            }
                            tempColumn.add(numericValue);
                        } else {
                            //linguistic
                            if(stringElement.isEmpty()) {
                                stringElement = null;
                            }
                            stringIndex = ((LinguisticAttribute)this.datasetAttributes.get(index % this.datasetAttributes.size())).addPossibleAttribute(stringElement);
                            categoricalValue = new LinguisticValue(stringIndex);
                            tempRow.addValue(categoricalValue);
                            if(index < this.datasetAttributes.size()) {
                                tempColumn = new ArrayList<>();
                                this.datasetColumns.put(this.datasetAttributes.get(index).getAttributeName(), tempColumn);
                            } else {
                                tempColumn = this.datasetColumns.get(this.datasetAttributes.get(index % this.datasetAttributes.size()).getAttributeName());
                            }
                            tempColumn.add(categoricalValue);
                        }
                        index++;
                    }
                }
            } 
        } catch (Exception e) { 
            System.out.println(e.getMessage());
        } 
    }

    /**
     * Used in readDataset function for skipping comments and white spaces
     * @param reader
     * @throws IOException 
     */
    private void skipCommentsAndWhiteSpace(CSVReader reader) throws IOException {
        while ((reader.peek()[0]).isEmpty() || Character.toString((reader.peek()[0]).charAt(0)).equals("%")) {
            reader.skip(1);
        }  
    }
    
    /**
     * Returns Dataset object
     * @return 
     */
    public Dataset getDataset() {
        return this;
    }
    
    /**
     * Returns all dataset columns
     * @return 
     */
    public Map<String, ArrayList<Value>> getDatasetColumns() {
        return this.datasetColumns;
    }

    /**
     * Returns all dataset rows
     * @return 
     */
    public ArrayList<DatasetRow> getDatasetRows() {
        return this.datasetRows;
    }
    
    /**
     * Returns whole column for given column name
     * @param columnName
     * @return 
     */
    public ArrayList<Value> getColumn(String columnName) {
        return this.datasetColumns.get(columnName);
    }
    
    /**
     * Returns whole DatasetRow for given row number
     * @param rowNumber
     * @return 
     */
    public DatasetRow getRow(int rowNumber) {
        if(rowNumber >= 0 && rowNumber < this.datasetRows.size()) {
            return this.datasetRows.get(rowNumber);
        } else {
            return null;
        }
    }
    
    /**
     * Returns list which contains all attributes
     * @return 
     */
    public ArrayList<Attribute> getAttributes() {
        return this.datasetAttributes;
    }
    
    /**
     * Returns list which contains only input attributes
     * @return 
     */
    public ArrayList<Attribute> getOnlyInputAttributes() {
        ArrayList<Attribute> inputAttributes = new ArrayList<>(this.datasetAttributes
                .stream()
                .filter(attribute -> !attribute.isOutputAttribute())
                .collect(Collectors.toList()));
        
        return inputAttributes;
    }

    /**
     * Returns output attribute
     * @return 
     */
    public Attribute getOutputAttribute() {
        return this.outputAttribute;
    }

    /**
     * Sets output attribute, returns false if the attribute doesn't exist, otherwise returns true
     * @param attributeName
     * @return 
     */
    public boolean setOutputAttribute(String attributeName) {
        for (Attribute attribute : this.datasetAttributes) {
            if(attribute.getAttributeName().equals(attributeName)) {
                attribute.setAsOutputAttribute();
                this.outputAttribute = attribute;
                return true;
            }
        }
        return false;
    }

    /**
     * Return relation name
     * @return 
     */
    public String getRelation() {
        return this.relation;
    }
    
    /**
     * Function prints whole Dataset content
     */
    public void printDataset() {
        System.out.print("\n@RELATION: " + this.relation + "\n\n");
        for (Attribute attribute : this.datasetAttributes) {
            System.out.print("@ATTRIBUTE: " + attribute.getAttributeName());
            if(attribute.isAttributeString()) {
                System.out.print(" String { " );
                ((LinguisticAttribute)attribute).getPossibleAttributes().forEach(element -> System.out.print(element + " "));
                System.out.print("}\n" );
            } else {
                System.out.print(" Double\n");
            }
        }
        System.out.println("\n@DATA - columns");
        for (Map.Entry<String, ArrayList<Value>> entry : this.datasetColumns.entrySet()) {
            String key = entry.getKey();
            ArrayList<Value> value = entry.getValue();
            System.out.print(key +  ": ");
            value.forEach(element -> System.out.print(element.getValue() + " "));
            System.out.print("\n");
        }
        System.out.print("\n@DATA - rows\nrow: ");
        this.datasetAttributes.forEach(element -> System.out.print(element.getAttributeName() + " "));
        System.out.print("\n");
        for (DatasetRow datasetRow : this.datasetRows) {
            System.out.print(datasetRow.getRowNumber() +  ": ");
            datasetRow.getRow().forEach(element -> System.out.print(element.getValue() + " "));
            System.out.print("\n");
        }
    }
}
