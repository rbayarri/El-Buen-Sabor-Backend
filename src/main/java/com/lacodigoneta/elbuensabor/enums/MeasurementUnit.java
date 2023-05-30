package com.lacodigoneta.elbuensabor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeasurementUnit {

    MILIGRAMS("mg", MeasurementType.WEIGHT, 0.001),
    GRAMS("g", MeasurementType.WEIGHT, 1),
    KILOGRAMS("kg", MeasurementType.WEIGHT, 1000),
    MILILITERS("ml", MeasurementType.CAPACITY, 0.001),
    LITERS("l", MeasurementType.CAPACITY, 1),
    CUBIC_CENTIMETERS("c3", MeasurementType.CAPACITY, 1000),
    UNITS("u", MeasurementType.UNIT, 1);

    private final String abbr;

    private final MeasurementType type;

    private final double factor;

}
