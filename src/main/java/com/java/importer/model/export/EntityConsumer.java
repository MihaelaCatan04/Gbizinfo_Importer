package com.java.importer.model.export;

import java.util.List;

@FunctionalInterface
public interface EntityConsumer {
    void accept(EntityType type, List<?> List);
}
