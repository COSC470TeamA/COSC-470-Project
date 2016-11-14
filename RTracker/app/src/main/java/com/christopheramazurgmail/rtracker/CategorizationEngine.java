package com.christopheramazurgmail.rtracker;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brewi on 2016-10-31.
 */

public class CategorizationEngine implements Serializable{
    private transient Context context;
    private LinkedList<Category> categories;
    private LinkedList<Item> uncategorizedItems = new LinkedList<Item>();
    private String datapath;

    public CategorizationEngine(Context context) {
        this.context = context;

        datapath = context.getFilesDir() + "/dictionary/";
        checkDictionaryFile(new File(datapath), context);

        try {
            this.categories =  buildCategoryList();
        } catch (XmlPullParserException e){

        } catch (IOException e) {

        }
    }

    public Receipt categorizeReceipt(Receipt receipt) {
        if (receipt.items != null) {
            List<Item> itemsList = receipt.items.getItems();
            if (itemsList != null)
                //for each item in receipt check if it exists in a category
                for (Item item : itemsList) {
                    Boolean foundCategory = false;
                    for (Category category : categories) {
                        if (foundCategory) break;
                        //go through each item in category to see if it matches current receipt item
                        for (Category.Item catItem : category.getItemList()) {
                            if (foundCategory) break;
                            //if category item matches receipt item set item category
                            if (catItem.getName().equals(item.getDesc())) {
                                item.setCat(category.getName());
                                foundCategory = true;
                            }
                        }
                    }
                    if (!foundCategory) {
                        uncategorizedItems.add(item);
                    }
                }
        }

        return receipt;
    }


    @NonNull
    private LinkedList<Category> buildCategoryList() throws XmlPullParserException, IOException{
        //XmlResourceParser dict = context.getAssets().open("/dictionary/dict.xml");

        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        xmlPullParserFactory.setNamespaceAware(true);
        XmlPullParser dict = xmlPullParserFactory.newPullParser();
        InputStream input = new FileInputStream(new File(datapath + "dict.xml"));
        Reader reader = new InputStreamReader(input, "UTF-8");
        dict.setInput(reader);

        //StringBuffer stringBuffer = new StringBuffer();
        LinkedList<Category> categoriesList = new LinkedList<Category>();
        dict.next();
        int eventType = dict.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG)
            {
                if(dict.getName().equals("Category")) {

                    Category category = new Category(dict.getAttributeValue(null, "name"));

                    //go through each of sub elements until Category end tag
                    Boolean foundEndTag = false;
                    while (!foundEndTag) {
                        if (eventType == XmlPullParser.END_TAG && dict.getName().equals("Category")){
                            foundEndTag = true;
                        }

                        //add all items to list
                        if (eventType == XmlPullParser.START_TAG && dict.getName().equals("Item")){
                            category.addItemToList(dict.getAttributeValue(null, "name"));
                        }

                        eventType = dict.next();
                    }

                    categoriesList.add(category);
                }
            }
            eventType = dict.next();
        }
        return categoriesList;
    }

    public LinkedList<Item> getUncategorizedItems() {
        return uncategorizedItems;
    }
    public LinkedList<Category> getCategories() {
        return categories;
    }

    private void checkDictionaryFile(File dir, Context context) {
        if (!dir.exists() && dir.mkdirs()) {
            copyDictionaryFile(context);
        }
        if (dir.exists()) {
            String datafilepath = datapath + "dict.xml";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyDictionaryFile(context);
            }
        }
    }

    private void copyDictionaryFile(Context context) {
        try {
            String filepath = datapath + "dict.xml";
            AssetManager assetManager = context.getAssets();

            InputStream instream = assetManager.open("dictionary/dict.xml");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }

            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
