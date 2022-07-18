package com.xevensolutions.baseapp.bottomOptions.UI.adapters;

import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.xevensolutions.baseapp.R;

import butterknife.ButterKnife;

public class EmployeeSelectorViewHolder extends RecyclerView.ViewHolder {

    ImageView ivProfile;
    TextView tvName;
    MaterialCardView mainCardView;
    FrameLayout contCheck;
    CheckBox checkBox;
    View view;

    public EmployeeSelectorViewHolder(@NonNull View itemView) {
        super(itemView);

        ivProfile = itemView.findViewById(R.id.iv_profile);
        tvName = itemView.findViewById(R.id.tv_name);
        mainCardView = itemView.findViewById(R.id.main_card_view);
        contCheck = itemView.findViewById(R.id.cont_check);
        checkBox = itemView.findViewById(R.id.checkBox);
        view = itemView.findViewById(R.id.view4);

        ButterKnife.bind(this, itemView);
    }


}
