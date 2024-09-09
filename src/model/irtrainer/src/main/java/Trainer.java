import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class Trainer {

    public static MultiLayerNetwork buildModel(
                int numInputs,
                int numOutputs,
                Float learningRate,
                Integer seed
        ) {
        // Initiate multi layer network model configuration
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                // Simple Adam optimizer
                .updater(new Adam(learningRate))
                .list()
                .layer(0, new ConvolutionLayer.Builder(3, 3)
                        .nIn(3) // input depth (e.g., RGB channels)
                        .nOut(16) // number of filters
                        .stride(1, 1) // stride
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(1, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2) // pool size
                        .stride(2, 2) // stride
                        .build())
                .layer(2, new ConvolutionLayer.Builder(3, 3)
                        .nOut(32) // number of filters
                        .stride(1, 1) // stride
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(3, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2) // pool size
                        .stride(2, 2) // stride
                        .build())
                .layer(4, new ConvolutionLayer.Builder(3, 3)
                        .nOut(16) // number of filters
                        .stride(1, 1) // stride
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(5, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.MAX)
                        .kernelSize(2, 2) // pool size
                        .stride(2, 2) // stride
                        .build())
                .layer(6, new DenseLayer.Builder()
                        .nOut(256) // number of neurons
                        .activation(Activation.RELU)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .layer(7, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(numOutputs) // number of output neurons (equal to number of classes)
                        .activation(Activation.SOFTMAX)
                        .weightInit(WeightInit.XAVIER)
                        .build())
                .setInputType(InputType.convolutional(500, 500, 3)) // Input dimensions: height, width, channels
                .build();


        // Initialize multi layer network model ( deep neural network )
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        return model;
    }
}
