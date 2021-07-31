package com.weibo.generator.entity;

public enum CitiBikeEdgeLabel {
    Ride,
    TripFrom,
    TripTo,
    RiddenFrom,
    RiddenTo,
    Control;

    public String getLabel() {
        return this.name();
    }
}
