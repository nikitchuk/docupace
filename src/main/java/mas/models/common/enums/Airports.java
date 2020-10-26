package mas.models.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString(includeFieldNames = false)
public enum Airports {

    NEW_YORK_JFK("New York JFK","New York JFK","New York, NY, US (JFK)"),
    MIAMI_ALL("Miami MFL","Miami","Miami, FL, US (MIA - All Airports)");

    private final String airPortName;
    private final String airPortSearch;
    private final String airPortNameInList;

}