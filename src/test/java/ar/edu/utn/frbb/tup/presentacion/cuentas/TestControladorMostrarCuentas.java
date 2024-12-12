package ar.edu.utn.frbb.tup.presentacion.cuentas;

import ar.edu.utn.frbb.tup.excepciones.CuentaNoEncontradaException;
import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
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

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TestControladorMostrarCuentas {

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
    void mostrarCuentasExitosamente() throws CuentaNoEncontradaException, ClienteNoEncontradoException {
        // Cargo los datos de entrada
        Long dni = 12345678L;

        // Creo un conjunto de cuentas para retornar
        Set<Cuenta> cuentas = new HashSet<>();
        Cuenta cuenta1 = new Cuenta();
        cuenta1.setDniTitular(dni);
        cuenta1.setCbu(123456789L);
        cuentas.add(cuenta1);

        // Simulo el comportamiento del servicio
        doNothing().when(validacionesPresentacion).validarDni(dni);
        when(servicioCuentas.mostrarCuentas(dni)).thenReturn(cuentas);

        // Ejecuto el método del controlador
        ResponseEntity<Set<Cuenta>> response = controladorCuentas.mostrarCuentas(dni);

        // Compruebo que el código de estado HTTP sea 200 (OK) y el cuerpo de la respuesta contenga las cuentas esperadas
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(cuentas, response.getBody());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDni(dni);
        verify(servicioCuentas, times(1)).mostrarCuentas(dni);
    }

    @Test
    void mostrarCuentasClienteNoExiste() throws CuentaNoEncontradaException, ClienteNoEncontradoException {
        // Cargo los datos de entrada
        Long dni = 12345678L;

        // Simulo que el cliente no existe
        doNothing().when(validacionesPresentacion).validarDni(dni);
        doThrow(new ClienteNoEncontradoException("El cliente no existe"))
                .when(servicioCuentas).mostrarCuentas(dni);

        // Llamo al controlador y espero que se lance la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> controladorCuentas.mostrarCuentas(dni)
        );

        // Verifico que el mensaje de la excepción sea el esperado
        assertEquals("El cliente no existe", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDni(dni);
        verify(servicioCuentas, times(1)).mostrarCuentas(dni);
    }

    @Test
    void mostrarCuentasNoExisten() throws CuentaNoEncontradaException, ClienteNoEncontradoException {
        // Cargo los datos de entrada
        Long dni = 12345678L;

        // Simulo que no hay cuentas asociadas al cliente
        doNothing().when(validacionesPresentacion).validarDni(dni);
        when(servicioCuentas.mostrarCuentas(dni)).thenReturn(new HashSet<>());

        // Ejecuto el método del controlador
        ResponseEntity<Set<Cuenta>> response = controladorCuentas.mostrarCuentas(dni);

        // Compruebo que el código de estado HTTP sea 200 (OK) y el cuerpo de la respuesta esté vacío
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(new HashSet<>(), response.getBody());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDni(dni);
        verify(servicioCuentas, times(1)).mostrarCuentas(dni);
    }
}
