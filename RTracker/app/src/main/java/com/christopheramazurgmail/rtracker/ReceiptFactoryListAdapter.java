package com.christopheramazurgmail.rtracker;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by brewi on 2016-11-10.
 */

public class ReceiptFactoryListAdapter extends ArrayAdapter<Item> {
    private LinkedList<Category> categories;
    private LayoutInflater inflater;

    public ReceiptFactoryListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ReceiptFactoryListAdapter(Context context, int resource, List<Item> items, LinkedList<Category> categories) {
        super(context, resource, items);
        this.categories = categories;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.content_item_receipt_factory, null);
            holder = new ViewHolder();
            holder.itemDesc = (TextView) view.findViewById(R.id.item_desc);
            holder.itemPrice = (TextView) view.findViewById(R.id.item_price);
            holder.itemSpinner = (Spinner) view.findViewById(R.id.item_spinner);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.itemDesc.setText(getItem(i).getDesc());
        holder.itemPrice.setText("$" + getItem(i).getPrice());

        //create array with one extra element to provide default none option for category
        String[] arraySpinner = new String[categories.size() + 1];

        arraySpinner[0] = "None";
        //set default selected category of none
        int categoryIndex = 0;

        int index = 1;
        for (Category category : categories) {
            arraySpinner[index] = category.getName();
            if (category.getName().equals(getItem(i).getCat())){
                //if category matches item category set
                categoryIndex = index;
            }
            index++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arraySpinner);
        holder.itemSpinner.setAdapter(adapter);
        holder.itemSpinner.setSelection(categoryIndex);

        return view;
    }

    static class ViewHolder {
        TextView itemDesc;
        TextView itemPrice;
        Spinner itemSpinner;
    }
}
