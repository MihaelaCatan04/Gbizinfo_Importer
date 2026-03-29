package com.java.importer.model.export;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class EntityCollector {
    private final Map<EntityType, List<Object>> data = new EnumMap<>(EntityType.class);

    public void add(EntityType type, Object item) {
        data.computeIfAbsent(type, k -> new ArrayList<>()).add(item);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(EntityType type) {
        return (List<T>) data.getOrDefault(type, List.of());
    }

    public void forEachNonEmpty(EntityConsumer consumer) {
        data.forEach((type, list) -> {
            if (!list.isEmpty()) {
                consumer.accept(type, list);
            }
        });
    }

}
