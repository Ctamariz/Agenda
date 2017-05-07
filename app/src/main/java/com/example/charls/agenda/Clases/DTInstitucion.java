package com.example.charls.agenda.Clases;

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
 * Created by charls on 05-06-17.
 */

public class DTInstitucion {

    ArrayList<Institucion> instituciones = new ArrayList<Institucion>();
    ArrayList<Institucion> institucionesDB = new ArrayList<Institucion>();
    Context c;
    private SQLiteDatabase db;

    public DTInstitucion(Context context) {
        this.c = context;
        agenda_sqlLit agenda = new agenda_sqlLit(this.c, "Agenda", null, 1);
        db = agenda.getWritableDatabase();
    }

    public void solicitar_instituciones() {


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


            if (!estado.equals("1")) {
                Alerta("Tenemos error de sincronizacion!");
            } else {
                int cantidad = response.getInt("cantidad");
                if (cantidad == 0) {
                    Alerta("No hay registros nuevos!");
                } else {
                    String cad1, cad2;

                    //  int id[] = new int[cantidad];

                    int id_institucion[] = new int[cantidad];

                    String nombre[] = new String[cantidad];


                    for (int i = 0; i < cantidad; i++) {
                        cad1 = "id_institucion" + (i);
                        cad2 = "nombre" + (i);

                        id_institucion[i] = response.getInt(cad1);
                        nombre[i] = response.getString(cad2);


                        Alerta("El registro es " + nombre[i]);
                        //  notificar(id[i] + "-" + actividad[i] + "-" + fecha_inicio[i] + "-" + fecha_fin[i] + "-" + arto[i] + "-" + cargo[i]);
                        Institucion institucion = new Institucion();
                        institucion.setIdInstitucion(id_institucion[i]);
                        // contacto.setInstitucion(id_institucion);
                        institucion.setNombre(nombre[i]);

                        this.setInstituciones(institucion);//se agrega el objeto al arreglo...
                    }
                    //     this.muestra_interfaz(this.contactos);
                    sincronizarInstituciones(instituciones, this.c);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }


    private void sincronizarInstituciones(ArrayList<Institucion> instituciones, Context c) {

        ThreadSyncInst tarea = new ThreadSyncInst(c);
        tarea.setInstituciones(instituciones);
        tarea.start();


    }

    public void eliminarInstitucion() {
        db.delete("Institucion", "1=" + 1 + "", null);

        Alerta("Eliminando");
    }

    public boolean insertarInstitucion(int idInstitucion, String nombre) {


        ContentValues InstitucionValues = new ContentValues();
        InstitucionValues.put("id_institucion", idInstitucion);
        InstitucionValues.put("nombre", nombre);


        db.insert("Institucion", null, InstitucionValues);

        Alerta("Registro guardado satisfactoriamente");


        return true;
    }

    class ThreadSyncInst extends Thread {
        ArrayList<Institucion> instituciones2;

        public void setInstituciones(ArrayList<Institucion> gr) {
            instituciones2 = gr;
        }

        Context c;

        public ThreadSyncInst(Context context) {
            c = context;
        }

        @Override
        public void run() {

            eliminarInstitucion();
            for (Institucion i : instituciones2) {
                insertarInstitucion(i.getIdInstitucion(), i.getNombre());
                Alerta("Se insertó " + i.getIdInstitucion() + " " + i.getNombre());
            }
            //  Contacto.ThreadLecturaInstitucion tl = new Contacto.ThreadLecturaInstitucion(c);
            // tl.start();

        }
    }

    class ThreadLecturaInstitucion extends Thread {
        ArrayList<Institucion> institucion2;
        Context c;

        public ThreadLecturaInstitucion(Context context) {
            c = context;
        }

        public int cantidad_registros() {
            Cursor a = db.rawQuery("select id_institucion, nombre from Institucion", null);
            int i = 0;
            if (a.moveToFirst()) {
                do {
                    i++;
                } while (a.moveToNext());
            }
            Alerta("la pinche i " + i);
            return i;
        }

        @Override


        public void run() {

            Alerta("ENTRO EN LA JUGADA");

            if (cantidad_registros() == 0) {
                Alerta("NO HAY NI UNO inst");
                //layout.removeAllViews();
            } else {
                Alerta("ESTAMOS TUANIS");
                institucion2 = new ArrayList<Institucion>();

                Cursor a = db.rawQuery("select id_institucion, nombre from Institucion", null);
                //      Alerta("-----------------------------------"+"select id , actividad , fecha_in , fecha_fin , arto , cargo  from Agenda where actividad like '%" + palabra + "%'");
                int i = 0;
                if (a.moveToFirst()) {
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
                setInstitucionesDB(institucion2);
                mostrarInstitucion(institucion2);
            }
/*

 */


        }

        public void mostrarInstitucion(ArrayList<Institucion> institucion2) {
            for (Institucion i : institucion2) {
                Alerta("desde la bd " + i.getNombre());
            }

        }
    }


    public ArrayList<Institucion> getInstituciones() {
        return instituciones;
    }

    public void setInstituciones(Institucion institucion) {//añadir un grupo
        this.instituciones.add(institucion);
    }

    public ArrayList<Institucion> getInstitucionesDB() {
        return institucionesDB;
    }

    public void setInstitucionesDB(ArrayList<Institucion> institucion) {//
        this.institucionesDB=institucion;
    }

    private void Alerta(String mensaje) {
        Log.i("Información", mensaje);
    }

    public void leerInstituciones(){
        ArrayList<Institucion> institucion2;
                int cantidad_registros=cantidad_registros();

            if (cantidad_registros() == 0) {
                Alerta("NO HAY NI UNO inst");
                //layout.removeAllViews();
            } else {
                Alerta("ESTAMOS TUANIS");
                institucion2 = new ArrayList<Institucion>();

                Cursor a = db.rawQuery("select id_institucion, nombre from Institucion", null);
                //      Alerta("-----------------------------------"+"select id , actividad , fecha_in , fecha_fin , arto , cargo  from Agenda where actividad like '%" + palabra + "%'");
                int i = 0;
                if (a.moveToFirst()) {
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
                setInstitucionesDB(institucion2);
            }


    }

    public int cantidad_registros() {
        Cursor a = db.rawQuery("select id_institucion, nombre from Institucion", null);
        int i = 0;
        if (a.moveToFirst()) {
            do {
                i++;
            } while (a.moveToNext());
        }
        Alerta("la pinche i " + i);
        return i;
    }

}
