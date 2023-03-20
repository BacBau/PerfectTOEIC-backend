package org.example.common.solr.repository;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static List<String> toList(String value, char separator) {
        if (value == null) {
            return new ArrayList(0);
        } else {
            List<String> temp = new ArrayList(value.length() / 2 + 1);
            int start = 0;

            for(int index = value.indexOf(separator); index >= 0; index = value.indexOf(separator, start)) {
                temp.add(value.substring(start, index));
                start = index + 1;
            }

            temp.add(value.substring(start));
            return temp;
        }
    }
}
