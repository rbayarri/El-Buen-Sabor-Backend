package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.CreditNote;
import com.lacodigoneta.elbuensabor.entities.Invoice;
import com.lacodigoneta.elbuensabor.entities.Order;
import com.lacodigoneta.elbuensabor.repositories.InvoiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService extends BaseServiceImpl<Invoice, InvoiceRepository> {

    private final CreditNoteService creditNoteService;

    public InvoiceService(InvoiceRepository repository, CreditNoteService creditNoteService) {
        super(repository);
        this.creditNoteService = creditNoteService;
    }

    @Override
    public Invoice changeStates(Invoice source, Invoice destination) {
        return null;
    }

    @Override
    public void beforeSaveValidations(Invoice entity) {
    }

    public Invoice createInvoice(Order order) {
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setNumber(getNextNumber());
        return save(invoice);
    }

    @Transactional(rollbackOn = Exception.class)
    public Invoice cancel(Order order) {
        Invoice invoice = order.getInvoice();
        CreditNote creditNote = creditNoteService.createCreditNote(invoice);
        invoice.setCreditNote(creditNote);
        return invoice;
    }

    private Integer getNextNumber() {
        return findAll().stream().mapToInt(Invoice::getNumber).max().orElse(0) + 1;
    }
}
