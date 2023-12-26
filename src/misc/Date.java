package misc;

public final class Date {
    private Date() { }

    /** checks if a date passed to it is valid */
    public static boolean isDateOK(final String date) {
        int day = Integer.valueOf(date.substring(0, 2));
        int month = Integer.valueOf(date.substring(3, 5));
        int year = Integer.valueOf(date.substring(6));

        if (year < 1990 || year > 2023) {
            return false;
        } else if (month > 12) {
            return false;
        } else if (month == 2 && day > 28) {
            return false;
        } else if (day > 31) {
            return false;
        }
        return true;
    }
}
