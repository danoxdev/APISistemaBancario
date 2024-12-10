package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClienteSinPrestamosException;
import ar.edu.utn.frbb.tup.excepciones.PrestamosVaciosException;
import ar.edu.utn.frbb.tup.modelo.*;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.PrestamoDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.excepciones.CuentaMonedaNoExisteException;
import ar.edu.utn.frbb.tup.servicios.ServicioScoreCrediticio;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ServicioPrestamo {

    private PrestamoDao prestamoDao;
    private ClienteDao clienteDao;
    private CuentaDao cuentaDao;
    private ServicioScoreCrediticio servicioScoreCrediticio;

    // Constructor que inyecta las dependencias
    public ServicioPrestamo(PrestamoDao prestamoDao, ClienteDao clienteDao, CuentaDao cuentaDao, ServicioScoreCrediticio servicioScoreCrediticio) {
        this.prestamoDao = prestamoDao;
        this.clienteDao = clienteDao;
        this.cuentaDao = cuentaDao;
        this.servicioScoreCrediticio = servicioScoreCrediticio;
    }

    public void inicializarPrestamos() {
        prestamoDao.inicializarPrestamos();
    }

    // Método para solicitar un préstamo
    public Map<String, Object> solicitarPrestamo(Long dniCliente, int plazoMeses, double monto, String tipoMoneda)
            throws CuentaMonedaNoExisteException, ClienteNoEncontradoException {

        double tasaInteresMensual = 0.05; // Tasa de interés mensual fija

        // Verifica que exista un cliente con el DNI ingresado
        Cliente cliente = clienteDao.findCliente(dniCliente);
        if (cliente == null) {
            throw new ClienteNoEncontradoException("No existe un cliente con el DNI ingresado");
        }

        // Convertimos el String moneda a TipoMoneda
        TipoMoneda moneda;
        try {
            moneda = TipoMoneda.fromString(tipoMoneda);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("La moneda ingresada no es válida: " + tipoMoneda);
        }

        // Se busca entre las cuentas bancarias del cliente la cuenta adecuada
        List<Cuenta> cuentasBancariasCliente = new ArrayList<>(cuentaDao.findAllCuentasDelCliente(cliente.getDni()));

        Cuenta cuentaBancaria = null;
        for (Cuenta cuenta : cuentasBancariasCliente) {
            if (cuenta.getTipoMoneda().equals(moneda)) {
                if (cuenta.getTipoCuenta() == TipoCuenta.CAJA_AHORRO) {
                    cuentaBancaria = cuenta;
                    break; // Caja de ahorro tiene prioridad, salimos del bucle
                }
                if (cuenta.getTipoCuenta() == TipoCuenta.CUENTA_CORRIENTE) {
                    cuentaBancaria = cuenta; // Seleccionamos cuenta corriente si no hay caja de ahorro
                }
            }
        }

        // Si no se encontró una cuenta con la moneda ingresada, se lanza una excepción
        if (cuentaBancaria == null) {
            throw new CuentaMonedaNoExisteException("No existe una cuenta bancaria con la moneda ingresada");
        }

        // Se verifica si el cliente tiene un buen score crediticio
        String estadoPrestamo;
        String mensaje;
        if (servicioScoreCrediticio.scoreCrediticio(cliente.getDni())) {

            // Si el préstamo es aprobado, se calcula el monto total y el monto mensual
            double montoTotal = monto * plazoMeses * tasaInteresMensual + monto;
            double montoMensual = montoTotal / plazoMeses;

            // Se genera el ID del préstamo buscando el ID del último préstamo agregado y se le suma 1
            List<Prestamo> prestamos = prestamoDao.findAllPrestamos();
            int id = prestamos.isEmpty() ? 1 : prestamos.get(prestamos.size() - 1).getIdPrestamo() + 1;

            // Se crea el préstamo y se agrega a la base de datos
            Prestamo prestamo = new Prestamo(id, cliente.getDni(), monto, plazoMeses, 0, monto);
            prestamoDao.savePrestamo(prestamo);

            //Elimino la cuenta para actualizar el archivo
            cuentaDao.deleteCuenta(cuentaBancaria.getCbu());

            //Se actualiza el saldo de la cuenta bancaria
            cuentaBancaria.setSaldo(cuentaBancaria.getSaldo() + monto);

            //Guardo la cuenta con el nuevo saldo
            cuentaDao.saveCuenta(cuentaBancaria);

            // Se crea el plan de pagos del préstamo
            List<Object> planPagos = new ArrayList<>();
            for (int j = 0; j < plazoMeses; j++) {
                Map<String, Object> cuota = new LinkedHashMap<>();
                cuota.put("cuotaNro", j + 1);
                cuota.put("monto", montoMensual);
                planPagos.add(cuota);
            }

            // Se crea el resultado para retornar con los datos del préstamo
            estadoPrestamo = "Aprobado";
            mensaje = "El préstamo fue acreditado en su cuenta bancaria (CBU: " + cuentaBancaria.getCbu() + ")";

            Map<String, Object> resultado = new LinkedHashMap<>();
            resultado.put("estado", estadoPrestamo);
            resultado.put("mensaje", mensaje);
            resultado.put("planPagos", planPagos);

            return resultado;

        } else {
            // Si el préstamo no se aprueba
            estadoPrestamo = "Rechazado";
            mensaje = "El préstamo fue rechazado debido a que su score crediticio es bajo";

            Map<String, Object> resultado = new LinkedHashMap<>();
            resultado.put("estado", estadoPrestamo);
            resultado.put("mensaje", mensaje);
            resultado.put("planPagos", "No hay plan de pagos");

            return resultado;
        }
    }

    // Método para listar los préstamos de un cliente por DNI
    public Set<Prestamo> listarPrestamos(Long dniCliente)
            throws ClienteNoEncontradoException, ClienteSinPrestamosException {

        // Se verifica que exista un cliente con el DNI ingresado
        Cliente cliente = clienteDao.findCliente(dniCliente);
        if (cliente == null) {
            throw new ClienteSinPrestamosException ("No existe un cliente con el DNI ingresado");
        }

        // Se arma el resultado con la lista de préstamos del cliente y se retorna
        Set<Prestamo> prestamosDelCliente = prestamoDao.findPrestamosDelCliente(dniCliente);
        if (prestamosDelCliente.isEmpty()) {
            throw new ClienteSinPrestamosException ("El cliente no tiene préstamos");
        } else {
            return prestamosDelCliente;
        }
    }
}
