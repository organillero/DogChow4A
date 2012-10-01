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

    private Integer idDueno;
    private Integer idUsuario;
    private String  duenoNombre;
    private Integer duenoIdGenero;
    private Date    duenoFechaCumpleanos;
    private Integer duenoIdEstado;

    private String  mascotaNombre;
    private String  mascotaRaza;
    private Integer mascotaIdGenero;
    private Integer mascotaIdTipoVida;
    private Integer mascotaIdActividadFisica;
    private Date    mascotaFechaCumpleanos;
    private String  mascotaImagen;

    private String tip;


    public Integer getIdDueno() {
        return idDueno;
    }

    public void setIdDueno(Integer idDueno) {
        this.idDueno = idDueno;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDuenoNombre() {
        return duenoNombre;
    }

    public void setDuenoNombre(String duenoNombre) {
        this.duenoNombre = duenoNombre;
    }

    public Integer getDuenoIdGenero() {
        return duenoIdGenero;
    }

    public void setDuenoIdGenero(Integer duenoIdGenero) {
        this.duenoIdGenero = duenoIdGenero;
    }

    public Date getDuenoFechaCumpleanos() {
        return duenoFechaCumpleanos;
    }

    public void setDuenoFechaCumpleanos(Date duenoFechaCumpleanos) {
        this.duenoFechaCumpleanos = duenoFechaCumpleanos;
    }

    public Integer getDuenoIdEstado() {
        return duenoIdEstado;
    }

    public void setDuenoIdEstado(Integer duenoIdEstado) {
        this.duenoIdEstado = duenoIdEstado;
    }

    public String getMascotaNombre() {
        return mascotaNombre;
    }

    public void setMascotaNombre(String mascotaNombre) {
        this.mascotaNombre = mascotaNombre;
    }

    public String getMascotaRaza() {
        return mascotaRaza;
    }

    public void setMascotaRaza(String mascotaRaza) {
        this.mascotaRaza = mascotaRaza;
    }

    public Integer getMascotaIdGenero() {
        return mascotaIdGenero;
    }

    public void setMascotaIdGenero(Integer mascotaIdGenero) {
        this.mascotaIdGenero = mascotaIdGenero;
    }

    public Integer getMascotaIdTipoVida() {
        return mascotaIdTipoVida;
    }

    public void setMascotaIdTipoVida(Integer mascotaIdTipoVida) {
        this.mascotaIdTipoVida = mascotaIdTipoVida;
    }

    public Integer getMascotaIdActividadFisica() {
        return mascotaIdActividadFisica;
    }

    public void setMascotaIdActividadFisica(Integer mascotaIdActividadFisica) {
        this.mascotaIdActividadFisica = mascotaIdActividadFisica;
    }

    public Date getMascotaFechaCumpleanos() {
        return mascotaFechaCumpleanos;
    }

    public void setMascotaFechaCumpleanos(Date mascotaFechaCumpleanos) {
        this.mascotaFechaCumpleanos = mascotaFechaCumpleanos;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

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
