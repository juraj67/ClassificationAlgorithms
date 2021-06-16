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
public class LinguisticValue extends Value<Integer>  {
    
    //it is position in LinguisticAttribute.possibleAttributes
    private final Integer index; 

    public LinguisticValue(Integer index) {
        this.index = index;
    }

    @Override
    public Integer getValue() {
        return this.index;
    }    
}
