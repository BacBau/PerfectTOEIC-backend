package org.example.search;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class SearchDataGenerator {

    private static final Pattern patternCheckWordVietnamese = Pattern.compile("^[a-zA-Z0-9 ]+$");


    public static String toDataSearch(String data) {
        List<String> words = toWords(data);
        int size = words.size();
        if (size == 0) return "";
        StringBuilder dataSearchBuilder = new StringBuilder();
        for (int i = 0; i < size - 1; i ++) {
            dataSearchBuilder.append("-").append(words.get(i));
        }
        dataSearchBuilder.append("-").append(words.get(size - 1)).append("-");
        return dataSearchBuilder.toString().toLowerCase();
    }

    public static String toAliasSearch(String keyword) {
        List<String> words = toWords(keyword.toLowerCase());
        StringBuilder searchTextBuilder = new StringBuilder();
        for (String word : words)
            searchTextBuilder.append("*-").append(word).append("* ");
        return searchTextBuilder.toString();
    }

    public static boolean isVietnameseWord(String input) {
        return !patternCheckWordVietnamese.matcher(input).matches();
    }

    public static String normalizationText(String input) {
        if (input == null) return null;
        return Normalizer.normalize(input, Normalizer.Form.NFC);
    }

    public static String toEn(String input) {
        return Normalizer
                .normalize(input, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    private static List<String> toWords(String keyWord) {
        return Arrays.asList(keyWord.trim().split("\\s"));
    }
}
