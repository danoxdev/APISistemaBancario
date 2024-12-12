package ar.edu.utn.frbb.tup.servicios;

import org.springframework.stereotype.Service;

@Service
public class ServicioScoreCrediticio {
    public boolean scoreCrediticio(Long dni){
        //Se verifica si el DNI ingresado es par o impar. Si es par, se devuelve false. Si es impar, se devuelve true.
        if (dni%2==0) {
            return false;
        }else{
            return true;
        }
    }
}
