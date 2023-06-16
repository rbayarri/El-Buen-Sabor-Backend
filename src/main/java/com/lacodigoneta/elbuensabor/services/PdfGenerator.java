package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.Order;
import com.lacodigoneta.elbuensabor.entities.OrderDetail;
import com.lacodigoneta.elbuensabor.enums.DeliveryMethod;
import com.lacodigoneta.elbuensabor.enums.PaymentMethod;
import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ByteArrayResource;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PdfGenerator {

    public static void generateResponse(HttpServletResponse response, Order order, boolean invoice) {

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo generar el archivo PDF");
        }
        generateOrderContent(order, invoice, document);
        document.close();
    }

    public static ByteArrayResource generate(Order order, boolean invoice) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();
        generateOrderContent(order, invoice, document);
        document.close();
        return new ByteArrayResource(out.toByteArray());
    }


    private static void generateOrderContent(Order order, boolean invoice, Document document) {

        Locale locale = new Locale("es", "AR");
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);

        Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTiltle.setSize(20);

        String title = invoice ? "Factura" : "Nota de Crédito";

        Integer numero = invoice ? order.getInvoice().getNumber() : order.getInvoice().getCreditNote().getNumber();

        Paragraph paragraph1 = new Paragraph(title + " N° " + numero, fontTiltle);
        paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph1);

        PdfPTable metadata = new PdfPTable(2);
        metadata.setWidthPercentage(100f);
        metadata.setWidths(new int[]{3, 9});
        metadata.setSpacingBefore(25);

        Font fontMetadata = FontFactory.getFont(BaseFont.TIMES_ROMAN, 11);

        PdfPCell cell2 = new PdfPCell();
        cell2.setPhrase(new Phrase("Fecha y hora", fontMetadata));
        cell2.setPaddingLeft(5);
        metadata.addCell(cell2);
        metadata.addCell(new Phrase(order.getDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontMetadata));
        cell2.setPhrase(new Phrase("Nombre y Apellido", fontMetadata));
        cell2.setPaddingLeft(5);
        metadata.addCell(cell2);
        metadata.addCell(new Phrase(order.getUser().getName() + " " + order.getUser().getLastName(), fontMetadata));
        cell2.setPhrase(new Phrase("Método de pago", fontMetadata));
        cell2.setPaddingLeft(5);
        metadata.addCell(cell2);
        metadata.addCell(new Phrase(order.getPaymentMethod().equals(PaymentMethod.MERCADO_PAGO) ? "Mercado Pago" : "Efectivo", fontMetadata));
        cell2.setPhrase(new Phrase("Método de envío,", fontMetadata));
        cell2.setPaddingLeft(5);
        metadata.addCell(cell2);
        metadata.addCell(new Phrase(order.getDeliveryMethod().equals(DeliveryMethod.HOME_DELIVERY) ? "Envío a domicilio" : "Retiro en local", fontMetadata));
        if (!invoice) {
            cell2.setPhrase(new Phrase("Factura anulada", fontMetadata));
            cell2.setPaddingLeft(5);
            metadata.addCell(new Phrase(String.valueOf(order.getInvoice().getNumber()), fontMetadata));
            metadata.addCell(cell2);
        }
        document.add(metadata);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setWidths(new int[]{4, 2, 3, 3});
        table.setSpacingBefore(5);
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11);
        font.setColor(CMYKColor.WHITE);
        cell.setBackgroundColor(Color.DARK_GRAY);
        cell.setPhrase(new Phrase("Producto", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Cantidad", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell.setPhrase(new Phrase("P. Unitario", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell.setPhrase(new Phrase("P. Total", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        Font fontTableData = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11);
        for (OrderDetail od : order.getOrderDetails()) {
            table.addCell(new Phrase(od.getProduct().getName(),fontTableData));
            table.addCell(new Phrase(String.valueOf(od.getQuantity()),fontTableData));
            table.addCell(new Phrase(formatter.format(od.getUnitPrice()),fontTableData));
            table.addCell(new Phrase(formatter.format(od.getUnitPrice().multiply(BigDecimal.valueOf(od.getQuantity()))),fontTableData));
        }
        document.add(table);

        Paragraph paragraph2 = new Paragraph("Total " +
                formatter.format(order.getOrderDetails()
                        .stream()
                        .mapToDouble(od -> od.getQuantity() * od.getUnitPrice().doubleValue())
                        .sum()),
                FontFactory.getFont(BaseFont.TIMES_ROMAN, 12, Font.BOLD));
        paragraph2.setAlignment(Paragraph.ALIGN_RIGHT);
        document.add(paragraph2);
    }


}
