package com.demo.commons.logging.obfuscation.header;

import com.demo.commons.constants.Symbol;
import com.demo.commons.logging.obfuscation.constants.ObfuscationConstant;
import com.demo.commons.properties.logging.ObfuscationTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class HeaderObfuscator {

    public static String process(ObfuscationTemplate obfuscation,
                                 Map<String, String> headers) {

        Set<String> headerFields = Optional.ofNullable(obfuscation)
            .filter(template -> Objects.nonNull(template.headers()))
            .map(ObfuscationTemplate::headers)
            .orElse(Set.of());

        return headers
            .entrySet()
            .stream()
            .map(entry -> obfuscateHeader(entry, headerFields))
            .collect(Collectors.joining(Symbol.COMMA_WITH_SPACE));
    }

    private static String obfuscateHeader(Map.Entry<String, String> header,
                                          Set<String> sensitiveHeaders) {
        String key = header.getKey();
        return !sensitiveHeaders.contains(key)
            ? key + Symbol.EQUAL + header.getValue()
            : Optional.ofNullable(header.getValue())
                .map(value -> key + Symbol.EQUAL + partiallyObfuscate(value))
                .orElse(key + Symbol.EQUAL + ObfuscationConstant.NULL_WARNING);
    }

    private static String partiallyObfuscate(String value) {
        return value.length() <= 6
            ? ObfuscationConstant.OBFUSCATION_MASK
            : value.substring(0, 3) + ObfuscationConstant.OBFUSCATION_MASK + value.substring(value.length() - 3);
    }
}