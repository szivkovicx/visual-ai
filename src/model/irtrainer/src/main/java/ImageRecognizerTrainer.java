import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.opencv.core.Mat;

public class ImageRecognizerTrainer {
    public static void main(String[] args) {
        try {
            // Read the conf.json
            JSONObject conf = new Configuration().read();

            // Pull out important metadata and training parameters
            final String name = conf.getString("name");
            final String version = conf.getString("version");
            final JSONArray jsonLabels = conf.getJSONArray("labels");
            final JSONArray confResolution = conf.getJSONObject("training_parameters").getJSONArray("resolution");
            final Integer[] resolution = { confResolution.getInt(0), confResolution.getInt(1) };
            final Float learningRate = conf.getJSONObject("training_parameters").getFloat("learning_rate");
            final Integer seed = conf.getJSONObject("training_parameters").getInt("seed");
            final Integer epochs = conf.getJSONObject("training_parameters").getInt("epochs");

            File modelDirectory = new File("../models/"+name+"_"+version);

            if (modelDirectory.exists()) {
                Logger.info("Model with this name already exists.", true);
                System.exit(0);
            }

            Logger.preTrainingSummaryLog(name, version, jsonLabels);
            Logger.info("Preparing data...", true);

            // Define labels and set of features for each label
            final List<String> labels = new ArrayList();
            final List<List<INDArray>> features = new ArrayList();

            for (int i = 0; i < jsonLabels.length(); i++) {
                // Get label ID
                String id = jsonLabels.getJSONObject(i).getString("id");
                labels.add(id);
                // Get image paths
                JSONArray jsonPaths = jsonLabels.getJSONObject(i).getJSONArray("data");
                // Initiate a new list of INDArrays for set of features for given label ID
                List<INDArray> tensors = new ArrayList();
                for (int j = 0; j < jsonPaths.length(); j++) {
                    // Get image, resize and convert to tensor ( feature tesor )
                    Mat image = ImageTensorConvertor.loadImage(jsonPaths.getString(j));
                    Mat resizedImage = ImageTensorConvertor.resizeImage(image, resolution[0], resolution[1]);
                    INDArray featureTensor = ImageTensorConvertor.imageToINDArray(resizedImage);
                    tensors.add(featureTensor);    
                }
                // Add set of tensors to features
                features.add(tensors);
            }

            // Generate an label map needed for preparing training data
            Map<String, Integer> labelMap = DataSetGenerator.createLabelMap(labels);
            int numOutputs = labelMap.size();

            // Create DataSetIterator from features, labels and label map
            DataSetIterator trainData = DataSetGenerator.createDataSetIterator(features, labels, labelMap);

            Logger.info("Training...", false);
            
            // Initiate multi layer network model and train it ( fitting )
            MultiLayerNetwork model = Trainer.buildModel(
                resolution[0] * resolution[1] * 3,
                numOutputs,
                learningRate,
                seed
            );

            for (int epoch = 0; epoch < epochs; epoch++) {
                model.fit(trainData);
                Logger.info("Epoch " + (epoch+1) + " completed.", false);
            }

            modelDirectory.mkdir();

            // Write model as a .vision file
            File modelFile = new File(modelDirectory, "model.vision");
            ModelSerializer.writeModel(model, modelFile, true);

            // Write model metadata into JSON
            JSONObject modelMeta = new JSONObject();
            modelMeta.put("name", name);
            modelMeta.put("version", version);
            modelMeta.put("labels", labels);

            File modelMetaFile = new File(modelDirectory, "meta.json");

            try (FileWriter fileWriter = new FileWriter(modelMetaFile)) {
                fileWriter.write(modelMeta.toString(4)); // 4 is the indentation factor
            } catch (IOException e) {
                Logger.info("An error occurred while writing to the file.", true);
                e.printStackTrace();
            }
    
            Logger.info("Model saved", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);
    }
}
