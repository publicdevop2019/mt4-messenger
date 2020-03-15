package com.hw.clazz;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> strings) {
        if (ObjectUtils.isEmpty(strings))
            return null;
        return String.join(",", strings);
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        if (StringUtils.hasText(s))
            return Arrays.stream(s.split(",")).collect(Collectors.toList());
        return Collections.emptyList();
    }
}
