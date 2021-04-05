package com.softloyal.myapplication.Vistas_Controladores.Java;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.softloyal.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class PostJavaActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;
    private ImageView newPostImage;
    private EditText newPostDesc, newPostDesc2, newPostDesc3;
    private Button newPostButton;
    private Uri urlImage = null;

    private ProgressBar newPostProgressBar;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String usuarioActualId;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        newPostToolbar = findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Añadir una nueva publicación");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostDesc2 = findViewById(R.id.new_post_desc2);
        newPostDesc3= findViewById(R.id.new_post_desc3);
        newPostButton = findViewById(R.id.post_btn);
        newPostProgressBar = findViewById(R.id.new_post_progress);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        usuarioActualId = firebaseAuth.getCurrentUser().getUid();

        //hace que podamos recortar la imagen seleccionada para seguir un "estandar" y que cuando nos la traigamos de la bbdd no se rompa por el tamaño desigual
        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(1024, 720)
                        .setAspectRatio(16, 9)
                        .start(PostJavaActivity.this);

            }
        });

        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String desc = newPostDesc.getText().toString().trim();
                final String desc2 = newPostDesc2.getText().toString().trim();
                final String desc3 = newPostDesc3.getText().toString().trim();
                if (urlImage != null) {
                    newPostProgressBar.setVisibility(View.VISIBLE);
                    //UUID aleatorio para la imagen
                    final String randomUrl = UUID.randomUUID().toString();
                    //Se guarda en post_images
                    StorageReference filePath = storageReference.child("post_images").child(randomUrl + ".jpg");
                    filePath.putFile(urlImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            //pilla la url de la imagen
                            final String downloadUri = task.getResult().getDownloadUrl().toString();

                            //crea un nuevo archivo con la imagen, su url y su bitmap
                            if (task.isSuccessful()) {
                                File newImageFile = new File(urlImage.getPath());

                                try {
                                    imageBitmap = new Compressor(PostJavaActivity.this)
                                            .setMaxHeight(2048)
                                            .setMaxWidth(1080)
                                            .setQuality(100)
                                            .compressToBitmap(newImageFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData = baos.toByteArray();

                                //Se inicializa la subida de la image al storage
                                UploadTask uploadTask = storageReference.child("post_images/thumbs").child(randomUrl + ".jpg").putBytes(thumbData);
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                                        //el documento referido al post, se crea como un map con esos atributos: image_url, image_thumb, desc, user_id, timestamp
                                        Map<String, Object> postmap = new HashMap<>();
                                        postmap.put("image_url", downloadUri);
                                        postmap.put("image_thumb",downloadthumbUri);
                                        postmap.put("desc", desc);
                                        postmap.put("desc2", desc2);
                                        postmap.put("desc3", desc3);
                                        postmap.put("user_id", usuarioActualId);
                                        postmap.put("timestamp", FieldValue.serverTimestamp());

                                        //Aquí se llama a la colección Posts y se le añade el Map creado anteriormente
                                        firebaseFirestore.collection("Java").add(postmap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()) {
                                                    //Si sale bien, se indica con un mensaje
                                                    Toast.makeText(PostJavaActivity.this, "Se ha añadido correctamente", Toast.LENGTH_SHORT).show();
                                                    Intent mainIntent = new Intent(PostJavaActivity.this, MainJavaActivty.class);
                                                    startActivity(mainIntent);
                                                    finish();

                                                } else {
                                                    //sino se muestra un mensaje de error
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(PostJavaActivity.this, "Error en el almacenamiento de la base de datos " + error, Toast.LENGTH_SHORT).show();

                                                }
                                                newPostProgressBar.setVisibility(View.INVISIBLE);
                                            }
                                        });


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        String error = e.getMessage();
                                        Toast.makeText(PostJavaActivity.this, "Error de subida " + error, Toast.LENGTH_SHORT).show();

                                    }
                                });


                            } else {
                                newPostProgressBar.setVisibility(View.INVISIBLE);
                                String error = task.getException().getMessage();
                                Toast.makeText(PostJavaActivity.this, "Error de subida " + error, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }


            }
        });

    }
    //Se adapta la imagen subida.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                urlImage = result.getUri();
                newPostImage.setImageURI(urlImage);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
