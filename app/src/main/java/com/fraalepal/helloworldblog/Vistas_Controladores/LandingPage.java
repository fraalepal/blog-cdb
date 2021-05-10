package com.fraalepal.helloworldblog.Vistas_Controladores;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    public Context context;

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

    private void goToLoginActivity() {
        Intent intent = new Intent(LandingPage.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }





}
