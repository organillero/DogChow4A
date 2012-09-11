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
}
