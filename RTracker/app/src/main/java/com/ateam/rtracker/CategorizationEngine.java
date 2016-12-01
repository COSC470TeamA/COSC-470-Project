package com.ateam.rtracker;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brewi on 2016-10-31.
 */

public class CategorizationEngine implements Serializable{
    private ArrayList<Category> categories;
    private LinkedList<Item> uncategorizedItems = new LinkedList<Item>();
    private transient MySQLiteHelper db;

    public CategorizationEngine(Context context) {
        this.db = new MySQLiteHelper(context);
        this.categories =  db.getAllCategories();
    }

    public Receipt categorizeReceipt(Receipt receipt) {
        this.categories =  db.getAllCategories();

        if (receipt.items != null) {
            List<Item> itemsList = receipt.items.getItems();
            if (itemsList != null)
                //for each item in receipt check if it exists in a category
                for (Item item : itemsList) {
                    for (Category category : categories) {
                        //go through each item in category to see if it matches current receipt item
                        for (String catItem : db.getCatItemPairsLibrary(category.getName())) {
                            //if category item matches receipt item set item category
                            if (catItem.equals(item.getDesc())) {
                                item.setCat(category.getName());
                            }
                        }
                    }
                }
        }

        return receipt;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void addToDictionary(Item first, String categoryName) {
        for (Category category : this.categories) {
            if(category.getName().equals(categoryName)){
                //if pair does not exist add
                if(!pairExists(category.getItemList(), first.getDesc())){
                    //TODO search all other pairings for this item and delete before adding new
                    category.addItemToList(first.getDesc());
                }
            }
        }
    }

    private boolean pairExists(LinkedList<Category.Item> itemList, String desc) {
        for (Category.Item item : itemList) {
            if (item.getName().equals(desc)) {
                return true;
            }
        }
        return false;
    }
}
