package ar.edu.utn.frbb.tup.persistencia;

import ar.edu.utn.frbb.tup.excepciones.CuentasVaciasException;
import ar.edu.utn.frbb.tup.modelo.Cliente;
import ar.edu.utn.frbb.tup.modelo.Cuenta;
import ar.edu.utn.frbb.tup.modelo.Prestamo;
import ar.edu.utn.frbb.tup.modelo.TipoPersona;
import ar.edu.utn.frbb.tup.excepciones.ClienteNoEncontradoException;
import ar.edu.utn.frbb.tup.excepciones.ClientesVaciosException;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public class ClienteDao extends BaseDao<Cliente> {
    private final String RUTA_ARCHIVO = "src/main/java/ar/edu/utn/frbb/tup/persistencia/data/clientes.txt";

    private final CuentaDao cuentaDao;
    private final PrestamoDao prestamoDao; // Se agrega PrestamoDao

    public ClienteDao() {
        this.cuentaDao = new CuentaDao();
        this.prestamoDao = new PrestamoDao();
    }

    public ClienteDao(CuentaDao cuentaDao, PrestamoDao prestamoDao) {
        this.cuentaDao = cuentaDao;
        this.prestamoDao = prestamoDao;
    }

    public void inicializarClientes() {
        String encabezado = "DNI, Nombre, Apellido, Domicilio, Fecha Nacimiento, Banco, Tipo Persona, Fecha alta";
        inicializarArchivo(encabezado, RUTA_ARCHIVO);
    }

    public void saveCliente(Cliente cliente) {
        String infoAguardar = cliente.getDni() + "," + cliente.getNombre() + "," + cliente.getApellido() + "," + cliente.getDomicilio() + "," + cliente.getFechaNacimiento() + "," + cliente.getBanco() + "," + cliente.getTipoPersona() + "," + cliente.getFechaAlta();
        saveInfo(infoAguardar, RUTA_ARCHIVO);
    }

    public void deleteCliente(Long dni) {
        deleteInfo(dni, RUTA_ARCHIVO);
    }

    public Cliente findCliente(Long dni) {
        Cliente cliente = findInfo(dni, RUTA_ARCHIVO);

        // Recuperar las cuentas del cliente
        Set<Cuenta> cuentas = cuentaDao.findAllCuentasDelCliente(dni);
        if (!cuentas.isEmpty()) {
            cliente.setCuentas(cuentas);
        }

        // Recuperar los préstamos del cliente
        Set<Prestamo> prestamos = prestamoDao.findPrestamosDelCliente(dni);
        if (!prestamos.isEmpty()) {
            cliente.setPrestamos(prestamos);
        }

        return cliente;
    }

    public List<Cliente> findAllClientes() throws CuentasVaciasException {
        List<Cliente> clientes = findAllInfo(RUTA_ARCHIVO);

        if (clientes.isEmpty()) {
            throw new CuentasVaciasException("No se encontraron clientes.");
        }

        for (Cliente cliente : clientes) {
            // Agregar préstamos
            Set<Cuenta> cuentas = cuentaDao.findAllCuentasDelCliente(cliente.getDni());
            if (!cuentas.isEmpty()) {
                cliente.setCuentas(cuentas);
            }
            // Agregar préstamos
            Set<Prestamo> prestamos = prestamoDao.findPrestamosDelCliente(cliente.getDni());
            if (!prestamos.isEmpty()) {
                cliente.setPrestamos(prestamos);
            }
        }

        return clientes;
    }

    // Función para parsear los datos leídos del archivo a un objeto tipo 'Cliente'
    @Override
    public Cliente parseDatosToObjet(String[] datos) {
        Cliente cliente = new Cliente();

        cliente.setDni(Long.parseLong(datos[0]));
        cliente.setNombre(datos[1]);
        cliente.setApellido(datos[2]);
        cliente.setDomicilio(datos[3]);
        cliente.setFechaNacimiento(LocalDate.parse(datos[4]));
        cliente.setBanco(datos[5]);
        cliente.setTipoPersona(TipoPersona.fromString(datos[6]));
        cliente.setFechaAlta(LocalDate.parse(datos[7]));

        return cliente;
    }
}

