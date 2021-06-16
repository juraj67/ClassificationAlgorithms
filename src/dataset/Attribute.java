/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataset;

/**
 *
 * @author Juraj
 */
public class Attribute {
    
    private final boolean isAttributeString;
    private final String attributeName;
    private boolean outputAttribute;
    private final int attributeIndex;

    public Attribute(boolean isAttributeString, String attributeName, int attributeIndex) {
        this.isAttributeString = isAttributeString;
        this.attributeName = attributeName;
        this.attributeIndex = attributeIndex;
    }

    /**
     * Returns true if attribute is string or false if double
     * @return 
     */
    public boolean isAttributeString() {
        return this.isAttributeString;
    }

    /**
     * Returns attribute name
     * @return 
     */
    public String getAttributeName() {
        return this.attributeName;
    }

    public int getAttributeIndex() {
        return this.attributeIndex;
    }
    
    public void setAsOutputAttribute() {
        this.outputAttribute = true;
    }

    public boolean isOutputAttribute() {
        return this.outputAttribute;
    }
}
