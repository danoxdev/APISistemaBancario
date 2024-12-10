package ar.edu.utn.frbb.tup.presentacion.clientes;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
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

@ExtendWith(MockitoExtension.class)
class TestControladorBuscarClientePorDni {

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
    void buscarClientePorDniExitosamente() throws ClienteNoEncontradoException {
        // Cargo los datos de entrada
        Long dni = 12345678L;

        // Creo el cliente esperado como resultado de la búsqueda
        Cliente clienteEsperado = new Cliente();
        clienteEsperado.setDni(dni);
        clienteEsperado.setNombre("Juan");
        clienteEsperado.setApellido("Pérez");

        // Simulo el comportamiento del servicio
        doNothing().when(validacionesPresentacion).validarDni(dni);
        when(servicioClientes.buscarCliente(dni)).thenReturn(clienteEsperado);

        // Ejecuto el método del controlador
        ResponseEntity<Cliente> response = controladorClientes.getClientePorDni(dni);

        // Compruebo que el código de estado HTTP sea 200 (OK) y el cuerpo de la respuesta sea el cliente esperado
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(clienteEsperado, response.getBody());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDni(dni);
        verify(servicioClientes, times(1)).buscarCliente(dni);
    }

    @Test
    void buscarClientePorDniNoExiste() throws ClienteNoEncontradoException {
        // Cargo los datos de entrada
        Long dni = 12345678L;

        // Simulo que el cliente no existe
        doNothing().when(validacionesPresentacion).validarDni(dni);
        doThrow(new ClienteNoEncontradoException("No se encontró el cliente con DNI: " + dni))
                .when(servicioClientes).buscarCliente(dni);

        // Llamo al controlador y espero que se lance la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> controladorClientes.getClientePorDni(dni)
        );

        // Verifico que el mensaje de la excepción sea el esperado
        assertEquals("No se encontró el cliente con DNI: " + dni, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarDni(dni);
        verify(servicioClientes, times(1)).buscarCliente(dni);
    }
}
