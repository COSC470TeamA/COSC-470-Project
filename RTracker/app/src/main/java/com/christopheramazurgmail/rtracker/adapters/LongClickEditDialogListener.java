package com.christopheramazurgmail.rtracker.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.christopheramazurgmail.rtracker.R;

/**
 * Created by Bre on 2016-11-23.
 */
public class LongClickEditDialogListener implements View.OnLongClickListener {
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
                textToEdit.setText(editText.getText());
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
}
