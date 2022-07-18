package com.xevensolutions.baseapp.bottomOptions.UI.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.xevensolutions.baseapp.R;
import com.xevensolutions.baseapp.bottomOptions.UI.adapters.AdapterListItemEmployeeSelector;
import com.xevensolutions.baseapp.bottomOptions.interfaces.EmployeeSelectorListener;
import com.xevensolutions.baseapp.bottomOptions.models.ItemSelection;
import com.xevensolutions.baseapp.interfaces.BaseFragmentListener;

import java.util.ArrayList;

public class FragmentItemSelector extends BaseBottomSheetDialog {

    RecyclerView rvUsers;
    TextView tvTitle;
    Button btnSelect;
    Button btnAddNew;
    EditText etSearch;
    LinearLayout contSearchBar;
    LinearLayout contCancel;
    LinearLayout mainLayout;


    ArrayList<? extends ItemSelection> employees;
    private static final String KEY_EMPLOYEES = "EMPLOYEES";
    private static final String KEY_REQUESTING_VIEW_ID = "REQUESTING_VIEW_ID";
    private static final String KEY_TITLE = "TITLE";
    private static final String KEY_IS_MULTISELECTION = "IS_MULTISELECTION";
    private static final String KEY_IS_ADD_NEW_ENABLED = "IS_ADD_NEW_ENABLED";


    EmployeeSelectorListener employeeSelectorListener;
    private AdapterListItemEmployeeSelector adapterEmployees;
    private int requestingViewId;
    private String title;
    private boolean isMultiSelection;
    private boolean isAddNewEnabled;
    private boolean isPeekHeight;
    private BottomSheetBehavior<FrameLayout> behaviour;


    public static FragmentItemSelector newInstance(String title, int requestingViewId,
                                                   ArrayList<? extends ItemSelection> employees,
                                                   boolean isMultiSelection, boolean isAddNewEnabled) {

        Bundle args = new Bundle();
        args.putSerializable(KEY_EMPLOYEES, employees);
        args.putInt(KEY_REQUESTING_VIEW_ID, requestingViewId);
        args.putString(KEY_TITLE, title);
        args.putBoolean(KEY_IS_MULTISELECTION, isMultiSelection);
        args.putBoolean(KEY_IS_ADD_NEW_ENABLED, isAddNewEnabled);
        FragmentItemSelector fragment = new FragmentItemSelector();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateView(View view) {
        super.onCreateView(view);

        //Init views...
        rvUsers = view.findViewById(R.id.rv_users);
        tvTitle = view.findViewById(R.id.tv_selection_title);
        btnSelect = view.findViewById(R.id.btn_select);
        btnAddNew = view.findViewById(R.id.btn_add_new);
        etSearch = view.findViewById(R.id.et_search_bar);
        contSearchBar = view.findViewById(R.id.cont_search_bar);
        contCancel = view.findViewById(R.id.cont_cancel);
        mainLayout = view.findViewById(R.id.main_layout);

        tvTitle.setText(title);
        if (TextUtils.isEmpty(title))
            tvTitle.setVisibility(View.GONE);
        btnSelect.setVisibility(isMultiSelection ? View.VISIBLE : View.GONE);
        btnAddNew.setVisibility(isAddNewEnabled ? View.VISIBLE : View.GONE);
        initAdapter();

    }

    @Override
    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, "");
    }

    @Override
    public void receiveExtras(Bundle arguments) {
        employees = (ArrayList<? extends ItemSelection>) arguments.getSerializable(KEY_EMPLOYEES);
        requestingViewId = arguments.getInt(KEY_REQUESTING_VIEW_ID);
        title = arguments.getString(KEY_TITLE);
        isMultiSelection = arguments.getBoolean(KEY_IS_MULTISELECTION);
        isAddNewEnabled = arguments.getBoolean(KEY_IS_ADD_NEW_ENABLED);
    }

    @Override
    public boolean addToBackStack() {
        return false;
    }

    @Override
    public int getFragmentName() {
        return 0;
    }


    @Override
    public int getLayoutResource() {
        return R.layout.fragment_user_selector;
    }

    @Override
    public boolean isChildFragment() {
        return false;
    }

    @Override
    public void setBaseFragmentListener(BaseFragmentListener baseFragmentListener) {
        employeeSelectorListener = (EmployeeSelectorListener) baseFragmentListener;
    }

    public void setEmployeeSelectorListener(final EmployeeSelectorListener employeeSelectorListener) {
        this.employeeSelectorListener = employeeSelectorListener;
    }

    @Override
    protected void setListeners() {
        super.setListeners();

        rvUsers.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                v.onTouchEvent(event);
                return true;
            }
        });

        contCancel.setOnClickListener(v -> {
            this.dismiss();
        });

        btnAddNew.setOnClickListener(v -> {
            employeeSelectorListener.onAddNewTapped(requestingViewId);
            this.dismiss();
        });


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (behaviour != null && behaviour.isFitToContents())
                    behaviour.setFitToContents(false);
                adapterEmployees.filterItems(s.toString());

            }
        });

        btnSelect.setOnClickListener(v -> {
            employeeSelectorListener.onMultipleItemsSelected(requestingViewId, adapterEmployees.getSelectedItems());
            this.dismiss();
        });
    }

    private void initAdapter() {
        adapterEmployees = new AdapterListItemEmployeeSelector(getActivity(),
                employees, new EmployeeSelectorListener() {
            @Override
            public void onEmployeeSelected(int requstingViewId, ItemSelection employee) {
                FragmentItemSelector.this.dismiss();
                employeeSelectorListener.onEmployeeSelected(requestingViewId, employee);
            }

            @Override
            public void onMultipleItemsSelected(final int requstingViewId, final ArrayList<? extends ItemSelection> selectedItems) {
                employeeSelectorListener.onMultipleItemsSelected(requstingViewId, selectedItems);
            }

        }, isMultiSelection);
        rvUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvUsers.setAdapter(adapterEmployees);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);

                behaviour = BottomSheetBehavior.from(bottomSheet);
                //behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                behaviour.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {

                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                        Log.i("Bottom Sheet", "onSlide: " + slideOffset);
                        if (slideOffset >= 0) {
                            LinearLayout.LayoutParams cancellayoutParams = new LinearLayout.LayoutParams(
                                    (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                            32, getResources().getDisplayMetrics()) * slideOffset), contCancel.getHeight());
                            contCancel.setLayoutParams(cancellayoutParams);

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                            44, getResources().getDisplayMetrics()) * slideOffset));
                            int margin = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                    12 * slideOffset, getResources().getDisplayMetrics()) * slideOffset);
                            layoutParams.leftMargin = margin;
                            layoutParams.rightMargin = margin;
                            layoutParams.bottomMargin = margin;
                            contSearchBar.setLayoutParams(layoutParams);


                            /*if (GenericUtils.isStringEmpty(etSearch.getText().toString())) {
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(contSearchBar.getWidth(),
                                        (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                44, getResources().getDisplayMetrics()) * slideOffset));
                                layoutParams.topMargin = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                        12, getResources().getDisplayMetrics()));
                                contSearchBar.setLayoutParams(layoutParams);
                            }*/
                        } else {

                        }

                        if (slideOffset == 1) {

                            behaviour.setFitToContents(false);

                            behaviour.setDraggable(false);
                            //behaviour.setDraggable(false);
                            //behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);

                            // behaviour.setPeekHeight(400);



                            /*rvUsers.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    new ScreenUtils(getActivity()).getHeight()));*/
                            //  GenericUtils.startVisibilityAnimation(mainLayout);
                            isPeekHeight = true;
                        }
                    }
                });

            }
        });
        return d;
    }

    @Override
    public void exitActivity() {

    }

    @Override
    public void showToast(String error) {

    }
}
