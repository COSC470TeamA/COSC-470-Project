package com.ateam.rtracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by haunter on 27/10/16.
 *
 * Handles building collections of Items.
 */
public class ItemGroup extends ArrayList implements Comparable {

    ArrayList<Item> items = new ArrayList<>();

    public ItemGroup() {
    }
    public ItemGroup(ArrayList<Item> item) {
        this.items = item;
    }


    public List<Item> getItems() {
        return items;
    }
    public List<String> getItemNames() {
        List<String> l = new ArrayList<String>();
        for (Item i : items) {
            l.add(i.getDesc());
        }
        return l;
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
    @Override
    public boolean isEmpty() { return items.size() == 0; }

    public void sortByPrice() {
        Collections.sort(items);
    }

    public Double getTotalPrice() {
        double runningTotal = 0;
        for (Item i : this.getItems()) {
            runningTotal += i.getPriceD();
        }
        return runningTotal;
    }

    public void add(Item item) {
        items.add(item);
    }
    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }


    @Override
    public int compareTo(Object o) {
        ItemGroup i = (ItemGroup) o;
        double r = this.getTotalPrice() - i.getTotalPrice();
        return r < 0 ? -1 : 1;
    }
}
