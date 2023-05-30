package com.lacodigoneta.elbuensabor.utilities;

import com.lacodigoneta.elbuensabor.enums.MeasurementUnit;
import com.lacodigoneta.elbuensabor.exceptions.IncompatibleMeasurementUnitTypeException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MeasurementUnitConversion {

    public static double convert(double quantity, MeasurementUnit from, MeasurementUnit to) {
        if (!from.getType().equals(to.getType())) {
            throw new IncompatibleMeasurementUnitTypeException(from, to);
        }
        return quantity * from.getFactor() / to.getFactor();
    }

    public static BigDecimal convert(BigDecimal quantity, MeasurementUnit from, MeasurementUnit to) {
        if (!from.getType().equals(to.getType())) {
            throw new IncompatibleMeasurementUnitTypeException(from, to);
        }
        BigDecimal multiplicand = BigDecimal.valueOf(from.getFactor());
        BigDecimal divisor = BigDecimal.valueOf(to.getFactor());
        return quantity.multiply(multiplicand).divide(divisor, 3, RoundingMode.UP);
    }
}
