package com.sismics.books.rest.util;

import java.util.List;

public class StringUtil {
    
    public static String escapeHtml(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;").replaceAll("'", "&#39;");
    }

    public static String ListToString(List<String> list, String separator) {
        if (list == null) {
            return null;
        }
        if (separator == null) {
            separator = "\\|";
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            result.append(list.get(i));
            if (i < list.size() - 1) {
                result.append(separator).append(" ");
            }
        }
        return result.toString();
    }

    public static List<String> StringToList(String input, String separator) {
        if (input == null) {
            return null;
        }
        if (separator == null) {
            separator = "\\|";
        }
        return java.util.Arrays.asList(input.split(separator));
    }
}
