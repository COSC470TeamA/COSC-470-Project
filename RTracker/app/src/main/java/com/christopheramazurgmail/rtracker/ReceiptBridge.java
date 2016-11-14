package com.christopheramazurgmail.rtracker;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by haunter on 27/10/16.
 */
public class ReceiptBridge {

    List<String> nonItemDescriptors = new ArrayList();

    public ReceiptBridge() {
        nonItemDescriptors = Arrays.asList("GST","PST","TOTAL","SUB","LQT","CHANGE","CASH","BILL","OPERATOR","M/C","REF");
    }
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

            if (lineWords.length < 2) {
                continue;
            }
            // If the line has words with a price, but it not an item, get rid of it
            if (isNonItemWithPrice(lineWords)) {
                continue;
            }

            // If the line's last or second last word is a price, add it to items
            if (isItem(lineWords)) {
                // Assemble the words into an item and price and add it to the receipt
                Item thisLineItem = new Item();
                // Set the description to be all the words before the price
                String description = parseItemDescription(lineWords);

                thisLineItem.setDesc(description);

                // Set the price to be the last word in the line
                thisLineItem.setPrice(toPrice(lineWords[lineWords.length - 1]));

                // Add it to the receipt object
                rec.add(thisLineItem);
            }
            // Assuming all words make up the item name, and the last word is the price
            // Build the new item
            if (!lineWords[0].equals("") && lineWords.length > 1) {

            }
        }
        return rec;
    }

    /**
     * Determines if a line is an item
     */
    public boolean isItem(String[] words) {
        // An item is any number of words followed by a price.
        if (words.length < 2) {
            return false;
        }
        if (isPrice(words[words.length - 1]) || isPrice(words[words.length - 2])) {
            return true;
        }
        return false;

    }

    /**
     * Determines if a word is a price.
     * @param string
     * @return
     */
    public boolean isPrice(String string) {

        double p;
        try {
            p = Double.parseDouble(string);
        }
        catch (NumberFormatException e) {
            return false;
        }

        return true;
    }


    /**
     * Looks for the description of an item in a line that has been identified as an item.
     * @param line
     * @return
     */
    public String parseItemDescription(String[] line) {
        String description = "";

        // If there are no letters in the first word, skip it
        // Often the first word is a quantity (1) or a code (123-123-132)
        if (line[0].matches("[a-zA-Z]")) {
            description = line[0] + " ";
        }

        for (int j = 1; j < line.length - 1; j++ ) {
            // Sometimes "$" is its own word. Skip it.
            if (!line[j].equals("$")) {
                description += line[j] + " ";
            }
        }
        if (description.endsWith(" ")) {
            // Remove any trailing space.
            description = description.trim();
        }

        return description;
    }
    /**
     * Determines if a line has an 'item like' structure but does not qualify as an item.
     * eg., taxes, subtotals, totals, charges
     * @param line
     * @return
     */
    public boolean isNonItemWithPrice(String[] line) {
        for (String desc : nonItemDescriptors) {
            for (int i = 0; i < line.length; i++) {
                if (line[i].toUpperCase().contains(desc)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * The OCR can return malformed numbers and decimals.
     * Fix the price formatting.
     * @param string
     * @return
     */
    public double toPrice(String string) {
        double p = 0;
        try {

            if (string.equals("")) {
                return 888.88;
            }
            // If there is more than one decimal
            if (string.indexOf(".") != string.lastIndexOf(".")) {
                return 999.99;
            }

            // Sometimes decimals will come through as commas
            string = string.replace(",", ".'");
            // Kill the rest of the non digit characters
            string = string.replaceAll("[^\\d.]", "");

            p = Double.parseDouble(string);

        }
        catch (NumberFormatException nfe) {

            nfe.printStackTrace();
        }
        return p;
    }
}
