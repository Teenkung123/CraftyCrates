package com.teenkung.craftycrates.utils;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.manager.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PullStorage {

    private String category;
    private String id;
    private Integer amount;
    private ArrayList<String> commands = new ArrayList<>();
    private Integer weight;

    public PullStorage(String category, String id, Integer amount, ArrayList<String> commands, Integer weight) {
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

    public ArrayList<ItemPair> generateItemStack(Player player, Boolean executecommand) {
        ArrayList<ItemPair> items = new ArrayList<>();
        for (int i = 0 ; i < amount ; i++) {
            ItemStack stack = MMOItems.plugin.getItem(category, id);
            ItemPair pair;
            if (executecommand) {
                pair = new ItemPair(stack, commands);
            } else {
                pair = new ItemPair(stack, new ArrayList<>());
            }
            items.add(pair);
        }
        return items;
    }

}
