package com.example.yourhome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class add_homeAct extends AppCompatActivity {
 private ImageButton imagecov;
 private  EditText edit_surnom,editAdress,addnumtel,des;
 private Button btn_add;
//permission constants
    private static final int CAMERA_REQUEST_CODE=200;
    private static final int Storage_REQUEST_CODE=300;
    //image pick constants
private static final int IMAGE_PICK_GALLERY_CODE=400;
    private static final int IMAGE_PICK_CAMERA_CODE=500;
    //permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;
    //image picked uri
    private Uri image_uri;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_home);
        des=findViewById(R.id.des);
       btn_add=findViewById(R.id.img_add);
       imagecov=findViewById(R.id.imagecov);
       edit_surnom=findViewById(R.id.edit_surnom);
       editAdress=findViewById(R.id.editAdress);
       addnumtel=findViewById(R.id.addnumtel);
       firebaseAuth=FirebaseAuth.getInstance();
       //setup progress dialog
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("attendez svp ");
        progressDialog.setCanceledOnTouchOutside(false);

       //init permission arrays
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imagecov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show dialog to pick image
                showImagePichDialog();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1)input data
                //2)validation data
                //3)add data to db
                inputData();
            }
        });




    }
private  String Addsur,AddDes,Addadress,Addnumtel;
    private boolean discountAvailable=false;
    private void inputData() {
        //1)input data
        Addsur =edit_surnom.getText().toString().trim();
        AddDes=des.getText().toString().trim();
        Addadress=editAdress.getText().toString().trim();
        Addnumtel=addnumtel.getText().toString().trim();
        //2)validation data
if(TextUtils.isEmpty(Addsur)){
    Toast.makeText(this, "le champs surnom est vide ", Toast.LENGTH_SHORT).show();
    return;//don't proceed further
}
        if(TextUtils.isEmpty(AddDes)){
            Toast.makeText(this, "le champs description est vide ", Toast.LENGTH_SHORT).show();
            return;//don't proceed further
        }
        if(TextUtils.isEmpty(Addadress)){
            Toast.makeText(this, "le champs adresse est vide ", Toast.LENGTH_SHORT).show();
            return;//don't proceed further
        }
        if(TextUtils.isEmpty(Addnumtel)){
            Toast.makeText(this, "le champs numero telephone est vide ", Toast.LENGTH_SHORT).show();
            return;//don't proceed further
        }
      addhouse();
    }

    private void addhouse() {
        //3)add data to db
     progressDialog.setMessage("ajoutée");
     progressDialog.show();
     String timestamp=""+System.currentTimeMillis();
     if(image_uri==null){
         //upload without image
         //setup data to upload
         HashMap<String,Object>hashMap=new HashMap<>();
         hashMap.put("AddId","" +timestamp);
         hashMap.put("Addsurnom","" +Addsur);
         hashMap.put("Adddescription","" +AddDes);
         hashMap.put("Addtel","" +Addnumtel);
         hashMap.put("Addadress","" +Addadress);
         hashMap.put("Addicon","");
         hashMap.put("timestamp","" +timestamp);
         hashMap.put("uid","" +firebaseAuth.getUid());
         //add to db
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
         reference.child(firebaseAuth.getUid()).child("Add").child(timestamp).setValue(hashMap).
                 addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {
                         //added to db
                         progressDialog.dismiss();
                         Toast.makeText(add_homeAct.this, "maison ajouter", Toast.LENGTH_SHORT).show();
                         clearData();

                     }
                 }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 //failed
                 progressDialog.dismiss();
                 Toast.makeText(add_homeAct.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
             }
         });








     }else{
         //upload with image
         // first upload image to storage
         //name and path of image to be upload
         String filePathAndName="images/"+""+timestamp;


    StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
    storageReference.putFile(image_uri).
            addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  //image upload
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadImageUri=uriTask.getResult();
                    if(uriTask.isSuccessful()){
                        HashMap<String,Object>hashMap=new HashMap<>();
                        hashMap.put("AddId","" +timestamp);
                        hashMap.put("Addsurnom","" +Addsur);
                        hashMap.put("Adddescription","" +AddDes);
                        hashMap.put("Addtel","" +Addnumtel);
                        hashMap.put("Addadress","" +Addadress);
                        hashMap.put("Addicon",""+downloadImageUri);
                        hashMap.put("timestamp","" +timestamp);
                        hashMap.put("uid","" +firebaseAuth.getUid());
                        //add to db
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                        reference.child(firebaseAuth.getUid()).child("Add").child(timestamp).setValue(hashMap).
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //added to db
                                        progressDialog.dismiss();
                                        Toast.makeText(add_homeAct.this, "maison ajouter", Toast.LENGTH_SHORT).show();
                                        clearData();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //failed
                                progressDialog.dismiss();
                                Toast.makeText(add_homeAct.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            //failed uploading
            progressDialog.dismiss();
            Toast.makeText(add_homeAct.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });

     }

    }
private void clearData(){
        edit_surnom.setText("");
       editAdress.setText("");
       des.setText("");
       addnumtel.setText("");
       imagecov.setImageResource(R.drawable.ic_baseline_image_24);
       image_uri=null;

}

    private void showImagePichDialog() {
        //options to display in dialog
        String[] options={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handel item clicks
                        if(which==0){
                            //camera clicked
                            if (checkCameraPermission()){
                                //permission granted
                                pickFromCamera();
                            }
                            else{
                                //permission not granted,request
                                requestCameraPermission();

                            }

                        }else{
                            //gallery clicked
                            if(checkStoragePermission()){
                                //permission granted
                                 pickFromGallery();

                            }else {
                                //permission not granted,request
                               requestStoragePermission();
                            }
                        }
                    }
                }).show();

    }
private void pickFromGallery(){
        //Intent to pick image from gallery
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);


}
private void pickFromCamera(){
        //intent to pick image from camera
    //using media store to pick high/original quality image
    ContentValues contentValues=new ContentValues();
    contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image_Titles");
    contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image_Description");
    image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
    startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);

}
private  boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);
        return result; //return true/false
}
private void  requestStoragePermission(){
    ActivityCompat.requestPermissions(this,storagePermissions,Storage_REQUEST_CODE);
}
private boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);
        return
                result && result1;
}
    private void  requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }
   //handle permission results

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{if(grantResults.length>0){
                Boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                Boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if(cameraAccepted && storageAccepted){
                    //both prmission granted
                    pickFromCamera();
                }else{
                    //both or one of permissions denied
                    Toast.makeText(this, "Camera & Storage permissions are required...", Toast.LENGTH_SHORT).show();
                }

            }}
            case Storage_REQUEST_CODE:{if(grantResults.length>0){
                Boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(storageAccepted){
                    //permission granted
                    pickFromGallery();
                }else{

                    //permission denied
                    Toast.makeText(this, "Storage permissions is required...", Toast.LENGTH_SHORT).show();

                }


            }}
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //handle image pick results

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            if(requestCode==IMAGE_PICK_GALLERY_CODE){
                //IMAGE PICKED FROM GALLERY
                //save picked image uri
                image_uri=data.getData();
                //set image
                imagecov.setImageURI(image_uri);

            }
            else if(requestCode==IMAGE_PICK_CAMERA_CODE){
                //IMAGE PICKED FROM CAMERA
                imagecov.setImageURI(image_uri);



            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}