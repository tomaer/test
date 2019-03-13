package test.reponse.org;

import java.util.List;

@lombok.Data
public class OrgListResponse implements java.io.Serializable {

    private String retCode;
    private String retDesc;
    private Data data;

    @Override
    public String toString() {
        return "OrgListResponse{" +
                "retCode='" + retCode + '\'' +
                ", retDesc='" + retDesc + '\'' +
                ", data=" + data +
                '}';
    }
}
