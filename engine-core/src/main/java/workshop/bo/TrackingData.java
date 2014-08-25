package workshop.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class TrackingData {
    private String id;
    private Date timestamp;
    private Date created;
    private Date updateTime;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal speed;
    private Integer heading;
    private Integer satellites;
    private Integer fixStatus;
    private Integer carrier;
    private Integer rssi;
    private Integer commState;
    private BigDecimal hdop;
    private Integer inputs;
    private Integer unitStatus;
    private Integer eventIndex;
    private Integer eventCode;
    private Integer accums;
    private Integer spare;
    private List<Integer> accum;
}
