package com.example.ramnik_singh.attendanceplus_teacher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class StudentDashboard extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button generateButton;
    ImageView image;
    String text2Qr;
    private ProgressBar progressbar2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth= FirebaseAuth.getInstance();
        final FirebaseUser user=mAuth.getCurrentUser();
        final String u=FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("tag","value of u "+u);
        progressbar2=(ProgressBar)findViewById(R.id.progressbar2);
        progressbar2.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String id=snapshot.child("id").getValue().toString();
                            Log.d("tag","value of id "+ id);// this is your user
                            if(id.equals(u)){
                                String text2Qr1=snapshot.child("SID").getValue().toString();
                                String text2Qr2=snapshot.child("Name").getValue().toString();
                                text2Qr=text2Qr1+"_"+text2Qr2;
                                //Toast.makeText(StudentDashboard.this, text2Qr,Toast.LENGTH_SHORT).show();
                                Log.d("tag","Matched "+ id);
                                // add to list
                            }
                        }
                        // Now may be display all user in listview
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        progressbar2.setVisibility(View.GONE);

        //Log.d("tag","value of text2qr "+text2Qr);
        //System.out.print(text2Qr);

        generateButton = findViewById(R.id.generateButton);
        image = findViewById(R.id.image);
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StudentDashboard.this,"Scan this code to mark your attendance!",Toast.LENGTH_SHORT).show();
                Log.d("tag","value of text2qr "+ text2Qr);
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try{
                    BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    image.setImageBitmap(bitmap);
                }
                catch (WriterException e){
                   e.printStackTrace();
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this,MainActivity.class));
                break;

            /*case R.id.menuProfile:
                startActivity(new Intent(this,StudentProfileActivity.class));
                break;*/
        }

        return true;
    }

}
