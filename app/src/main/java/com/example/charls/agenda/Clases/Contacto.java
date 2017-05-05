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

    Context c;
    private SQLiteDatabase db;


    ArrayList<Grupo>grupos=new ArrayList<Grupo>();
    ArrayList<Institucion>instituciones=new ArrayList<Institucion>();

    public Contacto(Context context)
    {

        this.c=context;
        agenda_sqlLit agenda =
                new agenda_sqlLit(this.c, "Agenda", null, 1);

        db = agenda.getWritableDatabase();
    }


    public void solicitar_grupos()
    {


        HashMap<String, String> map = new HashMap<>();
        JSONObject jobject = new JSONObject(map);
        String newURL;
        newURL = Constantes.GETContact;
        //+ "?imei=" + telefono + "&" + "u_id=" + cantidad;
        Alerta("" + newURL);
        VolleySingleton.getInstance(this.c).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                obtener_grupos(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //  notificar("Error en la conexion");
                                Alerta("Error Volley: " + error.getMessage());
                                //  guardarMeta(2);
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("dataType", "application/json");
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }


    public void solicitar_instituciones()
    {


        HashMap<String, String> map = new HashMap<>();
        JSONObject jobject = new JSONObject(map);
        String newURL;
        newURL = Constantes.GETInst;
        //+ "?imei=" + telefono + "&" + "u_id=" + cantidad;
        Alerta("" + newURL);
        VolleySingleton.getInstance(this.c).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        newURL,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                obtener_instituciones(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //  notificar("Error en la conexion");
                                Alerta("Error Volley: " + error.getMessage());
                                //  guardarMeta(2);
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("dataType", "application/json");
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }

    private void obtener_instituciones(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");


            if(!estado.equals("1")) {
                Alerta("Tenemos error de sincronizacion!");
            }
            else
            {
                int cantidad = response.getInt("cantidad");
                if(cantidad==0) {
                    Alerta("No hay registros nuevos!");
                } else
                {
                    String cad1, cad2;

                    //  int id[] = new int[cantidad];

                    int id_institucion[] = new int[cantidad];

                    String nombre[] = new String[cantidad];


                    for (int i = 0; i < cantidad; i++)
                    {
                        cad1 = "id_institucion" + (i);
                        cad2 = "nombre" + (i);

                        id_institucion[i]=response.getInt(cad1);
                        nombre[i]=response.getString(cad2);


                        Alerta("El registro es "+nombre[i]);
                        //  notificar(id[i] + "-" + actividad[i] + "-" + fecha_inicio[i] + "-" + fecha_fin[i] + "-" + arto[i] + "-" + cargo[i]);
                        Institucion institucion = new Institucion();
                        institucion.setIdInstitucion(id_institucion[i]);
                        // contacto.setInstitucion(id_institucion);
                        institucion.setNombre(nombre[i]);

                        this.setInstituciones(institucion);//se agrega el objeto al arreglo...
                    }
                    //     this.muestra_interfaz(this.contactos);
                    sincronizarInstituciones(instituciones,this.c);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void obtener_grupos(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");


            if(!estado.equals("1")) {
                Alerta("Tenemos error de sincronizacion!");
            }
            else
            {
                int cantidad = response.getInt("cantidad");
                if(cantidad==0) {
                    Alerta("No hay registros nuevos!");
                } else
                {
                    String cad1, cad2;

                    //  int id[] = new int[cantidad];

                    int id_grupo[] = new int[cantidad];

                    String nombre[] = new String[cantidad];


                    for (int i = 0; i < cantidad; i++)
                    {
                        cad1 = "id_grupo" + (i);
                        cad2 = "nombre" + (i);

                        id_grupo[i]=response.getInt(cad1);
                        nombre[i]=response.getString(cad2);


                        Alerta("El registro es"+nombre[i]);
                        //  notificar(id[i] + "-" + actividad[i] + "-" + fecha_inicio[i] + "-" + fecha_fin[i] + "-" + arto[i] + "-" + cargo[i]);
                        Grupo grupo = new Grupo();
                        grupo.setIdGrupo(id_grupo[i]);
                        // contacto.setInstitucion(id_institucion);
                        grupo.setNombre(nombre[i]);

                        this.setGrupos(grupo);//se agrega el objeto al arreglo...
                    }
                    //     this.muestra_interfaz(this.contactos);
                    sincronizar(grupos,this.c);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void sincronizar(ArrayList<Grupo>grupos,Context c)
    {

        Contacto.ThreadSync tarea = new Contacto.ThreadSync(c);
        tarea.setGrupos(grupos);
        tarea.start();

        Contacto.ThreadLectura tl = new Contacto.ThreadLectura(c);
        tl.start();

    }

    private void sincronizarInstituciones(ArrayList<Institucion>instituciones,Context c)
    {

        Contacto.ThreadSyncInst tarea = new Contacto.ThreadSyncInst(c);
        tarea.setInstituciones(instituciones);
        tarea.start();



    }

    public void eliminar_registros()
    {
        db.delete("Grupo", "1=" + 1 + "", null);

        Alerta("Eliminando");
    }

    public void eliminarInstitucion()
    {
        db.delete("Institucion", "1=" + 1 + "", null);

        Alerta("Eliminando");
    }

    public boolean insertarGrupo (int idGrupo, String nombre)
    {


        ContentValues grupoValues = new ContentValues();
        grupoValues.put("id_grupo", idGrupo);
        grupoValues.put("nombre", nombre);



        db.insert("Grupo", null, grupoValues);

        Alerta("Registro guardado satisfactoriamente");



        return true;
    }

    public boolean insertarInstitucion (int idInstitucion, String nombre)
    {


        ContentValues InstitucionValues = new ContentValues();
        InstitucionValues.put("id_institucion", idInstitucion);
        InstitucionValues.put("nombre", nombre);



        db.insert("Institucion", null, InstitucionValues);

        Alerta("Registro guardado satisfactoriamente");



        return true;
    }

    class ThreadSync extends Thread {
        ArrayList<Grupo>grupos2;
        public void setGrupos(ArrayList<Grupo>gr)
        {
            grupos2=gr;
        }

        Context c;

        public ThreadSync(Context context){
            c=context;
        }

        @Override
        public void run() {

            eliminar_registros();
            for (Grupo g : grupos2)
            {
                insertarGrupo(g.getIdGrupo(),g.getNombre());
                Alerta("Se insertó "+g.getIdGrupo()+" "+g.getNombre());
            }
           // Contacto.ThreadLectura tl = new Contacto.ThreadLectura(this.c);
          //  tl.start();

        }
    }


    class ThreadSyncInst extends Thread {
        ArrayList<Institucion>instituciones2;
        public void setInstituciones(ArrayList<Institucion>gr)
        {
            instituciones2=gr;
        }

        Context c;

        public ThreadSyncInst(Context context){
            c=context;
        }

        @Override
        public void run() {

            eliminar_registros();
            for (Institucion i : instituciones2)
            {
                insertarInstitucion(i.getIdInstitucion(),i.getNombre());
                Alerta("Se insertó "+i.getIdInstitucion()+" "+i.getNombre());
            }
         //  Contacto.ThreadLecturaInstitucion tl = new Contacto.ThreadLecturaInstitucion(c);
       // tl.start();

        }
    }







    class ThreadLectura extends Thread {
        ArrayList<Grupo>grupos2;
        Context c;

        public ThreadLectura(Context context){
            c=context;
        }

        public int cantidad_registros()
        {
            Cursor a = db.rawQuery("select id_grupo, nombre from Grupo", null);
            int i=0;
            if (a.moveToFirst())
            {
                do
                {
                    i++;
                } while (a.moveToNext());
            }
            Alerta("la pinche i "+i);
            return i;
        }
        @Override


        public void run() {

            Alerta("ENTRO EN LA JUGADA");

            if(cantidad_registros()==0)
            {
                Alerta("NO HAY NI UNO");
                //layout.removeAllViews();
            }
            else
            {
                Alerta("ESTAMOS TUANIS");
                grupos2=new ArrayList<Grupo>();

                Cursor a = db.rawQuery("select id_grupo, nombre from Grupo", null);
                //      Alerta("-----------------------------------"+"select id , actividad , fecha_in , fecha_fin , arto , cargo  from Agenda where actividad like '%" + palabra + "%'");
                int i=0;
                if (a.moveToFirst())
                {
                    do {

                        Grupo grupo = new Grupo();
                        grupo.setIdGrupo(a.getInt(0));
                        // contacto.setInstitucion(id_institucion);
                        grupo.setNombre(a.getString(1));

                        grupos2.add(grupo);



                        //  Alerta("EL CARGO ES ///////////// " + car[i]);

                        //  i++;
                    } while (a.moveToNext());


                }

                mostrarGrupos(grupos2);
            }
/*

 */


        }
        public void mostrarGrupos(ArrayList<Grupo>grupos2){
            for(Grupo g:grupos2){
                Alerta("desde la bd "+g.getNombre());
            }

        }
    }




    class ThreadLecturaInstitucion extends Thread {
        ArrayList<Institucion>institucion2;
        Context c;

        public ThreadLecturaInstitucion(Context context){
            c=context;
        }

        public int cantidad_registros()
        {
            Cursor a = db.rawQuery("select id_institucion, nombre from Institucion", null);
            int i=0;
            if (a.moveToFirst())
            {
                do
                {
                    i++;
                } while (a.moveToNext());
            }
            Alerta("la pinche i "+i);
            return i;
        }
        @Override


        public void run() {

            Alerta("ENTRO EN LA JUGADA");

            if(cantidad_registros()==0)
            {
                Alerta("NO HAY NI UNO");
                //layout.removeAllViews();
            }
            else
            {
                Alerta("ESTAMOS TUANIS");
                institucion2=new ArrayList<Institucion>();

                Cursor a = db.rawQuery("select id_institucion, nombre from Institucion", null);
                //      Alerta("-----------------------------------"+"select id , actividad , fecha_in , fecha_fin , arto , cargo  from Agenda where actividad like '%" + palabra + "%'");
                int i=0;
                if (a.moveToFirst())
                {
                    do {

                        Institucion institucion = new Institucion();
                        institucion.setIdInstitucion(a.getInt(0));
                        // contacto.setInstitucion(id_institucion);
                        institucion.setNombre(a.getString(1));

                        institucion2.add(institucion);



                        //  Alerta("EL CARGO ES ///////////// " + car[i]);

                        //  i++;
                    } while (a.moveToNext());


                }

                mostrarInstitucion(institucion2);
            }
/*

 */


        }
        public void mostrarInstitucion(ArrayList<Institucion>institucion2){
            for(Institucion i:institucion2){
                Alerta("desde la bd "+i.getNombre());
            }

        }
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



    public ArrayList<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(Grupo grupo) {//añadir un grupo
        this.grupos.add(grupo);
    }
    public ArrayList<Institucion> getInstituciones() {
        return instituciones;
    }

    public void setInstituciones(Institucion institucion) {//añadir un grupo
        this.instituciones.add(institucion);
    }



    private void Alerta(String mensaje) {
        Log.i("Información", mensaje);
    }


}
