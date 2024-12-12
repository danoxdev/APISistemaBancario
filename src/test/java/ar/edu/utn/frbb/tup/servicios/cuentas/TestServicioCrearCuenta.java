package ar.edu.utn.frbb.tup.servicios.cuentas;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaExistenteException;
import ar.edu.utn.frbb.tup.excepciones.TipoCuentaExistenteException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;
import ar.edu.utn.frbb.tup.servicios.ServicioCuentas;
import ar.edu.utn.frbb.tup.servicios.ValidacionesServicios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServicioCrearCuenta {

    @InjectMocks
    private ServicioCuentas servicioCuentas;

    @Mock
    private ValidacionesServicios validacionesServicios;

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteDao clienteDao;

    @Mock
    private MovimientosDao movimientosDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearCuentaExitosamente() throws ClienteNoEncontradoException, CuentaExistenteException, TipoCuentaExistenteException {
        // Preparo datos de entrada
        CuentaDto cuentaDto = new CuentaDto();
        cuentaDto.setDniTitular(12345678L);
        cuentaDto.setTipoCuenta("CAJA_AHORRO");
        cuentaDto.setTipoMoneda("PESOS");
        cuentaDto.setAlias("miAlias");

        // Mockeo la búsqueda del cliente
        Cliente clienteMock = new Cliente();
        clienteMock.setDni(cuentaDto.getDniTitular());
        when(clienteDao.findCliente(cuentaDto.getDniTitular())).thenReturn(clienteMock);

        // Mockeo la búsqueda de la cuenta (no existe)
        when(cuentaDao.findCuenta(anyLong())).thenReturn(null);

        // Mockeo la validación de cuenta con mismo tipo y moneda (no lanza excepción)
        doNothing().when(validacionesServicios).cuentaMismoTipoMoneda(any(), any(), anyLong());

        // Mockeo el guardado de la cuenta (no lanza excepción)
        doNothing().when(cuentaDao).saveCuenta(any(Cuenta.class));

        // Ejecuto el método a testear
        Cuenta resultado = servicioCuentas.crearCuenta(cuentaDto);

        // Verifico el resultado
        assertNotNull(resultado);
        assertEquals(cuentaDto.getDniTitular(), resultado.getDniTitular());
        assertEquals(cuentaDto.getTipoCuenta(), resultado.getTipoCuenta().name());
        assertEquals(cuentaDto.getTipoMoneda(), resultado.getTipoMoneda().name());
        assertEquals(cuentaDto.getAlias(), resultado.getAlias());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(cuentaDto.getDniTitular());
        verify(cuentaDao, times(1)).findCuenta(anyLong());
        verify(validacionesServicios, times(1)).cuentaMismoTipoMoneda(any(), any(), anyLong());
        verify(cuentaDao, times(1)).saveCuenta(any(Cuenta.class));
    }

    @Test
    void crearCuentaClienteNoEncontrado() throws TipoCuentaExistenteException {
        // Preparo datos de entrada
        CuentaDto cuentaDto = new CuentaDto();
        cuentaDto.setDniTitular(12345678L);
        cuentaDto.setTipoCuenta("CAJA_AHORRO");
        cuentaDto.setTipoMoneda("PESOS");
        cuentaDto.setAlias("miAlias");

        // Mockeo que el cliente no existe
        when(clienteDao.findCliente(cuentaDto.getDniTitular())).thenReturn(null);

        // Llamo al método y espero la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> servicioCuentas.crearCuenta(cuentaDto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("No se encontro el cliente con el DNI: " + cuentaDto.getDniTitular(), exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(cuentaDto.getDniTitular());
        verify(cuentaDao, never()).findCuenta(anyLong());
        verify(validacionesServicios, never()).cuentaMismoTipoMoneda(any(), any(), anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
    }

    @Test
    void crearCuentaExistente() throws TipoCuentaExistenteException {
        // Preparo datos de entrada
        CuentaDto cuentaDto = new CuentaDto();
        cuentaDto.setDniTitular(12345678L);
        cuentaDto.setTipoCuenta("CAJA_AHORRO");
        cuentaDto.setTipoMoneda("PESOS");
        cuentaDto.setAlias("miAlias");

        // Mockeo la búsqueda del cliente
        Cliente clienteMock = new Cliente();
        clienteMock.setDni(cuentaDto.getDniTitular());
        when(clienteDao.findCliente(cuentaDto.getDniTitular())).thenReturn(clienteMock);

        // Mockeo que la cuenta ya existe
        Cuenta cuentaExistente = new Cuenta();
        when(cuentaDao.findCuenta(anyLong())).thenReturn(cuentaExistente);

        // Llamo al método y espero la excepción
        CuentaExistenteException exception = assertThrows(
                CuentaExistenteException.class,
                () -> servicioCuentas.crearCuenta(cuentaDto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("Ya tiene una cuenta con ese CBU", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(cuentaDto.getDniTitular());
        verify(cuentaDao, times(1)).findCuenta(anyLong());
        verify(validacionesServicios, never()).cuentaMismoTipoMoneda(any(), any(), anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
    }

    @Test
    void crearCuentaTipoMonedaExistente() throws ClienteNoEncontradoException, TipoCuentaExistenteException {
        // Preparo datos de entrada
        CuentaDto cuentaDto = new CuentaDto();
        cuentaDto.setDniTitular(12345678L);
        cuentaDto.setTipoCuenta("CAJA_AHORRO");
        cuentaDto.setTipoMoneda("PESOS");
        cuentaDto.setAlias("miAlias");

        // Mockeo la búsqueda del cliente
        Cliente clienteMock = new Cliente();
        clienteMock.setDni(cuentaDto.getDniTitular());
        when(clienteDao.findCliente(cuentaDto.getDniTitular())).thenReturn(clienteMock);

        // Mockeo la búsqueda de la cuenta (no existe)
        when(cuentaDao.findCuenta(anyLong())).thenReturn(null);

        // Mockeo la validación de cuenta con mismo tipo y moneda (lanza excepción)
        doThrow(new TipoCuentaExistenteException("Ya tiene una cuenta con ese tipo de cuenta y tipo de moneda"))
                .when(validacionesServicios).cuentaMismoTipoMoneda(any(), any(), anyLong());

        // Llamo al método y espero la excepción
        TipoCuentaExistenteException exception = assertThrows(
                TipoCuentaExistenteException.class,
                () -> servicioCuentas.crearCuenta(cuentaDto)
        );

        // Verifico el mensaje de la excepción
        assertEquals("Ya tiene una cuenta con ese tipo de cuenta y tipo de moneda", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(clienteDao, times(1)).findCliente(cuentaDto.getDniTitular());
        verify(cuentaDao, times(1)).findCuenta(anyLong());
        verify(validacionesServicios, times(1)).cuentaMismoTipoMoneda(any(), any(), anyLong());
        verify(cuentaDao, never()).saveCuenta(any(Cuenta.class));
    }
}
