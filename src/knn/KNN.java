/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knn;

import classification.Classification;
import dataset.Value;
import distance.Distance;
import distance.EuclideanDistance;

/**
 *
 * @author Juraj
 */
public abstract class KNN extends Classification {
    
    protected final int k;
    protected final Distance distanceCounter;
    
    public KNN(int k) {
        this.k = k;
        this.distanceCounter = new EuclideanDistance();
    }
    
    public KNN(int k, Distance.DistanceType distanceType) {
        this.k = k;
        this.distanceCounter = Distance.getDistance(distanceType);
    }
    
    protected class KnnPair implements Comparable<KnnPair> {
        
        private final double distance;
        private final Value outputValue;

        public KnnPair(double distance, Value value) {
            this.distance = distance;
            this.outputValue = value;
        }

        @Override
        public int compareTo(KnnPair other) {
            if(this.getDistance() < other.getDistance()) {
                return 1;
            } else if(this.getDistance() > other.getDistance()) {
                return -1;
            } else {
                return 0;
            }
        }

        public double getDistance() {
            return this.distance;
        }

        public Value getOutputValue() {
            return this.outputValue;
        }
    }
}
