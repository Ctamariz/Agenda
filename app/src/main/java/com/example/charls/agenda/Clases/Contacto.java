package com.example.charls.agenda.Clases;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.charls.agenda.SQL.agenda_sqlLit;
import com.example.charls.agenda.web.Constantes;
import com.example.charls.agenda.web.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private Institucion institucion;
    ArrayList<Grupo> grupos=new ArrayList<Grupo>();




    Context c;
    private SQLiteDatabase db;



    public Contacto(Context context)
    {

        this.c=context;
        agenda_sqlLit agenda =
                new agenda_sqlLit(this.c, "Agenda", null, 1);

        db = agenda.getWritableDatabase();
    }


    public ArrayList<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos( ArrayList<Grupo> grupos) {
        this.grupos=grupos;
    }

    public Institucion getInstitucion() {
        return institucion;
    }

    public void setInstitucion(Institucion institucion) {
        this.institucion = institucion;
    }

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







    private void Alerta(String mensaje) {
        Log.i("Información", mensaje);
    }


}
