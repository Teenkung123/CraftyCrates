package com.teenkung.craftycrates.utils.storage;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public record ItemPair(ItemStack stack, ArrayList<String> command) {

}
