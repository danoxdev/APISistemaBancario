package ar.edu.utn.frbb.tup.presentacion.clientes;

import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TestControladorMostrarClientes {

    @InjectMocks
    private ControladorClientes controladorClientes;

    @Mock
    private ServicioClientes servicioClientes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mostrarClientesExitosamente() throws ClientesVaciosException {
        // Creo una lista simulada de clientes
        List<Cliente> clientesSimulados = new ArrayList<>();
        Cliente cliente1 = new Cliente();
        cliente1.setDni(12345678L);
        cliente1.setNombre("Juan");
        cliente1.setApellido("Pérez");

        Cliente cliente2 = new Cliente();
        cliente2.setDni(87654321L);
        cliente2.setNombre("María");
        cliente2.setApellido("González");

        clientesSimulados.add(cliente1);
        clientesSimulados.add(cliente2);

        // Simulo el comportamiento del servicio
        when(servicioClientes.mostrarClientes()).thenReturn(clientesSimulados);

        // Ejecuto el método del controlador
        ResponseEntity<List<Cliente>> response = controladorClientes.getClientes();

        // Compruebo que el código de estado HTTP sea 200 (OK) y el cuerpo de la respuesta sea la lista de clientes simulada
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(clientesSimulados, response.getBody());

        // Verifico las interacciones con los mocks
        verify(servicioClientes, times(1)).mostrarClientes();
    }

    @Test
    void mostrarClientesListaVacia() throws ClientesVaciosException {
        // Simulo que el servicio lanza una excepción de lista vacía
        doThrow(new ClientesVaciosException("No hay clientes disponibles"))
                .when(servicioClientes).mostrarClientes();

        // Llamo al controlador y espero que se lance la excepción
        ClientesVaciosException exception = assertThrows(
                ClientesVaciosException.class,
                () -> controladorClientes.getClientes()
        );

        // Verifico que el mensaje de la excepción sea el esperado
        assertEquals("No hay clientes disponibles", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(servicioClientes, times(1)).mostrarClientes();
    }
}
