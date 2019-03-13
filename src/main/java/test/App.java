package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import test.reponse.AccessTokenResponse;
import test.reponse.OauthTicketResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{

    public static final String APP_ID = "YaSrkbeM1NBWCJTtoXV1TFFuohYh1QSs";
    public static final String APP_KEY = "23IV6JFCGT3Igc3PE7xRKDYF21fYyXgR";
    public static final String SYS_CODE = "0";

    private static final MediaType MEDIATYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String URL = "http://gateway.system.eduyun.cn:40015";

    final static OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60,TimeUnit.SECONDS).readTimeout(60,TimeUnit.SECONDS).build();
    final static ObjectMapper objectMapper = new ObjectMapper();

    public static void main( String[] args ) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        AccessTokenResponse accessTokenResponse = getAccessToken(timestamp);
        OauthTicketResponse oauthTicketResponse = createOauthTicket(accessTokenResponse.data.accessToken);
        getJump2eduyunLoginUrl(oauthTicketResponse.accessTicket);
        getJump2eduyunLoginUrl2(oauthTicketResponse.accessTicket,accessTokenResponse.data.accessToken);
    }

    /**
     * sha1-hmac
     * @param timestamp
     * @return String
     */
    public static String getKeyInfo(String timestamp) {
        byte [] hmacSHA1 = new HmacUtils(HmacAlgorithms.HMAC_SHA_1,APP_KEY).hmac(APP_ID.concat(APP_KEY).concat(timestamp));
        return Hex.encodeHexString(hmacSHA1).toUpperCase();
    }

    /**
     * 获取token
     * @param timestamp
     * @return
     * @throws IOException
     */
    public static AccessTokenResponse getAccessToken(String timestamp) throws IOException {
        final String keyInfo = getKeyInfo(timestamp);
        System.out.println("请求的keyInfo为: " + keyInfo);
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("appId",APP_ID);
        dataMap.put("keyInfo",keyInfo);
        dataMap.put("timeStamp",timestamp);
        dataMap.put("sysCode",SYS_CODE);

        Object object = post(URL + "/apigateway/getAccessToken",null,dataMap,AccessTokenResponse.class);
        if(null != object) {
            return (AccessTokenResponse) object;
        }
        return null;
    }

    /**
     * 产生临时的Ticket，只能用一次
     * @param accessToken
     * @return
     * @throws IOException
     */
    public static OauthTicketResponse createOauthTicket(final String accessToken) throws IOException {
        Object object = get(URL + "/oauth/createOauthTicket?accessToken="+accessToken,null,OauthTicketResponse.class);
        if(null != object) {
            return (OauthTicketResponse) object;
        }
        return null;
    }

    /**
     * 获取登录授权页面的地址
     * @param accessTicket
     * @return
     */
    public static String getJump2eduyunLoginUrl(final String accessTicket) {
        String loginUrl = "http://gateway.system.eduyun.cn:40015/bmp-oauth?accessTicket={accessTicket}&redirect_uri=http%3a%2f%2flocalhost%2fwww";
        loginUrl = loginUrl.replace("{accessTicket}",accessTicket);
        System.out.println("Eduyun Login Url:  " + loginUrl);
        return loginUrl;
    }

    /**
     * 获取登录授权页面的地址2
     * @param accessTicket
     * @param accessToken
     * @return
     */
    public static String getJump2eduyunLoginUrl2(final String accessTicket,final String accessToken) {
        String loginUrl = "http://gateway.system.eduyun.cn:40015/bmp-oauth?accessTicket={accessTicket}&accessToken={accessToken}&redirect_uri=http%3a%2f%2flocalhost%2fwww";
        loginUrl = loginUrl.replace("{accessTicket}",accessTicket).replace("{accessToken}",accessToken);
        System.out.println("Eduyun Login Url2: " + loginUrl);
        return loginUrl;
    }

    /**
     * 通用的get请求方法
     * @param url
     * @param headerMap
     * @param valueType
     * @param <T>
     * @return
     */
    public static <T> Object get(final String url,final Map<String,String> headerMap ,final Class<T> valueType) {
        System.out.println("Get请求，地址为: " + url);
        Request request = getRequestBuilder(url,headerMap).build();
        return parseResponse(request, valueType);
    }

    /**
     * 通用的post请求方法
     * @param url
     * @param headerMap
     * @param object
     * @param valueType
     * @param <T>
     * @return
     * @throws JsonProcessingException
     */
    public static <T> Object post(final String url, final Map<String,String> headerMap ,final Object object, final Class<T> valueType) throws JsonProcessingException {
        final String data = objectMapper.writeValueAsString(object);
        System.out.println("Post请求地址为: "+ url + "\nbody: "+ data);
        RequestBody body = RequestBody.create(MEDIATYPE_JSON,data);
        Request request = getRequestBuilder(url,headerMap).post(body).build();
        return parseResponse(request,valueType);
    }

    /**
     *
     * @param url
     * @param headerMap
     * @return
     */
    public static Request.Builder getRequestBuilder(final String url, final Map<String,String> headerMap) {
        Request.Builder builder = new Request.Builder().url(url);
        if(null != headerMap && !headerMap.isEmpty()) {
            for (Map.Entry<String,String> entry : headerMap.entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                if (null != key && !key.equals("") && key.length() > 0) {
                    System.out.println("Header name: " + key + ", Header value: " + value);
                    builder.header(key,value);
                }
            }
        }
        return builder;
    }

    /**
     * 通用的处理接口返回
     * @param request
     * @param valueType
     * @param <T>
     * @return
     */
    public static <T> Object parseResponse(final Request request,final Class<T> valueType) {
        Response response = null;
        ResponseBody responseBody = null;
        try {
            response = okHttpClient.newCall(request).execute();
            responseBody = response.body();
            if(response.isSuccessful()){
                final String result = responseBody.string();
                System.out.println("Response Code: " + response.code() + "\n返回结果为: " + result);
                return objectMapper.readValue(result, valueType);
            } else {
                System.out.println(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != responseBody) {
                responseBody.close();
            }
        }
        return null;
    }
}