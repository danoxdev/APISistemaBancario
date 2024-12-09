package ar.edu.utn.frbb.tup.persistencia;

import ar.edu.utn.frbb.tup.excepciones.PrestamosVaciosException;
import ar.edu.utn.frbb.tup.modelo.Prestamo;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class PrestamoDao extends BaseDao<Prestamo> {

    private final String RUTA_ARCHIVO = "src/main/java/ar/edu/utn/frbb/tup/persistencia/data/prestamos.txt";

    // Inicializa el archivo de préstamos con el encabezado
    public void inicializarPrestamos() {
        String encabezado = "ID Prestamo, DNI Cliente, Monto, Plazo Meses, Pagos Realizados, Saldo Restante";
        inicializarArchivo(encabezado, RUTA_ARCHIVO);
    }

    // Guarda un préstamo en el archivo
    public void savePrestamo(Prestamo prestamo) {
        String infoAguardar = prestamo.getIdPrestamo() + "," +
                prestamo.getDniCliente() + "," +
                prestamo.getMonto() + "," +
                prestamo.getPlazoMeses() + "," +
                prestamo.getPagosRealizados() + "," +
                prestamo.getSaldoRestante();

        saveInfo(infoAguardar, RUTA_ARCHIVO);
    }

    public Set<Prestamo> findPrestamosDelCliente(Long dniCliente) {
        Set<Prestamo> prestamosCliente = new HashSet<>();
        List<Prestamo> prestamos = findAllInfo(RUTA_ARCHIVO);

        // Filtrar préstamos por DNI del cliente
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getDniCliente().equals(dniCliente)) {
                prestamosCliente.add(prestamo);
            }
        }
        return prestamosCliente;
    }

    // Elimina un préstamo por su ID
    public void deletePrestamo(int idPrestamo) {
        deleteInfo(idPrestamo, RUTA_ARCHIVO);
    }

    // Busca un préstamo por su ID
    public Prestamo findPrestamo(int idPrestamo) {
        return findInfo(idPrestamo, RUTA_ARCHIVO);
    }

    // Obtiene todos los préstamos
    public List<Prestamo> findAllPrestamos() {
        List<Prestamo> prestamos = findAllInfo(RUTA_ARCHIVO);
        return prestamos; // Devuelve la lista, aunque esté vacía
    }

    // Convierte una línea del archivo en un objeto Prestamo
    @Override
    public Prestamo parseDatosToObjet(String[] datos) {
        Prestamo prestamo = new Prestamo();

        prestamo.setIdPrestamo(Integer.parseInt(datos[0]));
        prestamo.setDniCliente(Long.parseLong(datos[1]));
        prestamo.setMonto(Double.parseDouble(datos[2]));
        prestamo.setPlazoMeses(Integer.parseInt(datos[3]));
        prestamo.setPagosRealizados(Integer.parseInt(datos[4]));
        prestamo.setSaldoRestante(Double.parseDouble(datos[5]));

        return prestamo;
    }
}
