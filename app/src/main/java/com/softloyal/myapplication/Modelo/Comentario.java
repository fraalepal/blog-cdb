package com.softloyal.myapplication.Modelo;

import java.util.Date;

//Entidad comentarios, los cuales seran creados por un usuario
public class Comentario {
    private String mensaje, idUsuario;
    private Date fecha;

    //Constructor vacío
    public Comentario(){

    }

    //Constructor con todos los atributos de Comentario
    public Comentario(String mensaje, String idUsuario, Date fecha) {
        this.mensaje = mensaje;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
    }

    //Getters y Setters

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
