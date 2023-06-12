package com.lacodigoneta.elbuensabor.config;

import com.mercadopago.MercadoPagoConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConf {

    @Bean
    public void MercadoPagoInit() {
        MercadoPagoConfig.setAccessToken("TEST-2872458347585384-061000-5b7c71de414edf7b8bf9faf689f4e197-331144077");
    }
}
