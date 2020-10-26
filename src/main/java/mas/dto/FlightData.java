package mas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlightData {
    public String depart;
    public String arrive;
    public String stops;
    public String duration;
    public String economyPrice;
}
