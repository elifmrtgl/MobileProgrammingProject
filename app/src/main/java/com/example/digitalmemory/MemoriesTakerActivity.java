package com.example.digitalmemory;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalmemory.models.Memories;
import com.google.android.gms.maps.model.LatLng;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MemoriesTakerActivity extends AppCompatActivity {

    EditText et_title, et_content, et_emoji;
    TextView tv_location;
    ImageView saveImageView;
    Memories memories;
    Button locButton;
    ImageButton pdfButton;
    LatLng latLng;
    boolean isOldMemory = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memories_taker);

        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        et_emoji = findViewById(R.id.et_emoji);
        saveImageView = findViewById(R.id.saveImageView);
        locButton = findViewById(R.id.locButton);
        tv_location = findViewById(R.id.locationTV);
        pdfButton = findViewById(R.id.pdfButton);

        memories = new Memories();
        try {
            memories = (Memories) getIntent().getSerializableExtra("toBeUpdated");
            et_title.setText(memories.getTitle());
            et_content.setText(memories.getContent());
            tv_location.setText(memories.getLocation());
            et_emoji.setText((memories.getEmoji()));
            isOldMemory = true;

        }catch(Exception e){
            e.printStackTrace();
        }

        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivityForResult(intent, 103);
            }
        });

        pdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pdfConverter();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

        saveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = et_title.getText().toString();
                String content = et_content.getText().toString();
                String location = tv_location.getText().toString();
                String emoji = et_emoji.getText().toString();
                //int emoji = et_emoji.getText();

                if(content.isEmpty()){
                    Toast.makeText(MemoriesTakerActivity.this, "Please add your memory", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                    Date date = new Date();
                    if(!isOldMemory){
                        memories = new Memories();
                    }

                    memories.setTitle(title);
                    memories.setContent(content);
                    memories.setDate(formatter.format(date));
                    memories.setLocation(location);
                    memories.setEmoji(emoji);
                    if(isOldMemory){
                        Toast.makeText(MemoriesTakerActivity.this, "Memory is updated.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MemoriesTakerActivity.this, "Memory is added.", Toast.LENGTH_SHORT).show();
                    }

                    Intent intent = new Intent();
                    intent.putExtra("memory", memories);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==103){
            latLng = (LatLng) data.getParcelableExtra("locRes");
            tv_location.setText(latLng2Address(latLng));
        }
    }

    public String latLng2Address(LatLng latLng){
        String res = "deneme";
        Double lat = latLng.latitude;
        Double lng = latLng.longitude;
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        List<Address> addressList = null;

        try{
            addressList = geocoder.getFromLocation(lat, lng, 1);
        }catch (Exception e){
            e.printStackTrace();
        }

        String address = addressList.get(0).getAddressLine(0);
        String country = addressList.get(0).getCountryName();

        String[] splits =  address.split(",");
        String[] splits2 = splits[2].split(" ");
        res=splits2[2];

        return res;
    }

    public void permissionControl(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 24);
        }
    }

    public void pdfConverter() throws FileNotFoundException{
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        permissionControl();
        File file = new File(path, et_title.getText()+".pdf");

        OutputStream output = new FileOutputStream(file);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);
        pdfDocument.setDefaultPageSize(PageSize.A4);

        Paragraph p1 = new Paragraph("Title: "+et_title.getText().toString()+"\n");
        Paragraph p2 = new Paragraph("Content: "+et_content.getText().toString()+"\n");
        Paragraph p3 = new Paragraph("Location: "+tv_location.getText().toString()+"\n");

        document.add(p1);
        document.add(p2);
        document.add(p3);
        document.close();




    }
}