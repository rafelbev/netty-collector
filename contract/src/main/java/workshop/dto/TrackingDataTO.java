package workshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import workshop.enums.TrackingType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingDataTO implements Serializable {
    private TrackingType type;
    private String updatetime;
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
    private Date timestamp;
}