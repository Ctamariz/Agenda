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

public class DTGrupoContacto {

    ArrayList<GrupoContacto> gruposContactos=new ArrayList<GrupoContacto>();
    ArrayList<GrupoContacto> gruposContactosDB=new ArrayList<GrupoContacto>();
    Context c;
    private SQLiteDatabase db;

    public DTGrupoContacto(Context context){
        this.c=context;
        agenda_sqlLit agenda = new agenda_sqlLit(this.c, "Agenda", null, 1);

        db = agenda.getWritableDatabase();
    }



    public void solicitarGrupoContacto()
    {


        HashMap<String, String> map = new HashMap<>();
        JSONObject jobject = new JSONObject(map);
        String newURL;
        newURL = Constantes.GETGC;
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
                                obtenerGrupoContacto(response);
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

    private void obtenerGrupoContacto(JSONObject response) {

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
                    String cad1, cad2,cad3;

                    //  int id[] = new int[cantidad];

                    int id_grupoContacto[] = new int[cantidad];
                    int id_grupo[] = new int[cantidad];
                    int id_contacto[] = new int[cantidad];




                    for (int i = 0; i < cantidad; i++)
                    {
                        cad1 = "id_grupoContacto" + (i);
                        cad2 = "id_grupo" + (i);
                        cad3 = "id_contacto" + (i);

                        id_grupoContacto[i]=response.getInt(cad1);
                        id_grupo[i]=response.getInt(cad2);
                        id_contacto[i]=response.getInt(cad3);


                        Alerta("El registro es "+id_grupoContacto[i]);
                        //  notificar(id[i] + "-" + actividad[i] + "-" + fecha_inicio[i] + "-" + fecha_fin[i] + "-" + arto[i] + "-" + cargo[i]);
                        GrupoContacto grupo = new GrupoContacto();
                        grupo.setIdGrupoContacto(id_grupoContacto[i]);
                        grupo.setId_grupo(id_grupo[i]);
                        grupo.setId_contacto(id_contacto[i]);



                        this.setGruposContactos(grupo);//se agrega el objeto al arreglo...
                    }
                    //     this.muestra_interfaz(this.contactos);
                    sincronizar(this.gruposContactos,this.c);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void sincronizar(ArrayList<GrupoContacto>grupos,Context c)
    {

        ThreadSync tarea = new ThreadSync(c);
        tarea.setGrupos(grupos);
        tarea.start();

        ThreadLectura tl = new ThreadLectura(c);
        tl.start();

    }

    public void eliminarGrupos()
    {
        db.delete("GrupoContacto", "1=" + 1 + "", null);

        Alerta("Eliminando");
    }

    public boolean insertarGrupoContacto (int idGrupoContacto,int idGrupo, int idContacto)
    {


        ContentValues grupoContactoValues = new ContentValues();
        grupoContactoValues.put("id_grupoContacto", idGrupoContacto);
        grupoContactoValues.put("id_grupo",idGrupo);
        grupoContactoValues.put("id_contacto",idContacto);



        db.insert("GrupoContacto", null, grupoContactoValues);

        Alerta("Registro guardado satisfactoriamente");



        return true;
    }

    class ThreadSync extends Thread {
        ArrayList<GrupoContacto>grupos2;
        public void setGrupos(ArrayList<GrupoContacto>gr)
        {
            grupos2=gr;
        }

        Context c;

        public ThreadSync(Context context){
            c=context;
        }

        @Override
        public void run() {

            eliminarGrupos();
            for (GrupoContacto g : grupos2)
            {
                insertarGrupoContacto(g.getIdGrupoContacto(),g.getId_grupo(),g.getId_contacto());
                Alerta("Se insertó "+g.getIdGrupoContacto());
            }
            // Contacto.ThreadLectura tl = new Contacto.ThreadLectura(this.c);
            //  tl.start();

        }
    }


    class ThreadLectura extends Thread {
        ArrayList<GrupoContacto>grupos2;
        Context c;

        public ThreadLectura(Context context){
            c=context;
        }

        public int cantidad_registros()
        {
            Cursor a = db.rawQuery("select id_grupoContacto,id_grupo,id_contacto  from GrupoContacto", null);
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
                Alerta("NO HAY NI UNO grupo");
                //layout.removeAllViews();
            }
            else
            {
                Alerta("ESTAMOS TUANIS");
                grupos2=new ArrayList<GrupoContacto>();

                Cursor a = db.rawQuery("select id_grupoContacto,id_grupo,id_contacto  from GrupoContacto", null);
                //      Alerta("-----------------------------------"+"select id , actividad , fecha_in , fecha_fin , arto , cargo  from Agenda where actividad like '%" + palabra + "%'");
                int i=0;
                if (a.moveToFirst())
                {
                    do {

                        GrupoContacto grupo = new GrupoContacto();
                        grupo.setIdGrupoContacto(a.getInt(0));
                        // contacto.setInstitucion(id_institucion);
                        grupo.setId_grupo(a.getInt(1));
                        grupo.setId_contacto(a.getInt(2));

                        grupos2.add(grupo);



                        //  Alerta("EL CARGO ES ///////////// " + car[i]);

                        //  i++;
                    } while (a.moveToNext());


                }
                setGruposContactosDB(grupos2);
                mostrarGrupos(grupos2);
            }
/*

 */


        }
        public void mostrarGrupos(ArrayList<GrupoContacto>grupos2){
            for(GrupoContacto g:grupos2){
                Alerta("desde la bd "+g.getIdGrupoContacto());
            }

        }
    }


    public ArrayList<GrupoContacto> getGruposContactos() {
        return this.gruposContactos;
    }

    public void setGruposContactos(GrupoContacto grupo) {//añadir un grupo
        this.gruposContactos.add(grupo);
    }

    public ArrayList<GrupoContacto> getGruposContactosDB() {
        return gruposContactosDB;
    }

    public void setGruposContactosDB(ArrayList<GrupoContacto> gruposContactosDB) {
        this.gruposContactosDB = gruposContactosDB;
    }

    private void Alerta(String mensaje) {
        Log.i("Información", mensaje);
    }


    public int cantidad_registros()
    {
        Cursor a = db.rawQuery("select id_grupoContacto,id_grupo,id_contacto  from GrupoContacto", null);
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

    public void leerGrupos(){
        ArrayList<GrupoContacto>grupos2;
        int cantidad_registros=cantidad_registros();
        if(cantidad_registros()==0)
        {
            Alerta("NO HAY NI UNO grupo");
            //layout.removeAllViews();
        }
        else
        {
            Alerta("ESTAMOS TUANIS");
            grupos2=new ArrayList<GrupoContacto>();

            Cursor a = db.rawQuery("select id_grupoContacto,id_grupo,id_contacto  from GrupoContacto", null);
            //      Alerta("-----------------------------------"+"select id , actividad , fecha_in , fecha_fin , arto , cargo  from Agenda where actividad like '%" + palabra + "%'");
            int i=0;
            if (a.moveToFirst())
            {
                do {

                    GrupoContacto grupo = new GrupoContacto();
                    grupo.setIdGrupoContacto(a.getInt(0));
                    // contacto.setInstitucion(id_institucion);
                    grupo.setId_grupo(a.getInt(1));
                    grupo.setId_contacto(a.getInt(2));

                    grupos2.add(grupo);


                } while (a.moveToNext());


            }
            setGruposContactosDB(grupos2);//se agregan al arreglo, pero desde la base de datos

        }
/*

 */
    }





}
