package com.dalati.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dalati.R;
import com.dalati.ui.activities.EditAccountActivity;
import com.dalati.ui.activities.auth.LoginActivity;
import com.dalati.ui.base.LanguageManager;
import com.dalati.ui.models.Request;
import com.dalati.ui.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment
        implements AdapterView.OnItemSelectedListener {
    View view;
    EditText etName, etPhone, etEmail;
    Button btnSignOut;
    User user;
    ImageButton btnEdit;
    DatabaseReference databaseReference;
    ImageView profileImage;
    String[] languages = new String[2];
    String currentLanguage = Locale.getDefault().getLanguage();

    LanguageManager languageManager;
    OnLanguageSelectedListener mCallback;
    int check = 0;
    int noOfAwards = 0;

    // Container Activity must implement this interface
    public interface OnLanguageSelectedListener {
        public void onLanguageSelected(String language);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnLanguageSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
        view = inflater.inflate(R.layout.fragment_account, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("End Users");
        defineViews();
        getUser();
        return view;
    }

    public void defineViews() {
        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etPhone = view.findViewById(R.id.et_phone);
        btnSignOut = view.findViewById(R.id.btnSignOut);
        btnEdit = view.findViewById(R.id.btnEdit);
        profileImage = view.findViewById(R.id.profileImg);
        languageManager = new LanguageManager(getContext());
        languages = getContext().getResources().getStringArray(R.array.languages);
        initSpinner();
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), EditAccountActivity.class));
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            }
        });
    }

    public void getUser() {

        SharedPreferences mPrefs = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json2 = mPrefs.getString("Key", "");
        user = gson.fromJson(json2, User.class);
        if (user != null) {
            etName.setText(user.getName());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
            if (!user.getImgUrl().equals("Default")) {
                Glide.with(getContext())
                        .load(user.getImgUrl())
                        .centerCrop()
                        .into(profileImage);
            }
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        User user1 = postSnapshot.getValue(User.class);
                        if (user1.getId().equals(firebaseUser.getUid())) {
                            user = user1;
                            etName.setText(user.getName());
                            etEmail.setText(user.getEmail());
                            etPhone.setText(user.getPhone());
                            getAwards(user.getId());
                            saveObjectToSharedPreference(user);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void saveObjectToSharedPreference(Object object) {
        SharedPreferences mPrefs = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(object);
        prefsEditor.putString("Key", json);
        prefsEditor.commit();
    }

    private void initSpinner() {
        Spinner spinner = view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, languages);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int selectedPosition = ((currentLanguage.equals("en")) ? 1 : 0);
        spinner.setSelection(selectedPosition);
        spinner.setOnItemSelectedListener(this); // Will call onItemSelected() Listener.
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String newLanguage = ((i == 0) ? "ar" : "en");
        if (++check > 1) {
            if (!currentLanguage.equals(newLanguage)) {
                mCallback.onLanguageSelected(newLanguage);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void getAwards(String userId) {
        TextView tvAwards = view.findViewById(R.id.tvAward);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Request request = snapshot.getValue(Request.class);
                    assert request != null;
                    if (userId.equals(request.getUser_id())) {
                        noOfAwards += 1;
                    }

                }

                if (noOfAwards == 0) {
                    tvAwards.setText("You didn't find any items yet");
                } else {
                    tvAwards.setText("You helped " + String.valueOf(noOfAwards) + " People to find their items");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}