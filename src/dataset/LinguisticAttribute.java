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
public class LinguisticAttribute extends Attribute {
    
    private ArrayList<String> possibleAttributes;
    
    public LinguisticAttribute(boolean isAttributeString, String attributeName, int attributeIndex) {
        super(isAttributeString, attributeName, attributeIndex);
        this.possibleAttributes = new ArrayList<>();
    }

    public ArrayList<String> getPossibleAttributes() {
        return this.possibleAttributes;
    }
    
    public int addPossibleAttribute(String attributeName) {
        int indexOfAttribute = this.possibleAttributes.indexOf(attributeName);
        if(indexOfAttribute == -1) {
            this.possibleAttributes.add(attributeName);
            return this.possibleAttributes.size() - 1;
        } else {
            return indexOfAttribute;
        }
    }
}
