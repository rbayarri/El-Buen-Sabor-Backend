package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.CreditNote;
import com.lacodigoneta.elbuensabor.entities.Invoice;
import com.lacodigoneta.elbuensabor.repositories.CreditNoteRepository;
import org.springframework.stereotype.Service;

@Service
public class CreditNoteService extends BaseServiceImpl<CreditNote, CreditNoteRepository> {

    public CreditNoteService(CreditNoteRepository repository) {
        super(repository);
    }

    @Override
    public CreditNote changeStates(CreditNote source, CreditNote destination) {
        return null;
    }

    @Override
    public void beforeSaveValidations(CreditNote entity) {

    }

    public CreditNote createCreditNote(Invoice invoice) {
        CreditNote creditNote = new CreditNote();
        creditNote.setInvoice(invoice);
        creditNote.setNumber(getNextNumber());
        return save(creditNote);
    }

    private Integer getNextNumber() {
        return findAll().stream().mapToInt(CreditNote::getNumber).max().orElse(0) + 1;
    }

}
