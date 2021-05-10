package com.fraalepal.helloworldblog.Vistas_Controladores.Java;

import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fraalepal.helloworldblog.Modelo.Comentario;
import com.fraalepal.helloworldblog.Modelo.ComentarioAdapter;
import com.fraalepal.helloworldblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsJavaActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText textoComentario;
    private ImageView comentarioPostButton;

    private RecyclerView recyclerViewComentarios;
    private ComentarioAdapter comentarioAdapter;
    private List<Comentario> comentarioList;
    private DividerItemDecoration dividerItemDecoration;
    private LinearLayoutManager linearLayoutManager;



    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private String postId;
    private String usuarioActualID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        toolbar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comentarios");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        usuarioActualID = firebaseAuth.getCurrentUser().getUid();
        postId = getIntent().getStringExtra("blog_post_id");

        textoComentario = findViewById(R.id.comment_field);
        comentarioPostButton = findViewById(R.id.comment_post_btn);
        recyclerViewComentarios = findViewById(R.id.comment_list);

        //RecyclerView a partir de listado cargado con los documentos de firebase
        comentarioList = new ArrayList<>();
        comentarioAdapter = new ComentarioAdapter(comentarioList);
        recyclerViewComentarios.setHasFixedSize(true);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(recyclerViewComentarios.getContext(), linearLayoutManager.getOrientation());
        recyclerViewComentarios.setAdapter(comentarioAdapter);


        //Es como el add de un post pero adaptado a los comentarios

        firebaseFirestore.collection("Posts/" + postId + "/Comentario")
                .addSnapshotListener(CommentsJavaActivity.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (!documentSnapshots.isEmpty()) {

                            for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String commentId = doc.getDocument().getId();
                                    Comentario comentario = doc.getDocument().toObject(Comentario.class);
                                    comentarioList.add(comentario);
                                    comentarioAdapter.notifyDataSetChanged();


                                }
                            }

                        }

                    }
                });

        //Al hacer click en guardar se crea el Map y se añade a la colección, comprobando los posibles errores.
        comentarioPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_message = textoComentario.getText().toString();


                Map<String, Object> commentsMap = new HashMap<>();
                commentsMap.put("message", comment_message);
                commentsMap.put("user_id", usuarioActualID);
                commentsMap.put("timestamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection("Posts/" + postId + "/Comentario").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if(!task.isSuccessful()){

                            Toast.makeText(CommentsJavaActivity.this, "Error al comentar : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        } else {

                            textoComentario.setText("");

                        }

                    }
                });

            }
        });

    }
}
