package com.weibo.generator.entity;

import com.github.javafaker.Faker;

import java.util.ArrayList;


public class Comment extends Vertex {
    private static Faker faker;
    private String id;
    private String name;  // Weibo's content, max size is 140 unicode
    private String agent; // User's phone type, such as Iphone X
    private User author;
    private Weibo replyOf;
    private ArrayList<User> atUsers;
    private ArrayList<Weibo> mentionedWeibos;


    public Comment(String id, String name, String agent, User author, Weibo replyOf) {
        this.id = id;
        this.name = name;
        this.agent = agent;
        this.author = author;
        this.replyOf = replyOf;
        this.atUsers = new ArrayList<>();
        this.mentionedWeibos = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    public String getAgent() {
        return agent;
    }

    public User getAuthor() {
        return author;
    }

    public Weibo getReplyOf() {
        return replyOf;
    }

    public ArrayList<User> getAtUsers() {
        return atUsers;
    }

    public ArrayList<Weibo> getMentionedWeibos() {
        return mentionedWeibos;
    }

    public void addAtUser(User atUser) {
        this.atUsers.add(atUser);
    }

    public void addMentionedWeibo(Weibo mentionedWeibo) {
        this.mentionedWeibos.add(mentionedWeibo);
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
