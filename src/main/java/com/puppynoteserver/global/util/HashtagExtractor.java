package com.puppynoteserver.global.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HashtagExtractor {

    // @해시태그 형태 추출 (한글, 영문, 숫자 포함)
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("@([\\w가-힣]+)");

    public List<String> extract(String content) {
        if (content == null || content.isBlank()) {
            return List.of();
        }
        Matcher matcher = HASHTAG_PATTERN.matcher(content);
        List<String> hashtags = new ArrayList<>();
        while (matcher.find()) {
            hashtags.add(matcher.group(1));
        }
        return hashtags;
    }
}
