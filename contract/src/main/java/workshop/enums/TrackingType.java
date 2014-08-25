package workshop.enums;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TrackingType {
    START_TRACKING("startTracking"),
    STOP_TRACKING("stopTracking"),
    TRACKING_POINT("trackingPoint"),
    UNKNOWN("unknown");

    private @NonNull @Getter String label;
}
