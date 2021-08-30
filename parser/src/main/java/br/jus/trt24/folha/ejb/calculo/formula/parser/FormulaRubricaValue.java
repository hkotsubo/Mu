package br.jus.trt24.folha.ejb.calculo.formula.parser;

import java.math.BigDecimal;

public class FormulaRubricaValue {

    // valor pode ser um booleano ou um número: se número é null, então é booleano
    private final boolean bool;
    private final BigDecimal number;

    public FormulaRubricaValue(boolean bool) {
        this(bool, null);
    }

    public FormulaRubricaValue(double d) {
        this(false, new BigDecimal(d));
    }

    public FormulaRubricaValue(BigDecimal number) {
        this(false, number);
    }

    private FormulaRubricaValue(boolean bool, BigDecimal number) {
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

        final FormulaRubricaValue other = (FormulaRubricaValue) obj;
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
