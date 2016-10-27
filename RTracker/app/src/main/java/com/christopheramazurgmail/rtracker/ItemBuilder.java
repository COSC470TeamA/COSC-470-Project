package com.christopheramazurgmail.rtracker;

import java.util.ArrayList;

/**
 * Created by haunter on 26/10/16.
 */
public class ItemBuilder {

    /**
     * Creates an arbitrary number of Item objects and returns them in an Array List
     * in the order they are input.
     * @param attr The list of description, price pairs. The price must be in number
     *             format and will be cast to a double.
     * @return The new list of Item objects.
     */
    public ArrayList<Item> build(String... attr) {
        // Don't divide by zero!
        if (attr.length == 0) return new ArrayList<>();
        ArrayList<Item> list = new ArrayList<>(attr.length / 2);
        if (attr.length % 2 == 0) {
            for (int i = 0; i < attr.length; i += 2) {
                try {
                    list.add(new Item(attr[i], Double.parseDouble(attr[i + 1])));
                }
                catch (NumberFormatException n) {
                    n.printStackTrace();
                }
            }
        }
        return list;
    }
}
