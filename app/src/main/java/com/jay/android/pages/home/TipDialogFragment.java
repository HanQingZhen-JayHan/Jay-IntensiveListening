package com.jay.android.pages.home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;

import com.jay.android.R;

public class TipDialogFragment extends DialogFragment {
    public static final String TAG = TipDialogFragment.class.getSimpleName();
    private DialogListener listener;
    public static final String DIALOG_TITLE = "dialog_title";
    public static final String DIALOG_BTN_CONFIRM = "dialog_btn_confirm";
    public static final String DIALOG_BTN_CANCEL = "dialog_btn_cancel";

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    public interface DialogListener {
        void confirm();

        void cancel();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        String title = getArguments().getString(DIALOG_TITLE);
        String confirm = getArguments().getString(DIALOG_BTN_CONFIRM);
        String cancel = getArguments().getString(DIALOG_BTN_CANCEL);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(title)
                .setCancelable(true)
                .setPositiveButton(confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.confirm();
                        }
                    }
                })
                .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        if (listener != null) {
                            listener.cancel();
                        }
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
    }
}
