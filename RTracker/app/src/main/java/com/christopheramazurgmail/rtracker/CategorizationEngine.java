package com.christopheramazurgmail.rtracker;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brewi on 2016-10-31.
 */

public class CategorizationEngine {
    private Context context;
    private LinkedList<Category> categories;
    public CategorizationEngine(Context context) {
        this.context = context;

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
                    for (Category category : categories) {
                        //go through each item in category to see if it matches current receipt item
                        for (Category.Item catItem : category.getItemList()) {
                            //if category item matches receipt item set item category
                            if (catItem.getName().equals(item.getDesc())) {
                                item.setCat(category.getName());
                            }
                        }
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
        InputStream input = context.getAssets().open("dictionary/dict.xml");
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
}
