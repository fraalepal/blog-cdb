package com.softloyal.myapplication.Vistas_Controladores.Java;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softloyal.myapplication.Modelo.Post;
import com.softloyal.myapplication.Modelo.PostAdapter;
import com.softloyal.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class JavaFragment extends Fragment {
    private RecyclerView blogListView;
    private List<Post> listado;
    private FirebaseFirestore firebaseFirestore;
    private PostAdapter adaptadorPost;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad= true;

    // Según el curso, es necesario un constructor vacío
    public JavaFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);



        listado = new ArrayList<>();
        blogListView = view.findViewById(R.id.blog_list_view);

        firebaseAuth = FirebaseAuth.getInstance();

        adaptadorPost = new PostAdapter(listado);
        blogListView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));
        blogListView.setAdapter(adaptadorPost);

        if(firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            blogListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);
                    if(reachedBottom){
                        String desc = lastVisible.getString("desc");
                          loadMorePost();
                    }

                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING).limit(3);

            firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if(isFirstPageFirstLoad) {

                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    }

                    if (documentSnapshots != null) {

                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();

                                Post blogPost = doc.getDocument().toObject(Post.class).withId(blogPostId);
                                if(isFirstPageFirstLoad) {
                                    listado.add(blogPost);
                                }else{
                                    listado.add(0,blogPost);
                                }
                                adaptadorPost.notifyDataSetChanged();

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
    //Se mandan querys para evitar cargar todos los posts del tirón
    public void loadMorePost(){
        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        nextQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size()-1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            String blogPostId = doc.getDocument().getId();

                            Post blogPost = doc.getDocument().toObject(Post.class).withId(blogPostId);
                            listado.add(blogPost);
                            adaptadorPost.notifyDataSetChanged();

                        }
                    }
                }

            }
        });

    }

}