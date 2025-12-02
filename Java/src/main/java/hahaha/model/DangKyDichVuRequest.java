package hahaha.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DangKyDichVuRequest {
    private String maKH;
    private Long accountId;
    private List<String> dsMaDV;
    private List<String> dsTrainerId;
    private List<String> dsClassId;

}
