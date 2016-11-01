package com.christopheramazurgmail.rtracker;

import java.util.LinkedList;

/**
 * Created by brewi on 2016-10-31.
 */

public class Category {
    private LinkedList<Item> itemList;
    private String name;

    public Category(String name) {
        this.name = name;
        itemList = new LinkedList<Item>();
    }

    public void addItemToList(String itemName) {
        itemList.add(new Item(itemName));
    }
    public String getName() {
        return name;
    }

    public LinkedList<Item> getItemList() {
        return itemList;
    }


    public class Item {
        private String name;

        public Item(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
