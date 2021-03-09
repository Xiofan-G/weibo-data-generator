package com.weibo.generator.entity;

import java.util.ArrayList;


public class Weibo extends Vertex {
    private String id;
    private String name;  // Weibo's content, max size is 140 unicode
    private String agent; // User's phone type, such as Iphone X
    private ArrayList<Tag> tags; // Weibo's Tag
    private User author;  // weibo's author
    private ArrayList<Weibo> mentionedWeibos;
    private ArrayList<User> atUsers;

    public Weibo(String id, String name, String agent, ArrayList<Tag> tags, User author) {
        this.id = id;
        this.name = name;
        this.agent = agent;
        this.tags = tags;
        this.author = author;
        this.mentionedWeibos = new ArrayList<>();
        this.atUsers = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public String getAgent() {
        return agent;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public User getAuthor() {
        return author;
    }

    public void addMentionedWeibo(Weibo weibo) {
        this.mentionedWeibos.add(weibo);
    }

    public void addAtUser(User atUser) {
        this.atUsers.add(atUser);
    }

    public ArrayList<Weibo> getMentionedWeibos() {
        return mentionedWeibos;
    }

    public ArrayList<User> getAtUsers() {
        return atUsers;
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getId() {
        return this.id;
    }


    @Override
    public String toString() {
        return String.format("{" +
                        "\"id\":\"%s\"," +
                        "\"label\":\"%s\"," +
                        "\"name\":\"%s\"," +
                        "\"agent\":\"%s\"" +
                        "}",
                this.id, this.getLabel(), this.name, this.agent);
    }
}
