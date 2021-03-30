package com.softloyal.myapplication.Vistas_Controladores;

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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.softloyal.myapplication.Vistas_Controladores.Java.JavaFragment;
import com.softloyal.myapplication.Vistas_Controladores.Java.PostJavaActivity;
import com.softloyal.myapplication.Perfil.LoginActivity;
import com.softloyal.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softloyal.myapplication.Perfil.SetupActivity;

public class MainActivity extends AppCompatActivity {

    //Declaración de una Toolbar, Auth de Firebase, el Storage de Firebase y elementos
    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String idUsuario;
    private FloatingActionButton addPostButton;
    private BottomNavigationView mainBottomNav;
    private JavaFragment javaFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_main);

        //Inicializamos con una instancia la Autentificación y Storage de Firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Nombre");

        //Si el usuario está logueado, se realiza una inicialización de botones y fragment donde se cargaran los post
        if(mAuth.getCurrentUser() != null) {
            mainBottomNav = findViewById(R.id.mainBottomNav);
            addPostButton = findViewById(R.id.add_post_btn);
            javaFragment = new JavaFragment();


            replaceFragment(javaFragment);


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

            //Al clickar en el botón de post, nos redirecciona a la vista de nuevo post
            addPostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, PostJavaActivity.class));
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
                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                        }
                    }
                    else{
                        String error = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error"+ error, Toast.LENGTH_SHORT).show();
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
                Intent settingIntent = new Intent(MainActivity.this, SetupActivity.class);
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
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
