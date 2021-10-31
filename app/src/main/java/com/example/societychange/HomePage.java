package com.example.societychange;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.societychange.menu.Developer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;


public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SliderView sliderView;
    int[] images = {
            R.drawable.a,
            R.drawable.b,
            R.drawable.c,
            R.drawable.fbad,
    };
    Button reportButton;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    LottieAnimationView lottieAnimationView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon, reportImage,reportImage2;
    TextView textView;
    String userId;
    Dialog dialog;
    EditText Reportbox, drug_report, take_idreport, take_idlink;
    TextView textViewrepot;
    private static final String BOWLING_KEY = "reportDetails";
    private static final String TAKEID_KEY = "reportDetails";
    private static final String TAKEIDLINK_KEY = "convictedIdLink";
    private static final String DRUG_KEY = "drugInformation";
    private static final String USER_KEY = "userEmail";
    private static final String USERIMAGE_KEY = "reportImage";
    private final int PICK_IMAGE_REQUEST = 71;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference mRef1 = db.collection("User").document("Profile").collection("User Profile");
    private CollectionReference mRef2 = db.collection("ReportSection").document("ReportTopics").collection("TakeId");
    private CollectionReference mRef3 = db.collection("ReportSection").document("ReportTopics").collection("Bowling");
    private CollectionReference mRef4 = db.collection("ReportSection").document("ReportTopics").collection("Drug");
    //choose image
    private Uri imageUri;
    private Bitmap compressor;

    String[] report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        sliderView = findViewById(R.id.image_slider);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();
        storageReference= FirebaseStorage.getInstance().getReference();

        menuIcon = findViewById(R.id.menuicon);

        reportButton = findViewById(R.id.report_button);

        Reportbox = findViewById(R.id.reportbox);
        drug_report = findViewById(R.id.drugreport);
        take_idreport = findViewById(R.id.takeidreport);
        take_idlink = findViewById(R.id.takeidlink);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        textViewrepot = findViewById(R.id.select_report);
        report = getResources().getStringArray(R.array.report);

        textView = findViewById(R.id.imagetext);
        reportImage = findViewById(R.id.imagereport);
        reportImage2 = findViewById(R.id.imagereport2);



        textViewrepot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issuereport();
            }
        });


        header();
        navigation();
        imageslider();
        reportIssue();


    }

    private void reportIssue() {


        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slectreport = textViewrepot.getText().toString();
                if (slectreport.trim().equals("")) {
                    textViewrepot.setText("Select Report issue");
                    Toast.makeText(getApplicationContext(), "Select Report issue", Toast.LENGTH_LONG).show();
                    return;
                }


            }
        });

    }


    private void imageslider() {
        SliderAdapter sliderAdapter = new SliderAdapter(images);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )

        {
            imageUri = data.getData();
            try {
                compressor = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                reportImage.setVisibility(View.VISIBLE);
                reportImage.setImageBitmap(compressor);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        /*

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                imageUri=result.getUri();
                reportImage.setImageURI(imageUri);
            }else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error=result.getError();

            }
        }
        */
    }

    private void issuereport() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.customspinner);
        dialog.getWindow().setLayout(650, 800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        EditText editText = dialog.findViewById(R.id.searchedit);
        ListView listView = dialog.findViewById(R.id.listview);


        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, report);
        listView.setAdapter(adapter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                if (i == 0) {
                    textViewrepot.setText(adapter.getItem(i));
                    Reportbox.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);

                    reportImage2.setVisibility(View.VISIBLE);
                    reportButton.setVisibility(View.VISIBLE);
                    drug_report.setVisibility(View.GONE);
                    take_idreport.setVisibility(View.GONE);
                    take_idlink.setVisibility(View.GONE);
                    dialog.dismiss();

                    reportImage2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ContextCompat.checkSelfPermission(HomePage.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                                        PackageManager.PERMISSION_GRANTED) {
                                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                                    ActivityCompat.requestPermissions(HomePage.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                } else {
                                    choseImage();
                                }
                            } else {
                                choseImage();
                            }
                        }



                    });


                    reportButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String bowlingtext = Reportbox.getText().toString();

                            if (bowlingtext.trim().equals("")) {
                                Toast.makeText(getApplicationContext(), "Write Bowling Issue", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if(reportImage.getDrawable()==null){
                                Toast.makeText(getApplicationContext(), "Please Select an Image", Toast.LENGTH_LONG).show();
                                return;
                            }
                            final ProgressDialog progressDialog=new ProgressDialog(HomePage.this);
                            progressDialog.startLoadingDialog();
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismissDialog();

                                }
                            },4000);

                           File file=new File(imageUri.getPath());
                            try {
                                compressor=new Compressor(HomePage.this)
                                        .setMaxHeight(125)
                                        .setMaxWidth(125)
                                        .setQuality(50)
                                        .compressToBitmap(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            compressor.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                            byte[] thumb= byteArrayOutputStream.toByteArray();
                            String useremail = mAuth.getCurrentUser().getEmail();
                            Random random=new Random();
                           int num=random.nextInt(100000)+1;

                            UploadTask image_path = storageReference.child("bowling_image").child(useremail).child(String.valueOf(num)).putBytes(thumb);
                            image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                       storeData(task);

                                    }else{
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getApplicationContext(),"Image error :"+error,Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                    });





                } else if (i == 1) {

                    textViewrepot.setText(adapter.getItem(i));
                    take_idreport.setVisibility(View.VISIBLE);
                    take_idlink.setVisibility(View.VISIBLE);
                    reportButton.setVisibility(View.VISIBLE);
                    drug_report.setVisibility(View.GONE);
                    Reportbox.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    reportImage.setVisibility(View.GONE);
                    reportImage2.setVisibility(View.GONE);

                    dialog.dismiss();

                    reportButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String takeid = take_idreport.getText().toString();
                            String takeidlink = take_idlink.getText().toString();


                            if (takeid.trim().equals("")) {
                                Toast.makeText(getApplicationContext(), "Report Your Issue", Toast.LENGTH_LONG).show();
                                return;
                            }
                            if (takeidlink.trim().equals("")) {
                                Toast.makeText(getApplicationContext(), "Provide Convicted Social Link", Toast.LENGTH_LONG).show();
                                return;
                            }
                            final ProgressDialog progressDialog=new ProgressDialog(HomePage.this);
                            progressDialog.startLoadingDialog();
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismissDialog();

                                }
                            },2000);
                            Map<String, Object> note = new HashMap<>();
                            note.put(TAKEID_KEY, takeid);
                            note.put(TAKEIDLINK_KEY, takeidlink);
                            mRef2.add(note);
                            Toast.makeText(getApplicationContext(), "Report Submitted", Toast.LENGTH_LONG).show();


                        }
                    });

                } else if (i == 2) {
                    textViewrepot.setText(adapter.getItem(i));
                    drug_report.setVisibility(View.VISIBLE);
                    reportButton.setVisibility(View.VISIBLE);
                    Reportbox.setVisibility(View.GONE);
                    take_idreport.setVisibility(View.GONE);
                    take_idlink.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    reportImage.setVisibility(View.GONE);
                    reportImage2.setVisibility(View.GONE);
                    dialog.dismiss();
                    reportButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String derugreport = drug_report.getText().toString();

                            if (derugreport.trim().equals("")) {
                                Toast.makeText(getApplicationContext(), "Write Drug Issue", Toast.LENGTH_LONG).show();
                                return;
                            }

                            final ProgressDialog progressDialog=new ProgressDialog(HomePage.this);
                            progressDialog.startLoadingDialog();
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismissDialog();

                                }
                            },2000);
                            String useremail = mAuth.getCurrentUser().getEmail();
                            Map<String, Object> note = new HashMap<>();
                            note.put(DRUG_KEY, derugreport);
                            note.put(USER_KEY, useremail);
                            mRef4.add(note);
                            Toast.makeText(getApplicationContext(), "Report Submitted", Toast.LENGTH_LONG).show();

                        }
                    });

                }


            }

            private void choseImage() {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                }


                /*
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(HomePage.this);
                Toast.makeText(getApplicationContext(), "Choose Image", Toast.LENGTH_LONG).show();

                 */

        });


    }

    private void storeData(Task<UploadTask.TaskSnapshot> task) {

        String bowlingtext = Reportbox.getText().toString();
        String useremail = mAuth.getCurrentUser().getEmail();
        Uri download_uri;
        if (task != null) {
            download_uri = task.getResult().getUploadSessionUri();
        }
        else {
            download_uri= imageUri;
        }
        Map<String,Object> note = new HashMap<>();
        note.put(BOWLING_KEY,bowlingtext);
        note.put(USERIMAGE_KEY,download_uri.toString());
        note.put(USER_KEY,useremail);
        mRef3.add(note);
        Toast.makeText(getApplicationContext(), "Report Submit", Toast.LENGTH_LONG).show();

    }


    private void header() {
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void navigation() {
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.home);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            finish();
        } else
            super.onBackPressed();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.admin)
        {
            Toast.makeText(getApplicationContext(),"Admin",Toast.LENGTH_LONG).show();
        }
        if(item.getItemId()==R.id.developer)
        {
           Intent intent=new Intent(getApplicationContext(), Developer.class);
           startActivity(intent);
        }
        if(item.getItemId()==R.id.logout)
        {

            mAuth.signOut();
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true;

    }

}
//  String download_uri = task.getResult().getUploadSessionUri().toString();

