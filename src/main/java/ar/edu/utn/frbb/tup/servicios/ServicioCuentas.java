//package ar.edu.utn.frbb.tup.servicios;
//
//import ar.edu.utn.frbb.tup.excepciones.CuentasVaciasException;
//import ar.edu.utn.frbb.tup.modelo.Cliente;
//import ar.edu.utn.frbb.tup.modelo.Cuenta;
//import ar.edu.utn.frbb.tup.persistencia.ClienteDao;
//import ar.edu.utn.frbb.tup.persistencia.CuentaDao;
//import ar.edu.utn.frbb.tup.presentacion.ValidacionesPresentacion;
//import java.util.List;
//
//public class ServicioCuentas {
//    ValidacionesServicios validar = new ValidacionesServicios();
//    ValidacionesPresentacion validarEntrada = new ValidacionesPresentacion();
//    ServicioClientes buscar = new ServicioClientes();
//    CuentaDao cuentaDao = new CuentaDao();
//    ClienteDao clienteDao = new ClienteDao();
//
////    public void findAllCuentas() throws CuentasVaciasException {
////        cuentaDao.findAllCuentas();
////    }
//
//    public void inicializarCuentas() {
//        cuentaDao.inicializarCuentas();
//    }
//
////    public Cuenta buscarCuentas(Set<Cuenta> cuentas, String cbu){
////        for (Cuenta c : cuentas) {
////            if (c.getCbu() == Long.parseLong(cbu)) {
////                return c;
////            }
////        }
////        return null;
////    }
//
//    public void mostrarCuentas(List<Cliente> clientes) throws CuentasVaciasException {
//        String dni;
//        //pedimos el dni del cliente del que queremos mostrar sus cuentas
//        do {
//            System.out.print("Ingrese el dni del cliente: ");
//            dni = entrada.nextLine();
//        } while (!validar.validarDni(dni));
//        Cliente cliente = buscar.buscarCliente(clientes, dni);
//        //validamos que el cliente exista
//        if (cliente == null) {
//            System.out.println("Cliente no encontrado, primero debe crearlo");
//        } else {
//            try {
//                //si existe creo una lista y la lleno con las cuentas que posea el cliente
//                List<Long> cbuList = cuentaDao.getRelacionesDni(Long.parseLong(dni));
//                if (cbuList.isEmpty()) {
//                    throw new CuentasVaciasException("El cliente no tiene cuentas registradas.");
//                }
//                //Muestro las cuentas recorriendo la lista de cbus y mostrando los datos de cada una de ellas
//                System.out.println("Cuentas del cliente:");
//                for (Long cbu : cbuList) {
//                    Cuenta cuenta = cuentaDao.findCuenta(cbu);
//                    if (cuenta != null) {
//                        System.out.println("CBU: " + cuenta.getCbu());
//                        System.out.println("Tipo de cuenta: " + cuenta.getTipoCuenta());
//                        System.out.println("Moneda: " + cuenta.getTipoMoneda());
//                        System.out.println("Alias: " + cuenta.getAlias());
//                        System.out.println("Fecha de creación: " + cuenta.getFechaCreacion());
//                        System.out.println("Saldo: " + cuenta.getSaldo());
//                        System.out.println("-------------------------");
//                    }
//                }
//            } catch (CuentasVaciasException e) {
//                System.out.println(e.getMessage());
//            }
//        }
//    }
//
//    public void crearCuenta(List<Cliente> clientes){
//        String dni;
//        do{
//            System.out.print("Ingrese el dni del cliente: ");
//            dni = entrada.nextLine();
//        } while (!validar.validarDni(dni));
//
//
//        Cliente cliente = buscar.buscarCliente(clientes, dni);
//
//        if (cliente == null) {
//            System.out.println("Cliente no encontrado, primero debe crearlo");
//        }
//        else{
//            Cuenta cuenta = inputcuenta.ingresarCuenta();
//            cuenta.setDniTitular(Long.parseLong(dni));
//            //agrego la cuenta a mi lista de cuentas del cliente
////          cliente.addCuenta(cuenta);
//            cuentaDao.saveCuenta(cuenta);
//            System.out.println("Cuenta creada con exito: " + cuenta.getAlias());
//            System.out.println("CBU: " + cuenta.getCbu());
//        }
//    }
//
//    public void eliminarCuenta (List<Cliente> clientes){
//        String dni;
//        do{
//            System.out.print("Ingrese el dni del cliente: ");
//            dni = entrada.nextLine();
//        } while (!validar.validarDni(dni));
//
//        Cliente cliente = buscar.buscarCliente(clientes, dni);
//
//        if (cliente == null) {
//            System.out.println("Cliente no encontrado, primero debe crearlo");
//        }
//        else{
//            String cbu;
//            do {
//                System.out.print("Ingrese el CBU de la cuenta a eliminar: ");
//                cbu = entrada.nextLine();
//            } while (!validarEntrada.esLong(cbu));
//
//            Cuenta cuenta = cuentaDao.findCuentaDelCliente(Long.parseLong(cbu),Long.parseLong(dni));
////            Cuenta cuenta = buscarCuentas(cliente.getCuentas(), cbu);
//            if(cuenta == null) {
//                System.out.println("Cuenta no encontrada");
//            } else {
//                System.out.println("La cuenta " + cuenta.getCbu() + " ha sido eliminada." );
//                cuentaDao.deleteCuenta(Long.parseLong(cbu));
////                cliente.getCuentas().remove(cuenta);
//            }
//
//        }
//    }
//}
