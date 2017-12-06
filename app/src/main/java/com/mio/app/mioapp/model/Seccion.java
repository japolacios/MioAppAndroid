package com.mio.app.mioapp.model;

public class Seccion {


    float latitud, longitud;
    String nameStation, nameRuta;

    public Seccion(String nameStation, float latitud, float longitud, String nameRuta){
        this.nameRuta = nameRuta;
        this.nameStation = nameStation;
        this.latitud = latitud;
        this.longitud = longitud;

    }

    public String getNameRuta() {
        return nameRuta;
    }

    public void setNameRuta(String nameRuta) {
        this.nameRuta = nameRuta;
    }

    public String getNameStation() {
        return nameStation;
    }

    public void setNameStation(String nameStation) {
        this.nameStation = nameStation;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }
}
