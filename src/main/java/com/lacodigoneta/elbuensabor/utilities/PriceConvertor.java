package com.lacodigoneta.elbuensabor.utilities;

import com.lacodigoneta.elbuensabor.enums.MeasurementUnit;
import com.lacodigoneta.elbuensabor.exceptions.IncompatibleMeasurementUnitTypeException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceConvertor {

    public static BigDecimal convert(BigDecimal quantity, MeasurementUnit from, MeasurementUnit to) {
        if (!from.getType().equals(to.getType())) {
            throw new IncompatibleMeasurementUnitTypeException(from, to);
        }
        BigDecimal multiplicand = BigDecimal.valueOf(to.getFactor());
        BigDecimal divisor = BigDecimal.valueOf(from.getFactor());
        return quantity.multiply(multiplicand).divide(divisor, 3, RoundingMode.UP);
    }

}
