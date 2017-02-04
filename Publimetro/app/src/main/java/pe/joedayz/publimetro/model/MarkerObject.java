package pe.joedayz.publimetro.model;

/**
 * Created by josediaz on 3/5/16.
 */

import java.io.Serializable;

/**
 * Created by josediaz on 2/17/16.
 */

public class MarkerObject implements Serializable {

    private Establecimiento establecimiento;
    private Ubicacion ubicacion;

    public MarkerObject(Establecimiento establecimiento, Ubicacion ubicacion) {
        this.establecimiento = establecimiento;
        this.ubicacion = ubicacion;
    }

    public Establecimiento getEstablecimiento() {
        return establecimiento;
    }

    public void setEstablecimiento(Establecimiento establecimiento) {
        this.establecimiento = establecimiento;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public String toString() {
        return "MarkerObject{" +
                "establecimiento=" + establecimiento +
                ", ubicacion=" + ubicacion +
                '}';
    }
}