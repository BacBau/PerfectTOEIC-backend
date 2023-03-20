package org.example.utils;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final String NO_BREAK_SPACE = " ";
    private static final String URL_REGEX ="(https?:\\/\\/([^\\s/]{1,256}\\.\\w{2,6})\\b(\\S*[\\w/])*)";

    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);


    public static String sanitizerHtml(String html) {
        PolicyFactory policy = Sanitizers.BLOCKS.and(Sanitizers.LINKS).
                and(Sanitizers.FORMATTING).and(Sanitizers.IMAGES).and(Sanitizers.STYLES);
        return removeNoBreakSpace(policy.sanitize(html).trim());
    }

    private static String removeNoBreakSpace(String s) {
        return s.replace(NO_BREAK_SPACE, " ");
    }

    public static String extractHtmlContent(String html) {
        String content = sanitizerHtml(html);
        final BoilerpipeExtractor extractor = CommonExtractors.KEEP_EVERYTHING_EXTRACTOR;
        try {
            return extractor.getText(removeNoBreakSpace(content)).trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static List<String> getLinksInContent(String content) {
        Matcher matcher = URL_PATTERN.matcher(content);
        List<String> urls = new ArrayList<>();

        while (matcher.find()) {
            urls.add(matcher.group());
        }
        return urls;
    }

}
