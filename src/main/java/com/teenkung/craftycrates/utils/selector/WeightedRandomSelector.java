package com.teenkung.craftycrates.utils.selector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WeightedRandomSelector<K> {

    private final Map<K, Integer> weights;
    private final Random random;

    public WeightedRandomSelector(Map<K, Integer> weights) {
        this.weights = new HashMap<>(weights);
        this.random = new Random();
    }

    public K select() {
        int totalWeight = 0;
        for (int weight : weights.values()) {
            totalWeight += weight;
        }

        int selectedWeight = random.nextInt(totalWeight);

        for (Map.Entry<K, Integer> entry : weights.entrySet()) {
            K key = entry.getKey();
            int weight = entry.getValue();

            if (selectedWeight < weight) {
                return key;
            }

            selectedWeight -= weight;
        }

        return null;
    }
}
