package com.weibo.generator.entity;

public enum CitiBikeVertexLabel {
    Bike,
    User,
    Station;

    public String getLabel() {
        return this.name();
    }
}
