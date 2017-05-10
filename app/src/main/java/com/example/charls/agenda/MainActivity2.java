package com.example.charls.agenda;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.charls.agenda.Clases.Agenda;
import com.example.charls.agenda.Clases.Contacto;
import com.example.charls.agenda.Clases.DTGrupo;
import com.example.charls.agenda.Clases.DTGrupoContacto;
import com.example.charls.agenda.Clases.DTInstitucion;
import com.example.charls.agenda.Clases.Grupo;
import com.example.charls.agenda.Clases.Institucion;

import java.util.LinkedList;


public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        LinearLayout ll_content = (LinearLayout) findViewById(R.id.content);
        ll_content.setVisibility(View.VISIBLE);
        LinearLayout ll_filtro = (LinearLayout) findViewById(R.id.contenedor_busqueda);
        ll_filtro.setVisibility(View.GONE);


       lectura();
        cargar_grupo();
        cargar_institucion();
        //  agenda.solicitar_contactos();


    }
    public void cancelar_busqueda(View v)
    {
        LinearLayout ll_content = (LinearLayout) findViewById(R.id.content);
        ll_content.setVisibility(View.VISIBLE);
        LinearLayout ll_filtro = (LinearLayout) findViewById(R.id.contenedor_busqueda);
        ll_filtro.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.busqueda) {

            LinearLayout ll_content = (LinearLayout) findViewById(R.id.content);
            ll_content.setVisibility(View.GONE);
            LinearLayout ll_filtro = (LinearLayout) findViewById(R.id.contenedor_busqueda);
            ll_filtro.setVisibility(View.VISIBLE);


        } else if (id == R.id.lectura) {
            lectura();
            notificar("Leyendo");

        } else if (id == R.id.actualizar) {

            sincronizar();
            notificar("Sincronizando");

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void sincronizar()
    {
      //  Agenda ag = new Agenda(this,MainActivity2.this);

        Agenda agenda = new Agenda(this,MainActivity2.this);
        //Contacto contacto=new Contacto(this);
        DTInstitucion institucion=new DTInstitucion(this);
        institucion.solicitar_instituciones();
        DTGrupo grupo=new DTGrupo(this);
        grupo.solicitar_grupos();
        DTGrupoContacto dtGrupoContacto=new DTGrupoContacto(this);
        dtGrupoContacto.solicitarGrupoContacto();

       // SystemClock.sleep(2800);
        agenda.solicitar_contactos();

    }
    public void lectura()
    {
        Agenda agenda = new Agenda(this,MainActivity2.this);
        agenda.leer_datos();
    }
    public void notificar(String cadena) {
        Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show();
    }

    public void cargar_grupo()
    {
        Spinner spn_grupo = (Spinner)findViewById(R.id.sp_grupo);
        DTGrupo dtgrupo = new DTGrupo(this);
        dtgrupo.leerGrupos();

        LinkedList grupos = new LinkedList();
        grupos.add("Seleccione");
        for(Grupo gr:dtgrupo.getGruposDB())
        {
            grupos.add(gr.getNombre());
        }

        ArrayAdapter spinner_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, grupos);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_grupo.setAdapter(spinner_adapter);
    }
    public void cargar_institucion()
    {
        Spinner spn_institucion = (Spinner)findViewById(R.id.sp_institucion);
        DTInstitucion dtinstitucion = new DTInstitucion(this);
        dtinstitucion.leerInstituciones();

        LinkedList instituciones = new LinkedList();
        instituciones.add("Seleccione");
        for(Institucion in:dtinstitucion.getInstitucionesDB())
        {
            instituciones.add(in.getNombre());
        }

        ArrayAdapter spinner_adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, instituciones);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_institucion.setAdapter(spinner_adapter);
    }

    public int idPorGrupo(String grupo){
        int idGrupo=0;

        DTGrupo dtGrupo=new DTGrupo(this);
        dtGrupo.leerGrupos();


        for(Grupo g: dtGrupo.getGruposDB()){

            if(g.getNombre().equals(grupo)){
                idGrupo=g.getIdGrupo();
                Log.i("INFO","el id del grupo "+g.getNombre()+" es "+g.getIdGrupo());
                return idGrupo;
            }
        }

        return idGrupo;
    }

    public int idPorInstitucion(String institucion){
        int idInstitucion=0;

        DTInstitucion dtInstitucion=new DTInstitucion(this);
        dtInstitucion.leerInstituciones();


        for(Institucion i: dtInstitucion.getInstitucionesDB()){

            if(i.getNombre().equals(institucion)){
                idInstitucion=i.getIdInstitucion();
                Log.i("INFO","el id del grupo "+i.getNombre()+" es "+i.getIdInstitucion());
                return idInstitucion;
            }
        }

        return idInstitucion;
    }

    public void Buscar(View v)
    {
        Spinner sp_grupo = (Spinner)findViewById(R.id.sp_grupo);
        Spinner sp_institucion=(Spinner)findViewById(R.id.sp_institucion);
        EditText et_indicio =(EditText)findViewById(R.id.et_indicio);

        String grupo = sp_grupo.getSelectedItem()+"";
        String institucion =sp_institucion.getSelectedItem().toString();

        int id_grupo;
        id_grupo = idPorGrupo(grupo);
        int id_institucion;
        id_institucion=idPorInstitucion(institucion);
        String indicio;
        indicio=et_indicio.getText().toString();

        Alerta("id_grupo "+id_grupo+" id_Institucion"+ id_institucion+" indicio"+indicio);
        lectura(id_grupo, id_institucion,indicio);

        LinearLayout ll_content = (LinearLayout) findViewById(R.id.content);
        ll_content.setVisibility(View.VISIBLE);
        LinearLayout ll_filtro = (LinearLayout) findViewById(R.id.contenedor_busqueda);
        ll_filtro.setVisibility(View.GONE);


       // Alerta("El grupo es "+grupo);
    }

    public void lectura(int id_grupo, int id_institucion, String indicio)
    {
        Agenda agenda = new Agenda(this,MainActivity2.this);
        agenda.leer_datos(id_grupo,id_institucion,indicio);
    }
    private void Alerta(String mensaje) {
        Log.i("Información", mensaje);
    }
}
