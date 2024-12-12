package ar.edu.utn.frbb.tup.presentacion.prestamos;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClienteSinPrestamosException;
import ar.edu.utn.frbb.tup.modelo.Prestamo;
import ar.edu.utn.frbb.tup.presentacion.controladores.ControladorPrestamo;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.servicios.ServicioPrestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestControladorListarPrestamos {

    @InjectMocks
    private ControladorPrestamo controladorPrestamo;

    @Mock
    private ValidacionesPresentacion validacionesPresentacion;

    @Mock
    private ServicioPrestamo servicioPrestamo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarPrestamosExitosamente() throws ClienteNoEncontradoException, ClienteSinPrestamosException {
        // Preparo datos de entrada
        Long dniCliente = 12345678L;

        Prestamo prestamo1 = new Prestamo();
        Prestamo prestamo2 = new Prestamo();

        Set<Prestamo> prestamosMock = new HashSet<>();
        prestamosMock.add(prestamo1);
        prestamosMock.add(prestamo2);

        // Mockeo la búsqueda de préstamos
        when(servicioPrestamo.listarPrestamos(dniCliente)).thenReturn(prestamosMock);

        // Ejecuto el método a testear
        ResponseEntity<Set<Prestamo>> response = controladorPrestamo.listarPrestamos(dniCliente);

        // Verifico el resultado
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        // Verifico las interacciones con los mocks
        verify(servicioPrestamo, times(1)).listarPrestamos(dniCliente);
    }

    @Test
    void listarPrestamosClienteNoEncontrado() throws ClienteNoEncontradoException, ClienteSinPrestamosException {
        // Preparo datos de entrada
        Long dniCliente = 12345678L;

        // Mockeo que el servicio lance una excepción
        when(servicioPrestamo.listarPrestamos(dniCliente)).thenThrow(new ClienteNoEncontradoException("No existe un cliente con el DNI ingresado"));

        // Llamo al método y espero la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> controladorPrestamo.listarPrestamos(dniCliente)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No existe un cliente con el DNI ingresado", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(servicioPrestamo, times(1)).listarPrestamos(dniCliente);
    }

    @Test
    void listarPrestamosClienteSinPrestamos() throws ClienteNoEncontradoException, ClienteSinPrestamosException {
        // Preparo datos de entrada
        Long dniCliente = 12345678L;

        // Mockeo la búsqueda de préstamos (sin préstamos)
        when(servicioPrestamo.listarPrestamos(dniCliente)).thenThrow(new ClienteSinPrestamosException("El cliente no tiene préstamos"));

        // Llamo al método y espero la excepción
        ClienteSinPrestamosException exception = assertThrows(
                ClienteSinPrestamosException.class,
                () -> controladorPrestamo.listarPrestamos(dniCliente)
        );

        // Verifico el mensaje de la excepción
        assertEquals("El cliente no tiene préstamos", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(servicioPrestamo, times(1)).listarPrestamos(dniCliente);
    }
}
