package test;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import test.reponse.AccessTokenResponse;
import test.reponse.OauthTicketResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Hello world!
 *
 */
public class App 
{

    public static final String APP_ID = "YaSrkbeM1NBWCJTtoXV1TFFuohYh1QSs";
    public static final String APP_KEY = "23IV6JFCGT3Igc3PE7xRKDYF21fYyXgR";
    public static final String SYS_CODE = "0";


    private static final String URL = "http://gateway.system.eduyun.cn:40015";

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

        Object object = OKHttpUtils.post(URL + "/apigateway/getAccessToken",null,dataMap,AccessTokenResponse.class);
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
        Object object = OKHttpUtils.get(URL + "/oauth/createOauthTicket?accessToken="+accessToken,null,OauthTicketResponse.class);
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


}