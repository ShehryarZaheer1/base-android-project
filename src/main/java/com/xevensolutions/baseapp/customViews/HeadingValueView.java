package com.xevensolutions.baseapp.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xevensolutions.baseapp.R;
import com.xevensolutions.baseapp.utils.GenericUtils;


public class HeadingValueView extends LinearLayout {

    OnClickListener onClickListener;
    private TextView tvTitle;
    private TextView tvDescription;

    public HeadingValueView(final Context context) {
        this(context, null);
    }

    @Override
    public void setOnClickListener(final OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public HeadingValueView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        settUpView(context, attrs);
    }

    public void settUpView(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.HeadingValueView);
        int orientation = attributes.getInt(R.styleable.HeadingValueView_layoutOrientation, 1);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout materialCardView = (LinearLayout) layoutInflater.inflate(orientation == 1 ? R.layout.layout_heading_value
                        : R.layout.layout_heading_value_horizontal
                , this, false);
        materialCardView.setOrientation(orientation);
        tvTitle = materialCardView.findViewById(R.id.tvHeading);
        tvDescription = materialCardView.findViewById(R.id.tvValue);

        setOrientation(VERTICAL);
        boolean isSmall = attributes.getBoolean(R.styleable.HeadingValueView_isSmall, false);
        boolean isOnPrimary = attributes.getBoolean(R.styleable.HeadingValueView_isOnPrimary, false);
        tvTitle.setText(attributes.getString(R.styleable.HeadingValueView_heading));
        tvDescription.setText(attributes.getString(R.styleable.HeadingValueView_value));
        if (isSmall)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvTitle.setTextAppearance(R.style.TextAppearance_MdcTypographyStyles_Body2_Poppins_Small_ColorPrimary);
            }

        if (isOnPrimary) {
            tvTitle.setTextColor(GenericUtils.getAttributedColor(context, com.google.android.material.R.attr.colorOnPrimary));
            tvDescription.setTextColor(GenericUtils.getAttributedColor(context, com.google.android.material.R.attr.colorOnPrimary));
        }

        attributes.recycle();
        addView(materialCardView);
    }

    public void setValue(String value) {
        tvDescription.setText(value);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }
}
