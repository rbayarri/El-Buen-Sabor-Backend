package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.entities.BaseEntity;
import com.lowagie.text.*;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfGenerator {

    public static void generateResponse(HttpServletResponse response) throws DocumentException, IOException {

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Font fontTiltle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTiltle.setSize(20);

        Paragraph paragraph1 = new Paragraph("List of the Students", fontTiltle);
        paragraph1.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(paragraph1);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setWidths(new int[]{3, 3, 3, 3});
        table.setSpacingBefore(5);
        // Create Table Cells for the table header
        PdfPCell cell = new PdfPCell();
        // Setting the background color and padding of the table cell
        cell.setBackgroundColor(CMYKColor.BLUE);
        cell.setPadding(5);
        // Creating font
        // Setting font style and size
        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.WHITE);
        // Adding headings in the created table cell or  header
        // Adding Cell to table
        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Student Name", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Email", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Mobile No", font));
        table.addCell(cell);
        // Iterating the list of students
        // Adding student id
        table.addCell("Valor 1");
        // Adding student name
        table.addCell("Valor 2");
        // Adding student email
        table.addCell("Valor 3");
        // Adding student mobile
        table.addCell("Valor 4");

        // Adding the created table to the document
        document.add(table);
        // Closing the document
        document.close();
    }

    public static ByteArrayInputStream generate(BaseEntity entity) {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();


//        document.add(table);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

}
