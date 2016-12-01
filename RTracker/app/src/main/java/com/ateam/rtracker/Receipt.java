package com.ateam.rtracker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by haunter on 27/10/16.
 *
 * A store receipt.
 *
 * Receipts come from a single store and contain one or many items.
 * The date of a receipt is the datetime when it was enetered into the program.
 */
public class Receipt implements Serializable, Comparable {

    /**
     * The name of the store that produced the receipt.
     * All items in a receipt are from this store.
     */
    private String store;

    // @TODO Change scope to private. Seek and destroy all Receipt.items usage.
    /** The collection of items in a receipt. */
    public ItemGroup items;

    /** The date the receipt was entered into the program. */
    Date dateCreated;

    /** The unique id that is returned from the database. */
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

    /**
     * Add a singular item to a receipt.
     * @param item The item to add.
     */
    public void add(Item item) {
        items.add(item);
    }

    /**
     * The textual representation of a receipt.
     * @return  The text.
     */
    @Override
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
