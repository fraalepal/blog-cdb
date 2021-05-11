package com.fraalepal.helloworldblog.Modelo;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fraalepal.helloworldblog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.fraalepal.helloworldblog.Vistas_Controladores.Java.CommentsJavaActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public List<Post> blog_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    public Context context;

    //Constructor de los posts a adaptar en formato lista
    public PostAdapter(List<Post> blog_list){
        this.blog_list= blog_list;
    }

    //Metodos propio de los adaptadores de la vista, se nos indica que al renderizar la vista de los posts, hay que incializar el layout donde se realizará la adpatación y el contexto.
    //ViewHolder = Como se verá finalmente cada uno de los posts de la lista a recorrer.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_blog_post_item,viewGroup,false);
        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();//Se define una instancia que llama a la BBDD
        firebaseAuth = FirebaseAuth.getInstance();//Se define una instancia que servira para realizar el proceso de comprobación de logueo para poder postear.

        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.setIsRecyclable(false);

        //Se declaran estas dos variables como estaticas para que puedan ser accesibles desde otros métodos y no queden fuera de las iteraciones
        final String blogPostId = blog_list.get(i).BlogPostId;
        final String currentUserId = firebaseAuth.getCurrentUser().getUid();

        //Aqui estamos seleccionando dentro de los elementos a recorrer, los atributos del post
        String title = blog_list.get(i).getTitle();
        String tech = "#"+blog_list.get(i).getTech();
        String desc_data = blog_list.get(i).getDesc();
        viewHolder.setTitle(title);
        viewHolder.setTech(tech);
        viewHolder.setDescText(desc_data);
        String image_uri = blog_list.get(i).getImage_url();
        String thumbUri = blog_list.get(i).getImage_thumb();
        viewHolder.setBlogImage(image_uri,thumbUri);

        String user_id = blog_list.get(i).getUser_id();


        //Se hace una llamada a la tabla de Users para poder obtener los datos del usuario, en caso de existir guardamos su nombre e imagen para luego
        //colocarlos en la vista (estetica)
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    viewHolder.setData(userName,userImage);

                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, "Error en el almacenamiento de la Base de Datos"+error, Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Se hace una llamada a la tabla de Posts para poder obtener los likes de cada post, en caso de existir colocamos el número total en la vista (estetica)
        firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots != null) {
                    if (!documentSnapshots.isEmpty()) {

                        viewHolder.updateLikesCount(documentSnapshots.size() + "");
                    } else {

                        viewHolder.updateLikesCount("0");
                    }
                }
            }
        });

        //Se hace una llamada a la tabla de Posts para poder obtener los comentarios de cada post, en caso de existir colocamos el número total en la vista (estetica)
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comentario").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (documentSnapshots != null) {
                    if (!documentSnapshots.isEmpty()) {

                        viewHolder.updateCommentsCount(documentSnapshots.size() + "");
                    } else {

                        viewHolder.updateCommentsCount("0");
                    }
                }
            }
        });

        //obtenemos los likes de la colección
        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot!= null) {
                    if (documentSnapshot.exists()) {

                        viewHolder.blogLikeBtn.setImageResource(R.drawable.like_accent);
                    } else {
                        viewHolder.blogLikeBtn.setImageResource(R.drawable.like_grey);
                    }
                }
            }
        });

        //funcionalidad de likes
        viewHolder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).set(likesMap);
                        }
                        else {
                            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).delete();

                        }
                    }
                });


            }
        });

        //funcionalidad de comentarios
        viewHolder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsJavaActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);

            }
        });



    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View mView;
        public TextView descView;
        public ImageView blogImageView;
        public TextView blogDate;
        public TextView blogUserName;
        public TextView titleView;
        public TextView techView;
        public CircleImageView blogUserImage;
        public ImageView blogLikeBtn;
        public TextView blogLikeCount, blogCommentCount;
        private ImageView blogCommentBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);
            blogLikeCount = mView.findViewById(R.id.blog_like_counter);
            blogCommentBtn = mView.findViewById(R.id.blog_comment_icon);



        }

        public void setTitle(String title){
            titleView= mView.findViewById(R.id.blog_title);
            titleView.setText(title);
        }

        public void setTech(String technology){
            techView= mView.findViewById(R.id.techo);
            techView.setText(technology);
        }

        public void setDescText(String descText){
            descView= mView.findViewById(R.id.blog_desc);
            descView.setText(descText);
        }




        public void setBlogImage(String downloadUri, String thumbUri) {

            blogImageView = mView.findViewById(R.id.blog_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);

        }

        public  void setData(String name, String image){
            blogUserName = mView.findViewById(R.id.blog_user_name);
            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName.setText(name);

            RequestOptions placeholderOption =new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);

        }



        public void updateLikesCount(String count) {
            blogLikeCount = mView.findViewById(R.id.blog_like_counter);
            blogLikeCount.setText(count + " Me gusta");
        }
        public void updateCommentsCount(String count) {
            blogCommentCount = mView.findViewById(R.id.blog_comment_count);
            blogCommentCount.setText(count + " comentarios");
        }
    }
}
