package com.xevensolutions.baseapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.xevensolutions.baseapp.MyBaseApp;
import com.xevensolutions.baseapp.R;
import com.xevensolutions.baseapp.interfaces.TextInputListener;

import java.util.Calendar;

import de.mrapp.android.bottomsheet.BottomSheet;

import static com.xevensolutions.baseapp.utils.TextUtils.isStringEmpty;
import static com.xevensolutions.baseapp.utils.ViewUtils.setFieldError;

public class AlertUtils {


    private static void addItemToBottomSheetDialog(BottomSheet.Builder builder, int text,
                                                   int icon) {
        builder.addItem(icon, text, icon);

    }

    public static void showPopupMenu(int menuRes, View anchor, MenuBuilder.Callback callback) {
        Context context = anchor.getContext();

        MenuBuilder menuBuilder = new MenuBuilder(context);
        menuBuilder.setCallback(callback);

        SupportMenuInflater supportMenuInflater = new SupportMenuInflater(context);
        supportMenuInflater.inflate(menuRes, menuBuilder);

        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(context, menuBuilder
                , anchor);
        menuPopupHelper.setForceShowIcon(true);

        menuPopupHelper.show();
    }

    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));

        item.setIcon(wrapDrawable);
    }


    public static void showConfirmationAlert(Context context, String title, String message,
                                             String positiveBtn, String negativeBtn,
                                             String neutralBtn,
                                             DialogInterface.OnClickListener onNegativeBtnClickListener,
                                             DialogInterface.OnClickListener onPositiveBtnClickListener,
                                             DialogInterface.OnClickListener onNeutralBtnClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        String posBtn = positiveBtn != null ? positiveBtn : context.getString(R.string.ok_cap);
        String negBtn = negativeBtn != null ? negativeBtn : "";
        String netBtn = neutralBtn != null ? neutralBtn : "";

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, posBtn, onPositiveBtnClickListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negBtn, onNegativeBtnClickListener);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, netBtn, onNeutralBtnClickListener);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnShowListener(dialogInterface -> {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(GenericUtils.getAttributedColor(context, com.google.android.material.R.attr.colorSecondary));
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(GenericUtils.getAttributedColor(context, com.google.android.material.R.attr.colorSecondary));
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(GenericUtils.getAttributedColor(context, com.google.android.material.R.attr.colorSecondary));
        });
        alertDialog.show();
    }

    public static void showEditTextAlert(Context context, String title, String message, String positiveBtn
            , String negativeBtn, String fieldHint, TextInputListener onPositiveBtnClickListener
    ) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        String posBtn = positiveBtn != null ? positiveBtn : context.getString(R.string.ok_cap);
        String negBtn = negativeBtn != null ? negativeBtn : "";

        EditText editText = new EditText(context);
        editText.setHint(fieldHint);
        alertDialog.setView(editText);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, posBtn, (dialog, which) -> {
            if (isStringEmpty(editText.getText().toString()))
                setFieldError(editText, context.getString(R.string.required_cap));
            else
                onPositiveBtnClickListener.onTextEntered(editText.getText().toString(), null);
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();

    }

    public static void showSuccessErrorAlert(Activity activity, boolean isForSuccess, String message, boolean shouldEndActivity
            , int requestCode, boolean showToast) {

      /*  if (isStringEmpty(message) )
            return;*/
        if (showToast) {
            try {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                if (shouldEndActivity) {
                    activity.finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showConfirmationAlert(activity, isForSuccess ? activity.getString(R.string.success_cap) : activity.getString(R.string.error_cap),
                    message, activity.getString(R.string.ok_cap), null, null, null, (dialog, which) -> {
                        if (shouldEndActivity)
                            activity.finish();
                    }, null);
        }

    }

    public static void showDatePicker(FragmentManager fragmentManager,
                                      DatePickerDialog.OnDateSetListener onDateSetListener) {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                onDateSetListener,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
// If you're calling this from a support Fragment


        /*MaterialDatePicker picker = MaterialDatePicker.Builder.datePicker().build();
        picker.show(fragmentManager, "");
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {

            }
        });*/
        dpd.show(fragmentManager, "Datepickerdialog");
    }


    public static void showTimePicker(boolean isTwentyFourHourFormat, FragmentManager fragmentManager,
                                      TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        Calendar now = Calendar.getInstance();

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener,
                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND), isTwentyFourHourFormat);

        timePickerDialog.setMinTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),
                now.get(Calendar.SECOND));
        timePickerDialog.setMaxTime(now.get(Calendar.HOUR_OF_DAY) + 4, now.get(Calendar.MINUTE),
                now.get(Calendar.SECOND));
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_1);
// If you're calling this from a support Fragment
        timePickerDialog.show(fragmentManager, "TimePickerDialog");
    }


}
