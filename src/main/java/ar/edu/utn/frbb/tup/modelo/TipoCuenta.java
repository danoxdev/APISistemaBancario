package ar.edu.utn.frbb.tup.modelo;

public enum TipoCuenta {
    CAJA_AHORRO("CAJA_AHORRO"),
    CUENTA_CORRIENTE("CUENTA_CORRIENTE");

    private final String descripcion;

    TipoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoCuenta fromString(String text) throws IllegalArgumentException {
        for (TipoCuenta tipo : TipoCuenta.values()) {
            if (tipo.descripcion.equalsIgnoreCase(text)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Error: El tipo de cuenta debe ser 'CAJA_AHORRO' o 'CUENTA_CORRIENTE'");
    }
}