package com.fraalepal.helloworldblog.Vistas_Controladores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fraalepal.helloworldblog.Perfil.ProfileFragment;
import com.fraalepal.helloworldblog.Perfil.SetupActivity;
import com.fraalepal.helloworldblog.Vistas_Controladores.Go.MainGoActivty;
import com.fraalepal.helloworldblog.Vistas_Controladores.Java.JavaFragment;
import com.fraalepal.helloworldblog.Vistas_Controladores.Java.PostJavaActivity;
import com.fraalepal.helloworldblog.Vistas_Controladores.JavaScript.MainJavascriptActivty;
import com.fraalepal.helloworldblog.Vistas_Controladores.TypeScript.MainTypescriptActivty;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.fraalepal.helloworldblog.Perfil.LoginActivity;
import com.fraalepal.helloworldblog.R;
import com.fraalepal.helloworldblog.Vistas_Controladores.Java.MainJavaActivty;
import com.fraalepal.helloworldblog.Vistas_Controladores.Python.MainPythonActivty;


import de.hdodenhof.circleimageview.CircleImageView;


public class LandingPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private CircleImageView imageUser;
    private TextView name;
    private CircleImageView profileimage;
    public Context context;
    TextView header;
    private Toolbar mainToolbar;

    private String idUsuario;
    private BottomNavigationView mainBottomNav;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.landing_page);

        //Inicializamos con una instancia la Autentificación y Storage de Firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        //Si el usuario está logueado, se realiza una inicialización de botones y fragment donde se cargaran los post
        if (mAuth.getCurrentUser() != null) {
            name = findViewById(R.id.landingName);


        }

        mainToolbar = findViewById(R.id.main_toolbar_main);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("");

        if(mAuth.getCurrentUser() != null) {
            mainBottomNav = findViewById(R.id.mainBottomNavMain);

            //Cuando un user hace click en un elemento del menu, se ejecutarán las acciones correspondientes según la ID del elemento clickado
            mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        default:
                            return false;
                    }

                }
            });

        }

        CardView java = findViewById (R.id.javaCardView);
        java.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent javaActivity = new Intent(LandingPage.this, MainJavaActivty.class);
                startActivity(javaActivity);
            }
        });

        CardView python = findViewById (R.id.pythonCardView);
        python.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pythonActivity = new Intent(LandingPage.this, MainPythonActivty.class);
                startActivity(pythonActivity);
            }
        });

        CardView javascript = findViewById (R.id.javascriptCardView);
        javascript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent javascriptActivity = new Intent(LandingPage.this, MainJavascriptActivty.class);
                startActivity(javascriptActivity);
            }
        });

        CardView typescript = findViewById (R.id.typescriptCardView);
        typescript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent typescriptActivity = new Intent(LandingPage.this, MainTypescriptActivty.class);
                startActivity(typescriptActivity);
            }
        });

        CardView go = findViewById (R.id.goCardView);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goActivity = new Intent(LandingPage.this, MainGoActivty.class);
                startActivity(goActivity);
            }
        });


    }

    private void loadprofileImage(Uri uri){
        Glide.with(LandingPage.this)
                .applyDefaultRequestOptions(new RequestOptions()
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .load(uri)
                .into(profileimage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioActual == null) {
            goToLoginActivity();

        } else {

            final String idUsuario = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(idUsuario).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                            String userName = task.getResult().getString("name");
                            name.setText(userName);

                    }
                    else{
                        String error = task.getException().getMessage();
                        Toast.makeText(LandingPage.this, "Error"+ error, Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
    }

    //Conecta el menú XML con la clase menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //Gestión de las opciones del menú (logout y perfil)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout_btn:
                logOut(); //Cierra sesión
                return true;
            case R.id.action_settings_btn:
                Intent settingIntent = new Intent(LandingPage.this, SetupActivity.class);
                startActivity(settingIntent); //Redirección a actividad de personalización de perfil
                return true;
            default:
                return false;

        }

    }

    //Llamamos con la autentificación del usuario a cerrar nuestra sesión y nos redireccionamos al login
    private void logOut() {
        mAuth.signOut();
        goToLoginActivity();
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(LandingPage.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }





}
