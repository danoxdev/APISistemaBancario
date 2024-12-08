package ar.edu.utn.frbb.tup.persistencia;

import ar.edu.utn.frbb.tup.modelo.Movimiento;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MovimientosDao extends BaseDao<Movimiento>{
    private final String RUTA_ARCHIVO = "src/main/java/ar/edu/utn/frbb/tup/persistencia/data/movimientos.txt";

    public void inicializarMovimientos(){
        String encabezado = "CBU Origen, Fecha de operacion, Hora de operacion, Tipo de operacion, Monto";
        inicializarArchivo(encabezado, RUTA_ARCHIVO);
    }

    public void saveMovimiento(String tipoOperacion, double monto, Long cbu){
        Movimiento movimiento = new Movimiento();
        movimiento.setCbu(cbu);
        movimiento.setFechaOperacion(LocalDate.now());
        //Pongo la hora actual con la hora, minutos y segundos (sin coma)
        movimiento.setHoraOperacion(LocalTime.now().withNano(0));
        movimiento.setTipoOperacion(tipoOperacion);
        movimiento.setMonto(monto);

        String infoAguardar = movimiento.getCbu() + "," + movimiento.getFechaOperacion() + "," + movimiento.getHoraOperacion() + "," + movimiento.getTipoOperacion() + "," + movimiento.getMonto();
        saveInfo(infoAguardar, RUTA_ARCHIVO);
    }

    public void deleteMovimiento(Long cbu){
        deleteInfo(cbu, RUTA_ARCHIVO);
    }

    public List<Movimiento> findMovimientos(long CBU){
        List<Movimiento> movimientos = new ArrayList<>();
        try {

            File file = new File(RUTA_ARCHIVO);

            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String linea; //Leo el encabezado
            linea = reader.readLine(); //Salto encabezado

            while ((linea = reader.readLine()) != null) { //Condicion para que lea el archivo hasta el final y lo guarde en la variable linea

                //Empiezo a leer las lineas de los movimientos, y cada linea la divido por comas con el '.split(",")', para tener los datos de los movimientos
                String[] datos = linea.split(",");

                if (Long.parseLong(datos[0]) == CBU){
                    //Guardo en una lista todos los movimientos del cvu ingresado
                    movimientos.add(parseDatosToObjet(datos));
                }

            }
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return movimientos;
    }

    //Funcion para parsear los datos leidos del archivo a un objeto tipo 'Movimiento'
    @Override
    public Movimiento parseDatosToObjet(String[] datos){
        Movimiento movimiento = new Movimiento();

        movimiento.setCbu(Long.parseLong(datos[0]));
        movimiento.setFechaOperacion(LocalDate.parse(datos[1]));
        movimiento.setHoraOperacion(LocalTime.parse(datos[2]));
        movimiento.setTipoOperacion(datos[3]);
        movimiento.setMonto(Double.parseDouble(datos[4]));

        return movimiento;
    }

}

