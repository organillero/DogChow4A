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

    public String[][] parseGetCatTipoVida(SoapObject result) {
        if (result == null)
            return null;

        SoapObject root = (SoapObject)result.getProperty(0);
        SoapObject diffgram = (SoapObject)root.getProperty(1);
        SoapObject newDataSet = (SoapObject)diffgram.getProperty(0);
        int count = newDataSet.getPropertyCount();
        String[][] values = new String[count][2];

        for(int i=0; i<count; i++) {
            SoapObject table = (SoapObject)newDataSet.getProperty(i);

            values[i][0] = table.getPropertyAsString(0);
            values[i][1] = table.getPropertyAsString(1);
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
