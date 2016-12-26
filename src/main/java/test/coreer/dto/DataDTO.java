package test.coreer.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Created by aieremenko on 12/26/16.
 */
@Data
@Builder
public class DataDTO {
    private Integer masterRowId;
    private Integer tenantRowId;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
}
