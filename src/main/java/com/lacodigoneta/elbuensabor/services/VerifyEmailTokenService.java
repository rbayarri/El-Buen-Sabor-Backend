package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.VerifyEmailToken;
import com.lacodigoneta.elbuensabor.repositories.VerifyEmailTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class VerifyEmailTokenService extends BaseServiceImpl<VerifyEmailToken, VerifyEmailTokenRepository> {


    public VerifyEmailTokenService(VerifyEmailTokenRepository repository) {
        super(repository);
    }

    @Override
    public VerifyEmailToken changeStates(VerifyEmailToken source, VerifyEmailToken destination) {
        return null;
    }

    @Override
    public void beforeSaveValidations(VerifyEmailToken entity) {

    }
}
