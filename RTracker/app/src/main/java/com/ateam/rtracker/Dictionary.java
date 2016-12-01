package com.ateam.rtracker;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * Reads nodes in dict.xml dictionary
 * Created by steve on 2016-11-13.
 */

// TODO Migrate dictionary logic from Categorization Engine to here for fair API use for all classes
public class Dictionary {

    Resources resources;
    XmlResourceParser xmlParser;
    int dictXmlFile = R.xml.dict;
    TreeMap<String, List<String>> categories = new TreeMap<>();

    public Dictionary(Context context) {

            resources = context.getResources();

            populateCategories();

    }

    public void populateCategories() {
        try {
            openXmlParser();

            int eventType = 0;
            eventType = xmlParser.getEventType();
            String currCategoryName = "";
            String currItemName = "";

            // Go through all the nodes
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // Look in the start tags for the attribute "Category"
                if (eventType == XmlPullParser.START_TAG) {

                    if (xmlParser.getName().equals("Category")) {
                        currCategoryName = xmlParser.getAttributeValue(null, "name");
                        categories.put(currCategoryName, new ArrayList<String>());
                    }
                    if (xmlParser.getName().equals("Item")) {
                        currItemName = xmlParser.getAttributeValue(null, "name");
                        addToItemsList(currCategoryName, currItemName);
                    }
                }

                eventType = xmlParser.next();

            }
            closeXmlParser();
        } catch (XmlPullParserException | IOException e) {
            closeXmlParser();
            e.printStackTrace();
        }
    }
    public boolean isCategory(String catName) {
        return categories.containsKey(catName);
    }
    public boolean isItem(String itemName) {
        return categories.containsValue(itemName);
    }
    public Set<String> getCategoryNames() {
        return categories.keySet();
    }
    public List<String> getItemNames(String catName) {
        return categories.get(catName);
    }
    public void addToItemsList(String catName, String itemName) {
        categories.get(catName).add(itemName);
    }
    public Iterator<String> getCategoryIterator() {
        return categories.keySet().iterator();
    }
    public Iterator<String> getItemsIterator(String catName) {
        return categories.get(catName).iterator();
    }

    public void openXmlParser() {
        try {
            xmlParser = resources.getXml(dictXmlFile);

            xmlParser.next();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeXmlParser() {
        xmlParser.close();
    }


}
