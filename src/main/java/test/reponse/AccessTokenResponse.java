package test.reponse;

import lombok.Data;

@Data
public class AccessTokenResponse implements java.io.Serializable {

    public String retCode;
    public String retDesc;
    public JsonObject data;

    @Data
    public class JsonObject {
        public String accessToken;
        public String appId;
        public String appLvl;
        public String appName;
        public String appType;
        public String sysCode;
        public String userId;
        public String validTime;
    }

    @Override
    public String toString() {
        return "AccessTokenResponse{" +
                "retCode='" + retCode + '\'' +
                ", retDesc='" + retDesc + '\'' +
                ", data=" + data +
                '}';
    }
}
