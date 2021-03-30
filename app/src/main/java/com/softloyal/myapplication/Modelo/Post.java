package com.softloyal.myapplication.Modelo;


import java.util.Date;

//Entidad post, la cual es creada por un usuario, esta entidad conformará las bases para la posterior creación de los posts dentro de cada una de las categorias
public class Post extends PostId {

    //Atributos del post
    public String urlImagen, image_thumb, descripcion, idUsuario;
    public Date fechaSubida;


    //Creación de un constructor vacio
    public Post() {

    }

    //Constructor con los atributos de Post
    public Post(String urlImagen, String image_thumb, String descripcion, String idUsuario, Date fechaSubida) {
        this.urlImagen = urlImagen;
        this.image_thumb = image_thumb;
        this.descripcion = descripcion;
        this.idUsuario = idUsuario;
        this.fechaSubida = fechaSubida;

    }


    //Getters y Setters de los atributos

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Date fechaSubida) {
        this.fechaSubida = fechaSubida;
    }
}
