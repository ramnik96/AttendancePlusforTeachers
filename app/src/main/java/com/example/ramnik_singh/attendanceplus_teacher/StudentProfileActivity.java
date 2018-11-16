package com.example.ramnik_singh.attendanceplus_teacher;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class User{
    String id;
    int roleId;
    String Name;
    String SID;
    String Branch;
    String Semester;

    public User(int roleId, String Name, String SID, String Branch, String Semester){
        this.roleId = roleId;
        this.Name = Name;
        this.SID=SID;
        this.Branch=Branch;
        this.Semester=Semester;
    }
}
public class StudentProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //private static final int CHOOSE_IMAGE = 101;
    private TextView textView;
    //ImageView imageView;
    EditText editTextDisplayName,editTextSID,editTextSemester;
    FirebaseAuth mAuth;
    //Uri uriProfileImage;
    ProgressBar progressBar;
    //String profileImageUrl;
    String selectedbranch,selectedsemester;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_student);
        textView=findViewById(R.id.textviewVerified);
        editTextDisplayName=findViewById(R.id.editTextDisplayName);
        editTextSID=findViewById(R.id.editTextSID);
        Spinner spinner=findViewById(R.id.spinner1);
        Spinner spinner2=findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.branches,android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2=ArrayAdapter.createFromResource(this,R.array.semester,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter2);
        spinner.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);
        mDatabase= FirebaseDatabase.getInstance().getReference();

        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });*/

        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });

        mAuth=FirebaseAuth.getInstance();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FirebaseUser user=mAuth.getCurrentUser();

        if(user!=null){
            if(user.isEmailVerified()){
                textView.setText("Email Verified");
            }
            else{
                textView.setText("Email NOT Verified (Click HERE to Verify)");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"Verification Email Sent",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_profile,menu);
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
            case R.id.menuDashboard:
                finish();
                startActivity(new Intent(this,StudentDashboard.class));
                break;
        }
        return true;
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CHOOSE_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uriProfileImage=data.getData();

            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                imageView.setImageBitmap(bitmap);
                uploadImagetoFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    private void saveUserInformation(){
        String displayName=editTextDisplayName.getText().toString();
        String SID=editTextSID.getText().toString();

        String Branch= selectedbranch;
        String Semester=selectedsemester;
        if(displayName.isEmpty()){
            editTextDisplayName.setError("Name Required");
            editTextDisplayName.requestFocus();
            return;
        }
        FirebaseUser user=mAuth.getCurrentUser();

        if(user!=null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(StudentProfileActivity.this, "Profile Updated",Toast.LENGTH_SHORT).show();
                }
            });
        }

        User u=new User(1,displayName,SID,Branch,Semester);
        u.id=FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("users").child(u.id).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(StudentProfileActivity.this, "Profile Updated",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(getBaseContext(),StudentDashboard.class));

                }
                else{
                    String msg=task.getException().toString();
                    Toast.makeText(StudentProfileActivity.this, msg,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /*private void uploadImagetoFirebaseStorage() {
        StorageReference profileImageRef= FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
        if(uriProfileImage != null){
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressBar.setVisibility(View.GONE);
                profileImageUrl=taskSnapshot.getDownloadUrl().toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(StudentProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void showImageChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),CHOOSE_IMAGE);
    }
*/
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getId()==R.id.spinner1) {
            selectedbranch = adapterView.getItemAtPosition(i).toString();
        }
        else if(adapterView.getId()==R.id.spinner2){
            selectedsemester = adapterView.getItemAtPosition(i).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
