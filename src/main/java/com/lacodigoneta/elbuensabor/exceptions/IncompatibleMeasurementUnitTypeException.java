package com.lacodigoneta.elbuensabor.exceptions;

import com.lacodigoneta.elbuensabor.enums.MeasurementUnit;

import static com.lacodigoneta.elbuensabor.config.AppConstants.INCOMPATIBLE_MEASUREMENT_UNIT_TYPE;

public class IncompatibleMeasurementUnitTypeException extends RuntimeException {

    public IncompatibleMeasurementUnitTypeException(MeasurementUnit from, MeasurementUnit to) {
        super(INCOMPATIBLE_MEASUREMENT_UNIT_TYPE + from + " y " + to);
    }
}
