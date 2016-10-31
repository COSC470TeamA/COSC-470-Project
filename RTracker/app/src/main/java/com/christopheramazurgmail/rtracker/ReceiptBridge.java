package com.christopheramazurgmail.rtracker;

/**
 * Created by haunter on 27/10/16.
 */
public class ReceiptBridge {

    /**
     * Uses the output of the OCR to build a Receipt.
     *
     * @param longString
     * @return
     */
    public Receipt makeReceipt(String longString) {

        String[] lines = longString.split("\n");
        // Assuming the store name is at the top
        Receipt rec = new Receipt(lines[0]);

        // Assuming the last line is reserved for the total
        for (int i = 1; i < lines.length - 1; i++) {
            // One line at a time
            // Grab all the words
            String[] lineWords = lines[i].split(" ");
            // Assuming all words make up the item name, and the last word is the price
            // Build the new item
            Item thisLineItem = new Item();
            thisLineItem.setDesc(lineWords[0]);
            thisLineItem.setPrice(toPrice(lineWords[1]));

            rec.add(thisLineItem);
        }
        return rec;
    }

    /**
     * The OCR can return malformed numbers and decimals.
     * Fix the price formatting.
     * @param string
     * @return
     */
    public double toPrice(String string) {
        try {
            double p = Double.parseDouble(string);
            return p;
        }
        catch (NumberFormatException nfe) {
            // Sometimes decimals will come through as commas
            string = string.replace(",", ".'");
            // Kill the rest of the non digit characters
            string = string.replaceAll("[^\\d.]", "");
            return Double.parseDouble(string);
        }
    }
}
