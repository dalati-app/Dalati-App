package com.dalati.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalati.R;
import com.dalati.ui.activities.SearchActivity;
import com.dalati.ui.adapters.ReportAdapter;
import com.dalati.ui.models.Category;
import com.dalati.ui.models.Report;
import com.dalati.ui.models.Type;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExploreFragment extends Fragment {
    ImageButton btnSearch, btnGrid, btnFilter;
    View view;
    Dialog filterDialog;
    boolean isGrid = false;
    ArrayList<String> drop_categoryList = new ArrayList<>();
    AutoCompleteTextView drop_menu_category;
    ArrayAdapter<String> adapter_category;

    ArrayList<String> drop_typeList = new ArrayList<>();
    AutoCompleteTextView drop_menu_type;
    ArrayAdapter<String> adapter_type;

    DatabaseReference databaseReference;
    List<Category> categoryList;
    List<Type> typeList;
    List<Report> reportList;
    RecyclerView recycler_reports;

    ReportAdapter reportAdapter;
    String currentLang, categoryId, typeId;
    int categoryIndex, typeIndex;
    private GridLayoutManager gridLayoutManager;
    ShimmerFrameLayout shimmerFrameLayout;
    boolean isCategorySelected, isTypeSelected;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExploreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_explore, container, false);
        defineViews();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getCategories();
            }
        }, 2000);
        filtering();
        return view;
    }

    private void defineViews() {

        btnGrid = view.findViewById(R.id.btnGrid);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnFilter = view.findViewById(R.id.btnFilter);
        recycler_reports = view.findViewById(R.id.reports_recycler);
        shimmerFrameLayout = view.findViewById(R.id.shimmer);
        shimmerFrameLayout.startShimmer();

        recycler_reports.setHasFixedSize(true);
        recycler_reports.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        databaseReference = FirebaseDatabase.getInstance().getReference();
        categoryList = new ArrayList<>();
        typeList = new ArrayList<>();
        reportList = new ArrayList<>();
        reportAdapter = new ReportAdapter(getActivity());
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SearchActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCategorySelected = false;
                isTypeSelected = false;
                filterDialog.show();
            }
        });
        btnGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGrid) {
                    reportAdapter.setVIEW_TYPE(0);
                    recycler_reports.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    recycler_reports.setAdapter(reportAdapter);
                    btnGrid.setImageResource(R.drawable.ic_element_3);
                    isGrid = false;
                } else {

                    reportAdapter.setVIEW_TYPE(1);
                    recycler_reports.setLayoutManager(gridLayoutManager);
                    recycler_reports.setAdapter(reportAdapter);
                    btnGrid.setImageResource(R.drawable.ic_row_vertical);
                    isGrid = true;

                }


            }
        });

    }

    private void filtering() {
        filterDialog = new Dialog(getContext());
        filterDialog.setContentView(R.layout.filter_dialog);
        filterDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Button btnFilter;

        drop_menu_category = filterDialog.findViewById(R.id.dropdown_category);
        drop_menu_type = filterDialog.findViewById(R.id.dropdown_type);
        btnFilter = filterDialog.findViewById(R.id.btnFilter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Report> tempReportList = new ArrayList<>();
                if (isCategorySelected && isTypeSelected) {
                    for (Report report : reportList) {
                        if (categoryId.equals(report.getCategory_id())
                                && typeId.equals(report.getType_id())) {
                            tempReportList.add(report);
                        }
                    }
                } else if (isCategorySelected) {
                    for (Report report : reportList) {
                        if (categoryId.equals(report.getCategory_id())) {
                            tempReportList.add(report);
                        }
                    }
                }
                reportAdapter.setReportList(tempReportList);
                filterDialog.dismiss();


            }
        });

        drop_menu_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                categoryIndex = i;
                categoryId = categoryList.get(i).getId();
                isCategorySelected = true;
                drop_menu_type.setText(R.string.choose_Type);
                databaseReference.child("Types").child(categoryId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        typeList.clear();
                        drop_typeList.clear();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Type type = postSnapshot.getValue(Type.class);
                            typeList.add(type);
                        }
                        currentLang = Locale.getDefault().getLanguage();
                        switch (currentLang) {
                            case "ar":
                                for (int i = 0; i < typeList.size(); i++) {
                                    drop_typeList.add(typeList.get(i).getNameAr());
                                }
                                break;


                            case "en":
                                for (int i = 0; i < typeList.size(); i++) {
                                    drop_typeList.add(typeList.get(i).getNameEn());
                                }
                                break;
                        }

                        adapter_type = new ArrayAdapter<String>(getContext(), R.layout.filter_item, drop_typeList);
                        drop_menu_type.setAdapter(adapter_type);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        drop_menu_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                typeIndex = i;
                typeId = typeList.get(i).getId();
                isTypeSelected = true;
            }
        });


    }

    public void saveObjectToSharedPreference(Object object) {
        SharedPreferences mPrefs = getContext().getSharedPreferences("DB", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        prefsEditor.putString("Category", json);
        prefsEditor.commit();


    }


    private void getCategories() {

        databaseReference.child("Types").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        for (DataSnapshot postSnapshot2 : postSnapshot.getChildren()) {
                            Type type = postSnapshot2.getValue(Type.class);
                            typeList.add(type);
                        }
                        //  reportAdapter.setTypeList(typeList);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);
                    categoryList.add(category);
                }
                // reportAdapter.setCategoryList(categoryList);
                // saveObjectToSharedPreference(categoryList);


                currentLang = Locale.getDefault().getLanguage();
                switch (currentLang) {
                    case "ar":
                        for (int i = 0; i < categoryList.size(); i++) {
                            drop_categoryList.add(categoryList.get(i).getNameAr());
                        }
                        break;


                    case "en":
                        for (int i = 0; i < categoryList.size(); i++) {
                            drop_categoryList.add(categoryList.get(i).getNameEn());
                        }
                        break;
                }

                adapter_category = new ArrayAdapter<String>(getContext(), R.layout.filter_item, drop_categoryList);
                drop_menu_category.setAdapter(adapter_category);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("Reports").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    reportList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Report report = postSnapshot.getValue(Report.class);

                        //TODO Change the view
                        if (report.getReport_type() == 1) {
                            reportList.add(report);
                        }
                    }
                    reportAdapter.setReportList(reportList);
                    recycler_reports.setAdapter(reportAdapter);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    recycler_reports.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /*   */

    }


}