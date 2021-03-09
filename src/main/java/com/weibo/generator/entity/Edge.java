package com.weibo.generator.entity;

import java.sql.Timestamp;

public class Edge<S extends Vertex, T extends Vertex> {
    private Long timestamp;
    private String label;
    private String id;
    private S source;
    private T target;
    private String remark;

    public Edge(S source, T target, String type, String id, String remark) {
        this.source = source;
        this.target = target;
        this.label = type;
        this.id = id;
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.timestamp = timestamp.getTime();
        this.remark = remark;
    }

    @Override
    public String toString() {
        return String.format("{" +
                        "\"id\":\"%s\"," +
                        "\"label\":\"%s\"," +
                        "\"remark\":\"%s\"," +
                        "\"source\":%s, " +
                        "\"target\":%s, " +
                        "\"timestamp\":\"%d\"" +
                        "}",
                id, label, remark, source.toString(), target.toString(), timestamp);
    }
}
