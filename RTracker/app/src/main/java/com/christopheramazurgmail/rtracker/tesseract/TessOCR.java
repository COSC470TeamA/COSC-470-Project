package com.christopheramazurgmail.rtracker.tesseract;

/**
 * Created by Chris Mazur on 10/27/2016.



import android.content.Context;
import android.graphics.Bitmap;


import com.googlecode.tesseract.android.TessBaseAPI;

public class TessOCR {
    private final TessBaseAPI mTess;

    public TessOCR(Context context, String language) {
        mTess = new TessBaseAPI();
        String datapath = context.getFilesDir() + "/tesseract/";
        mTess.init(datapath, language);
    }

    public String getOCRResult(Bitmap bitmap) {
        mTess.setImage(bitmap); return mTess.getUTF8Text();
    }

    public void onDestroy() {
        if (mTess != null) mTess.end();
    }


}

 */