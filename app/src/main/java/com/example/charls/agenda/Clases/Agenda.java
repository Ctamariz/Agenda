package com.example.charls.agenda.Clases;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.ActionBar.LayoutParams;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.charls.agenda.R;
import com.example.charls.agenda.SQL.agenda_sqlLit;
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

public class Agenda implements View.OnClickListener {

    private SQLiteDatabase db;
    ArrayList<Contacto>contactos=new ArrayList<Contacto>();
    Context c;
    private ViewGroup layout;
    private ScrollView scrollView;
    private Activity activity;

    public Agenda(Context context, Activity act)
    {
        this.activity = act;
       this.c=context;
        agenda_sqlLit agenda =
                new agenda_sqlLit(this.c, "Agenda", null, 1);

        db = agenda.getWritableDatabase();

        layout = (ViewGroup) activity.findViewById(R.id.content);
        scrollView = (ScrollView) activity.findViewById(R.id.scrollView);




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
                        Contacto contacto = new Contacto(this.c);
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
               //     this.muestra_interfaz(this.contactos);
                    sincronizar(contactos);
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

    private void muestra_interfaz(ArrayList<Contacto>contactos)
    {


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
            int i =0;
            for (Contacto con:contactos) {

                Alerta("i" + i);
                re_a[i] = new RelativeLayout(this.c);
                //rel.LayoutParams(Mar);
                re_a[i].setGravity(Gravity.LEFT);
                GradientDrawable shape =  new GradientDrawable();
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
               if(i % 2==0)//cambiamos el color si es par el num del layout
               {
                   shape.setColor(Color.argb(1000, 183,235,255));
               }
               else
               {
                   shape.setColor(Color.argb(1000, 159,225,255));
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


                re_a[i].setId(i);


                re_a[i].setOnClickListener(this);


                re_a[i].setPadding(10, 10, 10, 10);
                //   layoutParams.setMargins(0, 15, 2, 0);
                re_a[i].setLayoutParams(layoutParams);

                //layoutParams2.setMargins(1000, 1000, 1000, 1000);

                iv_a[i] = new ImageView(this.c);
                iv_a[i].setId(1000+i);
                iv_a[i].setImageResource(R.drawable.usuario);
                //  Alerta("alerta-" + aproducto[i]);


                re_text[i] = new RelativeLayout(this.c);
                re_text[i].setId(i + 5000);
                //  re_text[i].setLayoutParams();


                layoutParams2.width = (int)(tamano_pantalla()/10);
                layoutParams2.height = (int)(tamano_pantalla()/10);
                iv_a[i].setLayoutParams(layoutParams2);

                tv_a[i] = new TextView(this.c);
                tv_a[i].setId(i + 1000);

              /*  tv_a2[i] = new TextView(this);
                tv_a2[i].setId(id[i] + 2000);
                tv_a3[i] = new TextView(this);
                tv_a3[i].setId(id[i] + 3000);
                tv_fuente[i] = new TextView(this);
                tv_fuente[i].setId(id[i] + 4000);
*/

                ////Fecha
                tv_fecha[i] = new TextView(this.c);
                tv_fecha[i].setId(i + 6000);
                tv_fecha[i].setText("Institución: " + " Chocobananos");
                tv_fecha[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tv_fecha[i].setTextColor(Color.BLACK);
///

                ////Profundidad
                tv_prof[i] = new TextView(this.c);
                tv_prof[i].setId(i + 7000);
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

                String cadena="";
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
                    cadena="En : 5 días inicia";
               /* }*/
                tv_a[i].setText("" + con.getNombre());
                tv_prof[i].setText("" + con.getApellido());
                tv_fechaf[i].setText("" + "");//FECHA FINAL INFERIOR DERECHO
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
                i ++;
            }

            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            });
        } catch (Exception e) {
            Alerta("" + e);
        }



    }


    public double tamano_pantalla()
    {
        Resources resources = this.c.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        // Note, screenHeightDp isn't reliable
        // (it seems to be too small by the height of the status bar),
        // but we assume screenWidthDp is reliable.
        // Note also, dm.widthPixels,dm.heightPixels aren't reliably pixels
        // (they get confused when in screen compatibility mode, it seems),
        // but we assume their ratio is correct.
        double screenWidthInPixels = (double)config.screenWidthDp * dm.density;
        double screenHeightInPixels = screenWidthInPixels * dm.heightPixels / dm.widthPixels;
        //   widthHeightInPixels[0] = (int)(screenWidthInPixels + .5);
        //   widthHeightInPixels[1] = (int)(screenHeightInPixels + .5);
        Alerta("ANCHO DE LA PANTALLA //////////////////-------------------////////////////"+screenWidthInPixels);
        return screenWidthInPixels;
    }





    ////////

    public void onClick(View v) {
        Alerta("QUE NOTA LA MOTA");


    }

    public void eliminar_registros()
    {
        db.delete("Contacto", "1=" + 1 + "", null);

        Alerta("Eliminando");
    }

    public boolean insertarContacto(int idContacto,int idInstitucion, String nombre, String apellido, String claro, String movistar, String cootel,
                                    String casa,String trabajo,String correo1,String correo2,String apodo,String foto)
    {


        ContentValues contactoValues = new ContentValues();
        contactoValues.put("id_contacto", idContacto);
        contactoValues.put("id_institucion", idInstitucion);
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



        return true;
    }

    private void sincronizar(ArrayList<Contacto>contactos)
    {

        ThreadSync tarea = new ThreadSync(this.c);
        tarea.setContactos(contactos);
        tarea.start();

        ThreadLectura tl = new ThreadLectura(this.c);
        tl.start();

    }
    class ThreadSync extends Thread {
        ArrayList<Contacto>contacto2;
        public void setContactos(ArrayList<Contacto>cont)
        {
            this.contacto2=cont;
        }

        Context c;

        public ThreadSync(Context context){
            c=context;
        }

        @Override
        public void run() {

               eliminar_registros();
                for (Contacto c : contacto2)
                {
                 insertarContacto(c.getIdContacto(),0, c.getNombre(), c.getApellido(),c.getClaro(),c.getMovistar(),c.getCootel(),c.getCasa(),c.getTrabajo(),c.getCorreo1(),c.getCorreo2(),c.getApodo(),"");
                 Alerta("Se insertó "+c.getNombre()+" "+c.getApellido());
                }
            ThreadLectura tl = new ThreadLectura(this.c);
            tl.start();

        }
    }


    class ThreadLectura extends Thread {
        ArrayList<Contacto>contacto2;
        Context c;

        public ThreadLectura(Context context){
            c=context;
        }

        public int cantidad_registros()
        {
            Cursor a = db.rawQuery("select id_contacto, id_institucion, nombre, apellido, claro, movistar, cootel, casa, trabajo, correo1, correo2, apodo, foto from Contacto", null);
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
        try{
            if(cantidad_registros()==0)
            {
                Alerta("NO HAY NI UNO");
                layout.removeAllViews();
            }
            else
            {
                Alerta("ESTAMOS TUANIS");
                contacto2=new ArrayList<Contacto>();

                Cursor a = db.rawQuery("select id_contacto, id_institucion, nombre, apellido, claro, movistar, cootel, casa, trabajo, correo1, correo2, apodo, foto from Contacto" , null);

                //      Alerta("-----------------------------------"+"select id , actividad , fecha_in , fecha_fin , arto , cargo  from Agenda where actividad like '%" + palabra + "%'");
                int i=0;
                if (a.moveToFirst())
                {
                    do {

                        Contacto contacto = new Contacto(this.c);
                        contacto.setIdContacto(a.getInt(0));
                        // contacto.setInstitucion(id_institucion);
                        contacto.setNombre(a.getString(2));
                        contacto.setApellido(a.getString(3));
                        contacto.setClaro(a.getString(4));
                        contacto.setMovistar(a.getString(5));
                        contacto.setCootel(a.getString(6));
                        contacto.setCasa(a.getString(7));
                        contacto.setTrabajo(a.getString(8));
                        contacto.setCorreo1(a.getString(9));
                        contacto.setCorreo2(a.getString(10));
                        contacto2.add(contacto);



                        //  Alerta("EL CARGO ES ///////////// " + car[i]);

                        //  i++;
                    } while (a.moveToNext());


                }

                muestra_interfaz(contacto2);
            }
        }catch(Exception e){

        }

/*

 */


        }
    }






}
