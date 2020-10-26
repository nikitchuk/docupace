package mas.utils.runTime;


public enum Country {
    USA("US"),
    NOT_SPECIFIED("N/A"),
    ALL("ALL");

    private String code;

    Country(String code) {
        this.code = code;
    }

    public static Country getCountry(String code) {
        for (Country country : Country.values())
            if (country.toString().toUpperCase().equals(code))
                return country;

        return NOT_SPECIFIED;
    }

    @Override
    public String toString() {
        return code;
    }
}
