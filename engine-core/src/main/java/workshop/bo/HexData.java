package workshop.bo;

import lombok.Data;

import java.util.Date;

@Data
public class HexData {
    private String id;
    private String payload;
    private Date timestamp;
}
