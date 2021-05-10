package com.fraalepal.helloworldblog.Perfil;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.fraalepal.helloworldblog.Modelo.MyPostAdapter;
import com.fraalepal.helloworldblog.Modelo.Post;
import com.fraalepal.helloworldblog.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ProfileFragment extends Fragment {
    private RecyclerView blogListView;
    private List<Post> listado;
    private FirebaseFirestore firebaseFirestore;
    private MyPostAdapter adaptadorPost;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private String idUsuario;
    private Button button;
    private TextView emptyView;

    // Según el curso, es necesario un constructor vacío
    public ProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_post, container, false);

        View viewDetails = inflater.inflate(R.layout.activity_blog_my_posts_item, container, false);


        listado = new ArrayList<>();

        blogListView = view.findViewById(R.id.blog_list_view);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        button = viewDetails.findViewById(R.id.deleteMyPostButton);
        firebaseAuth = FirebaseAuth.getInstance();
        idUsuario = firebaseAuth.getCurrentUser().getUid();
        adaptadorPost = new MyPostAdapter(listado);
        blogListView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        /*
        int count = 0;
        if (adaptadorPost != null) {
            count = adaptadorPost.getItemCount();
            if (count == 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                blogListView.setVisibility(View.VISIBLE);
            }
        }*/

            blogListView.setAdapter(adaptadorPost);


            if (firebaseAuth.getCurrentUser() != null) {

                firebaseFirestore = FirebaseFirestore.getInstance();


                Query firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("user_id", idUsuario).orderBy("timestamp", Query.Direction.DESCENDING);


                firstQuery.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {

                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                        if (documentSnapshots != null) {

                            for (final DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                                if (doc.getType() == DocumentChange.Type.ADDED) {

                                    String blogPostId = doc.getDocument().getId();

                                    Post blogPost = doc.getDocument().toObject(Post.class).withId(blogPostId);
                                    if (isFirstPageFirstLoad) {
                                        listado.add(blogPost);
                                    } else {
                                        listado.add(0, blogPost);
                                    }
                                    adaptadorPost.notifyDataSetChanged();


                                    button.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View v) {
                                            firebaseFirestore.collection("Java").document(doc.getDocument().getId())
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error deleting document", e);
                                                        }
                                                    });
                                        }
                                    });

                                }
                            }
                            isFirstPageFirstLoad = false;


                        }

                    }
                });

            }

            // Se han ido cargado los posts y los datos asociados al mismo.
            return view;
        }


}




