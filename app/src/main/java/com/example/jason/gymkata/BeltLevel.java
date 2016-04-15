package com.example.jason.gymkata;

/**
 * Created by Jason on 2016-04-02.
 */
public class BeltLevel {
    private long id;
    private String beltLevel;
    private String description;


    public BeltLevel() {
    }

    public BeltLevel(long id, String beltLevel) {
        this.id = id;
        this.beltLevel = beltLevel;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBeltLevel() {
        return beltLevel;
    }

    public void setBeltLevel(String beltLevel) {
        this.beltLevel = beltLevel;
    }

    public String getDescription(){return description;}

    public void setDescription(String d) {this.description = d;}
}
