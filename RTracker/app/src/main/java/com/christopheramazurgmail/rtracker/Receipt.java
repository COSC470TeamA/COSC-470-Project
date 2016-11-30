package com.christopheramazurgmail.rtracker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by haunter on 27/10/16.
 */
public class Receipt implements Serializable, Comparable {
    String store;
    public ItemGroup items;
    Date dateCreated;
    String id;

    public Receipt() {
        items = new ItemGroup();
    }

    public Receipt(String store) {
        this.store = store;
        items = new ItemGroup();
    }
    public Receipt(String store, String... things) {
        this.store = store;
        ItemBuilder ib = new ItemBuilder();

        this.items = ib.build(things);
    }

    public void add(Item item) {
        items.add(item);
    }

    public String toString() {
        String out;
        out = store + "\n";
        if (items != null) {
            List<Item> itemsList = items.getItems();
            if (itemsList != null)
            for (Item item : itemsList) {
                out += item.getDesc() + " ";
                out += item.getPrice() + " ";
                if (item.getCat() != null) {
                    out += "- " + item.getCat() + " ";
                }
                out += "\n";
            }
        }
        return out;
    }

    public String getStore() {
        return store;
    }
    public ItemGroup getItems() {
        return items;
    }
    public ArrayList<Item> getItemList() {
        return items.items;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void sortByDate(List<Receipt> receipts) {
        Collections.sort(receipts);
    }

    @Override
    public int compareTo(Object o) {
        Receipt r = (Receipt) o;
        return this.getDateCreated().compareTo(r.getDateCreated());
    }
}
