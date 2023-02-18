package com.teenkung.craftycrates.utils.selector;

import java.util.Map;
import java.util.Random;

public class ChanceRandomSelector {
    public static <K> K selectByChance(Map<K, Float> map) {
        if (map.isEmpty()) {
            return null;
        }

        float total = 0;
        K highestKey = null;
        float highestChance = 0;

        for (Map.Entry<K, Float> entry : map.entrySet()) {
            float chance = entry.getValue();
            total += chance;
            if (chance > highestChance) {
                highestKey = entry.getKey();
                highestChance = chance;
            }
        }

        if (total == 0) {
            return highestKey;
        }

        float random = new Random().nextFloat() * total;
        float cumulative = 0;

        for (Map.Entry<K, Float> entry : map.entrySet()) {
            float chance = entry.getValue();
            cumulative += chance;
            if (cumulative > random) {
                return entry.getKey();
            }
        }

        return highestKey;
    }

}
