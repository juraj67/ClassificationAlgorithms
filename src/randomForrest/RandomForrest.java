/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package randomForrest;

import classification.Classification;
import dataset.Dataset;
import dataset.DatasetRow;
import dataset.LinguisticAttribute;
import splitData.BootstrapSampler;
import decisionTree.DecisionTree;
import informationMeasure.InformationMeasure;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.Tools;

/**
 *
 * @author Juraj
 */
public class RandomForrest extends Classification {
    
    private final int n_estimators;
    private final double recordsPercentage;
    private final boolean randomFeaturesSubset;
    private final InformationMeasure.CriterionType measureType;
    private BootstrapSampler randomSampler;
    private final DecisionTree[] forrest;
    
    public RandomForrest(int n_estimators, double recordsPercentage, boolean randomFeaturesSubset) {
        this.n_estimators = n_estimators;
        this.recordsPercentage = recordsPercentage;
        this.randomFeaturesSubset = randomFeaturesSubset;
        this.measureType = InformationMeasure.CriterionType.InformationGain;
        this.forrest = new DecisionTree[this.n_estimators];
    }
    
    public RandomForrest(int n_estimators, double recordsPercentage, boolean randomFeaturesSubset, InformationMeasure.CriterionType measureType) {
        this.n_estimators = n_estimators;
        this.recordsPercentage = recordsPercentage;
        this.randomFeaturesSubset = randomFeaturesSubset;
        this.measureType = measureType;
        this.forrest = new DecisionTree[this.n_estimators];
    }

    @Override
    public void buildModel(Dataset trainingDataset) {
        this.dataset = trainingDataset;
        if (this.dataset != null) {
            this.randomSampler = new BootstrapSampler(this.dataset);
            DecisionTree decisionTree;
            Dataset decisionTreeDataset;
            for (int i = 0; i < this.n_estimators; i++) {
                decisionTree = new DecisionTree(this.measureType);
                decisionTreeDataset = this.randomSamplingTrainingData();
                
                if(this.randomFeaturesSubset) {
                    decisionTree.setRandomFeaturesSubset(true);
                }
                
                decisionTree.buildModel(decisionTreeDataset);
                this.forrest[i] = decisionTree;
            }
        } else {
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
        int predictedClass;
        for (DecisionTree decisionTree : this.forrest) {
            predictedClass = Tools.findIndexOfMax(decisionTree.classify(instance));
            classesResults[predictedClass]++;
        }
        
        return classesResults;
    }
    
    private Dataset randomSamplingTrainingData() {
        return this.randomSampler.getSamples(this.recordsPercentage);
    }
}
