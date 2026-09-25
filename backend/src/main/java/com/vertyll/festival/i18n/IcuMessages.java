package com.vertyll.festival.i18n;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.icu.text.MessagePattern;

final class IcuMessages {

    private static final Pattern TAG = Pattern.compile("</?([A-Za-z][A-Za-z0-9]*)>");

    private IcuMessages() {
    }

    static boolean isValid(String message) {
        try {
            new MessagePattern(message);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    static Set<String> placeholders(String message) {
        MessagePattern pattern = new MessagePattern(message);
        Set<String> placeholders = new TreeSet<>();
        for (int index = 0; index < pattern.countParts(); index++) {
            MessagePattern.Part part = pattern.getPart(index);
            if (part.getType() == MessagePattern.Part.Type.ARG_NAME
                    || part.getType() == MessagePattern.Part.Type.ARG_NUMBER) {
                placeholders.add("{" + pattern.getSubstring(part) + "}");
            }
        }
        Matcher tags = TAG.matcher(message);
        while (tags.find()) {
            placeholders.add("<" + tags.group(1) + ">");
        }
        return placeholders;
    }
}
