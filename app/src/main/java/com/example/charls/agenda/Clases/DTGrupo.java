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

public class DTGrupo {
    ArrayList<Grupo> grupos=new ArrayList<Grupo>();
    ArrayList<Grupo> gruposDB=new ArrayList<Grupo>();
    Context c;
    private SQLiteDatabase db;

    public DTGrupo(Context context){
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

        ThreadSync tarea = new ThreadSync(c);
        tarea.setGrupos(grupos);
        tarea.start();

        /*ThreadLectura tl = new ThreadLectura(c);
        tl.start();*/

    }

    public void eliminarGrupos()
    {
        db.delete("Grupo", "1=" + 1 + "", null);

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
            solicitar_grupos();
            eliminarGrupos();
            for (Grupo g : grupos2)
            {
                insertarGrupo(g.getIdGrupo(),g.getNombre());
                Alerta("Se insertó "+g.getIdGrupo()+" "+g.getNombre());
            }
            // Contacto.ThreadLectura tl = new Contacto.ThreadLectura(this.c);
            //  tl.start();

        }
    }

    class  ThreadLectura extends Thread {
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
                Alerta("NO HAY NI UNO grupo");
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
                setGruposDB(grupos2);//se agregan al arreglo, pero desde la base de datos
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

    public ArrayList<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(Grupo grupo) {//añadir un grupo
        this.grupos.add(grupo);
    }

    public ArrayList<Grupo> getGruposDB() {
        return gruposDB;
    }

    public void setGruposDB(ArrayList<Grupo> grup) {//añadir un grupo
        this.gruposDB=grup;
    }

    private void Alerta(String mensaje) {
        Log.i("Información", mensaje);
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


    public void leerGrupos(){
        ArrayList<Grupo>grupos2;
        int cantidad_registros=cantidad_registros();
        if(cantidad_registros()==0)
        {
            Alerta("NO HAY NI UNO grupo");
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


                } while (a.moveToNext());


            }
            setGruposDB(grupos2);//se agregan al arreglo, pero desde la base de datos

        }
/*

 */
    }

}
