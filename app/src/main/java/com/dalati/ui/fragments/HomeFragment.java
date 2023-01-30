package com.dalati.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.dalati.R;
import com.dalati.ui.activities.ReportActivity;
import com.dalati.ui.models.User;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    MaterialCardView cvLost, cvFound;
    View view;
    TextView tvName;
    User user;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        view = inflater.inflate(R.layout.fragment_home, container, false);
        defineViews();
        return view;
    }

    private void defineViews() {
        cvLost = view.findViewById(R.id.cvLost);
        cvFound = view.findViewById(R.id.cvFound);
        tvName = view.findViewById(R.id.tvName);
        cvLost.setOnClickListener(this);
        cvFound.setOnClickListener(this);
        getUser();

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getContext(), ReportActivity.class);
        switch (view.getId()) {
            case R.id.cvFound:
                intent.putExtra("report_type", 1);
                break;

            case R.id.cvLost:
                intent.putExtra("report_type", 2);
                break;

        }
        startActivity(intent);

    }

    public void getUser() {

        SharedPreferences mPrefs = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json2 = mPrefs.getString("Key", "");
        user = gson.fromJson(json2, User.class);
        if (user != null) {
            tvName.setText(getString(R.string.hello) + " "+user.getName() + ",");
        }

    }

}