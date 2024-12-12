package ar.edu.utn.frbb.tup.servicios.prestamos;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClienteSinPrestamosException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.modelo.Prestamo;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.PrestamoDao;
import ar.edu.utn.frbb.tup.servicios.ServicioPrestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServicioListarPrestamos {

    @InjectMocks
    private ServicioPrestamo servicioPrestamo;

    @Mock
    private PrestamoDao prestamoDao;

    @Mock
    private ClienteDao clienteDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listarPrestamosExitosamente() throws ClienteNoEncontradoException, ClienteSinPrestamosException {
        // Preparo datos de entrada
        Long dniCliente = 12345678L;

        Cliente clienteMock = new Cliente();
        clienteMock.setDni(dniCliente);

        Prestamo prestamo1 = new Prestamo(1, dniCliente, 10000.0, 12, 0, 10000.0);
        Prestamo prestamo2 = new Prestamo(2, dniCliente, 5000.0, 6, 0, 5000.0);

        Set<Prestamo> prestamosMock = new HashSet<>();
        prestamosMock.add(prestamo1);
        prestamosMock.add(prestamo2);

        // Mockeo la búsqueda del cliente
        when(clienteDao.findCliente(dniCliente)).thenReturn(clienteMock);

        // Mockeo la búsqueda de los préstamos del cliente
        when(prestamoDao.findPrestamosDelCliente(dniCliente)).thenReturn(prestamosMock);

        // Ejecuto el método a testear
        Set<Prestamo> resultado = servicioPrestamo.listarPrestamos(dniCliente);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(prestamo1));
        assertTrue(resultado.contains(prestamo2));

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dniCliente);
        verify(prestamoDao, times(1)).findPrestamosDelCliente(dniCliente);
    }

    @Test
    void listarPrestamosClienteNoEncontrado() {
        // Preparo datos de entrada
        Long dniCliente = 12345678L;

        // Mockeo que el cliente no existe
        when(clienteDao.findCliente(dniCliente)).thenReturn(null);

        // Llamo al método y espero la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioPrestamo.listarPrestamos(dniCliente)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No existe un cliente con el DNI ingresado", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dniCliente);
        verify(prestamoDao, never()).findPrestamosDelCliente(anyLong());
    }

    @Test
    void listarPrestamosClienteSinPrestamos() throws ClienteNoEncontradoException {
        // Preparo datos de entrada
        Long dniCliente = 12345678L;

        Cliente clienteMock = new Cliente();
        clienteMock.setDni(dniCliente);

        Set<Prestamo> prestamosVacios = new HashSet<>();

        // Mockeo la búsqueda del cliente
        when(clienteDao.findCliente(dniCliente)).thenReturn(clienteMock);

        // Mockeo la búsqueda de los préstamos del cliente (sin préstamos)
        when(prestamoDao.findPrestamosDelCliente(dniCliente)).thenReturn(prestamosVacios);

        // Llamo al método y espero la excepción
        ClienteSinPrestamosException exception = assertThrows(
                ClienteSinPrestamosException.class,
                () -> servicioPrestamo.listarPrestamos(dniCliente)
        );

        // Verifico el mensaje de la excepción
        assertEquals("El cliente no tiene préstamos", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dniCliente);
        verify(prestamoDao, times(1)).findPrestamosDelCliente(dniCliente);
    }
}
