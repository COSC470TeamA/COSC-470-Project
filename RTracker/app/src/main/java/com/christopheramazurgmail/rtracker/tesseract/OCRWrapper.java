package com.christopheramazurgmail.rtracker.tesseract;

/**
 * Created by Chris Mazur on 10/27/2016.
 * Overview: One Public Method - a constructor that accepts a Context
 *           One Public Function - returns the OCR string.
 *           TODO: determine if it's necessary to break up processing and string return
 *
 *
*/


import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.christopheramazurgmail.rtracker.R;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;




public class OCRWrapper {



    private Bitmap image;
    private TessBaseAPI OCR;
    private String datapath = "";

    //constructor - accepts context & 3-letter language ID (e.g. eng, rus, chi)
    public OCRWrapper(Context context, String language) {
        image           = BitmapFactory.decodeResource(context.getResources(), R.drawable.testimage);
        OCR             = new TessBaseAPI();
        datapath        = context.getFilesDir() + "/tesseract/";

        File f = new File(datapath);
        if(!f.exists() && !f.isDirectory()) {
        // Then make it!
            f.mkdirs();
        }

        OCR.init(datapath, language);
        checkLanguageDataFile(new File(datapath + "tessdata/"), context);

}

    //Give the image to OCR and return the results!
    //TODO: Check if multiple calls to processImage result in conflicts
    public String processImage(){
        String OCRResult;
        OCR.setImage(image);
        OCRResult = OCR.getUTF8Text();
        return OCRResult;
    }


    //TODO: private void checkImageDataFile
    //TODO: private void copyImageDataFile

    //Check if language data exists in App memory. If not, copy it from App Assets.
    private void checkLanguageDataFile(File dir, Context context) {
        if (!dir.exists() && dir.mkdirs()) {
            copyLanguageDataFiles(context);
        }
        if (dir.exists()) {
            String datafilepath = datapath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyLanguageDataFiles(context);
            }
        }
    }

    /*
        Passes app context to copy language data into the App's local files.
        Necessary because Android Apps require everything to be local.
    */
    private void copyLanguageDataFiles(Context context) {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = context.getAssets();

            InputStream instream = assetManager.open("tessdata/eng.traineddata");
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

    //  Cleanup helper
    public void onDestroy() {

        if (OCR != null) OCR.end();
    }


}
