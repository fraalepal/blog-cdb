package com.softloyal.myapplication.Modelo;

import java.util.Date;

//Entidad comentarios, los cuales seran creados por un usuario
public class Comentario {
    private String message, user_id;
    private Date timestamp;

    //Constructor vacío
    public Comentario(){

    }

    //Constructor con todos los atributos de Comentario
    public Comentario(String message, String user_id, Date timestamp) {
        this.message = message;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }


    //Getters y Setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
