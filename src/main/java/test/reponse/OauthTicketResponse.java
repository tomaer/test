package test.reponse;

import lombok.Data;

@Data
public class OauthTicketResponse implements java.io.Serializable {
    public String accessTicket;
    public String retCode;
    public String retDesc;

    @Override
    public String toString() {
        return "OauthTicketResponse{" +
                "accessTicket='" + accessTicket + '\'' +
                ", retCode='" + retCode + '\'' +
                ", retDesc='" + retDesc + '\'' +
                '}';
    }
}
