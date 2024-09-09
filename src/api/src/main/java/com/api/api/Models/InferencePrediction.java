package com.api.api.Models;

import java.util.List;

public class InferencePrediction {
    private List<String> labels;
    private List<Float> prob;
    private String model;

    public InferencePrediction() {}
    public InferencePrediction(
        List<String> labels,
        List<Float> prob,
        String model
    ) {
        this.labels = labels;
        this.prob = prob;
        this.model = model;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getLabels() {
        return this.labels;
    }

    public void setModel(String model) {
        this.model = model;
    }

    
    public void setProb(List<Float> prob) {
        this.prob = prob;
    }

    public List<Float> getProb() {
        return this.prob;
    }

    public String getModel() {
        return this.model;
    }
}