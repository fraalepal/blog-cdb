package com.softloyal.myapplication.Modelo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.softloyal.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//Adaptador de Comentarios, permitirá adaptar el contenido extraido de Firebase y convertirlo en un RecyclerView de Android, estos seran recorridos como lista y adaptados en los XML de plantillas.
public class ComentariosAdapter extends RecyclerView.Adapter<ComentariosAdapter.ViewHolder> {

    //Atributos
    public List<Comentario> comentarioList;
    public Context context;
    private FirebaseFirestore firebaseFirestore; //Se crea una llamada a Firebase para posteriormente poder tratar sus datos.

    //Constructor de los comentarios a adaptar en formato lista
    public ComentariosAdapter(List<Comentario> comentarioList){
        this.comentarioList = comentarioList;
        firebaseFirestore = FirebaseFirestore.getInstance(); //Instancia de la BBDD
    }

    //Metodos propio de los adaptadores de la vista, se nos indica que al renderizar la vista de los comentarios, hay que incializar el layout donde se realizará la adpatación y el contexto.
    //ViewHolder = Como se verá finalmente cada uno de los comentarios de la lista a recorrer.
    @Override
    public ComentariosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
        context = parent.getContext();
        return new ComentariosAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ComentariosAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        //Aqui estamos seleccionando dentro de los elementos a recorrer, los atributos del comentario
        String mensajeComentario = comentarioList.get(position).getMessage();
        holder.setMensajeComentario(mensajeComentario);
        String idUsuario = comentarioList.get(position).getUser_id();

        //Se hace una llamada a la tabla de Users para poder obtener el id de los usuarios y así vincular los comentarios respectivos, en caso de existir guardamos su nombre e imagen para luego
        //colocarlos en la vista (estetica)

        firebaseFirestore.collection("Users").document(idUsuario).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String username = task.getResult().getString("name");
                    String imagenPerfilUsuario = task.getResult().getString("image");
                    holder.setData(username,imagenPerfilUsuario);

                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, "Error en el almacenamiento de la base de datos"+error, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        if(comentarioList != null) {
            return comentarioList.size();
        } else {
            return 0;
        }
    }

    //Aqui es donde se produce la verdadera adaptación del contenido, basicamente se le hace un set() a los elementos del layout(se identifican con el id)
    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView mensajeComentario;
        public TextView usernameComentario;
        public CircleImageView imagenUsuarioComentario;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setMensajeComentario(String mensaje){

            mensajeComentario = mView.findViewById(R.id.comment_message); //Identificamos el elemento donde irá el mensaje en el layout
            mensajeComentario.setText(mensaje); //Le hacemos un set para cambiar su valor

        }


        public  void setData(String name, String image){
            usernameComentario = mView.findViewById(R.id.comment_username);  //Identificamos el elemento donde irá el mensaje en el layout
            imagenUsuarioComentario = mView.findViewById(R.id.comment_image);  //Identificamos el elemento donde irá el mensaje en el layout
            usernameComentario.setText(name);  //Le hacemos un set para cambiar su valor

            RequestOptions placeholderImage =new RequestOptions(); //Indicamos que el lugar donde se encontrarán los comentarios está identificado por un placeholder
            placeholderImage.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderImage).load(image).into(imagenUsuarioComentario);   //Con Glide cambiamos la imagen del placeholder por la del usuario que comenta

        }

    }
}
