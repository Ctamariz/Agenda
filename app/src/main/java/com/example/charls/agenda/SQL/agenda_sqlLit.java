package com.example.charls.agenda.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by charls on 05-03-17.
 */

public class agenda_sqlLit extends SQLiteOpenHelper {
    String sqlCreateContacto = "CREATE TABLE Contacto (id_contacto INTEGER,id_institucion" +
            " INTEGER, nombre TEXT,apellido, TEXT,claro Text, movistar Text,cootel TEXT," +
            "casa TEXT,trabajo TEXT,correo1 TEXT,correo2 TEXT,apodo TEXT,foto TEXT)";

    String sqlCreateGrupo = "CREATE TABLE Grupo (id_grupo INTEGER,nombre TEXT)";
    String sqlCreateInstiucion = "CREATE TABLE Institucion (id_institucion INTEGER,nombre TEXT)";
    String sqlCreateGrupoContacto = "CREATE TABLE GrupoContacto (id_grupoContacto INTEGER,id_grupo INTEGER,id_contacto INTEGER)";

    public agenda_sqlLit(Context contexto, String nombre,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreateContacto);
        db.execSQL(sqlCreateGrupo);
        db.execSQL(sqlCreateInstiucion);
        db.execSQL(sqlCreateGrupoContacto);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Contacto");
        db.execSQL("DROP TABLE IF EXISTS Grupo");
        db.execSQL("DROP TABLE IF EXISTS Institucion");
        db.execSQL("DROP TABLE IF EXISTS GrupoContacto");


        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreateContacto);
        db.execSQL(sqlCreateGrupo);
        db.execSQL(sqlCreateInstiucion);
        db.execSQL(sqlCreateGrupoContacto);

    }
}
