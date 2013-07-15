package dbfit.util;

public enum Direction {
    RETURN_VALUE,
    INPUT,
    OUTPUT,
    INPUT_OUTPUT;

    public boolean isInput() {
        return this == INPUT;
    }

    public boolean isOutOrInout() {
        switch (this) {
            case OUTPUT:
            case INPUT_OUTPUT:
                return true;
        }

        return false;
    }

    public boolean isInOrInout() {
        switch (this) {
            case INPUT:
            case INPUT_OUTPUT:
                return true;
        }

        return false;
    }

    public boolean isOutputOrReturnValue() {
        switch (this) {
            case RETURN_VALUE:
            case OUTPUT:
            case INPUT_OUTPUT:
                return true;
            default:
                return false;
        }
    }

    public boolean isReturnValue() {
        return this == RETURN_VALUE;
    }
}
