package com.netcompany.machinelearning.neuralNetwork1;


import com.netcompany.machinelearning.preprocessing.Preprocessing;
import com.netcompany.machinelearning.preprocessing.PreprocessingFactory;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeuralNetwork1 {

    private static Logger log = LoggerFactory.getLogger(NeuralNetwork1.class);

    public static void main(String[] args) throws Exception {

        // ###############################################################
        // OPPGAVE 1: PREPROSSESERING -> NORMALISERE OG FLAT UT
        // ###############################################################

        Preprocessing preprocessing = Preprocessing.create();

        // a) Normaliser bildene
        double[][][] trainingImages = normalize(preprocessing.getTrainingImages());

        // b) Flat ut bildene
        double[][] trainingFlatImages = flatMapImage(trainingImages);

        // ###############################################################
        // OPPGAVE 2: NEVRALT NETTVERK -> BYGG NETTVERK
        // ###############################################################

        NevraltNettverkBygger nevraltNettverkBygger = new NevraltNettverkBygger();
        nevraltNettverkBygger = nevraltNettverkBygger.leggTilLag(256, 32);
        nevraltNettverkBygger = nevraltNettverkBygger.leggTilLag(32, 10);

        NevraltNettverk nevraltNettverk = nevraltNettverkBygger.bygg();


        nevraltNettverk.tren(trainingFlatImages, preprocessing.getTrainingLabels());

        System.out.println("FERDIG TRENT!!!");
        // Oppgave a)
//        final int numRows = 28;
//        final int numColumns = 28;
//        int outputNum = 10; // number of output classes
//        int batchSize = 2000; // batch size for each epoch
//        int rngSeed = 123; // random number seed for reproducibility
//        int numEpochs = 2; // number of epochs to perform
//
//        //Get the DataSetIterators:
//        log.info("Fetching training data");
//        DataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, true, rngSeed);
//        log.info("Fetching test data");
//        DataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, rngSeed);
//
//
//        log.info("Build model....");
//        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
//                .seed(rngSeed) //include a random seed for reproducibility
//                // use stochastic gradient descent as an optimization algorithm
//                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
//                .iterations(1)
//                .learningRate(0.006) //specify the learning rate
//                .updater(Updater.NESTEROVS).momentum(0.9) //specify the rate of change of the learning rate.
//                .regularization(true).l2(1e-4)
//                .list()
//                .layer(0, new DenseLayer.Builder() //create the first, input layer with xavier initialization
//                        .nIn(numRows * numColumns)
//                        .nOut(1000)
//                        .activation("relu")
//                        .weightInit(WeightInit.XAVIER)
//                        .build())
//                .layer(1, new OutputLayer.Builder() //create hidden layer
//                        .nIn(1000)
//                        .nOut(outputNum)
//                        .activation("softmax")
//                        .weightInit(WeightInit.XAVIER)
//                        .build())
//                .pretrain(false).backprop(true) //use backpropagation to adjust weights
//                .build();
//
//        MultiLayerNetwork model = new MultiLayerNetwork(conf);
//        model.init();
//        //print the score with every 1 iteration
//        model.setListeners(new ScoreIterationListener(1));
//
//        log.info("Train model....");
//        for( int i=0; i<numEpochs; i++ ){
//            model.fit(mnistTrain);
//        }
//
//
//        log.info("Evaluate model....");
//        Evaluation eval = new Evaluation(outputNum); //create an evaluation object with 10 possible classes
//        while(mnistTest.hasNext()){
//            DataSet next = mnistTest.next();
//            INDArray output = model.output(next.getFeatureMatrix()); //get the networks prediction
//            eval.eval(next.getLabels(), output); //check the prediction against the true class
//        }

        //log.info(eval.stats());
        //log.info("****************Example finished********************");




    }

    private static double[][][] normalize(int[][][] trainingImages) {
        int numberOfImages = trainingImages.length;
        int width = PreprocessingFactory.width;
        int height = PreprocessingFactory.height;

        double [][][] normalizedImages = new double[numberOfImages][width][height];

        double MAX_PIXEL_VERDI = 255.0; // FORDI VI VET DET :)

        for (int iCounter=0; iCounter < numberOfImages; iCounter++) {
            for (int row=0; row < width; row++) {
                for (int col=0; col < height; col++) {
                    normalizedImages[iCounter][row][col] = trainingImages[iCounter][row][col] / MAX_PIXEL_VERDI;
                }
            }
        }
        return normalizedImages;
    }

    private static double[][] flatMapImage(double[][][] images) {
        int numberOfImages = images.length;
        double[][] flatImages = new double[numberOfImages][images[0].length*images[0][0].length];

        for (int i = 0; i < numberOfImages; i++) {
            flatImages[i] = flatImage(images[i]);
        }
        return flatImages;
    }

    private static double[] flatImage(double[][] image) {
        int counter = 0;
        double[] flatImage = new double[image.length * image[0].length];
        for (int col = 0; col < image.length; col++) {
            for (int row = 0; row < image[col].length; row++) {
                flatImage[counter] = image[col][row];
            }
        }
        return flatImage;
    }
}
