package com.christopheramazurgmail.rtracker;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Bre on 2016-11-29.
 */

public class DictionaryBuilder {
    private String datapath;

    public DictionaryBuilder(Context context) {
        datapath = context.getFilesDir() + "/dictionary/";
        checkDictionaryFile(new File(datapath), context);
    }

    @NonNull
    public ArrayList<Category> buildCategoryList() throws XmlPullParserException, IOException{

        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        xmlPullParserFactory.setNamespaceAware(true);
        XmlPullParser dict = xmlPullParserFactory.newPullParser();
        InputStream input = new FileInputStream(new File(datapath + "dict.xml"));
        Reader reader = new InputStreamReader(input, "UTF-8");
        dict.setInput(reader);

        ArrayList<Category> categoriesList = new ArrayList<Category>();
        dict.next();
        int eventType = dict.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if(eventType == XmlPullParser.START_TAG)
            {
                if(dict.getName().equals("Category")) {

                    Category category = new Category(dict.getAttributeValue(null, "name"));System.out.println(dict.getAttributeValue(null, "name"));

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
            else {
                // TODO The datafile has to be unpacked from Assets and copied into a useful directory
                // TODO but it should be replaced only once, on install.
                datafile.delete();
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
}
