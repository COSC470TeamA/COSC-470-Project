package com.christopheramazurgmail.rtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by brewi on 2016-11-10.
 */

public class ReceiptFactoryListAdapter extends ArrayAdapter<Item> {
    private LinkedList<Category> categories;

    public ReceiptFactoryListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ReceiptFactoryListAdapter(Context context, int resource, List<Item> items, LinkedList<Category> categories) {
        super(context, resource, items);
        this.categories = categories;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int item = i;
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

        String[] arraySpinner = ReceiptFactory.populateSpinner(categories, "None");

        if (getItem(i).getDesc() != null) {
            holder.itemDesc.setText(getItem(i).getDesc());
        }

        if(getItem(i).getPrice() != null) {
            holder.itemPrice.setText("$" + getItem(i).getPrice());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arraySpinner);
        holder.itemSpinner.setAdapter(adapter);

        int index = setSelectedCategory(getItem(i).getCat(), arraySpinner);

        holder.itemSpinner.setSelection(index);

        holder.itemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getItem(item).setCat(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private int setSelectedCategory(String cat, String[] arraySpinner) {
        //set default selected category of none
        if (cat == null) {
            return  0;
        }

        int selectedIndex = 0;

        int index = 0;
        for (String category : arraySpinner) {
            if (cat.equals(category)) {
                selectedIndex = index;
            }
            index++;
        }

        return  selectedIndex;
    }

    static class ViewHolder {
        TextView itemDesc;
        TextView itemPrice;
        Spinner itemSpinner;
    }
}