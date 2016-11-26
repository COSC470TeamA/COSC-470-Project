package com.christopheramazurgmail.rtracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by haunter on 27/10/16.
 *
 * Handles building collections of Items.
 */
public class ItemGroup extends ArrayList{

    ArrayList<Item> items = new ArrayList<>();

    public ItemGroup() {
    }
    public ItemGroup(ArrayList<Item> item) {
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
    @Override
    public int size() {
        return items.size();
    }
    @Override
    public Item get(int position) {
        return items.get(position);
    }

    public void sortByPrice() {
        //this.add(0, getLowestPriced());
        // Remove the old ones
        Collections.sort(items);
//        Collections.sort(items, new Comparator<Item>() {
//            @Override
//            public int compare(Item itemA, Item itemB) {
//                return  itemB.getPriceD().compareTo(itemA.getPriceD());
//            }
//        });

        System.out.println(this.toString());
    }

    private Item getLowestPriced() {
        Item i = new Item();
        i.setPrice(Double.MAX_VALUE);
        for (Item item : items) {
            if (item.getPriceD() < i.getPriceD()) {
                i = item;
            }
        }
        return i;
    }

    public void add(Item item) {
        items.add(item);
    }
    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }



}
