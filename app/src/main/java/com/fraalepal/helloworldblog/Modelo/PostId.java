package com.fraalepal.helloworldblog.Modelo;

import com.google.firebase.firestore.Exclude;

import io.reactivex.annotations.NonNull;

public class PostId {

    //Necesario para poder asociar una marca de tiempo a los post

    @Exclude
    public String BlogPostId;

    public <T extends PostId> T withId(@NonNull final String id){
        this.BlogPostId = id;
        return (T) this;
    }

}
