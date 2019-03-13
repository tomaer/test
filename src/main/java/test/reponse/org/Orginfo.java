package test.reponse.org;

import lombok.Data;

@Data
public class Orginfo implements java.io.Serializable {

    private String orgType;
    private String orgId;
    private String areaCode;
    private String provinceCode;
    private String cityCode;
    private String orgName;

    @Override
    public String toString() {
        return "Orginfo{" +
                "orgType='" + orgType + '\'' +
                ", orgId='" + orgId + '\'' +
                ", areaCode='" + areaCode + '\'' +
                ", provinceCode='" + provinceCode + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", orgName='" + orgName + '\'' +
                '}';
    }
}
