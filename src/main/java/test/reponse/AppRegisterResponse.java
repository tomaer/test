package test.reponse;

import lombok.Data;

@Data
public class AppRegisterResponse implements java.io.Serializable {

    public String retCode;
    public String retDesc;
    public JsonObject data;

    @Data
    public class JsonObject {
        public String registerUrl;
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
