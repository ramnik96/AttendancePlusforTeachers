package com.example.ramnik_singh.attendanceplus_teacher;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Keep
public class TeacherDashboard extends AppCompatActivity implements Serializable {
    private static final String TAG = "TeacherDashboard";
    private Button buttonScan,buttonMail;
    private EditText editTextSubject,editTextSemester,editTextRemarks;
    String Semester,Subject,Timings,Remarks,Date,finalnode;
    private TextView textViewDate,textViewTime,textViewRecent;
    private ProgressBar progressbar3;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Map<String, String> map = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        buttonScan = findViewById(R.id.scan_btn);
        buttonMail=findViewById(R.id.buttonMail);
        editTextSemester=findViewById(R.id.editTextSemester);
        editTextSubject=findViewById(R.id.editTextSubject);
        editTextRemarks=findViewById(R.id.editTextRemarks);
        textViewRecent=findViewById(R.id.textViewRecent);

        textViewDate=findViewById(R.id.textViewDate);
        textViewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal=Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(TeacherDashboard.this,android.R.style.Theme_Holo_Dialog_MinWidth,mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "-" + day + "-" + year);
                String date = day + "-" + month + "-" + year;
                textViewDate.setText(date);
                Date=date;
            }
        };
        textViewTime=findViewById(R.id.textViewTime);
        textViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal=Calendar.getInstance();
                int hour=cal.get(Calendar.HOUR_OF_DAY);
                int minute=cal.get(Calendar.MINUTE);
                TimePickerDialog dialog1=new TimePickerDialog(TeacherDashboard.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        textViewTime.setText(selectedHour + ":" + selectedMinute);
                        Timings=selectedHour + ":" + selectedMinute;
                    }
                },hour,minute,false);
            dialog1.setTitle("Select Time");
            dialog1.show();
            }
        });

        final Activity activity = this;
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EXCEPTION HANDLING REQUIRED HERE
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan QR Code");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        progressbar3=findViewById(R.id.progressbar3);
        progressbar3.setVisibility(View.GONE);
        buttonMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar3.setVisibility(View.VISIBLE);
                //Map<String, String> map = new HashMap<String, String>();
                Semester=editTextSemester.getText().toString().trim();
                Subject=editTextSubject.getText().toString().trim();
                Remarks=editTextRemarks.getText().toString().trim();
                finalnode=Subject+"_"+Semester+"_"+Date+"_"+Timings+"_"+Remarks;
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("AttendanceRegister")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    Log.d("KEY", snapshot.getKey());
                                    Log.d("finalnode", finalnode);

                                    if(snapshot.getKey().equals(finalnode)) {
                                        for (DataSnapshot snapshot2 : snapshot.getChildren()) {

                                            Log.d("MAJOR", "value :" + snapshot.getValue());
                                            Log.d("MAJOR", "value :" + snapshot.getChildrenCount());

                                            Log.d("MAJOR", "value :" + snapshot2.getValue());
                                            Log.d("MAJOR", "value :" + snapshot2.getChildrenCount());
                                            String key, value;
                                            key = snapshot2.child("SID").getValue().toString();
                                            value = snapshot2.child("Name").getValue().toString();
                                            map.put(key, value);
                                            //Toast.makeText(TeacherDashboard.this, key+" "+value,Toast.LENGTH_LONG).show();
                                            Log.d("tag", "PAIR: " + key + " " + value);
                                        }
                                    }

                                }
                                Log.d("MAP",map.toString());
                                CSVWriter writer = null;
                                String csv = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyAttendanceFile.csv");
                                Log.d("FILE",csv);
                                String state = Environment.getExternalStorageState();
                                if (Environment.MEDIA_MOUNTED.equals(state)) {
                                    Log.d("FILE", "YES Available");
                                }
                                else{
                                    Log.d("FILE", "NO NOT Available");
                                }
                                try {
                                    writer = new CSVWriter(new FileWriter(csv));
                                    List<String[]> data = new ArrayList<String[]>();
                                    for (Map.Entry<String, String> entry : map.entrySet()) {
                                        data.add(new String[]{entry.getKey(), entry.getValue()});
                                    }
                                    writer.writeAll(data); // data is adding to csv
                                    //Toast.makeText(TeacherDashboard.this, "File Created",Toast.LENGTH_LONG).show();
                                    //Toast.makeText(TeacherDashboard.this, map.toString(),Toast.LENGTH_LONG).show();
                                    Log.d("FILE","File Created");
                                    writer.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                progressbar3.setVisibility(View.GONE);

                                //EMAIL THE CSV FILE
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String email = user.getEmail();
                                //Log.d("EMAIL",email);

                                String filename="MyAttendanceFile.csv";
                                File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
                                Uri path = Uri.fromFile(filelocation);
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent .setType("vnd.android.cursor.dir/email");
                                String to[] = {email};
                                emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
                                emailIntent .putExtra(Intent.EXTRA_STREAM, path);
                                emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Attendance Report");
                                startActivity(Intent.createChooser(emailIntent , "Send email..."));
                                //EMAIL ENDS HERE
                            }
                            // Now may be display all user in listview
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                //Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();
                String str=result.getContents().toString();
                final String[] arrOfStr = str.split("\\|");
                Semester=editTextSemester.getText().toString().trim();
                Subject=editTextSubject.getText().toString().trim();
                Remarks=editTextRemarks.getText().toString().trim();
                finalnode=Subject+"_"+Semester+"_"+Date+"_"+Timings+"_"+Remarks;
                textViewRecent.setText("Recently Marked Present: "+arrOfStr[0]+" | "+arrOfStr[1]);
                textViewRecent.setVisibility(View.VISIBLE);

                student A=new student(arrOfStr[0],arrOfStr[1]);
                //Toast.makeText(this, arrOfStr[0]+" "+arrOfStr[1],Toast.LENGTH_LONG).show();
                FirebaseDatabase.getInstance().getReference().child("AttendanceRegister").child(finalnode).push().setValue(A).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(TeacherDashboard.this, "Attendance Marked "+arrOfStr[0]+" | "+arrOfStr[1],Toast.LENGTH_SHORT).show();

                            //Toast.makeText(TeacherDashboard.this, finalnode,Toast.LENGTH_SHORT).show();
                            buttonScan.performClick();
                        }
                        else{
                            String msg=task.getException().toString();
                            Toast.makeText(TeacherDashboard.this, msg,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
           // case R.id.menuProfile:
             //   finish();
                //startActivity(new Intent(this,StudentProfileActivity.class));
               // break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }
    }

