package com.weibo.generator.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class ControlMessage implements Serializable {
    private String userId;
    private boolean withGrouping = false;
    private String windowSize;
    private String slideSize;
    private String vertexLabel;
    private String edgeLabel;
    private Long timestamp;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isWithGrouping() {
        return withGrouping;
    }

    public void setWithGrouping(boolean withGrouping) {
        this.withGrouping = withGrouping;
    }

    public String getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(String windowSize) {
        this.windowSize = windowSize;
    }

    public String getSlideSize() {
        return slideSize;
    }

    public void setSlideSize(String slideSize) {
        this.slideSize = slideSize;
    }

    public String getVertexLabel() {
        return vertexLabel;
    }

    public void setVertexLabel(String vertexLabel) {
        this.vertexLabel = vertexLabel;
    }

    public String getEdgeLabel() {
        return edgeLabel;
    }

    public void setEdgeLabel(String edgeLabel) {
        this.edgeLabel = edgeLabel;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp() {
        this.timestamp = new Timestamp(System.currentTimeMillis()).getTime();
    }

    @Override
    public String toString() {
        return String.format("{" +
                        "\"windowSize\":\"%s\"," +
                        "\"slideSize\":\"%s\"," +
                        "\"vertexLabel\":\"%s\"," +
                        "\"edgeLabel\":\"%s\"," +
                        "\"withGrouping\":\"%s\"," +
                        "\"timestamp\":%d" +
                        "}",
                windowSize, slideSize, vertexLabel, edgeLabel, withGrouping, timestamp);
    }
}