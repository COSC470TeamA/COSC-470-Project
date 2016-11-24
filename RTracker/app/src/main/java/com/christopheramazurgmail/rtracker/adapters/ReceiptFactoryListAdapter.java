package com.christopheramazurgmail.rtracker.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.christopheramazurgmail.rtracker.Category;
import com.christopheramazurgmail.rtracker.Item;
import com.christopheramazurgmail.rtracker.R;
import com.christopheramazurgmail.rtracker.ReceiptFactory;

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

        holder.itemDesc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Edit Field");
                // Get the layout inflater
                LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                View view = inflater.inflate(R.layout.edit_text_dialog, null);
                builder.setView(view);
                //Display text of calling text view in edit field
                final TextView textToEdit = (TextView) v;
                final EditText editText = (EditText) view.findViewById(R.id.text_to_edit);
                editText.setText(textToEdit.getText());
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getItem(item).setDesc(editText.getText().toString());
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                //Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });

        if(getItem(i).getPrice() != null) {
            holder.itemPrice.setText("$" + getItem(i).getPrice());
        }

        holder.itemPrice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Edit Field");
                // Get the layout inflater
                LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                View view = inflater.inflate(R.layout.edit_text_dialog, null);
                builder.setView(view);
                //Display text of calling text view in edit field
                final TextView textToEdit = (TextView) v;
                final EditText editText = (EditText) view.findViewById(R.id.text_to_edit);
                editText.setText(textToEdit.getText());
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            String editedPrice = editText.getText().toString().replace("$", "");
                            double price = Double.parseDouble(editedPrice);
                            getItem(item).setPrice(price);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        } catch (NumberFormatException nfe) {
                            Toast toast = Toast.makeText(getContext(), "Not a Valid Price", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                //Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });

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

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}