package com.weibo.generator.entity;

import com.github.javafaker.Faker;

public abstract class Vertex {
    private static Faker faker = new Faker();

    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    public String getVersion() {
        int version = faker.number().numberBetween(0, 10);
        return this.getLabel() + version;
    }


    abstract String getId();
}
