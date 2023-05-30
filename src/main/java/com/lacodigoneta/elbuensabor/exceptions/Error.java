package com.lacodigoneta.elbuensabor.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Error {

    private String message;

    private Map<String, String> errors = new HashMap<>();

}
