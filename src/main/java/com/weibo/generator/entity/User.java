package com.weibo.generator.entity;

import java.util.ArrayList;


public class User extends Vertex {
    private String name;
    private String id;
    private String city;
    private int age;
    private String gender;
    private ArrayList<User> fans;


    public User(String name, String id, String city, int age, String gender) {
        this.name = name;
        this.id = id;
        this.city = city;
        this.age = age;
        this.gender = gender;
        this.fans = new ArrayList<>();
    }


    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public ArrayList<User> getFans() {
        return fans;
    }

    public void followBy(User user) {
        this.fans.add(user);
    }


    @Override
    public String toString() {
        return String.format("{" +
                        "\"id\":\"%s\"," +
                        "\"label\":\"%s\"," +
                        "\"name\":\"%s\", " +
                        "\"city\":\"%s\", " +
                        "\"age\":%d, " +
                        "\"gender\":\"%s\"" +
                        "}",
                this.id, this.getLabel(), name, city, this.age, this.gender);
    }

}
