package ar.edu.utn.frbb.tup.excepciones;

public class CuentaSinDineroException extends Exception {
    public CuentaSinDineroException(String message) {
        super(message);
    }
}
