package com.teenkung.craftycrates.utils.record;

import java.util.ArrayList;

public record PoolStorage(String category, String id, Integer amount, ArrayList<String> commands, Integer weight) {

}
