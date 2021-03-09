package com.weibo.generator.entity;

public class Tag extends Vertex {
    private String id;
    private String name;
    private String desc;

    public Tag(String id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return String.format("{" +
                        "\"id\":\"%s\"," +
                        "\"label\":\"%s\"," +
                        "\"name\":\"%s\", " +
                        "\"desc\":\"%s\"" +
                        "}",
                this.id, this.getLabel(), name, desc);
    }
}
