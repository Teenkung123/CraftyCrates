package com.teenkung.craftycrates.utils.selector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WeightedRandomSelector {

    /**
     * Select a random item from a map of items and their corresponding weights.
     * @param items a map of items and their corresponding weights
     * @return the selected item, or null if the map is empty
     */
    public static <K> K select(Map<K, Integer> items) {
        // Create a new HashMap to store the items and their weights.
        // We do this to ensure that the original map is not modified by this method.
        Map<K, Integer> weights = new HashMap<>(items);

        // Calculate the total weight of all the items in the map.
        int totalWeight = 0;
        for (int weight : weights.values()) {
            totalWeight += weight;
        }

        // Generate a random number between 0 and the total weight.
        Random random = new Random();
        int selectedWeight = random.nextInt(totalWeight);

        // Loop through the items in the map and subtract their weights
        // from the selected weight until we find the selected item.
        for (Map.Entry<K, Integer> entry : weights.entrySet()) {
            K key = entry.getKey();
            int weight = entry.getValue();

            if (selectedWeight < weight) {
                return key;
            }

            selectedWeight -= weight;
        }

        // If no item was selected, return null.
        return null;
    }
}
