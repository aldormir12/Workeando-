package com.workeando.plataform.model;

public enum ModalidadPago {
    POR_DIA("por día"),
    POR_SEMANA("por semana"),
    POR_MES("por mes"),
    POR_PROYECTO("por el proyecto");

    //constructor
    private final String etiqueta;

    ModalidadPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
