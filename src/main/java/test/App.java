package test;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import test.reponse.AccessTokenResponse;
import test.reponse.AppRegisterResponse;
import test.reponse.OauthTicketResponse;
import test.reponse.org.OrgListResponse;
import test.reponse.org.Orginfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;


/**
 * Hello world!
 */
public class App {

    public static final String APP_ID = "";
    public static final String APP_KEY = "";
    public static final String SYS_CODE = "0";


    private static final String URL = "http://gateway.system.eduyun.cn:40015";

    public static void main(String[] args) throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        AccessTokenResponse accessTokenResponse = getAccessToken(timestamp);
        //实名认证
        //getCretValidate(accessTokenResponse.data.accessToken);
//        OauthTicketResponse oauthTicketResponse = createOauthTicket(accessTokenResponse.data.accessToken);
        //getJump2eduyunLoginUrl(oauthTicketResponse.accessTicket);
        //getJump2eduyunLoginUrl2(oauthTicketResponse.accessTicket,accessTokenResponse.data.accessToken);
        //getIndependentAppRegister(accessTokenResponse.data.accessToken);
//        validaTicket(accessTokenResponse.data.accessToken, "dU8wNWE0YzAzZTMtNDI5Mi00ODNkLThjMDMtZTM0MjkyZDgzZDQyMTU1NTU1MzU4ODczNA==");
        //生成机构编码excel
        getOrgList(accessTokenResponse.data.accessToken);


    }

    /**
     * sha1-hmac
     *
     * @param timestamp
     * @return String
     */
    public static String getKeyInfo(String timestamp) {
        byte[] hmacSHA1 = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, APP_KEY).hmac(APP_ID.concat(APP_KEY).concat(timestamp));
        return Hex.encodeHexString(hmacSHA1).toUpperCase();
    }

    /**
     * 获取token
     *
     * @param timestamp
     * @return
     * @throws IOException
     */
    public static AccessTokenResponse getAccessToken(String timestamp) throws IOException {
        final String keyInfo = getKeyInfo(timestamp);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("appId", APP_ID);
        dataMap.put("keyInfo", keyInfo);
        dataMap.put("timeStamp", timestamp);
        dataMap.put("sysCode", SYS_CODE);

        AccessTokenResponse accessTokenResponse = OKHttpUtils.post(URL + "/apigateway/getAccessToken", null, dataMap, AccessTokenResponse.class);
        return accessTokenResponse;
    }

    /**
     * 产生临时的Ticket，只能用一次
     *
     * @param accessToken
     * @return
     * @throws IOException
     */
    public static OauthTicketResponse createOauthTicket(final String accessToken) throws IOException {
        OauthTicketResponse oauthTicketResponse = OKHttpUtils.get(URL + "/oauth/createOauthTicket?accessToken=" + accessToken, null, OauthTicketResponse.class);
        return oauthTicketResponse;
    }

    /**
     * 获取登录授权页面的地址
     *
     * @param accessTicket
     * @return
     */
    public static String getJump2eduyunLoginUrl(final String accessTicket) {
        String loginUrl = "http://gateway.system.eduyun.cn:40015/bmp-oauth?accessTicket={accessTicket}&redirect_uri=http%3a%2f%2flocalhost%2fwww";
        loginUrl = loginUrl.replace("{accessTicket}", accessTicket);
        System.out.println("Eduyun Login Url:  " + loginUrl);
        return loginUrl;
    }

    /**
     * 获取登录授权页面的地址2
     *
     * @param accessTicket
     * @param accessToken
     * @return
     */
    public static String getJump2eduyunLoginUrl2(final String accessTicket, final String accessToken) {
        String loginUrl = "http://gateway.system.eduyun.cn:40015/bmp-oauth?accessTicket={accessTicket}&accessToken={accessToken}&redirect_uri=http%3a%2f%2flocalhost%2fwww";
        loginUrl = loginUrl.replace("{accessTicket}", accessTicket).replace("{accessToken}", accessToken);
        System.out.println("Eduyun Login Url2: " + loginUrl);
        return loginUrl;
    }

    /**
     * 校验应用用户登录(type为“0”表示接口登记，type为“1”表示页面登记)
     *
     * @param accessToken
     * @return
     */
    public static AppRegisterResponse getIndependentAppRegister(String accessToken) throws IOException {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("userId", "16764");
        dataMap.put("loginAccount", "SH000064");
        dataMap.put("type", "1");
        //orgId可根据用户所在学校获取，不存在时根据/baseInfo/getOrgList随意获取一个
        dataMap.put("orgId", "2fb6a725c584437a81758faf0868c63d");
        //身份(0学生 1教师 2家长 3学校工作人员 4机构工作人员)
        dataMap.put("userIdentity", "1");

        AppRegisterResponse appRegisterResponse = OKHttpUtils.post(URL + "/cert/independentAppRegister?accessToken=" + accessToken, null, dataMap, AppRegisterResponse.class);
        return appRegisterResponse;
    }

    /**
     * 获取全部机构编码信息
     *
     * @param accessToken
     * @return
     * @throws IOException
     */
    public static void getOrgList(String accessToken) throws IOException {
        List<Orginfo> allOrginfoList = new ArrayList<>();
        //参数皆为可选参数
        Integer checkNumber = 10000;
        Integer pageNo = 1;
        final Integer pageSize = 10000;
        while (checkNumber.equals(pageSize)) {
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("pageNo", String.valueOf(pageNo++));
            dataMap.put("pageSize", String.valueOf(pageSize));
            Object object = OKHttpUtils.post(URL + "/baseInfo/getOrgList?accessToken=" + accessToken, null, dataMap, OrgListResponse.class);
            if (null != object) {
                List<Orginfo> orginfoList = ((OrgListResponse) object).getData().getDataList();
                if (null != orginfoList && !orginfoList.isEmpty() && orginfoList.size() > 0) {
                    allOrginfoList.addAll(orginfoList);
                    checkNumber = orginfoList.size();
                }
            }
        }
        //TODO 插入数据库操作放到了内部工程中

        if (null != allOrginfoList && !allOrginfoList.isEmpty() && allOrginfoList.size() > 0) {

            List<String> headers = Arrays.asList("eduyun_org_id", "eduyun_org_title");

            OutputStream file = new FileOutputStream("D:\\"+"OrgInfo"+System.currentTimeMillis()+ ".xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("sheet1");
            XSSFRow row = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                XSSFCell cell = row.createCell(i);
                cell.setCellValue(headers.get(i));
            }
            int rownum = 1;
            for (int j = 0; j < allOrginfoList.size(); j++) {
                Orginfo data = allOrginfoList.get(j);

                XSSFRow rowContent = sheet.createRow(rownum);
                //eduyun_org_id
                XSSFCell cell = rowContent.createCell(0);
                if (data.getOrgId() != null) {
                    cell.setCellValue(data.getOrgId());
                }
                //eduyun_org_title
                cell = rowContent.createCell(1);
                if (data.getOrgName() != null) {
                    cell.setCellValue(data.getOrgName());
                }
                rownum++;
            }
            file.flush();
            workbook.write(file);
        }
    }

    /**
     * 实名认证校验
     *
     * @param accessToken
     * @return
     * @throws IOException
     */
    public static Object getCretValidate(String accessToken) throws IOException {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("userId", "27455");//已实名注册的ID为
        dataMap.put("loginAccount", "SH000090");//已实名注册的账号为
        Object object = OKHttpUtils.post(URL + "/userSession/validateSession?accessToken=" + accessToken, null, dataMap, Object.class);
        Map<String, Object> resultMap = (Map<String, Object>) object;

        //用户已经在eduyun系统平台通过了实名认证
        if (null != resultMap && !resultMap.isEmpty() && "000000".equals(resultMap.get("retCode"))) {
            //TODO 在hzs_eduyun_user_verified表中insert一条记录,用来记录用户已经实名认证过
            //TODO 返回 json {"eduyunVerified":true,"url":""}
            System.out.println("------");
        } else if (null != resultMap && !resultMap.isEmpty() && "300027".equals(resultMap.get("retCode"))) {
            //TODO 返回 json {"eduyunVerified":false,"url":”http://system.eduyun.cn:80/bmp-web/certification/indexPage?p5zVo4cYqZL02OtPcaiZNHx4aFpd0jESMrYFOFlx0H7kdQtEGWwChTfianf7Pjeg8xiHRc0enzOlprTQPR3smdgFW3m0op+TMynNts5VslFBRmlyH9M4fkFUVTT2tjrufLt/R/lpIQvPXxRV3F6+4A==”}
            Map<String, String> dataMap1 = (Map<String, String>) resultMap.get("data");
            System.out.println(dataMap1.get("certUrl"));
        }


        return null;
    }


    /**
     * 验证用户会话ticket
     *
     * @param accessToken
     * @param ticket
     * @return
     * @throws IOException
     */
    public static Object validaTicket(String accessToken, String ticket) throws IOException {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("ticket", ticket);
        Object validaTicket = OKHttpUtils.post(URL + "/userSession/validaTicket?accessToken=" + accessToken, null, dataMap, Object.class);
        return validaTicket;
    }

}
