package com.api.api.Models;

public class ModelMetadata {
    private String name;

    public ModelMetadata() {}
    public ModelMetadata(
        String name
    ) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
