package mx.ferreyra.dogapp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mx.ferreyra.dogapp.org.ksoap2.SoapEnvelope;
import mx.ferreyra.dogapp.org.ksoap2.serialization.SoapObject;
import mx.ferreyra.dogapp.org.ksoap2.serialization.SoapSerializationEnvelope;
import mx.ferreyra.dogapp.org.ksoap2.transport.HttpTransportSE;
import mx.ferreyra.dogapp.pojos.FotosMascotaByLatLonResponse;

import org.xmlpull.v1.XmlPullParserException;

/**
 * @author Israel Buitron
 */
public class WsDogUtils {

    private final String url;
    private final String namespace;

    private static final String EDIT_DUENO_MASCOTA = "editDuenoMascota";
    private static final String GET_CAT_ACTIVIDAD_FISICA = "getCatActividadFisica";
    private static final String GET_CAT_ESTADOS = "getCatEstados";
    private static final String GET_CAT_GENERO = "getCatGenero";
    private static final String GET_CAT_TIPO_VIDA = "getCatTipoVida";
    private static final String GET_DUENOS_MASCOTAS = "getDuenosMascotas";
    private static final String GET_DUENOS_MASCOTAS_BY_ID_USUARIO = "getDuenosMascotasByIdUsuario";
    private static final String GET_TIPS_BY_ID_USUARIO = "getTipsByIdUsuario";
    private static final String GET_TRAINING_SPOT = "getTrainingSpot";
    private static final String INSERT_IPHONE_ID = "insertIphoneID";
    private static final String INSERT_RATING = "insertRating";
    private static final String INSERT_ROUTE = "insertRoute";
    private static final String INSERT_USER_IPHONE = "insertUserIphone";
    private static final String INSERT_USERS = "insertUsers";
    private static final String USER_LOGIN = "userLogin";
    private static final String USER_RECOVERY_PWD = "userRecoveryPWD";
    private static final String DELETE_FOTO_MASCOTA                  = "deleteFotoMascota";
    private static final String GET_FOTOS_MASCOTA_BY_ID_FOTO         = "getFotosMascotaByIdFoto";
    private static final String GET_FOTOS_MASCOTA_BY_LAT_LON         = "getFotosMascotaByLatLon";
    private static final String GET_FOTOS_MASCOTA_BY_USUARIO_MES_ANO = "getFotosMascotaByUsuarioMesAno";
    private static final String INSERT_FOTO_MASCOTA                  = "insertFotoMascota";

    public WsDogUtils() {
        this("http://marketing7veinte.net/dc_app_perroton/appWSDog/wsDog.asmx?WSDL", "http://tempuri.org/");
    }

    public WsDogUtils(String url, String namespace) {
        this.url = url;
        this.namespace = namespace;
    }

    private Integer parseDeleteFotoMascota(SoapObject result) {
        return parseGenericReturnSoapObject(result);
    }

    public Integer deleteFotoMascota(int photoId, int userId)
        throws IOException, XmlPullParserException {
        return deleteFotoMascota(Integer.toString(photoId),
                                 Integer.toString(userId));
    }

    public Integer deleteFotoMascota(String photoId, String userId)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, DELETE_FOTO_MASCOTA);
        request.addProperty("idFoto",photoId);
        request.addProperty("idUsuario",userId);
        SoapObject result = (SoapObject)genericRequest(DELETE_FOTO_MASCOTA,
                                                       namespace + DELETE_FOTO_MASCOTA,
                                                       request);
        return parseDeleteFotoMascota(result);
    }

    private String[][] parseGetFotosMascotaByIdFoto(SoapObject result) {
        return parseGenericMatrixSoapObject(result, 9);
    }

    public String[][] getFotosMascotaByIdFoto(int photoId)
        throws IOException, XmlPullParserException {
        return getFotosMascotaByIdFoto(Integer.toString(photoId));
    }

    public String[][] getFotosMascotaByIdFoto(String photoId)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, GET_FOTOS_MASCOTA_BY_ID_FOTO);
        request.addProperty("idFoto",photoId);
        SoapObject result = (SoapObject)genericRequest(GET_FOTOS_MASCOTA_BY_ID_FOTO,
                                                       namespace + GET_FOTOS_MASCOTA_BY_ID_FOTO,
                                                       request);
        return parseGetFotosMascotaByIdFoto(result);
    }

    private String[][] parseGetFotosMascotaByLatLon(SoapObject result) {
        return parseGenericMatrixSoapObject(result, 10);
    }

    public String[][] getFotosMascotaByLatLon(double latitude,
                                              double longitude,
                                              double radio)
        throws IOException, XmlPullParserException {
        return getFotosMascotaByLatLon(Double.toString(latitude),
                                       Double.toString(longitude),
                                       Double.toString(radio));
    }

    public String[][] getFotosMascotaByLatLon(String latitude,
                                              String longitude,
                                              String radio)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, GET_FOTOS_MASCOTA_BY_LAT_LON);
        request.addProperty("lat",latitude);
        request.addProperty("lon",longitude);
        request.addProperty("radio",radio);
        SoapObject result = (SoapObject)genericRequest(GET_FOTOS_MASCOTA_BY_LAT_LON,
                                                       namespace + GET_FOTOS_MASCOTA_BY_LAT_LON,
                                                       request);
        return parseGetFotosMascotaByLatLon(result);
    }

    public static List<FotosMascotaByLatLonResponse> fotosMascotaByLatLonToPojo(String[][] response)
        throws ParseException {
        if(response==null)
            return null;

        List<FotosMascotaByLatLonResponse> list = new ArrayList<FotosMascotaByLatLonResponse>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for(int i=0; i<response.length; i++) {
            FotosMascotaByLatLonResponse pojo = new FotosMascotaByLatLonResponse();
            pojo.setPhotoId(Long.getLong(response[i][0]));
            pojo.setOwnerId(Long.getLong(response[i][1]));
            pojo.setDate(sdf.parse(response[i][2]));
            pojo.setPhotoAsBase64Binary(response[i][3]);
            pojo.setLatitude(Double.valueOf(response[i][4]));
            pojo.setLongitude(Double.valueOf(response[i][5]));
            pojo.setComments1(response[i][6]);
            pojo.setComments2(response[i][7]);
            pojo.setCreationDate(sdf.parse(response[i][8]));
            pojo.setDistance(Double.valueOf(response[i][9]));
            list.add(pojo);
        }
        return list;
    }

    private String[][] parseGetFotosMascotaByUsuarioMesAno(SoapObject result) {
        return parseGenericMatrixSoapObject(result, 9);
    }

    public String[][] getFotosMascotaByUsuarioMesAno(int userId, Date date)
        throws IOException, XmlPullParserException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return getFotosMascotaByUsuarioMesAno(Integer.toString(userId),
                                              sdf.format(date));
    }

    public String[][] getFotosMascotaByUsuarioMesAno(String userId, String date)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, GET_FOTOS_MASCOTA_BY_USUARIO_MES_ANO);
        request.addProperty("idUsuario",userId);
        request.addProperty("fecha",date);
        SoapObject result = (SoapObject)genericRequest(GET_FOTOS_MASCOTA_BY_USUARIO_MES_ANO,
                                                       namespace + GET_FOTOS_MASCOTA_BY_USUARIO_MES_ANO,
                                                       request);
        return parseGetFotosMascotaByUsuarioMesAno(result);
    }

    private Integer parseInsertFotoMascota(SoapObject result) {
        return parseGenericReturnSoapObject(result);
    }

    public Integer insertFotoMascota(int userId,
                                     Date date,
                                     String photoAsBase64Binary,
                                     double latitude,
                                     double longitude,
                                     String comment1,
                                     String comment2)
        throws IOException, XmlPullParserException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return insertFotoMascota(Integer.toString(userId),
                                 sdf.format(date),
                                 photoAsBase64Binary,
                                 Double.toString(latitude),
                                 Double.toString(longitude),
                                 comment1,
                                 comment2);
    }

    public Integer insertFotoMascota(String userId,
                                     String date,
                                     String photoAsBase64Binary,
                                     String latitude,
                                     String longitude,
                                     String comment1,
                                     String comment2)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, INSERT_FOTO_MASCOTA);
        request.addProperty("idUsuario",userId);
        request.addProperty("fecha",date);
        request.addProperty("foto", photoAsBase64Binary);
        request.addProperty("lat", latitude);
        request.addProperty("lon", longitude);
        request.addProperty("comentarios1", comment1);
        request.addProperty("comentarios2", comment2);
        SoapObject result = (SoapObject)genericRequest(INSERT_FOTO_MASCOTA,
                                                       namespace + INSERT_FOTO_MASCOTA,
                                                       request);
        return parseInsertFotoMascota(result);
    }

    private Integer parseEditDuenoMascota(SoapObject result) {
        return parseGenericReturnSoapObject(result);
    }

    public Integer editDuenoMascota(int ownerId,
                                    int userId,
                                    String ownerName,
                                    int ownerGenderId,
                                    Date ownerBirthday,
                                    int ownerStateId,
                                    String dogName,
                                    String dogBreed,
                                    int dogGenderId,
                                    int dogLifeStyleId,
                                    int dogExerciseId,
                                    Date dogBirthday,
                                    String dogPhotoAsBase64Binary,
                                    String comment1,
                                    String comment2)
            throws IOException, XmlPullParserException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return editDuenoMascota(Integer.toString(ownerId),
                                Integer.toString(userId),
                                ownerName,
                                Integer.toString(ownerGenderId),
                                sdf.format(ownerBirthday),
                                Integer.toString(ownerStateId),
                                dogName,
                                dogBreed,
                                Integer.toString(dogGenderId),
                                Integer.toString(dogLifeStyleId),
                                Integer.toString(dogExerciseId),
                                sdf.format(dogBirthday),
                                dogPhotoAsBase64Binary,
                                comment1,
                                comment2);
    }

    public Integer editDuenoMascota(String ownerId,
                                    String userId,
                                    String ownerName,
                                    String ownerGenderId,
                                    String ownerBirthday,
                                    String ownerStateId,
                                    String dogName,
                                    String dogBreed,
                                    String dogGenderId,
                                    String dogLifeStyleId,
                                    String dogExerciseId,
                                    String dogBirthday,
                                    String dogPhotoAsBase64Binary,
                                    String comment1,
                                    String comment2)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, EDIT_DUENO_MASCOTA);
        request.addProperty("idDueno",ownerId);
        request.addProperty("idUsuario",userId);
        request.addProperty("duenoNombre",ownerName);
        request.addProperty("duenoIdGenero",ownerGenderId);
        request.addProperty("duenoFechaCumpleanos",ownerBirthday);
        request.addProperty("duenoIdEstado",ownerStateId);
        request.addProperty("mascotaNombre",dogName);
        request.addProperty("mascotaRaza",dogBreed);
        request.addProperty("mascotaIdGenero",dogGenderId);
        request.addProperty("mascotaIdTipoVida",dogLifeStyleId);
        request.addProperty("mascotaIdActividadFisica",dogExerciseId);
        request.addProperty("mascotaFechaCumpleanos",dogBirthday);
        request.addProperty("comentarios1",comment1);
        request.addProperty("comentarios2",comment2);
        SoapObject result = (SoapObject)genericRequest(EDIT_DUENO_MASCOTA,
                                                       namespace + EDIT_DUENO_MASCOTA,
                                                       request);
        return parseEditDuenoMascota(result);
    }

    public Integer editDuenoMascota(Map<String, String> parameters)
        throws IOException, XmlPullParserException {
        Integer result = genericDuenoMascota(EDIT_DUENO_MASCOTA, namespace + EDIT_DUENO_MASCOTA, parameters);
        return result;
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
        return parseGenericMatrixSoapObject(result, 2);
    }

    public String[][] getCatActividadFisica()
        throws IOException, XmlPullParserException {
        SoapObject result = (SoapObject)genericRequest(GET_CAT_ACTIVIDAD_FISICA,
                                                       namespace + GET_CAT_ACTIVIDAD_FISICA,
                                                       new SoapObject(namespace, GET_CAT_ACTIVIDAD_FISICA));
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
        return parseGenericMatrixSoapObject(result, 2);
    }

    public String[][] getCatEstados()
        throws IOException, XmlPullParserException {
        SoapObject result = (SoapObject)genericRequest(GET_CAT_ESTADOS,
                                                       namespace + GET_CAT_ESTADOS,
                                                       new SoapObject(namespace, GET_CAT_ESTADOS));
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
        return parseGenericMatrixSoapObject(result, 3);
    }

    public String[][] getCatGenero()
        throws IOException, XmlPullParserException {
        SoapObject result = (SoapObject)genericRequest(GET_CAT_GENERO,
                                                       namespace + GET_CAT_GENERO,
                                                       new SoapObject(namespace, GET_CAT_GENERO));
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
        return parseGenericMatrixSoapObject(result, 17);
    }

    public String[][] getCatTipoVida()
        throws IOException, XmlPullParserException {
        SoapObject result = (SoapObject)genericRequest(GET_CAT_TIPO_VIDA,
                                                       namespace + GET_CAT_TIPO_VIDA,
                                                       new SoapObject(namespace, GET_CAT_TIPO_VIDA));
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
        return parseGenericMatrixSoapObject(result, 17);
    }

    public String[][] getDuenosMascotas()
        throws IOException, XmlPullParserException {
        SoapObject result = (SoapObject)genericRequest(GET_DUENOS_MASCOTAS,
                                                       namespace + GET_DUENOS_MASCOTAS,
                                                       new SoapObject(namespace, GET_DUENOS_MASCOTAS));
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
        return parseGenericMatrixSoapObject(result, 17);
    }

    public String[][] getDuenosMascotasByIdUsuario(Integer userId)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, GET_DUENOS_MASCOTAS_BY_ID_USUARIO);
        request.addProperty("idUsuario", userId);
        SoapObject result = (SoapObject)genericRequest(GET_DUENOS_MASCOTAS_BY_ID_USUARIO,
                                                       namespace + GET_DUENOS_MASCOTAS_BY_ID_USUARIO,
                                                       request);
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
        return parseGenericMatrixSoapObject(result, 3);
    }

    public String[][] getStats(Map parameters)
        throws IOException, XmlPullParserException {
        String method = "getStats";
        SoapObject request = new SoapObject(namespace, method);
        request.addProperty("userId",parameters.get("user_id"));
        SoapObject result = (SoapObject)genericRequest(method,
                                                       "http://tempuri.org/getStats",
                                                       request);
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
        return parseGenericMatrixSoapObject(result, 2);
    }

    public String[][] getTipsByIdUsuario(Map parameters)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, GET_TIPS_BY_ID_USUARIO);
        request.addProperty("idUsuario",(Integer)parameters.get("user_id"));
        SoapObject result = (SoapObject)genericRequest(GET_TIPS_BY_ID_USUARIO,
                                                       namespace + GET_TIPS_BY_ID_USUARIO,
                                                       request);
        return parseGetTipsByIdUsuario(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     */
    public String[][] parseGetTrainingSpot(SoapObject result) {
        return parseGenericMatrixSoapObject(result, 11);
    }

    public String[][] getTrainingSpot(Map parameters)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, GET_TRAINING_SPOT);
        request.addProperty("latitude",(String)parameters.get("latitude"));
        request.addProperty("longitude",(String)parameters.get("longitude"));
        SoapObject result = (SoapObject)genericRequest(GET_TRAINING_SPOT,
                                                       namespace + GET_TRAINING_SPOT,
                                                       request);
        return parseGetTrainingSpot(result);
    }

    public Integer insertDuenoMascota(Map<String, String> parameters)
        throws IOException, XmlPullParserException {
        return genericDuenoMascota("insertDuenoMascota",
                                   "http://tempuri.org/insertDuenoMascota",
                                   parameters);
    }

    public Integer genericDuenoMascota(String method, String action, Map<String, String> parameters)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, method);
        String[] keys = {
            "idDueno", "idUsuario", "duenoNombre", "duenoIdGenero", "duenoFechaCumpleanos",
            "duenoIdEstado", "mascotaNombre", "mascotaRaza", "mascotaIdGenero",
            "mascotaIdTipoVida", "mascotaIdGenero", "mascotaIdTipoVida",
            "mascotaFechaCumpleanos", "mascotaIdActividadFisica", "mascotaImagen",
            "comentarios1", "comentarios2"
        };

        // Puts parameters in SoapObject
        for(String key : keys)
            if(parameters.containsKey(key))
                request.addProperty(key,parameters.get(key));

        // Prepare Soap envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
        androidHttpTransport.debug = true;
        androidHttpTransport.call(action, envelope);

        Integer result;
        if((envelope.bodyIn) instanceof SoapObject) {
            // Soap object response
            result = parseGenericReturnSoapObject((SoapObject)envelope.bodyIn);
        } else {
            // Soap fault response
            result = null;
        }
        return result;
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["1|37"]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;return&gt;1|37&lt;/return&gt;
     * </code></p>
     */
    public String[][] parseInsertIphoneID(SoapObject result) {
        return parseGenericMatrixSoapObject(result, 1);
    }

    public String[][] insertIphoneID(Map parameters)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, INSERT_IPHONE_ID);
        request.addProperty("IphoneID",parameters.get("iphone_id"));
        SoapObject result = (SoapObject)genericRequest(INSERT_IPHONE_ID,
                                                       namespace + INSERT_IPHONE_ID,
                                                       request);
        return parseInsertIphoneID(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["1"]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;return&gt;1&lt;/return&gt;
     * </code></p>
     */
    public String[][] parseInsertRating(SoapObject result) {
        return parseGenericMatrixSoapObject(result, 1);
    }

    public String[][] insertRating(Map parameters)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, INSERT_RATING);
        request.addProperty("routeId",(Integer)parameters.get("route_id"));
        request.addProperty("rating",(String)parameters.get("rating"));
        SoapObject result = (SoapObject)genericRequest(INSERT_RATING,
                                                       namespace + INSERT_RATING,
                                                       request);
        return parseInsertRating(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["27"]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;return&gt;27&lt;/return&gt;
     * </code></p>
     */
    public String[][] parseInsertRoute(SoapObject result) {
        return parseGenericMatrixSoapObject(result, 1);
    }

    public String[][] insertRoute(Map parameters)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, INSERT_ROUTE);
        request.addProperty("routeName",(String)parameters.get("route_name"));
        request.addProperty("sourceLatitude",(String)parameters.get("source_latitude"));
        request.addProperty("sourceLongitude",(String)parameters.get("source_longitude"));
        request.addProperty("routeLatitude",(String)parameters.get("route_latitude"));
        request.addProperty("routeLongitude",(String)parameters.get("route_longitude"));
        request.addProperty("distance",(String)parameters.get("distance"));
        request.addProperty("timeTaken",(String)parameters.get("time_taken"));
        request.addProperty("difficulty",(String)parameters.get("difficulty"));
        request.addProperty("userId",(Integer)parameters.get("user_id"));
        SoapObject result = (SoapObject)genericRequest(INSERT_ROUTE,
                                                       namespace + INSERT_ROUTE,
                                                       request);
        return parseInsertRoute(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["1"]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;return&gt;1&lt;/return&gt;
     * </code></p>
     */
    public String[][] parseInsertUserIphone(SoapObject result) {
        return parseGenericMatrixSoapObject(result, 1);
    }

    public String[][] insertUserIphone(Map parameters)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, INSERT_USER_IPHONE);
        request.addProperty("IphoneID",(String)parameters.get("iphone_id"));
        request.addProperty("username",(String)parameters.get("username"));
        request.addProperty("password",(String)parameters.get("password"));
        request.addProperty("isFacebook",(String)parameters.get("is_facebook"));
        SoapObject result = (SoapObject)genericRequest(INSERT_USER_IPHONE,
                                                       namespace + INSERT_USER_IPHONE,
                                                       request);
        return parseInsertUserIphone(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["1"]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;return&gt;1&lt;/return&gt;
     * </code></p>
     */
    public Integer parseInsertUsers(SoapObject result) {
        return parseGenericReturnSoapObject(result);
    }

    public Integer insertUsers(String username, String password, boolean isFacebook)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, INSERT_USERS);
        request.addProperty("username",username);
        request.addProperty("password",password);
        request.addProperty("isFacebook",isFacebook ? "1" : "0");
        SoapObject result = (SoapObject)genericRequest(INSERT_USERS,
                                                       namespace + INSERT_USERS,
                                                       request);
        return parseInsertUsers(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["38"]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;return&gt;38&lt;/return&gt;
     * </code></p>
     */
    public Integer parseUserLogin(SoapObject result) {
        return parseGenericReturnSoapObject(result);
    }

    public Integer userLogin(String username, String password)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, USER_LOGIN);
        request.addProperty("username",username);
        request.addProperty("password",password);

        SoapObject result = (SoapObject)genericRequest(USER_LOGIN,
                                                       namespace + USER_LOGIN,
                                                       request);
        return parseUserLogin(result);
    }

    /**
     * Parse soap object response.
     * @param result Soap object response to parse.
     * @return If <code>result</code> parameter it returns <code>null</code>,
     * otherwise return <code>String</code> matrix with elements parsed.
     * This matrix should look like:
     * <p><code>
     * [["SERVER_ERROR"]]
     * </code></p>
     * for this soap response:
     * <p><code>
     * &lt;return&gt;SERVER_ERROR&lt;/return&gt;
     * </code></p>
     */
    public String[][] parseUserRecoveryPWD(SoapObject result) {
        return parseGenericMatrixSoapObject(result, 1);
    }

    public String[][] userRecoveryPWD(Map parameters)
        throws IOException, XmlPullParserException {
        SoapObject request = new SoapObject(namespace, USER_RECOVERY_PWD);
        request.addProperty("username",(String)parameters.get("username"));
        SoapObject result = (SoapObject)genericRequest(USER_RECOVERY_PWD,
                                                       namespace + USER_RECOVERY_PWD,
                                                       request);
        return parseUserRecoveryPWD(result);
    }

    public Integer parseGenericReturnSoapObject(SoapObject result) {
        if (result == null)
            return null;

        SoapObject root = (SoapObject)result.getProperty(0);
        SoapObject diffgram = (SoapObject)root.getProperty(1);
        SoapObject dataSet = (SoapObject)diffgram.getProperty(0);
        SoapObject table = (SoapObject)dataSet.getProperty(0);
        return Integer.valueOf(table.getPropertyAsString(0));
    }

    public String[][] parseGenericMatrixSoapObject(SoapObject result, int columns) {
        if (result == null)
            return null;

        SoapObject root = (SoapObject)result.getProperty(0);
        SoapObject diffgram = (SoapObject)root.getProperty(1);
	if(diffgram.getPropertyCount()==0) {
	    // No data returned
	    return null;
	}

        SoapObject dataSet = (SoapObject)diffgram.getProperty(0);
        int count = dataSet.getPropertyCount();
        String[][] values = new String[count][columns];

        for(int i=0; i<count; i++) {
            SoapObject table = (SoapObject)dataSet.getProperty(i);
            for(int j=0; j<columns; j++)
                values[i][j] = table.getPropertyAsString(j);
        }

        return values;
    }


    public Object genericRequest(String method,
                                 String action,
                                 SoapObject request)
        throws IOException, XmlPullParserException {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE androidHttpTransport = new HttpTransportSE(url);
        androidHttpTransport.debug = true;
        androidHttpTransport.call(action, envelope);

        return envelope.bodyIn;
    }

}
