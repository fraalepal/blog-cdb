package com.fraalepal.helloworldblog.Modelo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.fraalepal.helloworldblog.R;
import com.fraalepal.helloworldblog.Vistas_Controladores.LandingPage;

import java.util.List;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {

    public List<Post> blog_list;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    public Context context;
    private Button deleteBtn;
    private String currentUserId, blogPostId;

    //Constructor de los posts a adaptar en formato lista
    public MyPostAdapter(List<Post> blog_list){

        this.blog_list= blog_list;
    }

    //Metodos propio de los adaptadores de la vista, se nos indica que al renderizar la vista de los posts, hay que incializar el layout donde se realizará la adpatación y el contexto.
    //ViewHolder = Como se verá finalmente cada uno de los posts de la lista a recorrer.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_blog_my_posts_item,viewGroup,false);
        context = viewGroup.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();//Se define una instancia que llama a la BBDD
        firebaseAuth = FirebaseAuth.getInstance();//Se define una instancia que servira para realizar el proceso de comprobación de logueo para poder postear.


        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.setIsRecyclable(false);

        //Se declaran estas dos variables como estaticas para que puedan ser accesibles desde otros métodos y no queden fuera de las iteraciones
        blogPostId = blog_list.get(i).BlogPostId;
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        //Aqui estamos seleccionando dentro de los elementos a recorrer, los atributos del post
        String title = blog_list.get(i).getTitle();
        String desc_data = blog_list.get(i).getDesc();
        viewHolder.setTitle(title);
        String image_uri = blog_list.get(i).getImage_url();
        String thumbUri = blog_list.get(i).getImage_thumb();
        viewHolder.setBlogImage(image_uri,thumbUri);
        viewHolder.setDeletePost(currentUserId,blogPostId);

        String user_id = blog_list.get(i).getUser_id();
    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View mView;
        public ImageView blogImageView;
        public TextView titleView;
        private Button deleteBtn;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;


        }

        public void setTitle(String title){
            titleView= mView.findViewById(R.id.my_blog_title);
            titleView.setText(title);
        }



        public void setBlogImage(String downloadUri, String thumbUri) {

            blogImageView = mView.findViewById(R.id.my_blog_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(blogImageView);

        }

        public void setDeletePost(final String user, final String post_id){

            deleteBtn = mView.findViewById(R.id.deleteMyPostButton);
            if (currentUserId.equals(user)){
                deleteBtn.setVisibility(View.VISIBLE);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseFirestore.getInstance().collection("Posts").document(post_id).delete();
                        Intent main = new Intent(context, LandingPage.class);
                        context.startActivity(main);
                    }
                });

            }else {
                deleteBtn.setVisibility(View.GONE);
            }

        }





    }

}

