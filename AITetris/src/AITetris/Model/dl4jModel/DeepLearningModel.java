package AITetris.Model.dl4jModel;

import java.awt.Graphics;
import java.io.IOException;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer.Builder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import AITetris.Model.Neo;
import AITetris.Model.NeoModel.ControlModel;
import AITetris.View.Board.GameBoard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DeepLearningModel {

	private GameBoard gameBoard;
	private Neo neo;
	
	private ControlModel controlModel;
	
	private static Logger log = LoggerFactory.getLogger(DeepLearningModel.class);
	
	
	NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
	
	INDArray recentResult;
	
	public DeepLearningModel(Neo neo, GameBoard gameBoard) throws IOException {
		
	    	this.neo = neo;
		this.gameBoard = gameBoard;
		this.controlModel = new ControlModel(gameBoard);
		
		
		new Thread(() -> {
			try {
				initLearn();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}).start();;
		
		
	}
	
	private ObservableList<String> weightToVec() {
	    
	    ObservableList<String> model = FXCollections.observableArrayList();
	    
	    for(int i=0; i<gameBoard.BoardHeight; i++) {
		for(int j=0; j<gameBoard.BoardWidth; j++) {
		    model.add(String.valueOf(neo.getWeightModel()[j][i]));
		}
	    }
	    
	    return model;
	}
	
	public void initLearn() throws IOException {
		
		//number of rows and columns in the input pictures
        final int numRows = 28;
        final int numColumns = 28;
        int outputNum = 10; // number of output classes
        int batchSize = 128; // batch size for each epoch
        int rngSeed = 123; // random number seed for reproducibility
        int numEpochs = 15; // number of epochs to perform

        //Get the DataSetIterators:
        //DataSetIterator mnistTrain = new MnistDataSetIterator(batchSize, true, rngSeed);
        //DataSetIterator mnistTest = new MnistDataSetIterator(batchSize, false, rngSeed);
        
     // Instantiating RecordReader. Specify height and width of images.
        //RecordReader recordReader = new CollectionRecordReader(FXCollections.observableArrayList());
        
        // Point to data path. 
        //recordReader.initialize(new CollectionInputSplit(weightToVec()));
        
        //SentenceIterator train = new CollectionSentenceIterator(weightToVec());
        //SentenceIteratorConverter dataSet = new SentenceIteratorConverter(train);
        //dataSet.to
        
     // list off input values, 4 training samples with data for 2
        // input-neurons each
        INDArray input = Nd4j.zeros(gameBoard.BoardHeight, gameBoard.BoardWidth);

        // correspondending list with expected output values, 4 training samples
        // with data for 2 output-neurons each
        INDArray labels = Nd4j.zeros(gameBoard.BoardHeight, gameBoard.BoardWidth);

        for(int i=0; i<gameBoard.BoardHeight; i++) {
		for(int j=0; j<gameBoard.BoardWidth; j++) {
		    input.putScalar(new int[]{i, j}, neo.getModel()[j][i]);
		    labels.putScalar(new int[]{i, j}, neo.getModel()[j][i]);
		}
	}
        
        // create dataset object
        DataSet ds = new DataSet(input, labels);

        // Set up network configuration
        
        // how often should the training set be run, we need something above
        // 1000, or a higher learning-rate - found this values just by trial and
        // error
        builder.iterations(1000);
        // learning rate
        builder.learningRate(0.1);
        // fixed seed for the random generator, so any run of this program
        // brings the same results - may not work if you do something like
        // ds.shuffle()
        builder.seed(123);
        // not applicable, this network is to small - but for bigger networks it
        // can help that the network will not only recite the training data
        builder.useDropConnect(false);
        // a standard algorithm for moving on the error-plane, this one works
        // best for me, LINE_GRADIENT_DESCENT or CONJUGATE_GRADIENT can do the
        // job, too - it's an empirical value which one matches best to
        // your problem
        builder.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
        // init the bias with 0 - empirical value, too
        builder.biasInit(0);
        // from "http://deeplearning4j.org/architecture": The networks can
        // process the input more quickly and more accurately by ingesting
        // minibatches 5-10 elements at a time in parallel.
        // this example runs better without, because the dataset is smaller than
        // the mini batch size
        builder.miniBatch(false);

        // create a multilayer network with 2 layers (including the output
        // layer, excluding the input payer)
        ListBuilder listBuilder = builder.list();

        DenseLayer.Builder hiddenLayerBuilder = new DenseLayer.Builder();
        // two input connections - simultaneously defines the number of input
        // neurons, because it's the first non-input-layer
        hiddenLayerBuilder.nIn(gameBoard.BoardWidth);
        // number of outgooing connections, nOut simultaneously defines the
        // number of neurons in this layer
        hiddenLayerBuilder.nOut(gameBoard.BoardWidth * 2);
        // put the output through the sigmoid function, to cap the output
        // valuebetween 0 and 1
        hiddenLayerBuilder.activation(Activation.SIGMOID);
        // random initialize weights with values between 0 and 1
        hiddenLayerBuilder.weightInit(WeightInit.DISTRIBUTION);
        hiddenLayerBuilder.dist(new UniformDistribution(0, 1));

        // build and set as layer 0
        listBuilder.layer(0, hiddenLayerBuilder.build());

        // MCXENT or NEGATIVELOGLIKELIHOOD (both are mathematically equivalent) work ok for this example - this
        // function calculates the error-value (aka 'cost' or 'loss function value'), and quantifies the goodness
        // or badness of a prediction, in a differentiable way
        // For classification (with mutually exclusive classes, like here), use multiclass cross entropy, in conjunction
        // with softmax activation function
        Builder outputLayerBuilder = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD);
        // must be the same amout as neurons in the layer before
        outputLayerBuilder.nIn(gameBoard.BoardWidth * 2);
        // two neurons in this layer
        outputLayerBuilder.nOut(gameBoard.BoardWidth);
        outputLayerBuilder.activation(Activation.SOFTMAX);
        outputLayerBuilder.weightInit(WeightInit.DISTRIBUTION);
        outputLayerBuilder.dist(new UniformDistribution(0, 1));
        listBuilder.layer(1, outputLayerBuilder.build());

        // no pretrain phase for this network
        listBuilder.pretrain(false);

        // seems to be mandatory
        // according to agibsonccc: You typically only use that with
        // pretrain(true) when you want to do pretrain/finetune without changing
        // the previous layers finetuned weights that's for autoencoders and
        // rbms
        listBuilder.backprop(true);

        // build and init the network, will check if everything is configured
        // correct
        MultiLayerConfiguration conf = listBuilder.build();
        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();

        // add an listener which outputs the error every 100 parameter updates
        net.setListeners(new ScoreIterationListener(100));

        // C&P from GravesLSTMCharModellingExample
        // Print the number of parameters in the network (and for each layer)
        Layer[] layers = net.getLayers();
        int totalNumParams = 0;
        for (int i = 0; i < layers.length; i++) {
            int nParams = layers[i].numParams();
            System.out.println("Number of parameters in layer " + i + ": " + nParams);
            totalNumParams += nParams;
        }
        System.out.println("Total number of network parameters: " + totalNumParams);

        // here the actual learning takes place
        if(recentResult == null)
            net.fit(ds);
        else
            net.pretrain(recentResult);
        

        // create output for every training sample
        INDArray output = net.output(ds.getFeatureMatrix());
        recentResult = output;
        
        System.out.println(output);

        // let Evaluation prints stats how often the right output had the
        // highest value
        Evaluation eval = new Evaluation(2);
        eval.eval(ds.getLabels(), output);
        System.out.println(eval.stats());
		
	}
	
	/**
	 * 상위 패널에서 그래픽스 정보를 받아 정보를 그린다.
	 * @param g
	 */
	public void paint(Graphics g) {
		
	}
}
