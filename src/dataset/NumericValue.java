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
public class NumericValue extends Value<Double> {
    
    private Double value;

    public NumericValue(Double value) {
        this.value = value;
    }

    @Override
    public Double getValue() {
        return this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
