package com.teenkung.craftycrates.utils;

public class RarityStorage {

    private String display;
    private Integer chance;
    private String pool;
    public RarityStorage(String display, Integer chance, String pool) {
        this.display = display;
        this.chance = chance;
        this.pool = pool;
    }

    public String getDisplay() { return display; }
    public Integer getChance() { return chance; }
    public String getPool() { return pool; }

}
