package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Profit;
import com.lacodigoneta.elbuensabor.repositories.ProfitRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfitService extends BaseServiceImpl<Profit, ProfitRepository> {

    public ProfitService(ProfitRepository repository) {
        super(repository);
    }

    @Override
    public Profit changeStates(Profit source, Profit destination) {
        return null;
    }

    @Override
    public void beforeSaveValidations(Profit entity) {

    }
}
