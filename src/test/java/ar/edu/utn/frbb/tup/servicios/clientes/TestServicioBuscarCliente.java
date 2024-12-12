package ar.edu.utn.frbb.tup.servicios.clientes;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.servicios.ServicioClientes;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServicioBuscarCliente {

    @InjectMocks
    private ServicioClientes servicioClientes;

    @Mock
    private ValidacionesServicios validacionesServicios;

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private MovimientosDao movimientosDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void buscarClienteExitosamente() throws ClienteNoEncontradoException {
        // Preparo datos de entrada
        long dni = 12345678L;
        Cliente clienteMock = new Cliente();
        clienteMock.setDni(dni);
        clienteMock.setNombre("Juan");
        clienteMock.setApellido("Pérez");

        // Mockeo la búsqueda del cliente
        when(clienteDao.findCliente(dni)).thenReturn(clienteMock);

        // Ejecuto el método a testear
        Cliente resultado = servicioClientes.buscarCliente(dni);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(clienteMock.getDni(), resultado.getDni());
        assertEquals(clienteMock.getNombre(), resultado.getNombre());
        assertEquals(clienteMock.getApellido(), resultado.getApellido());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dni);
    }

    @Test
    void buscarClienteNoExistente() {
        // Preparo datos de entrada
        long dni = 12345678L;

        // Mockeo que el cliente no existe
        when(clienteDao.findCliente(dni)).thenReturn(null);

        // Llamo al método y espero la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioClientes.buscarCliente(dni)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No se encontro el cliente con el DNI: " + dni, exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dni);
    }
}
