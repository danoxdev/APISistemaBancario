package ar.edu.utn.frbb.tup.modelo;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoPersona {
    PERSONA_FISICA("PERSONA_FISICA"),
    PERSONA_JURIDICA("PERSONA_JURIDICA");

    private final String descripcion;

    TipoPersona(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoPersona fromString(String text) {
        for (TipoPersona tipo : TipoPersona.values()) {
            if (tipo.descripcion.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Error: El tipo de persona debe ser 'PERSONA_FISICA' o 'PERSONA_JURIDICA'");
    }
}
