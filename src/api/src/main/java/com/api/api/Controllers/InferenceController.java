package com.api.api.Controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.opencv.core.Mat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.api.Models.InferencePrediction;
import com.api.api.Models.ModelMetadata;
import com.api.api.Utils.Utils;

@RestController
@CrossOrigin
@RequestMapping("/api/inference")
public class InferenceController {
    
    @GetMapping("/list")
    public List<ModelMetadata> list() {
        List<ModelMetadata> data = new ArrayList();
        List<String> fileNames = Utils.getDirectoryNames("../model/models");
        for (String fileName : fileNames) {
            data.add(new ModelMetadata(fileName));
        }
        return data;
    }

    @PostMapping("/predict/{id}")
    public InferencePrediction predict(
        @PathVariable("id") String id,
        @RequestParam("file") MultipartFile input
    ) {
        try {
            File modelFile = new File("../model/models/" + id + "/model.vision");
            String metaFileContents = new String(
                Files.readAllBytes(
                    Paths.get("../model/models/" + id + "/meta.json")
                )
            );
            JSONObject meta = new JSONObject(metaFileContents);
            MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork(modelFile);

            byte[] imageBytes = input.getBytes();

            Mat loadedImage = Utils.loadImageFromBytes(imageBytes);
            Mat resized = Utils.resizeImage(loadedImage, 500, 500);
            INDArray inputData = Utils.imageToINDArray(resized);

            INDArray output = model.output(inputData).getRow(0);

            InferencePrediction pred = new InferencePrediction();
            List<String> labels = new ArrayList();
            List<Float> prob = new ArrayList();

            for (Object label: meta.getJSONArray("labels").toList()) {
                labels.add(label.toString());
            }
            
            for (int i = 0; i < output.length(); i++) {
                prob.add(output.getFloat(i)*100);
            }

            pred.setLabels(labels);
            pred.setProb(prob);
            pred.setModel(id);
    
            return pred;    
        } catch (Exception e) {
            e.printStackTrace();
            return new InferencePrediction();
        }
    }
}
