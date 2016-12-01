package com.ateam.rtracker;

import java.io.Serializable;

/**
 * Created by haunter on 26/10/16.
 */
public class Item implements Serializable, Comparable {
    private String desc;
    private double price;
    private String cat;

    public Item() {}
    public Item(String desc, double price) {
        this.desc = desc;
        this.price = price;
        this.cat = null;
    }
    public Item(String desc) {
        this.desc = desc;
        this.price = 0;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return String.format("%.2f", price);
    }
    public double getPriceD() { return price; }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Item{" +
                "desc='" + desc + '\'' +
                ", price=" + price +
                ", car=" + cat +
                '}';
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        if (cat == null) {
            this.cat = null;
        } else {
            this.cat = cat;
        }
    }

    /**
     * Determines if an Item is greater than or less than
     * another Item.
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        Item i = (Item) o;
        double r = this.getPriceD() - i.getPriceD();
        return r > 0 ? -1 : 1;
    }
}