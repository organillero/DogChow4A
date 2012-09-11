package mx.ferreyra.dogapp;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import mx.ferreyra.dogapp.org.ksoap2.SoapEnvelope;
import mx.ferreyra.dogapp.org.ksoap2.serialization.SoapObject;
import mx.ferreyra.dogapp.org.ksoap2.serialization.SoapSerializationEnvelope;
import mx.ferreyra.dogapp.org.ksoap2.transport.HttpTransportSE;
import android.content.Context;
import android.widget.Toast;

/**
 * @author Israel Buitron
 */
public class WsDogUtils {

    public Context context;
    private String url = "http://marketing7veinte.net/dc_app_perroton/appWSDog/wsDog.asmx?WSDL";
    private String namespace = "http://tempuri.org/";

    public WsDogUtils(Context context) {
        this.context = context;
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["1","Moderada"]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;IdActividadFisica&gt;1&lt;/IdActividadFisica&gt;
     * &lt;Descripcion&gt;Moderada&lt;/Descripcion&gt;
     * </code></p>
     */
    public String[][] parseGetCatActividadFisica(SoapObject result) {
        if (result == null)
            return null;

        SoapObject root = (SoapObject)result.getProperty(0);
        SoapObject diffgram = (SoapObject)root.getProperty(1);
        SoapObject newDataSet = (SoapObject)diffgram.getProperty(0);
        int count = newDataSet.getPropertyCount();
        String[][] values = new String[count][2];

        for(int i=0; i<newDataSet.getPropertyCount(); i++) {
            SoapObject table = (SoapObject)newDataSet.getProperty(i);

            values[i][0] = table.getPropertyAsString(0);
            values[i][1] = table.getPropertyAsString(1);
        }

        return values;
    }

    public String[][] getCatActividadFisica()
        throws IOException, XmlPullParserException {
        String method = "getCatActividadFisica";
        String action = "http://tempuri.org/getCatActividadFisica";

        SoapObject request = new SoapObject(namespace, method);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
        androidHttpTransport.call(action, envelope);

        SoapObject result = (SoapObject)envelope.bodyIn;
        return parseGetCatActividadFisica(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["1","Aguascalientes"]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;IdEstado&gt;1&lt;/IdEstado&gt;
     * &lt;Descripcion&gt;Aguascalientes&lt;/Descripcion&gt;
     * </code></p>
     */
    public String[][] parseGetCatEstados(SoapObject result) {
        if (result == null)
            return null;

        SoapObject root = (SoapObject)result.getProperty(0);
        SoapObject diffgram = (SoapObject)root.getProperty(1);
        SoapObject newDataSet = (SoapObject)diffgram.getProperty(0);
        int count = newDataSet.getPropertyCount();
        String[][] values = new String[count][2];

        for(int i=0; i<newDataSet.getPropertyCount(); i++) {
            SoapObject table = (SoapObject)newDataSet.getProperty(i);
            values[i][0] = table.getPropertyAsString(0);
            values[i][1] = table.getPropertyAsString(1);
        }

        return values;
    }

    public String[][] getCatEstados()
        throws IOException, XmlPullParserException {
        String method = "getCatEstados";
        String action = "http://tempuri.org/getCatEstados";

        SoapObject request = new SoapObject(namespace, method);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
        androidHttpTransport.call(action, envelope);

        SoapObject result = (SoapObject)envelope.bodyIn;
        return parseGetCatEstados(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["1","Masculino","Macho"]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;IdGenero&gt;1&lt;/IdGenero&gt;
     * &lt;Descripcion&gt;Masculino&lt;/Descripcion&gt;
     * &lt;Descripcion2&gt;Macho&lt;/Descripcion2&gt;
     * </code></p>
     */
    public String[][] parseGetCatGenero(SoapObject result) {
        if (result == null)
            return null;

        SoapObject root = (SoapObject)result.getProperty(0);
        SoapObject diffgram = (SoapObject)root.getProperty(1);
        SoapObject newDataSet = (SoapObject)diffgram.getProperty(0);
        int count = newDataSet.getPropertyCount();
        String[][] values = new String[count][3];

        for(int i=0; i<count; i++) {
            SoapObject table = (SoapObject)newDataSet.getProperty(i);

            values[i][0] = table.getPropertyAsString(0);
            values[i][1] = table.getPropertyAsString(1);
            values[i][2] = table.getPropertyAsString(2);
        }

        return values;
    }

    public String[][] getCatGenero()
        throws IOException, XmlPullParserException {
        String method = "getCatGenero";
        String action = "http://tempuri.org/getCatGenero";

        SoapObject request = new SoapObject(namespace, method);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
        androidHttpTransport.call(action, envelope);

        SoapObject result = (SoapObject)envelope.bodyIn;
        return parseGetCatGenero(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["1","Exterior"]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;IdTipoVida&gt;1&lt;/IdTipoVida&gt;
     * &lt;Descripcion&gt;Exterior&lt;/Descripcion&gt;
     * </code></p>
     */
    public String[][] parseGetCatTipoVida(SoapObject result) {
        if (result == null)
            return null;

        SoapObject root = (SoapObject)result.getProperty(0);
        SoapObject diffgram = (SoapObject)root.getProperty(1);
        SoapObject newDataSet = (SoapObject)diffgram.getProperty(0);
        int count = newDataSet.getPropertyCount();
        int columns = 17;
        String[][] values = new String[count][columns];

        for(int i=0; i<newDataSet.getPropertyCount(); i++) {
            SoapObject table = (SoapObject)newDataSet.getProperty(i);
            for(int j=0; j<columns; j++)
                values[i][j] = table.getPropertyAsString(j);
        }

        return values;
    }

    public String[][] getCatTipoVida()
        throws IOException, XmlPullParserException {
        String method = "getCatTipoVida";
        String action = "http://tempuri.org/getCatTipoVida";

        SoapObject request = new SoapObject(namespace, method);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
        androidHttpTransport.call(action, envelope);

        SoapObject result = (SoapObject)envelope.bodyIn;
        return parseGetCatTipoVida(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["14","Israel","1","2012-08-09","6","Balam","Sharpei","2","2",
     *   "2012-09-09","/9j/....",null,null,"2012-09-10","Mientras..."]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;IdDueno&gt;14&lt;/IdDueno&gt;
     * &lt;IdUsuario&gt;10&lt;/IdUsuario&gt;
     * &lt;DuenoNombre&gt;Israel&lt;/DuenoNombre&gt;
     * &lt;DuenoIdGenero&gt;1&lt;/DuenoIdGenero&gt;
     * &lt;DuenoFechaCumpleanos&gt;2012-08-09&lt;/DuenoFechaCumpleanos&gt;
     * &lt;DuenoIdEstado&gt;6&lt;/DuenoIdEstado&gt;
     * &lt;MascotaNombre&gt;Balam&lt;/MascotaNombre&gt;
     * &lt;MascotaRaza&gt;Sharpei&lt;/MascotaRaza&gt;
     * &lt;MascotaIdGenero&gt;2&lt;/MascotaIdGenero&gt;
     * &lt;MascotaIdTipoVida&gt;2&lt;/MascotaIdTipoVida&gt;
     * &lt;MascotaIdActividadFisica&gt;2&lt;/MascotaIdActividadFisica&gt;
     * &lt;MascotaFechaCumpleanos&gt;2012-09-09&lt;/MascotaFechaCumpleanos&gt;
     * &lt;MascotaImagen&gt;/9j/...&lt;/MascotaImagen&gt;
     * &lt;Comentarios1/&gt;
     * &lt;Comentarios2/&gt;
     * &lt;FechaRegistro&gt;2012-09-10&lt;/FechaRegistro&gt;
     * &lt;Tip&gt;Mientras...&lt;/Tip&gt;
     * </code></p>
     */
    public String[][] parseGetDuenosMascotas(SoapObject result) {
        if (result == null)
            return null;

        SoapObject root = (SoapObject)result.getProperty(0);
        SoapObject diffgram = (SoapObject)root.getProperty(1);
        SoapObject newDataSet = (SoapObject)diffgram.getProperty(0);
        int count = newDataSet.getPropertyCount();
        int columns = 17;
        String[][] values = new String[count][columns];

        for(int i=0; i<count; i++) {
            SoapObject table = (SoapObject)newDataSet.getProperty(i);
            for(int j=0; j<columns; j++)
                values[i][j] = table.getPropertyAsString(j);
        }

        return values;
    }

    public String[][] getDuenosMascotas()
        throws IOException, XmlPullParserException {
        String method = "getDuenosMascotas";
        String action = "http://tempuri.org/getDuenosMascotas";

        SoapObject request = new SoapObject(namespace, method);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
        androidHttpTransport.call(action, envelope);

        SoapObject result = (SoapObject)envelope.bodyIn;
        return parseGetDuenosMascotas(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["14","Israel","1","2012-08-09","6","Balam","Sharpei","2","2",
     *   "2012-09-09","/9j/....",null,null,"2012-09-10","Mientras..."]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;IdDueno&gt;14&lt;/IdDueno&gt;
     * &lt;IdUsuario&gt;10&lt;/IdUsuario&gt;
     * &lt;DuenoNombre&gt;Israel&lt;/DuenoNombre&gt;
     * &lt;DuenoIdGenero&gt;1&lt;/DuenoIdGenero&gt;
     * &lt;DuenoFechaCumpleanos&gt;2012-08-09&lt;/DuenoFechaCumpleanos&gt;
     * &lt;DuenoIdEstado&gt;6&lt;/DuenoIdEstado&gt;
     * &lt;MascotaNombre&gt;Balam&lt;/MascotaNombre&gt;
     * &lt;MascotaRaza&gt;Sharpei&lt;/MascotaRaza&gt;
     * &lt;MascotaIdGenero&gt;2&lt;/MascotaIdGenero&gt;
     * &lt;MascotaIdTipoVida&gt;2&lt;/MascotaIdTipoVida&gt;
     * &lt;MascotaIdActividadFisica&gt;2&lt;/MascotaIdActividadFisica&gt;
     * &lt;MascotaFechaCumpleanos&gt;2012-09-09&lt;/MascotaFechaCumpleanos&gt;
     * &lt;MascotaImagen&gt;/9j/...&lt;/MascotaImagen&gt;
     * &lt;Comentarios1/&gt;
     * &lt;Comentarios2/&gt;
     * &lt;FechaRegistro&gt;2012-09-10&lt;/FechaRegistro&gt;
     * &lt;Tip&gt;Mientras...&lt;/Tip&gt;
     * </code></p>
     */
    public String[][] parseGetDuenosMascotasByIdUsuario(SoapObject result) {
        if (result == null)
            return null;

        SoapObject root = (SoapObject)result.getProperty(0);
        SoapObject diffgram = (SoapObject)root.getProperty(1);
        SoapObject newDataSet = (SoapObject)diffgram.getProperty(0);
        int count = newDataSet.getPropertyCount();
        if(count==0)
            return null;

        int columns = 17;
        String[][] values = new String[count][columns];

        for(int i=0; i<count; i++) {
            SoapObject table = (SoapObject)newDataSet.getProperty(i);
            for(int j=0; j<columns; j++)
                values[i][j] = table.getPropertyAsString(j);
        }

        return values;
    }

    public String[][] getDuenosMascotasByIdUsuario(Map parameters)
        throws IOException, XmlPullParserException {
        String method = "getDuenosMascotasByIdUsuario";
        String action = "http://tempuri.org/getDuenosMascotasByIdUsuario";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("idUsuario",(Integer)parameters.get("user_id"));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
        androidHttpTransport.call(action, envelope);

        SoapObject result = (SoapObject)envelope.bodyIn;
        return parseGetDuenosMascotas(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["0","0","NaN"]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;Distance&gt;0&lt;/Distance&gt;
     * &lt;SecondTime&gt;0&lt;/SecondTime&gt;
     * &lt;Speed&gt;NaN&lt;/Speed&gt;
     * </code></p>
     */
    public String[][] parseGetStats(SoapObject result) {
        if (result == null)
            return null;

        SoapObject root = (SoapObject)result.getProperty(0);
        SoapObject diffgram = (SoapObject)root.getProperty(1);
        SoapObject documentElement = (SoapObject)diffgram.getProperty(0);
        int count = documentElement.getPropertyCount();
        if(count==0)
            return null;

        int columns = 3;
        String[][] values = new String[count][columns];

        for(int i=0; i<dtRegister.getPropertyCount(); i++) {
            SoapObject dtRegister = (SoapObject)documentElement.getProperty(i);
            for(int j=0; j<columns; j++)
                values[i][j] = dtRegister.getPropertyAsString(j);
        }

        return values;
    }

    public String[][] getStats(Map parameters)
        throws IOException, XmlPullParserException {
        String method = "getStats";
        String action = "http://tempuri.org/getStats";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("userId",(Integer)parameters.get("user_id"));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
        androidHttpTransport.call(action, envelope);

        SoapObject result = (SoapObject)envelope.bodyIn;
        return parseGetStats(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["1","Evita..."]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;IdTip&gt;1&lt;/IdTip&gt;
     * &lt;Descripcion&gt;Evita...&lt;/Descripcion&gt;
     * </code></p>
     */
    public String[][] parseGetTipsByIdUsuario(SoapObject result) {
        if (result == null)
            return null;

        SoapObject root = (SoapObject)result.getProperty(0);
        SoapObject diffgram = (SoapObject)root.getProperty(1);
        SoapObject newDataSet = (SoapObject)diffgram.getProperty(0);
        int count = newDataSet.getPropertyCount();
        if(count==0)
            return null;

        int columns = 2;
        String[][] values = new String[count][columns];

        for(int i=0; i<count; i++) {
            SoapObject table = (SoapObject)newDataSet.getProperty(i);
            for(int j=0; j<columns; j++)
                values[i][j] = table.getPropertyAsString(j);
        }

        return values;
    }

    public String[][] getTipsByIdUsuario(Map parameters)
        throws IOException, XmlPullParserException {
        String method = "getTipsByIdUsuario";
        String action = "http://tempuri.org/getTipsByIdUsuario";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("idUsuario",(Integer)parameters.get("user_id"));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
        androidHttpTransport.call(action, envelope);

        SoapObject result = (SoapObject)envelope.bodyIn;
        return parseGetTipsByIdUsuario(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     */
    public String[][] parseGetTrainingSpot(SoapObject result) {
        if (result == null)
            return null;

        SoapObject root = (SoapObject)result.getProperty(0);
        SoapObject diffgram = (SoapObject)root.getProperty(1);
        SoapObject newDataSet = (SoapObject)diffgram.getProperty(0);
        int count = newDataSet.getPropertyCount();
        if(count==0)
            return null;

        int columns = 11;
        String[][] values = new String[count][columns];

        for(int i=0; i<count; i++) {
            SoapObject table = (SoapObject)newDataSet.getProperty(i);
            for(int j=0; j<columns; j++)
                values[i][j] = table.getPropertyAsString(j);
        }

        return values;
    }

    public String[][] getTrainingSpot(Map parameters)
        throws IOException, XmlPullParserException {
        String method = "getTrainingSpot";
        String action = "http://tempuri.org/getTrainingSpot";

        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("latitude",(String)parameters.get("latitude"));
        request.addProperty("longitude",(String)parameters.get("longitude"));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
        androidHttpTransport.call(action, envelope);

        SoapObject result = (SoapObject)envelope.bodyIn;
        return parseGetTrainingSpot(result);
    }
}
