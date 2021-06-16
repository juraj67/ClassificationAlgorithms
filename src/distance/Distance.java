/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distance;

import dataset.DatasetRow;
        
/**
 *
 * @author Juraj
 */
public abstract class Distance {
    
    public enum DistanceType {
        Euclidean, 
        Manhattan
        //etc
    }
    
    public static Distance getDistance(DistanceType distanceType) {
        switch(distanceType) {
            case Euclidean:
                return new EuclideanDistance();
            case Manhattan:
                return new ManhattanDistance();
            default:
                return new EuclideanDistance();   
        }
    }
    
    public abstract double getDistance(DatasetRow datasetRow1, DatasetRow datasetRow2);
}
