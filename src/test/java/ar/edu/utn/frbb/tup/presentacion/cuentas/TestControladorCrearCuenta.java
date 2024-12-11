package ar.edu.utn.frbb.tup.presentacion.cuentas;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.CuentaExistenteException;
import ar.edu.utn.frbb.tup.excepciones.TipoCuentaExistenteException;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.modelo.TipoCuenta;
import ar.edu.utn.frbb.tup.modelo.TipoMoneda;
import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
import ar.edu.utn.frbb.tup.presentacion.controladores.ControladorCuentas;
import ar.edu.utn.frbb.tup.presentacion.DTOs.CuentaDto;
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
class TestControladorCrearCuenta {

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
    void crearCuentaExitosamente() throws ClienteNoEncontradoException, TipoCuentaExistenteException, CuentaExistenteException {
        // Cargo los datos de entrada
        CuentaDto cuentaDto = new CuentaDto();
        cuentaDto.setDniTitular(12345678L);
        cuentaDto.setTipoCuenta("CAJA_AHORRO");
        cuentaDto.setTipoMoneda("PESOS");

        // Creo la cuenta esperada como resultado de la creación
        Cuenta cuentaEsperada = new Cuenta();
        cuentaEsperada.setCbu(12345678L);
        cuentaEsperada.setTipoCuenta(TipoCuenta.CAJA_AHORRO);
        cuentaEsperada.setTipoMoneda(TipoMoneda.PESOS);

        // Simulo el comportamiento del servicio
        doNothing().when(validacionesPresentacion).validarCuenta(cuentaDto);
        when(servicioCuentas.crearCuenta(cuentaDto)).thenReturn(cuentaEsperada);

        // Ejecuto el método del controlador
        ResponseEntity<Cuenta> response = controladorCuentas.crearCuenta(cuentaDto);

        // Compruebo que el código de estado HTTP sea 201 (CREATED) y el cuerpo de la respuesta sea la cuenta esperada
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(cuentaEsperada, response.getBody());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCuenta(cuentaDto);
        verify(servicioCuentas, times(1)).crearCuenta(cuentaDto);
    }

    @Test
    void crearCuentaClienteNoEncontrado() throws ClienteNoEncontradoException, TipoCuentaExistenteException, CuentaExistenteException {
        // Cargo los datos de entrada
        CuentaDto cuentaDto = new CuentaDto();
        cuentaDto.setDniTitular(12345678L);
        cuentaDto.setTipoCuenta("CAJA_AHORRO");
        cuentaDto.setTipoMoneda("PESOS");

        // Simulo que el cliente no existe
        doNothing().when(validacionesPresentacion).validarCuenta(cuentaDto);
        doThrow(new ClienteNoEncontradoException("No se encontró el cliente con DNI: 12345678"))
                .when(servicioCuentas).crearCuenta(cuentaDto);

        // Llamo al controlador y espero que se lance la excepción
        ClienteNoEncontradoException exception = assertThrows(
                ClienteNoEncontradoException.class,
                () -> controladorCuentas.crearCuenta(cuentaDto)
        );

        // Verifico que el mensaje de la excepción sea el esperado
        assertEquals("No se encontró el cliente con DNI: 12345678", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCuenta(cuentaDto);
        verify(servicioCuentas, times(1)).crearCuenta(cuentaDto);
    }

    @Test
    void crearCuentaYaExistente() throws ClienteNoEncontradoException, TipoCuentaExistenteException, CuentaExistenteException {
        // Cargo los datos de entrada
        CuentaDto cuentaDto = new CuentaDto();
        cuentaDto.setDniTitular(12345678L);
        cuentaDto.setTipoCuenta("CAJA_AHORRO");
        cuentaDto.setTipoMoneda("PESOS");

        // Simulo que ya existe una cuenta con el mismo tipo para el cliente
        doNothing().when(validacionesPresentacion).validarCuenta(cuentaDto);
        doThrow(new TipoCuentaExistenteException("El cliente ya tiene una cuenta de tipo CAJA_AHORRO en PESOS"))
                .when(servicioCuentas).crearCuenta(cuentaDto);

        // Llamo al controlador y espero que se lance la excepción
        TipoCuentaExistenteException exception = assertThrows(
                TipoCuentaExistenteException.class,
                () -> controladorCuentas.crearCuenta(cuentaDto)
        );

        // Verifico que el mensaje de la excepción sea el esperado
        assertEquals("El cliente ya tiene una cuenta de tipo CAJA_AHORRO en PESOS", exception.getMessage());

        // Verifico las interacciones con los mocks
        verify(validacionesPresentacion, times(1)).validarCuenta(cuentaDto);
        verify(servicioCuentas, times(1)).crearCuenta(cuentaDto);
    }
}
