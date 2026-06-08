package uhk.slechjo1.stag.util;

import java.time.LocalTime;
import java.util.List;

public record TimeSlot(LocalTime od, LocalTime doCas) {
    public static List<TimeSlot> vychoziSloty() {
        return List.of(
                new TimeSlot(LocalTime.of(7, 30), LocalTime.of(9, 5)),
                new TimeSlot(LocalTime.of(9, 10), LocalTime.of(10, 45)),
                new TimeSlot(LocalTime.of(10, 50), LocalTime.of(12, 25)),
                new TimeSlot(LocalTime.of(12, 30), LocalTime.of(14, 5)),
                new TimeSlot(LocalTime.of(14, 10), LocalTime.of(15, 45)),
                new TimeSlot(LocalTime.of(15, 50), LocalTime.of(17, 25)),
                new TimeSlot(LocalTime.of(17, 30), LocalTime.of(19, 5))
        );
    }
}
