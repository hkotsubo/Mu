package mu;

import java.math.BigDecimal;

public class Value {

    // valor pode ser um booleano ou um número: se número é null, então é booleano
    private final boolean bool;
    private final BigDecimal number;

    public Value(boolean bool) {
        this(bool, null);
    }

    public Value(double d) {
        this(false, BigDecimal.valueOf(d));
    }

    public Value(BigDecimal number) {
        this(false, number);
    }

    private Value(boolean bool, BigDecimal number) {
        this.bool = bool;
        this.number = number;
    }

    public Boolean booleanValue() {
        return this.bool;
    }

    public BigDecimal numberValue() {
        return this.number;
    }

    public boolean isNumber() {
        return this.number != null;
    }

    @Override
    public int hashCode() {
        if (this.isNumber()) {
            return this.number.hashCode();
        }
        return Boolean.hashCode(this.bool);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final Value other = (Value) obj;
        boolean thisIsDouble = this.isNumber();
        boolean otherIsDouble = other.isNumber();
        if (thisIsDouble != otherIsDouble) {
            return false;
        }
        if (thisIsDouble) {
            return this.number.equals(other.number);
        }
        return this.bool == other.bool;
    }

    @Override
    public String toString() {
        if (this.isNumber()) {
            return String.valueOf(this.number);
        }
        return String.valueOf(this.bool);
    }
}
