package com.softloyal.myapplication.Modelo;


import com.softloyal.myapplication.Modelo.PostId;

import java.util.Date;
//Entidad post, la cual es creada por un usuario, esta entidad conformará las bases para la posterior creación de los posts dentro de cada una de las categorias
public class Post extends PostId {

    //Atributos del post
    public String image_url, image_thumb, title, desc, desc2, desc3, user_id;
    public Date timestamp;


    //Creación de un constructor vacio
    public Post() {

    }

    //Constructor con los atributos de Post
    public Post(String image_url, String image_thumb, String title, String desc, String desc2, String desc3, String user_id, Date timestamp) {
        this.image_url = image_url;
        this.image_thumb = image_thumb;
        this.title = title;
        this.desc = desc;
        this.desc2 = desc;
        this.desc3 = desc;
        this.user_id = user_id;
        this.timestamp = timestamp;

    }

    //Getters y Setters de los atributos

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getDesc3() {
        return desc3;
    }

    public void setDesc3(String desc3) {
        this.desc3 = desc3;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
