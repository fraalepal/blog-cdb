package com.fraalepal.helloworldblog.Perfil;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fraalepal.helloworldblog.R;
import com.fraalepal.helloworldblog.Vistas_Controladores.Java.MainJavaActivty;
import com.fraalepal.helloworldblog.Vistas_Controladores.LandingPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    //Elementos que se encuentran en la plantilla XML
    private Button registerButton, loginButton;

    //FireBaseAuth servirá para crear una instancia de la autentificación
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Indicamos cual es la plantilla asociada a esta Actividad
        setContentView(R.layout.welcome_activity);
        //Instanciamos la autentificación
        mAuth = FirebaseAuth.getInstance();

        //Elementos que se encuentran en la plantilla XML relacionados por la id

        registerButton = findViewById(R.id.welcomeRegister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        loginButton = findViewById(R.id.welcomeLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });


    }

    //Comprobar que el usuario está autentificado, si lo está lo manda para la Actividad Principal
    @Override
    protected void onStart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity();
        }

        super.onStart();
    }


    //Manda a un usuario a la actividad principal
    private void goToMainActivity() {
        Intent mainIntent = new Intent(WelcomeActivity.this, LandingPage.class);
        startActivity(mainIntent);

        finish();

    }
}