package com.example.node.model.metaData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@ToString
public class IndexMetaData {
    List<String> items;

    public IndexMetaData() {
        items = new ArrayList<>();
    }

    public IndexMetaData(String path) {
        items = new ArrayList<>();
        items.add(path);
    }

    public void addItem(String item) {
        items.add(item);
    }

    public void removeItemById(String id) {
        items.removeIf(item -> item.contains(id));
    }
}
