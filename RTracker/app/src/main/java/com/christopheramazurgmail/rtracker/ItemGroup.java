package com.christopheramazurgmail.rtracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haunter on 27/10/16.
 *
 * Handles building collections of Items.
 */
public class ItemGroup extends ArrayList{

    List<Item> items = new ArrayList<>();

    public ItemGroup() {}
    public ItemGroup(List<Item> item) {
        this.items = item;
    }


    public List<Item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        String r = "ItemGroup{" + "items=";
        for (Item i : items) {
            r += i.toString() + ",";
        }
        return r += "}\n";
    }

    public void add(Item item) {
        items.add(item);
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }


}
