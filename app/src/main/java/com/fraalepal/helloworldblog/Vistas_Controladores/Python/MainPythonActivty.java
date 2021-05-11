package com.fraalepal.helloworldblog.Vistas_Controladores.Python;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.fraalepal.helloworldblog.Perfil.LoginActivity;
import com.fraalepal.helloworldblog.Perfil.SetupActivity;
import com.fraalepal.helloworldblog.R;

public class MainPythonActivty extends AppCompatActivity {

    //Declaración de una Toolbar, Auth de Firebase, el Storage de Firebase y elementos
    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String idUsuario;
    private FloatingActionButton addPostButton;
    private BottomNavigationView mainBottomNav;
    private PythonFragment javaFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_main);

        //Inicializamos con una instancia la Autentificación y Storage de Firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        //Si el usuario está logueado, se realiza una inicialización de botones y fragment donde se cargaran los post
        if(mAuth.getCurrentUser() != null) {
            mainBottomNav = findViewById(R.id.mainBottomNav);
            addPostButton = findViewById(R.id.add_post_btn);
            javaFragment = new PythonFragment();

            replaceFragment(javaFragment);

            //Al clickar en el botón de post, nos redirecciona a la vista de nuevo post
            addPostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainPythonActivty.this, PostPythonActivity.class));
                }
            });
        }

    }


    //Al iniciar la app, comprobamos que el usuario está logueado, si lo está cargamos sus datos y si no lo está, lo redireccionamos al login
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
        if (usuarioActual == null) {
            goToLoginActivity();

        } else {
            idUsuario = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(idUsuario).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            Intent setupIntent = new Intent(MainPythonActivty.this, SetupActivity.class);
                            startActivity(setupIntent);
                        }
                    }
                    else{
                        String error = task.getException().getMessage();
                        Toast.makeText(MainPythonActivty.this, "Error"+ error, Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }
    }


    private void goToLoginActivity() {
        Intent intent = new Intent(MainPythonActivty.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //Gestión del fragment
    private void replaceFragment(Fragment fragment){

        //Inicialización de un FragmentTransaction
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //Replace the fragment by given fragment which was passed as arguement
        //Remplaza un fragment por el indicado
        fragmentTransaction.replace(R.id.main_content_fragment,fragment);
        //Necesario para que funcione correctamente
        fragmentTransaction.commit();

    }
}
