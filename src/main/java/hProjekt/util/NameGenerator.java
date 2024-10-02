package hProjekt.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

public class NameGenerator {
    public final Map<String, SortedMap<Character, Integer>> ngramCollection = new HashMap<>();
    private final int order;
    private final Random random;

    public NameGenerator(String[] names, int order, Random random) {
        this.random = random;
        this.order = order;
        train(names, order);
    }

    public NameGenerator(String[] names, int order) {
        this(names, order, new Random());
    }

    private void train(String[] names, int order) {
        for (String name : names) {
            name = "^" + name + "$";
            for (int i = order; i < name.length(); i++) {
                String ngram = name.substring(i - order, i);
                char nextChar = name.charAt(i);

                ngramCollection.putIfAbsent(ngram, new TreeMap<>());

                ngramCollection.get(ngram).put(nextChar,
                        ngramCollection.get(ngram).getOrDefault(nextChar, 0) + 1);
            }
        }
    }

    private char weightedRandomChoice(List<Character> characters, List<Integer> weights) {
        int totalWeight = weights.stream().mapToInt(Integer::intValue).sum();
        int randomIndex = random.nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (int i = 0; i < characters.size(); i++) {
            cumulativeWeight += weights.get(i);
            if (randomIndex < cumulativeWeight) {
                return characters.get(i);
            }
        }

        return characters.get(characters.size() - 1);
    }

    public String generateName(int length) {
        String result = ((Function<List<String>, String>) (ngrams) -> {
            return ngrams.stream().skip(random.nextInt(ngrams.size())).findFirst().get();
        }).apply(ngramCollection.keySet().stream().filter(ngram -> ngram.startsWith("^")).toList());

        for (int i = 0; i < length; i++) {
            String ngram = result.substring(result.length() - order);

            if (!ngramCollection.containsKey(ngram)) {
                break;
            }

            Character nextChar = weightedRandomChoice(ngramCollection.get(ngram).keySet().stream().toList(),
                    ngramCollection.get(ngram).values().stream().toList());

            if (nextChar == '$') {
                break;
            }

            result += nextChar;
        }
        return result.substring(1, 2).toUpperCase() + result.substring(2);
    }
}
