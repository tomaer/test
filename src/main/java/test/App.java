package test;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import test.reponse.AccessTokenResponse;
import test.reponse.AppRegisterResponse;
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
        getOrgList(accessTokenResponse.data.accessToken);
        getIndependentAppRegister(accessTokenResponse.data.accessToken);
        //实名认证
        getCretValidate(accessTokenResponse.data.accessToken);
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

    /**
     * 校验应用用户登录(type为“0”表示接口登记，type为“1”表示页面登记)
     * @param accessToken
     * @return
     */
    public static AppRegisterResponse getIndependentAppRegister(String accessToken) throws IOException{
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("userId","16764");
        dataMap.put("loginAccount","SH000064");
        dataMap.put("type","1");
        //orgId可根据用户所在学校获取，不存在时根据/baseInfo/getOrgList随意获取一个
        dataMap.put("orgId","2fb6a725c584437a81758faf0868c63d");
        //身份(0学生 1教师 2家长 3学校工作人员 4机构工作人员)
        dataMap.put("userIdentity","1");

        Object object = OKHttpUtils.post(URL + "/cert/independentAppRegister?accessToken="+accessToken,null,dataMap,AppRegisterResponse.class);
        if(null != object) {
            return (AppRegisterResponse) object;
        }
        return null;
    }

    /**
     * 获取全部机构编码信息
     * @param accessToken
     * @return
     * @throws IOException
     */
    public static Object getOrgList(String accessToken) throws IOException{
        Map<String,String> dataMap = new HashMap<>();
        //参数皆为可选参数
        dataMap.put("pageNo","1");
        dataMap.put("pageSize","5");
        Object object = OKHttpUtils.post(URL + "/baseInfo/getOrgList?accessToken="+accessToken,null,dataMap,Object.class);
        if(null != object) {
            return object;
        }
        return null;
    }

    /**
     * 实名认证校验
     * @param accessToken
     * @return
     * @throws IOException
     */
    public static Object getCretValidate(String accessToken) throws IOException{
        Map<String,String> dataMap = new HashMap<>();
        dataMap.put("userId","16764");//已实名注册的ID为27455
        dataMap.put("loginAccount","SH000064");//已实名注册的账号为SH000090

        Object object = OKHttpUtils.post(URL + "/userSession/validateSession?accessToken="+accessToken,null,dataMap,Object.class);
        if(null != object) {
            return object;
        }
        return null;
    }


}