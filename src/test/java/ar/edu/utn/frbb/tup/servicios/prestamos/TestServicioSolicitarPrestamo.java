package ar.edu.utn.frbb.tup.servicios.prestamos;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaMonedaNoExisteException;
import ar.edu.utn.frbb.tup.modelo.*;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.PrestamoDao;
import ar.edu.utn.frbb.tup.servicios.ServicioPrestamo;
import ar.edu.utn.frbb.tup.servicios.ServicioScoreCrediticio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServicioSolicitarPrestamo {

    @InjectMocks
    private ServicioPrestamo servicioPrestamo;

    @Mock
    private PrestamoDao prestamoDao;

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ServicioScoreCrediticio servicioScoreCrediticio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void solicitarPrestamoExitosamente() throws ClienteNoEncontradoException, CuentaMonedaNoExisteException {
        // Preparo datos de entrada
        Long dniCliente = 12345678L;
        int plazoMeses = 12;
        double monto = 10000;
        String tipoMoneda = "PESOS";

        Cliente clienteMock = new Cliente();
        clienteMock.setDni(dniCliente);

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setDniTitular(dniCliente);
        cuentaMock.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuentaMock.setTipoMoneda(TipoMoneda.PESOS);
        cuentaMock.setSaldo(0);
        cuentaMock.setCbu(11111111L);

        List<Prestamo> prestamosMock = new ArrayList<>();

        // Mockeo la búsqueda del cliente
        when(clienteDao.findCliente(dniCliente)).thenReturn(clienteMock);

        // Mockeo la búsqueda de cuentas del cliente
        when(cuentaDao.findAllCuentasDelCliente(dniCliente)).thenReturn(new HashSet<>(Collections.singletonList(cuentaMock)));

        // Mockeo el score crediticio (aprobado)
        when(servicioScoreCrediticio.scoreCrediticio(dniCliente)).thenReturn(true);

        // Mockeo la búsqueda de todos los préstamos
        when(prestamoDao.findAllPrestamos()).thenReturn(prestamosMock);

        // Mockeo el guardado de préstamo
        doNothing().when(prestamoDao).savePrestamo(any(Prestamo.class));

        // Mockeo el guardado de cuenta
        doNothing().when(cuentaDao).saveCuenta(any(Cuenta.class));
        doNothing().when(cuentaDao).deleteCuenta(anyLong());

        // Ejecuto el método a testear
        Map<String, Object> resultado = servicioPrestamo.solicitarPrestamo(dniCliente, plazoMeses, monto, tipoMoneda);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals("Aprobado", resultado.get("estado"));
        assertTrue(((String) resultado.get("mensaje")).contains("acreditado en su cuenta bancaria"));
        assertNotNull(resultado.get("planPagos"));

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dniCliente);
        verify(cuentaDao, times(1)).findAllCuentasDelCliente(dniCliente);
        verify(servicioScoreCrediticio, times(1)).scoreCrediticio(dniCliente);
        verify(prestamoDao, times(1)).findAllPrestamos();
        verify(prestamoDao, times(1)).savePrestamo(any(Prestamo.class));
        verify(cuentaDao, times(1)).saveCuenta(any(Cuenta.class));
        verify(cuentaDao, times(1)).deleteCuenta(anyLong());
    }

    @Test
    void solicitarPrestamoClienteNoEncontrado() {
        // Preparo datos de entrada
        Long dniCliente = 12345678L;
        int plazoMeses = 12;
        double monto = 10000;
        String tipoMoneda = "PESOS";

        // Mockeo que el cliente no existe
        when(clienteDao.findCliente(dniCliente)).thenReturn(null);

        // Llamo al método y espero la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioPrestamo.solicitarPrestamo(dniCliente, plazoMeses, monto, tipoMoneda)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No existe un cliente con el DNI ingresado", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dniCliente);
        verify(cuentaDao, never()).findAllCuentasDelCliente(anyLong());
        verify(servicioScoreCrediticio, never()).scoreCrediticio(anyLong());
        verify(prestamoDao, never()).findAllPrestamos();
        verify(prestamoDao, never()).savePrestamo(any(Prestamo.class));
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
        verify(cuentaDao, never()).deleteCuenta(anyLong());
    }

    @Test
    void solicitarPrestamoCuentaMonedaNoExiste() throws ClienteNoEncontradoException {
        // Preparo datos de entrada
        Long dniCliente = 12345678L;
        int plazoMeses = 12;
        double monto = 10000;
        String tipoMoneda = "DOLARES";

        Cliente clienteMock = new Cliente();
        clienteMock.setDni(dniCliente);

        Cuenta cuentaMock = new Cuenta();
        cuentaMock.setDniTitular(dniCliente);
        cuentaMock.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuentaMock.setTipoMoneda(TipoMoneda.PESOS);
        cuentaMock.setSaldo(0);
        cuentaMock.setCbu(11111111L);

        // Mockeo la búsqueda del cliente
        when(clienteDao.findCliente(dniCliente)).thenReturn(clienteMock);

        // Mockeo la búsqueda de cuentas del cliente
        when(cuentaDao.findAllCuentasDelCliente(dniCliente)).thenReturn(new HashSet<>(Collections.singletonList(cuentaMock)));

        // Llamo al método y espero la excepción
        CuentaMonedaNoExisteException exception = assertThrows(
                CuentaMonedaNoExisteException.class,
                () -> servicioPrestamo.solicitarPrestamo(dniCliente, plazoMeses, monto, tipoMoneda)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No existe una cuenta bancaria con la moneda ingresada", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(dniCliente);
        verify(cuentaDao, times(1)).findAllCuentasDelCliente(dniCliente);
        verify(servicioScoreCrediticio, never()).scoreCrediticio(anyLong());
        verify(prestamoDao, never()).findAllPrestamos();
        verify(prestamoDao, never()).savePrestamo(any(Prestamo.class));
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
        verify(cuentaDao, never()).deleteCuenta(anyLong());
    }
}
