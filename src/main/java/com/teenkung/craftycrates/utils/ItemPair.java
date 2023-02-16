package com.teenkung.craftycrates.utils;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ItemPair {

    private final ArrayList<String> command;
    private final ItemStack stack;

    public ItemPair(ItemStack stack, ArrayList<String> command) {
        this.command = command;
        this.stack = stack;
    }

    public ArrayList<String> getCommand() {
        return command;
    }

    public ItemStack getStack() {
        return stack;
    }
}
