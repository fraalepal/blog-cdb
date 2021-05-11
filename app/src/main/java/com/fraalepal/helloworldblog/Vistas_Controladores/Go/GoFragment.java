package com.fraalepal.helloworldblog.Vistas_Controladores.Go;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fraalepal.helloworldblog.Modelo.Post;
import com.fraalepal.helloworldblog.Modelo.PostAdapter;
import com.fraalepal.helloworldblog.R;
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


public class GoFragment extends Fragment {
    private RecyclerView blogListView;
    private List<Post> listado;
    private FirebaseFirestore firebaseFirestore;
    private PostAdapter adaptadorPost;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad= true;

    // Según el curso, es necesario un constructor vacío
    public GoFragment() {

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
                    }

                }
            });

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING);

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
                                String tech = blogPost.getTech();
                                if(isFirstPageFirstLoad) {
                                    if(tech.equals("Go")) {
                                        listado.add(blogPost);
                                    }

                                }else{
                                    if(tech.equals("Go")) {
                                        listado.add(0,blogPost);
                                    }

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


}

