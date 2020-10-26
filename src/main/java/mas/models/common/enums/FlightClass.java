package mas.models.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString(includeFieldNames = false)
public enum FlightClass {

    ECONOMY("Economy"),
    PREMIUM_ECONOMY("Premium economy"),
    BUSINESS_OR_FIRST("Business or First");

    private final String classFlight;

}