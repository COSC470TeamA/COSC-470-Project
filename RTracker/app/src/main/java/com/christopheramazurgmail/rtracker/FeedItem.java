package com.christopheramazurgmail.rtracker;

import android.media.Image;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Bre on 2016-11-28.
 */

public class FeedItem {
    int imageID;
    boolean hasImage;
    String title;
    String content;

    public FeedItem(int imageID, String title, String content){
        this.imageID = imageID;
        this.hasImage = true;
        this.title = title;
        this.content = content;
    }

    public FeedItem(String title, String content){
        this.title = title;
        this.content = content;
        this.hasImage = false;
    }
}
