package ar.edu.utn.frbb.tup.presentacion.clientes;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClienteTieneCuentasException;
import ar.edu.utn.frbb.tup.excepciones.ClienteTienePrestamosException;
import ar.edu.utn.frbb.tup.excepciones.CuentaTieneSaldoException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.presentacion.controladores.ControladorClientes;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
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

class TestControladorEliminarCliente {

    @InjectMocks
    private ControladorClientes controladorClientes;

    @Mock
    private ValidacionesPresentacion validacionesPresentacion;

    @Mock
    private ServicioClientes servicioClientes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void eliminarClienteExitosamente() throws ClienteNoEncontradoException, ClienteTieneCuentasException, ClienteTienePrestamosException {
        // Cargo los datos de entrada
        Long dni = 12345678L;

        // Simulo el cliente a retornar después de ser eliminado
        Cliente clienteEliminado = new Cliente();
        clienteEliminado.setDni(dni);
        clienteEliminado.setNombre("Juan");
        clienteEliminado.setApellido("Pérez");

        // Configuro el mock para que no lance excepciones y retorne el cliente esperado
        doNothing().when(validacionesPresentacion).validarDni(dni);
        when(servicioClientes.eliminarCliente(dni)).thenReturn(clienteEliminado);

        // Ejecuto el método del controlador
        ResponseEntity<Cliente> response = controladorClientes.eliminarCliente(dni);

        // Verifico que el código HTTP sea 200 (OK) y el cliente devuelto sea el esperado
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(clienteEliminado, response.getBody());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDni(dni);
        verify(servicioClientes, times(1)).eliminarCliente(dni);
    }

    @Test
    void eliminarClienteNoExiste() throws ClienteNoEncontradoException, ClienteTieneCuentasException, ClienteTienePrestamosException {
        // Cargo los datos de entrada
        Long dni = 12345678L;

        // Configuro el mock para lanzar la excepción ClienteNoEncontradoException
        doNothing().when(validacionesPresentacion).validarDni(dni);
        doThrow(new ClienteNoEncontradoException("El cliente no existe"))
                .when(servicioClientes).eliminarCliente(dni);

        // Llamo al controlador y espero que se lance la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> controladorClientes.eliminarCliente(dni)
        );

        // Verifico que el mensaje de la excepción sea el esperado
        assertEquals("El cliente no existe", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDni(dni);
        verify(servicioClientes, times(1)).eliminarCliente(dni);
    }

    @Test
    void eliminarClienteConPrestamos() throws ClienteTienePrestamosException, ClienteNoEncontradoException, ClienteTieneCuentasException {
        // Cargo los datos de entrada
        Long dni = 12345678L;

        // Configuro el mock para lanzar la excepción ClienteConPrestamosException
        doNothing().when(validacionesPresentacion).validarDni(dni);
        doThrow(new ClienteTienePrestamosException("El cliente tiene préstamos activos"))
                .when(servicioClientes).eliminarCliente(dni);

        // Llamo al controlador y espero que se lance la excepción
        ClienteTienePrestamosException exception = assertThrows(
                ClienteTienePrestamosException.class,
                () -> controladorClientes.eliminarCliente(dni)
        );

        // Verifico que el mensaje de la excepción sea el esperado
        assertEquals("El cliente tiene préstamos activos", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDni(dni);
        verify(servicioClientes, times(1)).eliminarCliente(dni);
    }

    @Test
    void eliminarClienteConCuentas() throws ClienteTieneCuentasException, ClienteNoEncontradoException, ClienteTienePrestamosException {
        // Cargo los datos de entrada
        Long dni = 12345678L;

        // Simulo que el cliente tiene cuentas asociadas
        doNothing().when(validacionesPresentacion).validarDni(dni);
        doThrow(new ClienteTieneCuentasException("El cliente tiene cuentas asociadas"))
                .when(servicioClientes).eliminarCliente(dni);

        // Llamo al controlador y espero que se lance la excepción
        ClienteTieneCuentasException exception = assertThrows(
                ClienteTieneCuentasException.class,
                () -> controladorClientes.eliminarCliente(dni)
        );

        // Verifico que el mensaje de la excepción sea el esperado
        assertEquals("El cliente tiene cuentas asociadas", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDni(dni);
        verify(servicioClientes, times(1)).eliminarCliente(dni);
    }

}

