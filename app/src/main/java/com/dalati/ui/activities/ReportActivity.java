package com.dalati.ui.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.dalati.R;
import com.dalati.ui.adapters.ModernImageSlider;
import com.dalati.ui.base.BaseActivity;
import com.dalati.ui.models.Category;
import com.dalati.ui.models.Report;
import com.dalati.ui.models.Type;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends BaseActivity {
    EditText etDate, etPlace, etDescription;
    Button btnConfirm;
    int PICK_IMAGE_MULTIPLE = 1;
    String urls = " ";
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;
    List<Uri> imagesList;
    ArrayList<String> urlStrings;
    ImageView btnAddImage;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    List<Category> categoryList;
    List<Type> typeList;
    String currentLang, categoryId, typeId;
    int categoryIndex, typeIndex;
    ViewPager2 adsViewPager;
    int report_type;

    TextInputLayout foundLayout, dateLayout;
    TextView tvTitle;
    LottieAnimationView lottieAnimationView;
    LinearLayout uploadLinearLayout, linearSuccess;


    ArrayList<String> drop_categoryList = new ArrayList<>();
    AutoCompleteTextView drop_menu_category;
    ArrayAdapter<String> adapter_category;

    ArrayList<String> drop_typeList = new ArrayList<>();
    AutoCompleteTextView drop_menu_type;
    ArrayAdapter<String> adapter_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defineViews();
        getCategories();
    }

    @Override
    public int defineLayout() {
        return R.layout.activity_report;
    }

    private void defineViews() {
        etDate = findViewById(R.id.etDate);
        btnConfirm = findViewById(R.id.btn_upload);
        btnAddImage = findViewById(R.id.add_image);
        drop_menu_category = findViewById(R.id.dropdown_category);
        drop_menu_type = findViewById(R.id.dropdown_type);
        adsViewPager = findViewById(R.id.viewPagerImageSlider);
        etDescription = findViewById(R.id.etDescription);
        etPlace = findViewById(R.id.etPlace);
        foundLayout = findViewById(R.id.foundLayout);
        dateLayout = findViewById(R.id.dateLayout);
        tvTitle = findViewById(R.id.tvTitle);
        uploadLinearLayout = findViewById(R.id.linearUpload);
        linearSuccess = findViewById(R.id.linearSuccess);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);


        report_type = getIntent().getIntExtra("report_type", 0);
        if (report_type == 2) {
            dateLayout.setHint(R.string.lost_since);
            foundLayout.setHint(R.string.lost_location);
            tvTitle.setText(R.string.report_lost);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(ReportActivity.this);
        progressDialog.setTitle(getString(R.string.uploading));

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        imagesList = new ArrayList<>();
        categoryList = new ArrayList<>();
        typeList = new ArrayList<>();

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker("");
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*"); //allows any image file type. Change * to specific extension to limit it
//**The following line is the important one!
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE); //SELECT_PICTURES is simply a global int used to check the calling intent in onActivityResult

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                upload(imagesList);

                //Toast.makeText(ReportActivity.this, categoryId + "\n" + typeId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPicker(String type) {

        final Calendar c = Calendar.getInstance();
        // on below line we are getting
        // our day, month and year.
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                ReportActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our text view.
                        etDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                },
                // on below line we are passing year,
                // month and day for selected date in our date picker.
                year, month, day);
        // at last we are calling show to
        // display our date picker dialog.
        datePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_MULTIPLE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imagesList.add(imageUri);
                        //do something with the image (save it to some directory or whatever you need to do with it here)
                        urls = urls + "\n" + String.valueOf(imageUri);
                    }
                }
            } else if (data.getData() != null) {
                String imagePath = data.getData().getPath();
            }

            for (int i = 0; i < imagesList.size(); i++) {
                System.out.println("Hi Dalati" + imagesList.get(i));
            }
            displayImages();
        }
    }

    public void upload(List<Uri> imagesList) {
        urlStrings = new ArrayList<>();
        progressDialog.show();
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ImageFolder");

        for (int i = 0; i < imagesList.size(); i++) {

            Uri IndividualImage = imagesList.get(i);
            final StorageReference ImageName = ImageFolder.child("Images" + IndividualImage.getLastPathSegment());

            ImageName.putFile(IndividualImage).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ImageName.getDownloadUrl().addOnSuccessListener(
                                    new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            urlStrings.add(String.valueOf(uri));


                                            if (urlStrings.size() == imagesList.size()) {
                                                store(urlStrings);
                                            }
                                        }
                                    }
                            );
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    String size = String.format("Uploading Images:\n %5.2f MB transferred",
                            snapshot.getBytesTransferred() / 1024.0 / 1024.0);
                    progressDialog.setMessage(size);

                }
            });
        }
    }

    private void store(ArrayList<String> urlStrings) {
        String date = etDate.getText().toString();

        //TODO dalati changr type
        String place = etPlace.getText().toString();
        String description = etDescription.getText().toString();
        String report_id = databaseReference.push().getKey();
        String publisher_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Report report = null;
        if (report_type == 1) {
            report = new Report(report_id, publisher_id, "none", "none", publisher_id, categoryId, typeId, date, place, description, 0, report_type, true, urlStrings);

        } else {
            report = new Report(report_id, publisher_id, "none", publisher_id, "none", categoryId, typeId, date, place, description, 0, report_type, true, urlStrings);
        }
        assert report_id != null;
        databaseReference.child("Reports").child(report_id).setValue(report).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    uploadLinearLayout.setVisibility(View.GONE);
                    linearSuccess.setVisibility(View.VISIBLE);
                    findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    });

                }
            }
        });
       /* for (int i = 0; i < urlStrings.size(); i++) {
            databaseReference.child("Images").child(report_id).child(String.valueOf(i)).setValue(urlStrings.get(i));
        }*/



       /* databaseReference.push().setValue(hashMap)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ReportActivity.this, "Successfully Uplosded", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                ).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReportActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });*/
        progressDialog.dismiss();


        imagesList.clear();
    }

    private void getCategories() {

        databaseReference.child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Category category = postSnapshot.getValue(Category.class);
                    categoryList.add(category);
                }

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

                adapter_category = new ArrayAdapter<String>(getApplicationContext(), R.layout.filter_item, drop_categoryList);
                drop_menu_category.setAdapter(adapter_category);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        drop_menu_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                categoryIndex = i;
                categoryId = categoryList.get(i).getId();
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

                        adapter_type = new ArrayAdapter<String>(getApplicationContext(), R.layout.filter_item, drop_typeList);
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
            }
        });

    }

    private void displayImages() {
        adsViewPager.setVisibility(View.VISIBLE);
        btnAddImage.setVisibility(View.GONE);
        adsViewPager.setAdapter(new ModernImageSlider(imagesList, ReportActivity.this));
        adsViewPager.setClipToPadding(false);
        adsViewPager.setClipChildren(false);
        adsViewPager.setOffscreenPageLimit(3);
        adsViewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(10));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        adsViewPager.setPageTransformer(compositePageTransformer);
    }
}

