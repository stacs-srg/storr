package uk.ac.standrews.cs.digitising_scotland.linkage.event_records;

/**
 * Created by graham on 13/05/2014.
 */
public abstract class Record {

    public static final String SEPARATOR = "|";

    protected String uid;
    protected String entry;
    protected String entry_corrected;

    protected String registration_year;
    protected String registration_district_number;
    protected String registration_district_suffix;

    protected String image_quality;

    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(final String entry) {
        this.entry = entry;
    }

    public String getEntryCorrected() {
        return entry_corrected;
    }

    public void setEntryCorrected(final String entry_corrected) {
        this.entry_corrected = entry_corrected;
    }

    public String getRegistrationYear() {
        return registration_year;
    }

    public void setRegistrationYear(final String registration_year) {
        this.registration_year = registration_year;
    }

    public String getRegistrationDistrictNumber() {
        return registration_district_number;
    }

    public void setRegistrationDistrictNumber(final String registration_district_number) {
        this.registration_district_number = registration_district_number;
    }

    public String getRegistrationDistrictSuffix() {
        return registration_district_suffix;
    }

    public void setRegistrationDistrictSuffix(final String registration_district_suffix) {
        this.registration_district_suffix = registration_district_suffix;
    }

    public String getImageQuality() {
        return image_quality;
    }

    public void setImageQuality(final String image_quality) {
        this.image_quality = image_quality;
    }

    protected String getRecordedParentsSurname(final String parents_surname, final String childs_surname) {

        return parents_surname.equals(childs_surname) ? "0" : parents_surname;
    }

    protected void append(final StringBuilder builder, final Object... fields) {

        for (Object field : fields) {
            append(builder, field != null ? field.toString().toUpperCase() : null);
        }
    }

    protected void append(final StringBuilder builder, final String field) {

        if (field != null) {
            builder.append(field);
        }
        builder.append(SEPARATOR);
    }

    /**
     * Created by graham on 14/05/2014.
     */
    public static class DateRecord {

        private String day;
        private String month;
        private String year;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }
    }
}
