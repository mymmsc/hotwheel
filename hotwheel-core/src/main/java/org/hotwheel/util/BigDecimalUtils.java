package org.hotwheel.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * 高精度浮点数
 *
 * @since 3.0.0
 */
public class BigDecimalUtils implements Comparable<BigDecimalUtils>, Serializable {
    private static final long serialVersionUID = -3178121776163258055L;
    private static final RoundingMode DEFAULT_ROUNDING;
    private static final RoundingMode ROUNDING_MODE_DOWN;
    private static final MathContext DEF_MC;
    private static final int PARSE_SCALE = 6;
    private final BigDecimal amount;

    public static BigDecimalUtils of(BigDecimal amount) {
        return new BigDecimalUtils(amount);
    }

    public static BigDecimalUtils of(String amount) {
        return new BigDecimalUtils(new BigDecimal(amount));
    }

    public static BigDecimalUtils of(double amount) {
        return of(BigDecimal.valueOf(amount));
    }

    public static BigDecimalUtils ofMajor(long amountMajor) {
        return of(BigDecimal.valueOf(amountMajor));
    }

    public static BigDecimalUtils parseStoreValue(long amount) {
        return of(BigDecimal.valueOf(amount, 6));
    }

    public static BigDecimalUtils of(String amount, String defaultValue) {
        return amount != null && amount.trim().length() != 0?new BigDecimalUtils(new BigDecimal(amount)):new BigDecimalUtils(new BigDecimal(defaultValue));
    }

    public static BigDecimalUtils total(BigDecimalUtils... monies) {
        if(monies.length == 0) {
            throw new IllegalArgumentException("Money array must not be empty");
        } else {
            BigDecimalUtils total = monies[0];

            for(int i = 1; i < monies.length; ++i) {
                total = total.plus(new BigDecimalUtils[]{monies[i]});
            }

            return total;
        }
    }

    private BigDecimalUtils(BigDecimal amount) {
        this.amount = amount;
    }

    private BigDecimalUtils with(BigDecimal newAmount) {
        return newAmount.equals(this.amount)?this:new BigDecimalUtils(newAmount);
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isPositiveOrZero() {
        return this.amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isNegativeOrZero() {
        return this.amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    public BigDecimalUtils plus(BigDecimalUtils[] toAdds) {
        BigDecimal total = this.amount;
        BigDecimalUtils[] arr$ = toAdds;
        int len$ = toAdds.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            BigDecimalUtils money = arr$[i$];
            total = total.add(money.amount, DEF_MC);
        }

        return this.with(total);
    }

    public BigDecimalUtils plus(BigDecimal amountToAdd) {
        if(amountToAdd.compareTo(BigDecimal.ZERO) == 0) {
            return this;
        } else {
            BigDecimal newAmount = this.amount.add(amountToAdd, DEF_MC);
            return this.with(newAmount);
        }
    }

    public BigDecimalUtils plus(BigDecimalUtils amountToAdd) {
        return total(new BigDecimalUtils[]{this, amountToAdd});
    }

    public BigDecimalUtils plus(double amountToAdd) {
        if(amountToAdd == 0.0D) {
            return this;
        } else {
            BigDecimal newAmount = this.amount.add(BigDecimal.valueOf(amountToAdd), DEF_MC);
            return this.with(newAmount);
        }
    }

    public BigDecimalUtils minus(BigDecimalUtils[] accountMoneys) {
        BigDecimal total = this.amount;
        BigDecimalUtils[] arr$ = accountMoneys;
        int len$ = accountMoneys.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            BigDecimalUtils money = arr$[i$];
            total = total.subtract(money.amount, DEF_MC);
        }

        return this.with(total);
    }

    public BigDecimalUtils minus(BigDecimalUtils moneyToSubtract) {
        return this.minus(moneyToSubtract.amount);
    }

    public BigDecimalUtils minus(BigDecimal amountToSubtract) {
        if(amountToSubtract.compareTo(BigDecimal.ZERO) == 0) {
            return this;
        } else {
            BigDecimal newAmount = this.amount.subtract(amountToSubtract, DEF_MC);
            return this.with(newAmount);
        }
    }

    public BigDecimalUtils minus(double amountToSubtract) {
        if(amountToSubtract == 0.0D) {
            return this;
        } else {
            BigDecimal newAmount = this.amount.subtract(BigDecimal.valueOf(amountToSubtract), DEF_MC);
            return this.with(newAmount);
        }
    }

    public BigDecimalUtils minusMajor(long amountToSubtract) {
        if(amountToSubtract == 0L) {
            return this;
        } else {
            BigDecimal newAmount = this.amount.subtract(BigDecimal.valueOf(amountToSubtract), DEF_MC);
            return this.with(newAmount);
        }
    }

    public BigDecimalUtils minusMinor(long amountToSubtract, int scale) {
        if(amountToSubtract == 0L) {
            return this;
        } else {
            BigDecimal newAmount = this.amount.subtract(BigDecimal.valueOf(amountToSubtract, scale), DEF_MC);
            return this.with(newAmount);
        }
    }

    public BigDecimalUtils multiply(BigDecimalUtils valueToMultiplyBy) {
        return this.multiply(valueToMultiplyBy.amount);
    }

    public BigDecimalUtils multiply(BigDecimal valueToMultiplyBy) {
        if(valueToMultiplyBy.compareTo(BigDecimal.ONE) == 0) {
            return this;
        } else {
            BigDecimal newAmount = this.amount.multiply(valueToMultiplyBy, DEF_MC);
            return this.with(newAmount);
        }
    }

    public BigDecimalUtils multiply(double valueToMultiplyBy) {
        if(valueToMultiplyBy == 1.0D) {
            return this;
        } else {
            BigDecimal newAmount = this.amount.multiply(BigDecimal.valueOf(valueToMultiplyBy), DEF_MC);
            return this.with(newAmount);
        }
    }

    public BigDecimalUtils multiply(long valueToMultiplyBy) {
        if(valueToMultiplyBy == 1L) {
            return this;
        } else {
            BigDecimal newAmount = this.amount.multiply(BigDecimal.valueOf(valueToMultiplyBy), DEF_MC);
            return this.with(newAmount);
        }
    }

    public BigDecimalUtils divide(BigDecimalUtils value) {
        return this.divide(value.amount);
    }

    public BigDecimalUtils divide(BigDecimal value) {
        if(value.compareTo(BigDecimal.ONE) == 0) {
            return this;
        } else {
            BigDecimal newAmount = this.amount.divide(value, DEF_MC);
            return this.with(newAmount);
        }
    }

    public BigDecimalUtils divide(double value) {
        if(value == 1.0D) {
            return this;
        } else {
            BigDecimal newAmount = this.amount.divide(BigDecimal.valueOf(value), DEF_MC);
            return this.with(newAmount);
        }
    }

    public BigDecimalUtils divide(long value) {
        if(value == 1L) {
            return this;
        } else {
            BigDecimal newAmount = this.amount.divide(BigDecimal.valueOf(value), DEF_MC);
            return this.with(newAmount);
        }
    }

    public BigDecimalUtils negate() {
        return this.isZero()?this:this.with(this.amount.negate());
    }

    public BigDecimalUtils abs() {
        return this.isNegative()?this.negate():this;
    }

    public boolean isGreaterThan(BigDecimalUtils other) {
        return this.compareTo(other) > 0;
    }

    public boolean isLessThan(BigDecimalUtils other) {
        return this.compareTo(other) < 0;
    }

    public long toStoreValue() {
        return this.amount.movePointRight(6).setScale(0, DEFAULT_ROUNDING).longValue();
    }

    public BigDecimal toStoreDecimal() {
        return this.amount.setScale(6, DEFAULT_ROUNDING);
    }

    public int compareTo(BigDecimalUtils o) {
        return this.amount.compareTo(o.amount);
    }

    public boolean equals(Object other) {
        if(this == other) {
            return true;
        } else if(other instanceof BigDecimalUtils) {
            BigDecimalUtils otherMoney = (BigDecimalUtils)other;
            return this.amount.equals(otherMoney.amount);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return 7 * this.amount.hashCode();
    }

    public String toString() {
        return this.amount.toString();
    }

    public String toMoneyString() {
        return this.amount.setScale(2, DEFAULT_ROUNDING).toString();
    }

    public String toMoneyString(int scale) {
        return this.amount.setScale(scale, ROUNDING_MODE_DOWN).toString();
    }

    public BigDecimalUtils zero() {
        return of(0.0D);
    }

    static {
        DEFAULT_ROUNDING = RoundingMode.HALF_EVEN;
        ROUNDING_MODE_DOWN = RoundingMode.DOWN;
        DEF_MC = MathContext.DECIMAL64;
    }
}
