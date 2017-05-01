package com.example.charls.agenda.Clases;

import java.util.ArrayList;

/**
 * Created by cvande on 27/4/2017.
 */

public class Contacto {
    private int idContacto;
    private String nombre;
    private String apellido;
    private String claro;
    private String movistar;
    private String cootel;
    private String casa;
    private String trabajo;
    private String correo1;
    private String correo2;
    private String apodo;
    private String foto;
    private Institucion institucion=new Institucion();


    ArrayList<Grupo>grupos=new ArrayList<Grupo>();

    public int getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(int idContacto) {
        this.idContacto = idContacto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getClaro() {
        return claro;
    }

    public void setClaro(String claro) {
        this.claro = claro;
    }

    public String getMovistar() {
        return movistar;
    }

    public void setMovistar(String movistar) {
        this.movistar = movistar;
    }

    public String getCootel() {
        return cootel;
    }

    public void setCootel(String cootel) {
        this.cootel = cootel;
    }

    public String getCasa() {
        return casa;
    }

    public void setCasa(String casa) {
        this.casa = casa;
    }

    public String getTrabajo() {
        return trabajo;
    }

    public void setTrabajo(String trabajo) {
        this.trabajo = trabajo;
    }

    public String getCorreo1() {
        return correo1;
    }

    public void setCorreo1(String correo1) {
        this.correo1 = correo1;
    }

    public String getCorreo2() {
        return correo2;
    }

    public void setCorreo2(String correo2) {
        this.correo2 = correo2;
    }

    public String getApodo() {
        return apodo;
    }

    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Institucion getInstitucion() {
        return institucion;
    }

    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
    }

    public ArrayList<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(Grupo grupo) {//añadir un grupo
        this.grupos.add(grupo);
    }


}
