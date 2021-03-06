package com.fraalepal.helloworldblog.Perfil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fraalepal.helloworldblog.Vistas_Controladores.LandingPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.fraalepal.helloworldblog.Vistas_Controladores.Java.MainJavaActivty;
import com.fraalepal.helloworldblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private Uri urlImage = null;
    private EditText nombre;
    private Button profileButton;
    private String idUsuario;
    private boolean isChanged = false;
    private ProgressBar profileProgressBar;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private FloatingActionButton addPostButton;
    private BottomNavigationView mainBottomNav;
    private ProfileFragment profileFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        //Se inicializan los atributos e instancias de firebase
        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Ajustes de cuenta");

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        idUsuario = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();

        profileImage = findViewById(R.id.setup_image);

        nombre = findViewById(R.id.setup_name);
        profileButton = findViewById(R.id.setup_btn);
        //Barra de progreso
        profileProgressBar = findViewById(R.id.setup_progress);
        profileProgressBar.setVisibility(View.VISIBLE);
        profileButton.setEnabled(false);

        //Nos traemos la info del usuario de la colecci??n Users y hacemos un set en los elementos del XML (Glide es util para ahcer set de la imagen imagen), si algo sale mal se notifica
        firebaseFirestore.collection("Users").document(idUsuario).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        urlImage = Uri.parse(image);

                        nombre.setText(name);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);
                        Glide.with(SetupActivity.this).
                                setDefaultRequestOptions(placeholderRequest).load(image).into(profileImage);
                    }
                }
                else{
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Error de base de datos "+error, Toast.LENGTH_SHORT).show();
                }
                profileProgressBar.setVisibility(View.INVISIBLE);
                profileButton.setEnabled(true);
            }
        });

        //Comprobamos si la info ha cambiado, si lo ha hecho tenemos que actualizarla en el Storage
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = nombre.getText().toString().trim();
                if (!TextUtils.isEmpty(username) && urlImage != null) {
                    profileProgressBar.setVisibility(View.VISIBLE);
                    if(isChanged){
                        idUsuario = firebaseAuth.getCurrentUser().getUid();
                        StorageReference image_path = storageReference.child("profile_images").child(idUsuario + ".jpg");
                        image_path.putFile(urlImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storeFirestore(task,username);
                                } else {
                                    profileProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(SetupActivity.this, " Error de imagen" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else{
                        storeFirestore(null,username);
                    }
                }
            }
        });

        //Cuando clickamos en la imagen se comprueban los permisos para acceder a las fotos del dispositivo, si no estan aceptados se notifica y se pide que se acepten, en otro caso se lanza el metodo para seleccionar foto
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(SetupActivity.this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {

                        SelectImage();
                    }
                } else {

                    SelectImage();

                }


            }
        });

        //Si el usuario est?? logueado, se realiza una inicializaci??n de botones y fragment donde se cargaran los post
        if(firebaseAuth.getCurrentUser() != null) {
            mainBottomNav = findViewById(R.id.mainBottomNav);
            addPostButton = findViewById(R.id.add_post_btn);
            profileFragment = new ProfileFragment();


            replaceFragment(profileFragment);



        }


    }
    //Metodo auxiliar que actualiza la info en caso de cambio en el perfil en la colecci??n Users, si algo sale bien se notifica y se redirige a la Actividad Principal y si algo sale mal se notifica el error
    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name) {
        Uri download_uri;
        if(task != null){
            download_uri = task.getResult().getDownloadUrl();
        }else {
            download_uri = urlImage;
        }

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("image", download_uri.toString());
        firebaseFirestore.collection("Users").document(idUsuario).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Toast.makeText(SetupActivity.this, "Se han modificado los ajustes de su usuario correctamente", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(SetupActivity.this, LandingPage.class);
                    startActivity(mainIntent);
                    finish();

                }else{
                    Toast.makeText(SetupActivity.this, "Error de base de datos"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                profileProgressBar.setVisibility(View.INVISIBLE);
            }
        });

    }
    //Seleccionador de imagen con recorte (se muestra una cuadricula de guia y se indica un ratio de forma cuadrada) cuando se selecciona de redirecciona a la Actividad de personalizar perfil
    private void SelectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetupActivity.this);
    }

    //Si la imagen se ha recortado bien, se recibe un codigo 2XX y se hace un set del elemento XML que contiene la imagen, en caso contrario, se notifica el error.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                urlImage = result.getUri();
                profileImage.setImageURI(urlImage);
                isChanged=true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    //Gesti??n del fragment
    private void replaceFragment(Fragment fragment){

        //Inicializaci??n de un FragmentTransaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //Replace the fragment by given fragment which was passed as arguement
        //Remplaza un fragment por el indicado
        fragmentTransaction.replace(R.id.main_content_fragment,fragment);
        //Necesario para que funcione correctamente
        fragmentTransaction.commit();

    }
}
