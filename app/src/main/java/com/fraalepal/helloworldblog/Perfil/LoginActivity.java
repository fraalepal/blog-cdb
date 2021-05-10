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

import com.fraalepal.helloworldblog.Vistas_Controladores.LandingPage;
import com.fraalepal.helloworldblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    //Elementos que se encuentran en la plantilla XML
    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private Button loginRegisterButton;
    private ProgressBar loginProgressBar;

    //FireBaseAuth servirá para crear una instancia de la autentificación
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Instanciamos la autentificación
        mAuth = FirebaseAuth.getInstance();
        //Indicamos cual es la plantilla asociada a esta Actividad
        setContentView(R.layout.activity_login);

        //Elementos que se encuentran en la plantilla XML relacionados por la id
        loginEmail = findViewById(R.id.reg_email);
        loginPassword = findViewById(R.id.reg_confirm_password);
        loginButton = findViewById(R.id.login_btn);
        loginRegisterButton = findViewById(R.id.login_reg_btn);
        loginProgressBar = findViewById(R.id.login_progress);

        //Al clickar en el boton de login se coge el email y la pass, se validan y si no existen problemas, se inicia sesión con Firebase (signInWithEmailAndPassword) y se redirecciona a la Actividad Principal
        //En caso de problema se muestra un mensaje de excepción
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmail = LoginActivity.this.loginEmail.getText().toString().trim();
                String loginPass = loginPassword.getText().toString().trim();
                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)) {
                    //Una barrita de progreso de la operación
                    loginProgressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        //On complete and there are two possibilities success or failure
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                goToMainActivity();
                            }
                            else {
                                Toast.makeText(LoginActivity.this,
                                        task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            //Una vez terminada la tarea se pone la barrita de progreso invisible
                            loginProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });

                }else{
                    Toast.makeText(LoginActivity.this, "El Email o la contraseña no pueden estar vacíos", Toast.LENGTH_SHORT).show();
                if(TextUtils.isEmpty(loginEmail)){
                    LoginActivity.this.loginEmail.getError();
                }else if(TextUtils.isEmpty(loginPass)){
                    loginPassword.getError();
                }else{
                    LoginActivity.this.loginEmail.getError();
                    loginPassword.getError();
                }
                }

            }
        });

        // Si le damos al boton de "Aún no tengo cuenta" te manda a registrarte
        loginRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(regIntent);
            }
        });


    }


    //Esto es importante, nos comprueba cuando iniciamos la app si estamos logueados, si lo estamos nos manda directamente a la Actividad Principal sin necesidad de reloguear
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity();
        }
    }

    //Intent para ir desde la Actividad de Login hasta la Actividad Principal
    private void goToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, LandingPage.class);
        startActivity(mainIntent);
        finish();
    }
}
