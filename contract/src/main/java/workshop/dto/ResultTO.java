package workshop.dto;

import lombok.Data;
import lombok.NonNull;
import workshop.enums.ResultCode;

@Data
public class ResultTO {
    private @NonNull ResultCode code;
}
