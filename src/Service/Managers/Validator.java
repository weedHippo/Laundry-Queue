package Service.Managers;


public final class Validator {

    private Validator() {} // Prevent instantiation

    // Check null or empty strings
    public static void requireNotBlank(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    field + " cannot be empty."
            );
        }
    }

    // Check positive IDs
    public static void requirePositiveId(int id, String field) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    field + " must be greater than 0."
            );
        }
    }

    // Check positive numeric values
    public static void requirePositive(double value, String field) {
        if (!Double.isFinite(value) || value <= 0) {
            throw new IllegalArgumentException(
                    field + " must be greater than 0."
            );
        }
    }

    // Check if a string matches a pattern
    public static void requirePattern(
            String value,
            String regex,
            String field) {

        requireNotBlank(value, field);

        if (!value.matches(regex)) {
            throw new IllegalArgumentException(
                    "Invalid " + field + " format."
            );
        }
    }

    // Check if an object exists
    public static <T> T requireExists(T object, String field) {
        if (object == null) {
            throw new IllegalArgumentException(
                    field + " does not exist."
            );
        }
        return object;
    }

    // Check if an enum/state is valid
    public static void requireState(
            boolean condition,
            String message) {

        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}