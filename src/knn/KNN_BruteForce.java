/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knn;

import dataset.*;
import distance.*;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
        
/**
 *
 * @author Juraj
 */
public class KNN_BruteForce extends KNN {

    public KNN_BruteForce(int k) {
        super(k);
    }

    public KNN_BruteForce(int k, Distance.DistanceType distanceType) {
        super(k, distanceType);
    }

    @Override
    public void buildModel(Dataset trainingDataset) {
        this.dataset = trainingDataset;
        if (this.dataset == null) {
            try {
                throw new NoSuchFieldException();
            } catch (NoSuchFieldException ex) {
                ex.printStackTrace(System.out);
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "No dataset found.");
            } finally {
                System.exit(1);
            }
        }
    }

    @Override
    public double[] classify(DatasetRow instance) {
        int outputClassCount = ((LinguisticAttribute)this.dataset.getOutputAttribute()).getPossibleAttributes().size();
        double[] classesResults = new double[outputClassCount];
        double actualRowDistance;
        //KnnPair object - distanceCounter is key, outputValue is outputValue
        PriorityQueue<KnnPair> distances = new PriorityQueue<>(this.k);
        //loop through all rows and count distance
        for (DatasetRow datasetRow : this.dataset.getDatasetRows()) {
            actualRowDistance = this.distanceCounter.getDistance(datasetRow, instance);
            //store only k elements with best distance
            if(distances.size() < this.k || actualRowDistance < distances.peek().getDistance()) {
                if(distances.size() == this.k) {
                    distances.remove();
                }
                distances.offer(this.new KnnPair(actualRowDistance, datasetRow.getOutputValue()));
            }
        }
        
        int whichOutputClass;
        for (int i = 0; i < this.k; i++) {
            whichOutputClass = (int)distances.poll().getOutputValue().getValue();
            classesResults[whichOutputClass] += 1d / this.k;
        }

        return classesResults;
    }
}
