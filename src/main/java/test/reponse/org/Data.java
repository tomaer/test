package test.reponse.org;

import java.util.List;

@lombok.Data
public class Data implements java.io.Serializable {

    private List<Orginfo> dataList;
    private int count;

    @Override
    public String toString() {
        return "Data{" +
                "dataList=" + dataList +
                ", count=" + count +
                '}';
    }
}
