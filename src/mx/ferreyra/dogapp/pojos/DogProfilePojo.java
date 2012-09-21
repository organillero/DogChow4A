package mx.ferreyra.dogapp.pojos;

import java.io.Serializable;
import java.util.Date;

import android.graphics.Bitmap;

public class DogProfilePojo implements Serializable {

    private static final long serialVersionUID = 506435013254921708L;

    public Integer idDueno;
    public Integer idUsusario;
    public String duenoNombre;
    public Integer duenoIdGenero;
    public Date duenoFechaCumpleanos;
    public Integer duenoIdEstado;

    public String mascotaNombre;
    public String mascotaRaza;
    public Integer mascotaIdGenero;
    public Integer mascotaIdTipoVida;
    public Integer mascotaIdActividadFisica;
    public Date mascotaFechaCumpleanos;
    public Bitmap mascotaImagen;
    
    public String tip; 

}
