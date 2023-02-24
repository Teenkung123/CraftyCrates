package com.teenkung.craftycrates.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ItemSerialization {
    // Convert ItemStack to a String
    public static String serializeItemStack(ItemStack itemStack) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

        // Write the ItemStack to the ObjectOutputStream
        dataOutput.writeObject(itemStack);

        // Convert the output stream to a byte array and then to a base-64 encoded string
        return java.util.Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    // Convert a String to an ItemStack
    public static ItemStack deserializeItemStack(String data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(java.util.Base64.getDecoder().decode(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

        // Read the ItemStack from the ObjectInputStream
        return (ItemStack) dataInput.readObject();
    }
}
