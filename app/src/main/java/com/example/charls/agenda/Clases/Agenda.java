package com.example.charls.agenda.Clases;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.ActionBar.LayoutParams;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.charls.agenda.R;
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

public class Agenda implements View.OnClickListener {

    private SQLiteDatabase db;
    ArrayList<Contacto> contactos = new ArrayList<Contacto>();
    Context c;
    private ViewGroup layout;
    private ScrollView scrollView;
    private Activity activity;
    ArrayList<Grupo> gruposDB;
    ArrayList<GrupoContacto> gruposContactoDB;
    ArrayList<Institucion> institucionDB;
    DTGrupo dtGrupo;
    String tel="";
    String mensaje="";
    DTInstitucion dtInstitucion;
    DTGrupoContacto dtGrupoContacto;
    Dialog customDialog;
    final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;
    final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    int claro=0, movistar=0, cootel=0;
    ArrayList<Contacto> muestraContacto = new ArrayList<Contacto>();

    public Agenda(Context context, Activity act) {
        this.activity = act;
        this.c = context;
        agenda_sqlLit agenda = new agenda_sqlLit(this.c, "Agenda", null, 1);

        db = agenda.getWritableDatabase();
        dtGrupo = new DTGrupo(c);
        dtGrupoContacto = new DTGrupoContacto(c);
        dtInstitucion = new DTInstitucion(c);
        layout = (ViewGroup) activity.findViewById(R.id.content);
        scrollView = (ScrollView) activity.findViewById(R.id.scrollView);
        //leerDatos();


    }

    public int[] idGruposPorContacto(int idContacto) {
        int cantidad = dtGrupoContacto.cantidad_registros();
        int grupos[] = new int[cantidad];
        int i = 0;
        dtGrupoContacto.leerGrupos();//ejecuta la consulta con el hilo y llena el arreglo
        this.gruposContactoDB = dtGrupoContacto.getGruposContactosDB();
        for (GrupoContacto g : gruposContactoDB) {
            if (g.getId_contacto() == idContacto) {
                grupos[i] = g.getId_grupo();
                Alerta("id grupo agregado al arreglo " + grupos[i]);
                i++;
            }

        }

        return grupos;
    }


    public ArrayList<Grupo> grupoPorId(int[] idGrupos) {
        ArrayList<Grupo> gr = new ArrayList<Grupo>();
        dtGrupo.leerGrupos();//ejecuta la consulta llena el arreglo
        this.gruposDB = dtGrupo.getGruposDB();
        int i = 0;

        for (Grupo g : gruposDB) {
            Alerta("grupo bd " + g.getNombre() + " grupo arreglo i " + idGrupos[i]);
            if (g.getIdGrupo() == idGrupos[i]) {

                gr.add(g);
                Alerta("Elemento i " + i + " grupo " + g.getNombre());
                i++;
            }

        }
        return gr;
    }

    public String institucionPorId(int idInstitucion) {
        Alerta("entra al metodo institucion x id");
        String institucion = "";
        dtInstitucion.leerInstituciones();//ejecuta la consulta con el hilo y llena el arreglo
        //this.institucionDB=dtInstitucion.getInstitucionesDB();
        if (dtInstitucion.getInstitucionesDB().size() == 0) {
            Alerta("arreglo de instituciones vacio");
        } else {
            Alerta("existe mas de un objeto en el arreglo istitucion");
            for (Institucion i : dtInstitucion.getInstitucionesDB()) {
                Alerta("id institucion " + i.getIdInstitucion() + "nombre " + i.getNombre());
                if (i.getIdInstitucion() == idInstitucion) {
                    institucion = String.valueOf(i.getNombre());
                    Alerta("institucion en el metodo " + institucion);
                    return institucion;
                }
            }
        }

        return institucion;
    }


    public ArrayList<Contacto> getContactos() {
        return contactos;
    }

    public void leer_datos() {
        ThreadLectura tl = new ThreadLectura(this.c,0,0,"");
        tl.start();
    }
    public void leer_datos(int grupo, int institucion, String indicio) {
        ThreadLectura tl = new ThreadLectura(this.c,institucion,grupo,indicio);
        tl.start();
    }

    private void sincronizar(ArrayList<Contacto> contactos) {

        ThreadSync tarea = new ThreadSync(this.c);
        tarea.setContactos(contactos);
        tarea.start();


    }

    public void solicitar_contactos() {


        HashMap<String, String> map = new HashMap<>();
        JSONObject jobject = new JSONObject(map);
        String newURL;
        newURL = Constantes.GET;
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


            if (!estado.equals("1")) {
                Alerta("Tenemos error de sincronizacion!");
            } else {
                int cantidad = response.getInt("cantidad");
                if (cantidad == 0) {
                    Alerta("No hay registros nuevos!");
                } else {
                    String cad1, cad2, cad3, cad4, cad5, cad6, cad7, cad8, cad9, cad10, cad11, cad12;

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

                    for (int i = 0; i < cantidad; i++) {
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
                        id_contacto[i] = response.getInt(cad1);
                        institucion[i] = response.getInt(cad2);
                        nombre[i] = response.getString(cad3);
                        apellido[i] = response.getString(cad4);
                        claro[i] = response.getString(cad5);
                        movistar[i] = response.getString(cad6);

                        cootel[i] = response.getString(cad7);
                        casa[i] = response.getString(cad8);
                        trabajo[i] = response.getString(cad9);
                        correo1[i] = response.getString(cad10);

                        correo2[i] = response.getString(cad11);
                        apodo[i] = response.getString(cad12);

                        Alerta("El registro es" + nombre[i]);
                        //  notificar(id[i] + "-" + actividad[i] + "-" + fecha_inicio[i] + "-" + fecha_fin[i] + "-" + arto[i] + "-" + cargo[i]);
                        Contacto contacto = new Contacto(this.c);
                        contacto.setIdContacto(id_contacto[i]);

                        contacto.setNombre(nombre[i]);
                        contacto.setApellido(apellido[i]);
                        contacto.setClaro(claro[i]);
                        contacto.setMovistar(movistar[i]);
                        contacto.setCootel(cootel[i]);
                        contacto.setCasa(casa[i]);
                        contacto.setTrabajo(trabajo[i]);
                        contacto.setCorreo1(correo1[i]);
                        contacto.setCorreo2(correo2[i]);
                        contacto.setApodo(apodo[i]);

                        int[] idGrupos = this.idGruposPorContacto(id_contacto[i]);
                        //obtengo todos los id grupo de un contacto
                        ArrayList<Grupo> gruposPorContacto = this.grupoPorId(idGrupos);//obtengo los objetos grupos por id grupo
                        contacto.setGrupos(gruposPorContacto);
                        mostrarGruposXcontacto(contacto.getGrupos());

                        Institucion inst = new Institucion();
                        inst.setIdInstitucion(institucion[i]);
                        String nombreIns = institucionPorId(institucion[i]);
                        inst.setNombre(nombreIns);

                        contacto.setInstitucion(inst);
                        //    inst.getNombre();
                        Alerta("el nombre de la institucion es " + contacto.getInstitucion().getIdInstitucion() + " el id es: " + contacto.getInstitucion().getNombre());

                        this.setContacto(contacto);//se agrega el objeto al arreglo...
                    }
                    //     this.muestra_interfaz(this.contactos);
                    sincronizar(contactos);
                }

            }
        } catch (JSONException e) {
            Alerta("Este Jodido try " + e);
            // e.printStackTrace();

        }
    }

    private void Alerta(String mensaje) {
        Log.i("Información", mensaje);
    }

    public void setContacto(Contacto contacto) {
        this.contactos.add(contacto);
    }


    private void muestra_interfaz(ArrayList<Contacto> contactos) {
        muestraContacto = contactos;

        int q = contactos.size();
        try {
            //  cantidad = q;
            RelativeLayout re_a[] = new RelativeLayout[q];
            RelativeLayout re_text[] = new RelativeLayout[q];
            TextView tv_a[] = new TextView[q];
            //  TextView tv_a2[] = new TextView[q];
            //  TextView tv_a3[] = new TextView[q];
            //  TextView tv_fuente[] = new TextView[q];
            TextView tv_fecha[] = new TextView[q];
            TextView tv_prof[] = new TextView[q];
            ImageView iv_a[] = new ImageView[q];
            TextView tv_fechaf[];
            tv_fechaf = new TextView[q];
            Alerta("2-" + q);
            layout.removeAllViews();
            int i = 0;
            for (Contacto con : contactos) {

                Alerta("i" + i);
                re_a[i] = new RelativeLayout(this.c);
                //rel.LayoutParams(Mar);
                re_a[i].setGravity(Gravity.LEFT);
                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(30);
                shape.setStroke(3, Color.WHITE);




               /* if(consulta_fechas(getCurrentTimeStamp(3),fi[i])<0)
                {
                    if(consulta_fechas(getCurrentTimeStamp(3),ff[i])<0)
                    {
                        shape.setColor(Color.argb(1000, 217,216,216));
                    }
                    else
                    {
                        shape.setColor(Color.argb(1000, 255,255,229));
                    }


                }
                else
                {*/
                if (i % 2 == 0)//cambiamos el color si es par el num del layout
                {
                    shape.setColor(Color.argb(1000, 183, 235, 255));
                } else {
                    shape.setColor(Color.argb(1000, 159, 225, 255));
                }

            /*    }*/




              /*  if ((i % 2) == 0) {
                    //re_a[i].setBackgroundColor(Color.argb(1000, 233, 235, 224));
                    shape.setColor(Color.argb(1000, 221,247,255));
                } else {
                    shape.setColor(Color.argb(1000,200,227,238));
                    //  re_a[i].setBackgroundColor(Color.WHITE);
                  //  re_a[i].setBackgroundColor(Color.argb(1000, 211, 213, 200));
                }*/
                //  re_a[i].setBackgroundResource(R.drawable.layout_container);
                re_a[i].setBackground(shape);
                re_a[i].setMinimumWidth(2000);

                //	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT); // Verbose!
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams params6 = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams params7 = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams params8 = new RelativeLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);


                re_a[i].setId(con.getIdContacto());


                re_a[i].setOnClickListener(this);


                re_a[i].setPadding(10, 10, 10, 10);
                //   layoutParams.setMargins(0, 15, 2, 0);
                re_a[i].setLayoutParams(layoutParams);

                //layoutParams2.setMargins(1000, 1000, 1000, 1000);

                iv_a[i] = new ImageView(this.c);
                iv_a[i].setId(1000 + con.getIdContacto());
                iv_a[i].setImageResource(R.drawable.usuario);
                //  Alerta("alerta-" + aproducto[i]);


                re_text[i] = new RelativeLayout(this.c);
                re_text[i].setId(con.getIdContacto() + 5000);
                //  re_text[i].setLayoutParams();


                layoutParams2.width = (int) (tamano_pantalla() / 10);
                layoutParams2.height = (int) (tamano_pantalla() / 10);
                iv_a[i].setLayoutParams(layoutParams2);

                tv_a[i] = new TextView(this.c);
                tv_a[i].setId(con.getIdContacto() + 1000);

              /*  tv_a2[i] = new TextView(this);
                tv_a2[i].setId(id[i] + 2000);
                tv_a3[i] = new TextView(this);
                tv_a3[i].setId(id[i] + 3000);
                tv_fuente[i] = new TextView(this);
                tv_fuente[i].setId(id[i] + 4000);
*/

                ////Fecha
                tv_fecha[i] = new TextView(this.c);
                tv_fecha[i].setId(con.getIdContacto() + 6000);
                tv_fecha[i].setText("Institución: " + con.getInstitucion().getNombre());
                tv_fecha[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tv_fecha[i].setTextColor(Color.BLACK);
///

                ////Profundidad
                tv_prof[i] = new TextView(this.c);
                tv_prof[i].setId(con.getIdContacto() + 7000);
                //  tv_prof[i].setText(""+prof[i]+" Km. Prof.");
                tv_prof[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tv_prof[i].setTextColor(Color.BLACK);

                //// Fechaf
                tv_fechaf[i] = new TextView(this.c);
                tv_fechaf[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tv_fechaf[i].setTextColor(Color.BLACK);

///


                // tv_a2[i].setText("" + t_evento[i]);
                //   tv_a2[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                //  tv_a3[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tv_a[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                //      tv_fechaf[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                //  tv_a3[i].setText("" + ub_array[i]);
                //   tv_a[i].setText("" + mag[i]+" Richter");
                //--OJO- tv_fuente[i].setText("" + fuent[i]);

                tv_a[i].setTextColor(Color.BLACK);
                tv_prof[i].setTextColor(Color.BLACK);

                //  tv.setLayoutParams(layoutParams);
                layout.addView(re_a[i]);
                re_a[i].addView(iv_a[i]);

                String cadena = "";
             /*   if(consulta_fechas(getCurrentTimeStamp(3),fi[i])<0)
                {
                    if(consulta_fechas(getCurrentTimeStamp(3),ff[i])<0)
                    {
                        cadena="Ya pasó esta actividad";
                    }
                    else
                    {
                        cadena="En curso: "+consulta_fechas(getCurrentTimeStamp(3),ff[i])+ " días para finalizar";
                    }


                }
                else
                {*/
                cadena = "En : 5 días inicia";
               /* }*/
                tv_a[i].setText("" + con.getNombre());
                tv_prof[i].setText("" + con.getApellido());

                cadena = "";
                int cont = 1;
                for (Grupo g : con.getGrupos()) {
                    if (cont == con.getGrupos().size()) {
                        cadena += " " + g.getNombre();
                    } else {
                        cadena += " " + g.getNombre() + ",";
                    }

                    cont++;
                }
                if (cadena.equals("")) {
                    tv_fechaf[i].setText("" + "");//FECHA FINAL INFERIOR DERECHO
                } else {
                    tv_fechaf[i].setText("Grupos" + cadena);//FECHA FINAL INFERIOR DERECHO
                }

                ///////////40


                //         tv_a3[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);


                //
                //  params.setMargins(170, 0, 0, 0);
                Alerta("id_de_iv_a " + iv_a[i].getId());
                //VER AQUI   params5.addRule(RelativeLayout.i, iv_a[i].getId());
                params.setMargins(20, 0, 0, 0);
                params.addRule(RelativeLayout.BELOW, tv_fecha[i].getId());
                params5.addRule(RelativeLayout.RIGHT_OF, iv_a[i].getId());


                params2.addRule(RelativeLayout.BELOW, tv_a[i].getId());
                params2.setMargins(20, 0, 0, 0);
                //   params2.setMargins(20,35,0,0);
                // params2.addRule(RelativeLayout.RIGHT_OF, tv_a[i].getId()+1000);
                //  params2.addRule(RelativeLayout.ALIGN_BOTTOM,tv_a[i].getId()+1000);
                //   params2.setMargins(0, 30, 0, 0);
                // params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,iv_a[i].getId());
                //  params3.addRule(RelativeLayout.BELOW,tv_a[i].getId());
                //      params3.addRule(RelativeLayout.BELOW, tv_a2[i].getId());
                params3.setMargins(20, 0, 0, 0);
                //   params4.addRule(RelativeLayout.BELOW, iv_a[i].getId());
                params4.setMargins(20, 110, 0, 0);
                params6.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, tv_a[i].getId());
                params7.addRule(RelativeLayout.BELOW, tv_a[i].getId());
                params7.setMargins(20, 0, 0, 0);
                params8.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, tv_prof[i].getId());
                params8.addRule(RelativeLayout.BELOW, tv_prof[i].getId());

                //params.addRule(RelativeLayout.RIGHT_OF, iv_a[i].getId());
                re_text[i].setLayoutParams(params5);
                re_a[i].addView(re_text[i]);


                tv_a[i].setLayoutParams(params);

                tv_fecha[i].setLayoutParams(params6);
                tv_prof[i].setLayoutParams(params7);
                tv_fechaf[i].setLayoutParams(params8);

                // layoutParams.addRule(RelativeLayout.RIGHT_OF, iv);
                Alerta("i2-" + i);

                //  tv_a[i].setText(tv_a2[i].getText()+"\n"+tv_a3[i].getText()+"\n"+tv_prof[i].getText());


                re_text[i].addView(tv_a[i]);
                //    re_text[i].addView(tv_a2[i]);
                //  re_text[i].addView(tv_a3[i]);
                //  re_a[i].addView(tv_fuente[i]);
                re_text[i].addView(tv_fecha[i]);
                re_text[i].addView(tv_prof[i]);
                re_text[i].addView(tv_fechaf[i]);
                //  re_a[i].setOnClickListener(this);
                // Alerta("Sonido es "+sonido);
                i++;
            }

            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            });
        } catch (Exception e) {
            Alerta("ESTE JODIDO CATCH" + e);
        }


    }


    public double tamano_pantalla() {
        Resources resources = this.c.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        // Note, screenHeightDp isn't reliable
        // (it seems to be too small by the height of the status bar),
        // but we assume screenWidthDp is reliable.
        // Note also, dm.widthPixels,dm.heightPixels aren't reliably pixels
        // (they get confused when in screen compatibility mode, it seems),
        // but we assume their ratio is correct.
        double screenWidthInPixels = (double) config.screenWidthDp * dm.density;
        double screenHeightInPixels = screenWidthInPixels * dm.heightPixels / dm.widthPixels;
        //   widthHeightInPixels[0] = (int)(screenWidthInPixels + .5);
        //   widthHeightInPixels[1] = (int)(screenHeightInPixels + .5);
        Alerta("ANCHO DE LA PANTALLA //////////////////-------------------////////////////" + screenWidthInPixels);
        return screenWidthInPixels;
    }


    public void eliminar_registros() {
        db.delete("Contacto", "1=" + 1 + "", null);

        Alerta("Eliminando");
    }

    public boolean insertarContacto(int idContacto, int idInstitucion, String nombre, String apellido, String claro, String movistar, String cootel,
                                    String casa, String trabajo, String correo1, String correo2, String apodo, String foto) {


        ContentValues contactoValues = new ContentValues();
        contactoValues.put("id_contacto", idContacto);
        contactoValues.put("id_institucion", idInstitucion);
        Alerta("El id de institucion insertado es " + idInstitucion);
        contactoValues.put("nombre", nombre);
        contactoValues.put("apellido", apellido);
        contactoValues.put("claro", claro);
        contactoValues.put("movistar", movistar);
        contactoValues.put("cootel", cootel);
        contactoValues.put("casa", casa);
        contactoValues.put("trabajo", trabajo);
        contactoValues.put("correo1", correo1);
        contactoValues.put("correo2", correo2);
        contactoValues.put("apodo", apodo);
        contactoValues.put("foto", foto);


        db.insert("Contacto", null, contactoValues);

        Alerta("Registro guardado satisfactoriamente");


        //  db.close();
        return true;
    }


    class ThreadSync extends Thread {
        ArrayList<Contacto> contacto2;

        public void setContactos(ArrayList<Contacto> cont) {
            this.contacto2 = cont;
        }

        Context c;

        public ThreadSync(Context context) {
            c = context;
        }

        @Override
        public void run() {


            eliminar_registros();
            for (Contacto c : contacto2) {
                insertarContacto(c.getIdContacto(), c.getInstitucion().getIdInstitucion(), c.getNombre(), c.getApellido(), c.getClaro(), c.getMovistar(), c.getCootel(), c.getCasa(), c.getTrabajo(), c.getCorreo1(), c.getCorreo2(), c.getApodo(), "");
                Alerta("Se insertó " + c.getNombre() + " " + c.getApellido());
            }

            // leerDatos();


        }
    }


    class ThreadLectura extends Thread {
        ArrayList<Contacto> contacto2;
        Context c;
        int id_institucion,id_grupo;
        String cadena;

        public ThreadLectura(Context context, int institucion, int grupo, String indicio) {
            c = context;
            this.id_institucion = institucion;
            this.id_grupo=grupo;
            this.cadena=indicio;
        }

        public int cantidad_registros() {



            String where=" where nombre  like '%"+cadena+"%'";

            if(this.id_institucion==0)
            {
                where +="";
            }
            else
            {
                where += " and id_institucion = "+id_institucion;
            }
          /*  if(this.cadena.equals(""))
            {
                where +="";
            }
            else
            {
                where +=" and (nombre || ' ' || apellido) like '%"+cadena+"%'";
            }*/

            if(this.id_grupo ==0)
            {
                where +="";
            }
            else
            {
                where += " and id_grupo = "+id_grupo;
            }


          //  where +="";
            Alerta("select distinct Contacto.id_contacto, id_institucion, nombre, apellido, claro, movistar, cootel, casa, trabajo, correo1, correo2, apodo, foto from Contacto  left join GrupoContacto  on Contacto.id_contacto = GrupoContacto.id_contacto "+where);
            Cursor a = db.rawQuery("select distinct Contacto.id_contacto, id_institucion, nombre, apellido, claro, movistar, cootel, casa, trabajo, correo1, correo2, apodo, foto from Contacto  left join GrupoContacto  on Contacto.id_contacto = GrupoContacto.id_contacto "+where, null);
            int i = 0;
            if (a.moveToFirst()) {
                do {
                    i++;
                } while (a.moveToNext());
            }
            //  db.close();
            Alerta("la pinche i " + i);
            return i;
        }

        @Override


        public void run() {

            Alerta("ENTRO EN LA JUGADA");

            if (cantidad_registros() == 0) {
                Alerta("NO HAY NI UNO lect");


                  /*      activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Alerta("ENTRA AQUI AL ARRANCAR");
                            }
                        });*/

            } else {
                Alerta("ESTAMOS TUANIS");
                contacto2 = new ArrayList<Contacto>();


                String where=" where nombre  like '%"+cadena+"%'";

                if(this.id_institucion==0)
                {
                    where +="";
                }
                else
                {
                    where += " and id_institucion = "+id_institucion;
                }
          /*  if(this.cadena.equals(""))
            {
                where +="";
            }
            else
            {
                where +=" and (nombre || ' ' || apellido) like '%"+cadena+"%'";
            }*/

                if(this.id_grupo ==0)
                {
                    where +="";
                }
                else
                {
                    where += " and id_grupo = "+id_grupo;
                }

                Alerta("select distinct Contacto.id_contacto, id_institucion, nombre, apellido, claro, movistar, cootel, casa, trabajo, correo1, correo2, apodo, foto from Contacto  left join GrupoContacto  on Contacto.id_contacto = GrupoContacto.id_contacto "+where);

                Cursor a = db.rawQuery("select distinct Contacto.id_contacto, id_institucion, nombre, apellido, claro, movistar, cootel, casa, trabajo, correo1, correo2, apodo, foto from Contacto  left join GrupoContacto  on Contacto.id_contacto = GrupoContacto.id_contacto "+where, null);

                if (a.moveToFirst()) {
                    do {

                        Contacto contacto = new Contacto(this.c);
                        contacto.setIdContacto(a.getInt(0));
                        //contacto.setInstitucion(a.getInt(1));
                        contacto.setNombre(a.getString(2));
                        contacto.setApellido(a.getString(3));
                        contacto.setClaro(a.getString(4));
                        contacto.setMovistar(a.getString(5));
                        contacto.setCootel(a.getString(6));
                        contacto.setCasa(a.getString(7));
                        contacto.setTrabajo(a.getString(8));
                        contacto.setCorreo1(a.getString(9));
                        contacto.setCorreo2(a.getString(10));
                        contacto.setApodo(a.getString(11));


                        int[] idGrupos = idGruposPorContacto(a.getInt(0));
                        //obtengo todos los id grupo de un contacto
                        ArrayList<Grupo> gruposPorContacto = grupoPorId(idGrupos);//obtengo los objetos grupos por id grupo
                        contacto.setGrupos(gruposPorContacto);
                        mostrarGruposXcontacto(contacto.getGrupos());

                        Institucion inst = new Institucion();
                        Alerta("el id de isntitucion es " + a.getInt(1));
                        inst.setIdInstitucion(a.getInt(1));
                        String nombreIns = institucionPorId(a.getInt(1));
                        inst.setNombre(nombreIns);

                        contacto.setInstitucion(inst);
                        Alerta("La isntitucion es " + inst.getNombre());

                        for (Grupo g : contacto.getGrupos()) {
                            Alerta("El grupo es " + g.getNombre());
                        }


                        contacto2.add(contacto);


                    } while (a.moveToNext());

                    db.close();
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        muestra_interfaz(contacto2);
                    }
                });

            }


        }
    }


    public void mostrarGruposXcontacto(ArrayList<Grupo> gr) {
        for (Grupo g : gr) {

            Alerta("el grupo para el contacto es " + g.getNombre());
        }
    }

    public void onClick(View v) {


        Contacto contact = null;
        for (Contacto cont : muestraContacto) {
            if (v.getId() == cont.getIdContacto()) {
                contact = cont;
            }

        }
        if (contact == null) {
            Alerta("No hay ninguno");
        } else {
            Alerta("QUE NOTA LA MOTA " + contact.getNombre());

            //////////
            //     Button btn_pluvi=(Button) findViewById(R.id.btn_pluvi);
            //     btn_pluvi.setEnabled(false);

            // con este tema personalizado evitamos los bordes por defecto
            customDialog = new Dialog(this.c, R.style.Theme_Dialog_Translucent);
            //deshabilitamos el título por defecto
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //obligamos al usuario a pulsar los botones para cerrarlo
            customDialog.setCancelable(false);
            //establecemos el contenido de nuestro dialog
            customDialog.setContentView(R.layout.contacto);

            TextView tv_nombre = (TextView) customDialog.findViewById(R.id.tv_alpha);
            tv_nombre.setText("" + contact.getNombre() + " " + contact.getApellido() + " (" + contact.getApodo() + ")");
            TextView tv_institucion = (TextView) customDialog.findViewById(R.id.tv_alpha_3);
            tv_institucion.setText("Institución: " + contact.getInstitucion().getNombre());
            TextView tv_grupo = (TextView) customDialog.findViewById(R.id.tv_alpha_4);
            String cadena = "";
            int cont = 1;
            for (Grupo g : contact.getGrupos()) {
                if (cont == contact.getGrupos().size()) {
                    cadena += " " + g.getNombre();
                } else {
                    cadena += " " + g.getNombre() + ",";
                }

                cont++;
            }

            tv_grupo.setText("Grupo(s): " + cadena);

            TextView tv_celular1 = (TextView) customDialog.findViewById(R.id.tv_cel_claro_mov);
            tv_celular1.setText("C: " + contact.getClaro() + " M: " + contact.getMovistar());

            TextView tv_celular2 = (TextView) customDialog.findViewById(R.id.tv_cel_cootel_casa);
            tv_celular2.setText("Co: " + contact.getCootel() + " Ca: " + contact.getCasa());

            TextView tv_celular3 = (TextView) customDialog.findViewById(R.id.tv_cel_trabajo);
            tv_celular3.setText("Tr: " + contact.getTrabajo());

            TextView tv_email1 = (TextView) customDialog.findViewById(R.id.tv_email1);
            tv_email1.setText("E1: " + contact.getCorreo1());

            TextView tv_email2 = (TextView) customDialog.findViewById(R.id.tv_email2);
            tv_email2.setText("E2: " + contact.getCorreo2());

            // params.width = (int)(tamano_pantalla()/5);


          /*  TextView titulo = (TextView) customDialog.findViewById(R.id.titulo_p);
            titulo.setText("PLUVIOMETRÍA (mm)");*/

            //     Pluviometria pv = new Pluviometria(MainActivity.this);
            //     pv.setDialog(customDialog);
            //     pv.lectura();
            final Contacto c = contact;

            ((RelativeLayout) customDialog.findViewById(R.id.telefono_rl)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    //customDialog.dismiss();
                    llamar(customDialog, c);


                }
            });

            ((RelativeLayout) customDialog.findViewById(R.id.sms)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    //customDialog.dismiss();
                    llamar_sms_layout(customDialog, c);


                }
            });

            ((RelativeLayout) customDialog.findViewById(R.id.cancelar)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    customDialog.dismiss();
                }
            });

            ((RelativeLayout) customDialog.findViewById(R.id.cancel_telefono)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mostrar_dialog(customDialog);
                }
            });

            ((RelativeLayout) customDialog.findViewById(R.id.cancel_telefono2)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mostrar_dialog(customDialog);
                }
            });

            ((RelativeLayout) customDialog.findViewById(R.id.tel_claro)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                  try{
                      Llamar_por_telefono(c.getClaro());
                  }
                  catch(Exception e)
                  {

                  }

                }
            });

            ((RelativeLayout) customDialog.findViewById(R.id.tel_movistar)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    try{
                        Llamar_por_telefono(c.getMovistar());
                    }
                    catch(Exception e)
                    {

                    }

                }
            });

            ((RelativeLayout) customDialog.findViewById(R.id.tel_cootel)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    try{
                        Llamar_por_telefono(c.getCootel());
                    }
                    catch(Exception e)
                    {

                    }

                }
            });
            ((RelativeLayout) customDialog.findViewById(R.id.tel_casa)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    try{
                        Llamar_por_telefono(c.getCasa());
                    }
                    catch(Exception e)
                    {

                    }

                }
            });

            ((RelativeLayout) customDialog.findViewById(R.id.tel_trabajo)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    try{
                        Llamar_por_telefono(c.getTrabajo());
                    }
                    catch(Exception e)
                    {

                    }

                }
            });



            ////BOTONES DEL SMS

            ((RelativeLayout) customDialog.findViewById(R.id.sms_claro)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    claro=1;
                    movistar=0;
                    cootel=0;

                      llamar_sms_envio_layout(customDialog,c);


                }
            });

            ((RelativeLayout) customDialog.findViewById(R.id.sms_movistar)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    claro=0;
                    movistar=1;
                    cootel=0;

                    llamar_sms_envio_layout(customDialog,c);


                }
            });


            ((RelativeLayout) customDialog.findViewById(R.id.sms_cootel)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    claro=0;
                    movistar=0;
                    cootel=1;

                    llamar_sms_envio_layout(customDialog,c);


                }
            });





            ((Button) customDialog.findViewById(R.id.b_cancelar)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {


                        llamar_sms_layout(customDialog,c);


                }
            });

            ((Button) customDialog.findViewById(R.id.b_enviar)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    String msg ="";
                    EditText et_sms=(EditText) customDialog.findViewById(R.id.et_sms);
                    msg=et_sms.getText().toString();
                    if(claro==1)
                    {
                        enviarMensaje(c.getClaro(),msg);
                    }
                    if(movistar==1)
                    {
                        enviarMensaje(c.getMovistar(),msg);
                    }
                    if(cootel==1)
                    {
                        enviarMensaje(c.getCootel(),msg);
                    }

                    et_sms.setText("");
                    llamar_sms_layout(customDialog,c);
                  ///ENVIO DE SMS


                }
            });

            ((RelativeLayout) customDialog.findViewById(R.id.email1)).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {




                    enviar_email(c.getCorreo1(),c.getCorreo2());


                }
            });




            customDialog.show();
            mostrar_dialog(customDialog);
            /////////
        }


    }


    public void llamar(Dialog d, Contacto c) {


        RelativeLayout rl_contacto = (RelativeLayout) d.findViewById(R.id.alpha_elegido);
        RelativeLayout rl_contacto2 = (RelativeLayout) d.findViewById(R.id.alpha_elegido2);
        RelativeLayout rl_telefono = (RelativeLayout) d.findViewById(R.id.contendor_telefonos);
        RelativeLayout rl_sms = (RelativeLayout) d.findViewById(R.id.contenedor_sms);
        LinearLayout rl_sms_enviar = (LinearLayout) d.findViewById(R.id.envio_sms);



        RelativeLayout rl_claro = (RelativeLayout) d.findViewById(R.id.tel_claro);
        RelativeLayout rl_movistar = (RelativeLayout) d.findViewById(R.id.tel_movistar);
        RelativeLayout rl_cootel = (RelativeLayout) d.findViewById(R.id.tel_cootel);
        RelativeLayout rl_casa = (RelativeLayout) d.findViewById(R.id.tel_casa);
        RelativeLayout rl_trabajo = (RelativeLayout) d.findViewById(R.id.tel_trabajo);


        //     animar(true,rl_contacto);
        rl_telefono.setVisibility(View.VISIBLE);
        rl_sms.setVisibility(View.GONE);
        rl_contacto.setVisibility(View.GONE);
        rl_contacto2.setVisibility(View.GONE);
        rl_sms_enviar.setVisibility(View.GONE);


        if (c.getClaro().equals("") || c.getClaro() == null) {
            rl_claro.setVisibility(View.GONE);
        } else {
            rl_claro.setVisibility(View.VISIBLE);
        }

        if (c.getMovistar().equals("") || c.getMovistar() == null) {
            rl_movistar.setVisibility(View.GONE);
        } else {
            rl_movistar.setVisibility(View.VISIBLE);
        }
        if (c.getCootel().equals("") || c.getCootel() == null) {
            rl_cootel.setVisibility(View.GONE);
        } else {
            rl_cootel.setVisibility(View.VISIBLE);
        }
        if (c.getCasa().equals("") || c.getCasa() == null) {
            rl_casa.setVisibility(View.GONE);
        } else {
            rl_casa.setVisibility(View.VISIBLE);
        }
        if (c.getTrabajo().equals("") || c.getTrabajo() == null) {
            rl_trabajo.setVisibility(View.GONE);
        } else {
            rl_trabajo.setVisibility(View.VISIBLE);
        }
        if ((c.getClaro().equals("") || c.getClaro() == null) && (c.getMovistar().equals("") || c.getMovistar() == null) && (c.getCootel().equals("") || c.getCootel() == null)
                && (c.getCasa().equals("") || c.getCasa() == null) && (c.getTrabajo().equals("") || c.getTrabajo() == null)) {

            mostrar_dialog(d);
            notificar("El contacto no tiene números registrados");

        }


    }

    public void llamar_sms_envio_layout(Dialog d, Contacto c) {


        RelativeLayout rl_contacto = (RelativeLayout) d.findViewById(R.id.alpha_elegido);
        RelativeLayout rl_contacto2 = (RelativeLayout) d.findViewById(R.id.alpha_elegido2);
        RelativeLayout rl_telefono = (RelativeLayout) d.findViewById(R.id.contendor_telefonos);
        RelativeLayout rl_sms = (RelativeLayout) d.findViewById(R.id.contenedor_sms);
        LinearLayout rl_sms_enviar = (LinearLayout) d.findViewById(R.id.envio_sms);



        //     animar(true,rl_contacto);
        rl_telefono.setVisibility(View.GONE);
        rl_sms.setVisibility(View.GONE);
        rl_contacto.setVisibility(View.GONE);
        rl_contacto2.setVisibility(View.GONE);
        rl_sms_enviar.setVisibility(View.VISIBLE);
        //    animar(false,rl_telefono);




    }


    public void llamar_sms_layout(Dialog d, Contacto c) {


        RelativeLayout rl_contacto = (RelativeLayout) d.findViewById(R.id.alpha_elegido);
        RelativeLayout rl_contacto2 = (RelativeLayout) d.findViewById(R.id.alpha_elegido2);
        RelativeLayout rl_telefono = (RelativeLayout) d.findViewById(R.id.contendor_telefonos);
        RelativeLayout rl_sms = (RelativeLayout) d.findViewById(R.id.contenedor_sms);
        LinearLayout rl_sms_enviar = (LinearLayout) d.findViewById(R.id.envio_sms);


        RelativeLayout rl_claro = (RelativeLayout) d.findViewById(R.id.sms_claro);
        RelativeLayout rl_movistar = (RelativeLayout) d.findViewById(R.id.sms_movistar);
        RelativeLayout rl_cootel = (RelativeLayout) d.findViewById(R.id.sms_cootel);
      //  RelativeLayout rl_casa = (RelativeLayout) d.findViewById(R.id.sms_casa);
     //   RelativeLayout rl_trabajo = (RelativeLayout) d.findViewById(R.id.sms_trabajo);


        //     animar(true,rl_contacto);
        rl_telefono.setVisibility(View.GONE);
        rl_sms.setVisibility(View.VISIBLE);
        rl_contacto.setVisibility(View.GONE);
        rl_contacto2.setVisibility(View.GONE);
        rl_sms_enviar.setVisibility(View.GONE);
        //    animar(false,rl_telefono);

        if (c.getClaro().equals("") || c.getClaro() == null) {
            rl_claro.setVisibility(View.GONE);
        } else {
            rl_claro.setVisibility(View.VISIBLE);
        }

        if (c.getMovistar().equals("") || c.getMovistar() == null) {
            rl_movistar.setVisibility(View.GONE);
        } else {
            rl_movistar.setVisibility(View.VISIBLE);
        }
        if (c.getCootel().equals("") || c.getCootel() == null) {
            rl_cootel.setVisibility(View.GONE);
        } else {
            rl_cootel.setVisibility(View.VISIBLE);
        }

        if ((c.getClaro().equals("") || c.getClaro() == null) && (c.getMovistar().equals("") || c.getMovistar() == null) && (c.getCootel().equals("") || c.getCootel() == null))
        {

            mostrar_dialog(d);
            notificar("El contacto no tiene números registrados");

        }


    }

    public void mostrar_dialog(Dialog d) {
        RelativeLayout rl_contacto = (RelativeLayout) d.findViewById(R.id.alpha_elegido);
        RelativeLayout rl_telefono = (RelativeLayout) d.findViewById(R.id.contendor_telefonos);
        RelativeLayout rl_contacto2 = (RelativeLayout) d.findViewById(R.id.alpha_elegido2);
        RelativeLayout rl_sms=(RelativeLayout) d.findViewById(R.id.contenedor_sms);
        LinearLayout  LL_enviio_sms=(LinearLayout) d.findViewById(R.id.envio_sms);
        rl_contacto.setVisibility(View.VISIBLE);
        rl_telefono.setVisibility(View.GONE);
        rl_contacto2.setVisibility(View.VISIBLE);
        rl_sms.setVisibility(View.GONE);
        LL_enviio_sms.setVisibility(View.GONE);
    }

    public void notificar(String cadena) {
        Toast.makeText(this.c, cadena, Toast.LENGTH_SHORT).show();
    }

    public void Llamar_por_telefono(String telefono) {
        this.tel=telefono;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this.activity, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
                {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, Manifest.permission.CALL_PHONE))
                    {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setPackage("com.android.server.telecom");
                    intent.setData(Uri.parse("tel:" + telefono));
                    this.c.startActivity(intent);
                    }
                    else {
                    ActivityCompat.requestPermissions(this.activity,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setPackage("com.android.server.telecom");
                    intent.setData(Uri.parse("tel:" + telefono));
                    this.c.startActivity(intent);

                }
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setPackage("com.android.server.telecom");
                intent.setData(Uri.parse("tel:" + telefono));
                this.c.startActivity(intent);
            }
        }

    }



    public void onRequestPermissionsResult(int requestCode,

                                           String permissions[], int[] grantResults) {
        try {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setPackage("com.android.server.telecom");
                        //    intent.setPackage("com.android.phone");
                        intent.setData(Uri.parse("tel:" + this.tel));
                        this.c.startActivity(intent);
                    } else {
                        System.out.println("El usuario ha rechazado el permiso");
                    }

                }


                case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(this.tel, null, this.mensaje, null, null);
                        notificar("Enviando SMS");
                    } else {
                        Toast.makeText(this.activity,
                                "No se brindó permiso para enviar sms", Toast.LENGTH_LONG).show();
                        return;
                    }
                }


            }
        } catch (SecurityException s) {
            Alerta("Este es el jodido error " + s);
        } catch (Exception e) {
            Alerta("El numero está jodido");
        }


    }


    public void enviarMensaje(String numero, String msg) {
        this.tel=numero;
        this.mensaje=msg;
        try {

            if (ContextCompat.checkSelfPermission(this.activity,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {


                if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity,
                        Manifest.permission.SEND_SMS)) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(numero, null, this.mensaje, null, null);
                    notificar("Enviando SMS");


                } else {
                    ActivityCompat.requestPermissions(this.activity,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(numero, null, mensaje, null, null);
                    notificar("Enviando SMS");
                }
            } else {
                // Notificar("Entro aqui");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(numero, null, this.mensaje, null, null);
                notificar("Enviando SMS");
            }
        } catch (SecurityException s) {
            Alerta("Este es el jodido error " + s);
        } catch (Exception e) {
            Alerta("El numero está jodido");
        }
    }


    public void enviar_email(String email1, String email2)
    {


        // Notificar(email);
        if((email1.equals("") || email1 ==null)&&(email2.equals("") || email2 ==null)){
            notificar("El contacto no tiene correos registrados");
            Alerta("El contacto no tiene correos registrados");
        }
        else if(email1.equals("") || email1 ==null){
            String[] emails = {email2};
        }else if(email2.equals("") || email2 ==null){
            String[] emails = {email1};
        }
        else{
            String[] emails = {email1,email2};
            String subject = "Asunto";
            String message = "Tu mensaje";

            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, emails);
            email.putExtra(Intent.EXTRA_SUBJECT, subject);
            email.putExtra(Intent.EXTRA_TEXT, message);

            // need this to prompts email client only
            email.setType("message/rfc822");

            this.activity.startActivity(Intent.createChooser(email, "Escoja su cliente de email :"));
        }




      /*  Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"+"ramiro@agaton.ni"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_CC, "ramiro@agaton.ni");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "CONTENIDO");
        emailIntent.setType("message/rfc822");
        this.m.startActivity(Intent.createChooser(emailIntent, "Email "));*/
    }


}
