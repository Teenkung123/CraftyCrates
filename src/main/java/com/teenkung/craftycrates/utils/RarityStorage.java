package com.teenkung.craftycrates.utils;

public class RarityStorage {

    private String display;
    private Double chance;
    private String pool;
    public RarityStorage(String display, Double chance, String pool) {
        this.display = display;
        this.chance = chance;
        this.pool = pool;
    }

    public String getDisplay() { return display; }
    public Double getChance() { return chance; }
    public String getPool() { return pool; }

}
