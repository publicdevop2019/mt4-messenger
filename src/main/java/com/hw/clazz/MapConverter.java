package com.hw.clazz;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MapConverter implements AttributeConverter<Map<String, String>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, String> stringStringMap) {
        if (stringStringMap == null)
            return "";
        return stringStringMap.keySet().stream().map(e -> e + ":" + stringStringMap.get(e)).collect(Collectors.joining(","));
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String s) {
        if (s.equals("")) {
            return null;
        }
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        Arrays.stream(s.split(",")).forEach(e -> {
            stringStringHashMap.put(e.split(":")[0], e.split(":")[1]);
        });
        return stringStringHashMap;
    }
}
