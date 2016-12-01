package com.ateam.rtracker;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by brewi on 2016-10-31.
 */

public class Category implements Serializable {
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


    public class Item implements Serializable {
        private String name;

        public Item(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
