package ar.edu.utn.frbb.tup.servicios;

import ar.edu.utn.frbb.tup.excepciones.*;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
import ar.edu.utn.frbb.tup.persistencia.MovimientosDao;
import ar.edu.utn.frbb.tup.presentacion.DTOs.ClienteDto;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioClientes {

    private final ValidacionesServicios validacionesServicios;
    private final ClienteDao clienteDao;
    private final CuentaDao cuentaDao;
    private final MovimientosDao movimientosDao;

    // Constructor con inyección de dependencias
    public ServicioClientes(ValidacionesServicios validacionesServicios, ClienteDao clienteDao, CuentaDao cuentaDao, MovimientosDao movimientosDao) {
        this.validacionesServicios = validacionesServicios;
        this.clienteDao = clienteDao;
        this.cuentaDao = cuentaDao;
        this.movimientosDao = movimientosDao;
    }

    public List<Cliente> mostrarClientes() throws ClientesVaciosException {
        return clienteDao.findAllClientes();
    }

    public void inicializarClientes() {
        clienteDao.inicializarClientes();
    }

    public Cliente crearCliente(ClienteDto clienteDto) throws ClienteExistenteException, ClienteMenorDeEdadException {
        // Validar cliente antes de crearlo
//        validacionesServicios.validarDni(clienteDto.getDni());
        validacionesServicios.validarClienteYaExiste(clienteDto);
        validacionesServicios.esMayordeEdad(clienteDto.getFechaNacimiento());

        // Crear el cliente y guardarlo en la capa de persistencia
        Cliente cliente = new Cliente(clienteDto);
        clienteDao.saveCliente(cliente);
        return cliente;
    }


    public Cliente eliminarCliente(long dni) throws ClienteNoEncontradoException, ClienteTieneCuentasException, ClienteTienePrestamosException {

        // Busca y valida que el cliente exista
        Cliente clienteBorrado = buscarCliente(dni);

        //Valido que el cliente no tenga cuentas
        validacionesServicios.validarClienteSinCuentas(dni);

        //Valido que el cliente no tenga prestamos
        validacionesServicios.validarClienteSinPrestamos(dni);

        //Elimino el cliente con el DNI ingresado
        clienteDao.deleteCliente(dni);

        //Elimino las relaciones que tiene con las Cuentas y Movimientos
        List<Long> cbuEliminar = cuentaDao.getRelacionesDni(clienteBorrado.getDni()); //Obtengo lista de todos los CBUs a eliminar

        for (Long cbu : cbuEliminar){
            cuentaDao.deleteCuenta(cbu);
            movimientosDao.deleteMovimiento(cbu);
        }
        return clienteBorrado;
    }

    public Cliente buscarCliente(long dni) throws ClienteNoEncontradoException {
        //Funcion que devuelve el cliente encontrado o devuelve null si no lo encontro
        Cliente cliente = clienteDao.findCliente(dni);
        if (cliente == null){
            throw new ClienteNoEncontradoException("No se encontro el cliente con el DNI: " + dni);
        }
        return cliente;
    }
}