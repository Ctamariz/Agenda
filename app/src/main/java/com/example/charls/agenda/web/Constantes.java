package com.example.charls.agenda.web;


/**
 * Clase que contiene los códigos usados en "I Wish" para
 * mantener la integridad en las interacciones entre actividades
 * y fragmentos
 */
public class Constantes {
    /**
     * Transición Home -> Detalle
     */
    public static final int CODIGO_DETALLE = 100;

    /**
     * Transición Detalle -> Actualización
     */
    public static final int CODIGO_ACTUALIZACION = 101;
    /**
     * Puerto que utilizas para la conexión.
     * Dejalo en blanco si no has configurado esta carácteristica.
     */
    private static final String PUERTO_HOST = ":8080";
    /**
     * Dirección IP de genymotion o AVD
     */
    // private static final String IP = "172.16.45.26";
    //  private static final String IP = "ingenierianica.net";
    //   private static final String IP="172.16.45.152";
    //private static final String IP = "monitoreo.managua.gob.ni";
    private static final String IP = "192.168.1.115";
    /**
     * URLs del Web Service
     */

    public static final String GET = "http://" + IP + PUERTO_HOST + "/agenda_contacto/agenda.php";
    public static final String GETContact = "http://" + IP + PUERTO_HOST + "/agenda_contacto/grupo.php";
    public static final String GETInst = "http://" + IP + PUERTO_HOST + "/agenda_contacto/institucion.php";
    public static final String GETGC = "http://" + IP + PUERTO_HOST + "/agenda_contacto/grupoContacto.php";
    /**
     * Clave para el valor extra que representa al identificador de una meta
     */
    public static final String EXTRA_ID = "IDEXTRA";

}