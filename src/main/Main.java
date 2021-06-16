package main;

import splitData.*;
import dataset.*;
import classification.*;
import decisionTree.*;
import informationMeasure.*;
import distance.*;
import knn.*;
import modelEvaluation.*;
import perceptron.*;
import randomForrest.RandomForrest;
import tools.*;

/**
 *
 * @author Juraj
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Dataset dataset = new Dataset("test_data/ionosphere.arff");
        dataset.setOutputAttribute("class");
        KFoldCrossValidation crossValidator = new KFoldCrossValidation(dataset, 10);
        /*TrainTestSplitter ds = new TrainTestSplitter(dataset, 0.2);
        //ds.getTrainData().printDataset();
        //ds.getTestData().printDataset();*/
        
        //KNN
        System.out.println("\nBuilding KNN model...");
        Classification c;
        Evaluator eval = new BinaryClassEvaluator(dataset);
        for (int i = 0; i < crossValidator.getK(); i++) {
            c = new KNN_BruteForce(5, Distance.DistanceType.Euclidean);
            c.buildModel(crossValidator.getTrainData(i));
            
            int predictedIndex;
            for (DatasetRow datasetRow : crossValidator.getTestData(i).getDatasetRows()) {
                predictedIndex = Tools.findIndexOfMax(c.classify(datasetRow)); 
                eval.addMatrixRecord((int)datasetRow.getOutputValue().getValue(), predictedIndex);
            }
        }
        eval.printEvaluationResults();
        
        //KNN WITH A KD-TREE
        System.out.println("\nBuilding KNN KD-Tree model...");
        Classification c1;
        Evaluator eval1 = new BinaryClassEvaluator(dataset);
        for (int i = 0; i < crossValidator.getK(); i++) {
            c1 = new KNN_KD_Tree(5, Distance.DistanceType.Euclidean);
            c1.buildModel(crossValidator.getTrainData(i));
            
            int predictedIndex1;
            for (DatasetRow datasetRow : crossValidator.getTestData(i).getDatasetRows()) {
                predictedIndex1 = Tools.findIndexOfMax(c1.classify(datasetRow)); 
                eval1.addMatrixRecord((int)datasetRow.getOutputValue().getValue(), predictedIndex1);
            }
        }
        eval1.printEvaluationResults();
        
        //DECISION TREE
        System.out.println("\nBuilding Decision tree model...");
        Classification c2;
        Evaluator eval2 = new BinaryClassEvaluator(dataset);
        for (int i = 0; i < crossValidator.getK(); i++) {
            c2 = new DecisionTree(InformationMeasure.CriterionType.GainRatio, Prunning.PrunningType.PessimisticErrorPrunning);
            c2.buildModel(crossValidator.getTrainData(i));
            
            int predictedIndex2;
            for (DatasetRow datasetRow : crossValidator.getTestData(i).getDatasetRows()) {
                predictedIndex2 = Tools.findIndexOfMax(c2.classify(datasetRow)); 
                eval2.addMatrixRecord((int)datasetRow.getOutputValue().getValue(), predictedIndex2);
            }
        }
        eval2.printEvaluationResults();
        
        //RANDOM FORREST
        System.out.println("\nBuilding Random forrest model...");
        Classification c3;
        Evaluator eval3 = new BinaryClassEvaluator(dataset);
        for (int i = 0; i < crossValidator.getK(); i++) {
            c3 = new RandomForrest(10, 1, true, InformationMeasure.CriterionType.GainRatio);
            c3.buildModel(crossValidator.getTrainData(i));
            
            int predictedIndex3;
            for (DatasetRow datasetRow : crossValidator.getTestData(i).getDatasetRows()) {
                predictedIndex3 = Tools.findIndexOfMax(c3.classify(datasetRow)); 
                eval3.addMatrixRecord((int)datasetRow.getOutputValue().getValue(), predictedIndex3);
            }
        }
        eval3.printEvaluationResults();
        
        //KERNEL PERCEPTRON FOR BINARY CLASSIFICATION
        System.out.println("\nBuilding Kernel Perceptron for binary classification...");
        Classification c4;
        Evaluator eval4 = new BinaryClassEvaluator(dataset);
        for (int i = 0; i < crossValidator.getK(); i++) {
            c4 = new KernelPerceptron(100, KernelPerceptron.KernelFunctionType.Polynomial, 4);
            c4.buildModel(crossValidator.getTrainData(i));
            
            int predictedIndex4;
            for (DatasetRow datasetRow : crossValidator.getTestData(i).getDatasetRows()) {
                predictedIndex4 = Tools.findIndexOfMax(c4.classify(datasetRow)); 
                eval4.addMatrixRecord((int)datasetRow.getOutputValue().getValue(), predictedIndex4);
            }
        }
        eval4.printEvaluationResults();
        
        //LINEAR PERCEPTRON FOR BINARY CLASSIFICATION
        System.out.println("\nBuilding Linear Perceptron for binary classification...");
        Classification c5;
        Evaluator eval5 = new BinaryClassEvaluator(dataset);
        for (int i = 0; i < crossValidator.getK(); i++) {
            c5 = new LinearPerceptron(0.1, 100);
            c5.buildModel(crossValidator.getTrainData(i));
            
            int predictedIndex5;
            for (DatasetRow datasetRow : crossValidator.getTestData(i).getDatasetRows()) {
                predictedIndex5 = Tools.findIndexOfMax(c5.classify(datasetRow)); 
                eval5.addMatrixRecord((int)datasetRow.getOutputValue().getValue(), predictedIndex5);
            }
        }
        eval5.printEvaluationResults();
    } 
}
