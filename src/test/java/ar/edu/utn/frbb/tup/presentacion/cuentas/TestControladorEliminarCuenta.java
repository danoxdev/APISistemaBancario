package ar.edu.utn.frbb.tup.presentacion.cuentas;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.CuentaTieneSaldoException;
import ar.edu.utn.frbb.tup.excepciones.CuentasVaciasException;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.presentacion.controladores.ControladorCuentas;
import ar.edu.utn.frbb.tup.servicios.ServicioCuentas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestControladorEliminarCuenta {

    @InjectMocks
    private ControladorCuentas controladorCuentas;

    @Mock
    private ValidacionesPresentacion validacionesPresentacion;

    @Mock
    private ServicioCuentas servicioCuentas;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void eliminarCuentaExitosamente() throws CuentaNoEncontradaException, CuentaTieneSaldoException, CuentasVaciasException, ClienteNoEncontradoException {
        // Cargo los datos de entrada
        Long cbu = 123456789L;
        Long dniTitular = 12345678L;

        // Creo la cuenta a retornar como resultado de la eliminación
        Cuenta cuentaEliminada = new Cuenta();
        cuentaEliminada.setDniTitular(dniTitular);
        cuentaEliminada.setCbu(cbu);

        // Simulo el comportamiento del servicio
        doNothing().when(validacionesPresentacion).validarCBU(cbu);
        when(servicioCuentas.eliminarCuenta(dniTitular,cbu)).thenReturn(cuentaEliminada);

        // Ejecuto el método del controlador
        ResponseEntity<Cuenta> response = controladorCuentas.eliminarCuenta(dniTitular, cbu);

        // Compruebo que el código de estado HTTP sea 200 (OK) y el cuerpo de la respuesta sea la cuenta eliminada
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(cuentaEliminada, response.getBody());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(servicioCuentas, times(1)).eliminarCuenta(dniTitular, cbu);
    }

    @Test
    void eliminarCuentaNoExiste() throws CuentaNoEncontradaException, CuentaTieneSaldoException, CuentasVaciasException, ClienteNoEncontradoException {
        // Cargo los datos de entrada
        Long dniTitular = 12345678L;
        Long cbu = 123456789L;

        // Simulo que la cuenta no existe
        doNothing().when(validacionesPresentacion).validarCBU(cbu);
        doThrow(new CuentaNoEncontradaException("La cuenta no existe"))
                .when(servicioCuentas).eliminarCuenta(dniTitular, cbu);

        // Llamo al controlador y espero que se lance la excepción
        CuentaNoEncontradaException exception = assertThrows(
                CuentaNoEncontradaException.class,
                () -> controladorCuentas.eliminarCuenta(dniTitular, cbu)
        );

        // Verifico que el mensaje de la excepción sea el esperado
        assertEquals("La cuenta no existe", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(servicioCuentas, times(1)).eliminarCuenta(dniTitular, cbu);
    }

    @Test
    void eliminarCuentaConSaldo() throws CuentaNoEncontradaException, CuentaTieneSaldoException, CuentasVaciasException, ClienteNoEncontradoException {
        // Cargo los datos de entrada
        Long dniTitular = 12345678L;
        Long cbu = 123456789L;

        // Simulo que la cuenta tiene saldo
        doNothing().when(validacionesPresentacion).validarCBU(cbu);
        doThrow(new CuentaTieneSaldoException("La cuenta tiene saldo y no se puede eliminar"))
                .when(servicioCuentas).eliminarCuenta(dniTitular, cbu);

        // Llamo al controlador y espero que se lance la excepción
        CuentaTieneSaldoException exception = assertThrows(
                CuentaTieneSaldoException.class,
                () -> controladorCuentas.eliminarCuenta(dniTitular, cbu)
        );

        // Verifico que el mensaje de la excepción sea el esperado
        assertEquals("La cuenta tiene saldo y no se puede eliminar", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCBU(cbu);
        verify(servicioCuentas, times(1)).eliminarCuenta(dniTitular, cbu);
    }
}
