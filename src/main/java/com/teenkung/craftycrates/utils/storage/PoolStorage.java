package com.teenkung.craftycrates.utils.storage;

import java.util.ArrayList;

public class PoolStorage {

    private String category;
    private String id;
    private Integer amount;
    private ArrayList<String> commands;
    private Integer weight;

    public PoolStorage(String category, String id, Integer amount, ArrayList<String> commands, Integer weight) {
        this.category = category;
        this.id = id;
        this.amount = amount;
        this.commands = commands;
        this.weight = weight;
    }

    public String getCategory() { return category; }
    public String getId() { return id; }
    public Integer getAmount() { return amount; }
    public ArrayList<String> getCommands() { return commands; }
    public Integer getWeight() { return weight; }

}
