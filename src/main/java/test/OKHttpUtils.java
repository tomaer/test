package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OKHttpUtils {

    private static final MediaType MEDIATYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    final static OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60,TimeUnit.SECONDS).readTimeout(60,TimeUnit.SECONDS).build();
    final static ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 通用的get请求方法
     * @param url
     * @param headerMap
     * @param valueType
     * @param <T>
     * @return
     */
    public static <T> Object get(final String url, final Map<String,String> headerMap , final Class<T> valueType) {
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
    private static Request.Builder getRequestBuilder(final String url, final Map<String,String> headerMap) {
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
    private static <T> Object parseResponse(final Request request,final Class<T> valueType) {
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