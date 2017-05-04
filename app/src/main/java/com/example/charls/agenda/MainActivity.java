package com.example.charls.agenda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.charls.agenda.Clases.Agenda;
import com.example.charls.agenda.Clases.Contacto;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Agenda agenda = new Agenda(this,MainActivity.this);
        agenda.solicitar_contactos();
        Contacto contacto = new Contacto(this);
        contacto.solicitar_grupos();

    }
}
