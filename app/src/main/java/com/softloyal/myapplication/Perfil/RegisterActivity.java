package com.softloyal.myapplication.Perfil;

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

import com.softloyal.myapplication.Vistas_Controladores.Java.MainJavaActivty;
import com.softloyal.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    //Elementos que se encuentran en la plantilla XML
    private EditText registerEmail;
    private EditText registerPassword;
    private Button registerButton;
    private EditText registerConfirmPassword;
    private Button registerLoginButton;
    private ProgressBar registerProgressBar;

    //FireBaseAuth servirá para crear una instancia de la autentificación
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Indicamos cual es la plantilla asociada a esta Actividad
        setContentView(R.layout.activity_register);
        //Instanciamos la autentificación
        mAuth = FirebaseAuth.getInstance();

        //Elementos que se encuentran en la plantilla XML relacionados por la id
        registerEmail = findViewById(R.id.reg_email);
        registerPassword = findViewById(R.id.reg_pass_btn);
        registerConfirmPassword = findViewById(R.id.reg_confirm_password);
        registerButton = findViewById(R.id.reg_btn);
        registerLoginButton = findViewById(R.id.reg_login_btn);
        registerProgressBar = findViewById(R.id.reg_progress);

        //Se realiza el envio de datos y se llama a createUserWithEmailAndPassword, si es correcto se redirecciona a la Actividad de Setup para terminar de configurar el perfil y se crea el user correctamente, sino se muestra el error
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = registerEmail.getText().toString().trim();
                String pass = registerPassword.getText().toString().trim();
                String passConf = registerConfirmPassword.getText().toString().trim();

                if(!emailVerify(email)){
                    Toast.makeText(RegisterActivity.this, "El Email no es valido", Toast.LENGTH_SHORT).show();

                }else{
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {
                        if (pass.equals(passConf)) {
                            //Una barrita de progreso de la operación
                            registerProgressBar.setVisibility(View.VISIBLE);

                            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                //On complete and there are two possibilities success or failure
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent setUpIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                        startActivity(setUpIntent);

                                        finish();

                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    registerProgressBar.setVisibility(View.INVISIBLE);

                                }
                            });


                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(RegisterActivity.this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                    }


                }


            }
        });

        // Volver al Login
        registerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    //Validador de email
    private boolean emailVerify(String email){
        int flag=0,f=0;
        for(int i=0;i<email.length();i++){
            if(email.charAt(i) == '@'){
                flag=1;
            } else if(email.charAt(i) == '.' && flag==1){
                flag=2;
            } else if((flag==2 && (int)email.charAt(i) >= 97 && (int)email.charAt(i) <= 122)){
                f=1;
            } else if(f==1){
                return false;
            }
        }
        if(f==1){
            return true;
        } else{
            return false;
        }
    }

    //Manda a un usuario a la actividad principal
    private void goToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainJavaActivty.class);
        startActivity(mainIntent);

        finish();

    }
}
