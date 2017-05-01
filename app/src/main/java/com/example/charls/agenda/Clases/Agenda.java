package com.example.charls.agenda.Clases;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.charls.agenda.web.Constantes;
import com.example.charls.agenda.web.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cvande on 27/4/2017.
 */

public class Agenda {


    ArrayList<Contacto>contactos=new ArrayList<Contacto>();
    Context c;

    public Agenda(Context context)
    {
       this.c=context;
    }

    public ArrayList<Contacto> getContactos() {
        return contactos;
    }

    public void solicitar_contactos()
    {


        HashMap<String, String> map = new HashMap<>();
        JSONObject jobject = new JSONObject(map);
        String newURL;
        newURL = Constantes.GET ;
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
                                obtener_contactos(response);
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

    private void obtener_contactos(JSONObject response) {

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
                String cad1, cad2, cad3, cad4, cad5, cad6,cad7, cad8, cad9, cad10, cad11, cad12;

                  //  int id[] = new int[cantidad];

                    int id_contacto[] = new int[cantidad];
                    int institucion[] = new int[cantidad];
                    String nombre[] = new String[cantidad];
                    String apellido[] = new String[cantidad];
                    String claro[] = new String[cantidad];
                    String movistar[] = new String[cantidad];
                    String cootel[] = new String[cantidad];
                    String casa[] = new String[cantidad];
                    String trabajo[] = new String[cantidad];
                    String correo1[] = new String[cantidad];
                    String correo2[] = new String[cantidad];
                    String apodo[] = new String[cantidad];

                 for (int i = 0; i < cantidad; i++)
                    {
                        cad1 = "id_contacto" + (i);
                        cad2 = "institucion" + (i);
                        cad3 = "nombre" + (i);
                        cad4 = "apellido" + (i);
                        cad5 = "claro" + (i);
                        cad6 = "movistar" + (i);
                        cad7 = "cootel" + (i);
                        cad8 = "casa" + (i);
                        cad9 = "trabajo" + (i);
                        cad10 = "correo1" + (i);
                        cad11 = "correo2" + (i);
                        cad12 = "apodo" + (i);
                        id_contacto[i]=response.getInt(cad1);
                        institucion[i]=response.getInt(cad2);
                        nombre[i]=response.getString(cad3);
                        apellido[i]=response.getString(cad4);
                        claro[i]=response.getString(cad5);
                        movistar[i]=response.getString(cad6);

                        cootel[i]=response.getString(cad7);
                        casa[i]=response.getString(cad8);
                        trabajo[i]=response.getString(cad9);
                        correo1[i]=response.getString(cad10);

                        correo2[i]=response.getString(cad11);
                        apodo[i]=response.getString(cad12);

                        Alerta("El registro es"+nombre[i]);
                        //  notificar(id[i] + "-" + actividad[i] + "-" + fecha_inicio[i] + "-" + fecha_fin[i] + "-" + arto[i] + "-" + cargo[i]);
                        Contacto contacto = new Contacto();
                        contacto.setIdContacto(id_contacto[i]);
                       // contacto.setInstitucion(id_institucion);
                        contacto.setNombre(nombre[i]);
                        contacto.setApellido(apellido[i]);
                        contacto.setClaro(claro[i]);
                        contacto.setMovistar(movistar[i]);
                        contacto.setCootel(cootel[i]);
                        contacto.setCasa(casa[i]);
                        contacto.setTrabajo(trabajo[i]);
                        contacto.setCorreo1(correo1[i]);
                        contacto.setCorreo2(correo2[i]);
                        this.setContacto(contacto);//se agrega el objeto al arreglo...




                    }
            //        al.lectura();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void Alerta(String mensaje) {
        Log.i("Información", mensaje);
    }

    public void setContacto(Contacto contacto){
        this.contactos.add(contacto);
    }


}
