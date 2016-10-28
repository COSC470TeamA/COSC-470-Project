package com.christopheramazurgmail.rtracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haunter on 27/10/16.
 *
 * Handles building collections of Items.
 */
public class ItemGroup extends ArrayList{

    List<Item> items;

    public ItemGroup() {}
    public ItemGroup(List<Item> item) {
        this.items = item;
    }


    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }


}
