package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.ForgetPasswordToken;
import com.lacodigoneta.elbuensabor.repositories.ForgetPasswordTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class ForgetPasswordTokenService extends BaseServiceImpl<ForgetPasswordToken, ForgetPasswordTokenRepository> {

    public ForgetPasswordTokenService(ForgetPasswordTokenRepository repository) {
        super(repository);
    }

    @Override
    public ForgetPasswordToken changeStates(ForgetPasswordToken source, ForgetPasswordToken destination) {
        return null;
    }

    @Override
    public void beforeSaveValidations(ForgetPasswordToken entity) {

    }
}
