package ar.edu.utn.frbb.tup.presentacion.controladores.manejoexepciones;

import ar.edu.utn.frbb.tup.excepciones.ClienteExistenteException;
import ar.edu.utn.frbb.tup.excepciones.ClienteMenorDeEdadException;
import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.excepciones.CuentaExistenteException;
import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaSinDineroException;
import ar.edu.utn.frbb.tup.excepciones.CuentasVaciasException;
import ar.edu.utn.frbb.tup.excepciones.MovimientosVaciosException;
import ar.edu.utn.frbb.tup.excepciones.TipoCuentaExistenteException;
import ar.edu.utn.frbb.tup.excepciones.*;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class ResponseEntityExceptionHandler extends org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler {

    // Manejo de excepciones 404 - Not Found
    @ExceptionHandler(value = { ClienteNoEncontradoException.class,
            ClientesVaciosException.class, CuentaNoEncontradaException.class,
            ClienteSinPrestamosException.class, CuentasVaciasException.class,
            CuentaMonedaNoExisteException.class, MovimientosVaciosException.class,
            PrestamosVaciosException.class})
    protected ResponseEntity<Object> handleMateriaNotFound(Exception ex, WebRequest request) {
        String exceptionMessage = ex.getMessage();
        CustomApiError error = new CustomApiError();
        error.setErrorCode(404);
        error.setErrorMessage(exceptionMessage);

        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    // Manejo de excepciones 400 - Bad Request
    @ExceptionHandler(value = { IllegalArgumentException.class,
            ClienteExistenteException.class, ClienteMenorDeEdadException.class,
            TipoCuentaExistenteException.class, TipoMonedaExistenteException.class,
            CuentaExistenteException.class, CuentaDistintaMonedaException.class,
            CuentaSinDineroException.class })
    protected ResponseEntity<Object> handleBadRequest(Exception ex, WebRequest request) {
        String exceptionMessage = ex.getMessage();
        CustomApiError error = new CustomApiError();
        error.setErrorCode(400);
        error.setErrorMessage(exceptionMessage);

        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // Manejo de errores de conversión
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class, ConversionFailedException.class })
    protected ResponseEntity<Object> handleTypeMismatch(Exception ex, WebRequest request) {
        String parameterName = null;

        if (ex instanceof MethodArgumentTypeMismatchException) {
            parameterName = ((MethodArgumentTypeMismatchException) ex).getName();
        } else if (ex instanceof ConversionFailedException) {
            parameterName = "un parámetro";
        }

        String exceptionMessage = String.format("Error: El valor ingresado para '%s' es invalido.", parameterName != null ? parameterName : "el campo");

        CustomApiError error = new CustomApiError();
        error.setErrorCode(400);
        error.setErrorMessage(exceptionMessage);

        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }


    // Manejo de excepciones 500 - Internal Server Error
    @ExceptionHandler(value = { RuntimeException.class })
    protected ResponseEntity<Object> handleInternalServerError(RuntimeException ex, WebRequest request) {
        String exceptionMessage = ex.getMessage();
        CustomApiError error = new CustomApiError();
        error.setErrorCode(500);
        error.setErrorMessage(exceptionMessage != null ? exceptionMessage : "Error interno del servidor");

        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (body == null) {
            CustomApiError error = new CustomApiError();
            error.setErrorMessage(ex.getMessage());
            body = error;
        }

        return new ResponseEntity<>(body, headers, status);
    }
}
