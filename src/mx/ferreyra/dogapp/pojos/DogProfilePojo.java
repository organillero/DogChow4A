package mx.ferreyra.dogapp.pojos;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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
    private String mascotaImagen;

    public String tip; 


    public void setMascotaImagen (String mascotaImagen){
        this.mascotaImagen = mascotaImagen;
    } 

    public Bitmap getMascotaImagen (){
        byte[] bytes = Base64.decode(mascotaImagen, Base64.DEFAULT);
        InputStream is = new ByteArrayInputStream(bytes);
        Bitmap bmp = BitmapFactory.decodeStream(is);

        return bmp;
    }

}
