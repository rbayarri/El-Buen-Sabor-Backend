package com.lacodigoneta.elbuensabor.services;

import com.lacodigoneta.elbuensabor.dto.order.ProfitReport;
import com.lacodigoneta.elbuensabor.dto.order.RankingClients;
import com.lacodigoneta.elbuensabor.dto.order.RankingProduct;
import com.lacodigoneta.elbuensabor.entities.Order;
import com.lacodigoneta.elbuensabor.entities.OrderDetail;
import com.lacodigoneta.elbuensabor.entities.Profit;
import com.lacodigoneta.elbuensabor.enums.Status;
import com.lacodigoneta.elbuensabor.repositories.OrderRepository;
import com.lacodigoneta.elbuensabor.repositories.ProfitRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ReportsService {

    private final UserService userService;

    private final ProductService productService;

    private final OrderRepository orderRepository;

    private final ProfitRepository profitRepository;

    public ReportsService(UserService userService, ProductService productService, OrderRepository orderRepository, ProfitRepository profitRepository) {
        this.userService = userService;
        this.productService = productService;
        this.orderRepository = orderRepository;
        this.profitRepository = profitRepository;
    }

    public List<RankingProduct> getRankingProducts(LocalDate from, LocalDate to, int quantityRegisters) {

        if (from.isAfter(to)) {
            throw new RuntimeException("La fecha desde debe ser posterior a la fecha hasta");
        }

        List<RankingProduct> rankingProducts = productService.findAll().stream().map(p -> RankingProduct.builder()
                        .productName(p.getName())
                        .cookingTime(p.getCookingTime())
                        .quantity(p.getOrderDetails().stream()
                                .filter(o -> !o.getOrder().getStatus().equals(Status.CANCELLED))
                                .filter(o -> o.getOrder().getDateTime().toLocalDate().isBefore(to.plusDays(1)))
                                .filter(o -> o.getOrder().getDateTime().toLocalDate().isAfter(from.minusDays(1)))
                                .filter(o -> o.getProduct().getCookingTime() > 0)
                                .mapToInt(OrderDetail::getQuantity)
                                .sum())
                        .build())
                .filter(r -> r.getQuantity() > 0)
                .sorted(Comparator.comparingInt(RankingProduct::getQuantity).reversed())
                .limit(quantityRegisters)
                .toList();

        List<RankingProduct> rankingProductsDrinks = productService.findAll().stream().map(p -> RankingProduct.builder()
                        .productName(p.getName())
                        .cookingTime(p.getCookingTime())
                        .quantity(p.getOrderDetails().stream()
                                .filter(o -> !o.getOrder().getStatus().equals(Status.CANCELLED))
                                .filter(o -> o.getOrder().getDateTime().toLocalDate().isBefore(to.plusDays(1)))
                                .filter(o -> o.getOrder().getDateTime().toLocalDate().isAfter(from.minusDays(1)))
                                .filter(o -> o.getProduct().getCookingTime() == 0)
                                .mapToInt(OrderDetail::getQuantity)
                                .sum())
                        .build())
                .filter(r -> r.getQuantity() > 0)
                .sorted(Comparator.comparingInt(RankingProduct::getQuantity).reversed())
                .limit(quantityRegisters)
                .toList();

        List<RankingProduct> listToReturn = new ArrayList<>();
        listToReturn.addAll(rankingProducts);
        listToReturn.addAll(rankingProductsDrinks);
        return listToReturn;
    }

    public void generateRankingProductsExcel(HttpServletResponse response, LocalDate from, LocalDate to, int quantityRegisters, boolean drinks) {

        List<RankingProduct> rankingProducts = getRankingProducts(from, to, quantityRegisters).stream()
                .filter(r -> drinks ? r.getCookingTime() == 0 : r.getCookingTime() > 0)
                .toList();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("RankingProductos");
        sheet.setColumnWidth(0, 8000);
        sheet.setColumnWidth(1, 4000);
        XSSFRow headerRow = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);

        XSSFCell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Producto");
        headerCell1.setCellStyle(headerStyle);
        XSSFCell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Cantidad");
        headerCell2.setCellStyle(headerStyle);
        int currentRow = 1;

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        for (RankingProduct rp : rankingProducts) {
            XSSFRow row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(rp.getProductName());
            row.createCell(1).setCellValue(rp.getQuantity());
            row.getCell(1).setCellStyle(cellStyle);
            currentRow++;
        }
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel");
        }
    }

    public List<RankingClients> getRankingClients(LocalDate from, LocalDate to, int quantityRegisters, boolean byQuantity) {

        if (from.isAfter(to)) {
            throw new RuntimeException("La fecha desde debe ser posterior a la fecha hasta");
        }

        return userService.findAll().stream().map(u -> RankingClients.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .lastName(u.getLastName())
                        .quantity(u.getOrders().stream()
                                .filter(o -> !o.getStatus().equals(Status.CANCELLED))
                                .filter(o -> o.getDateTime().toLocalDate().isBefore(to.plusDays(1)))
                                .filter(o -> o.getDateTime().toLocalDate().isAfter(from.minusDays(1)))
                                .flatMap(o -> o.getOrderDetails().stream())
                                .mapToInt(OrderDetail::getQuantity)
                                .sum())
                        .total(u.getOrders().stream()
                                .filter(o -> !o.getStatus().equals(Status.CANCELLED))
                                .filter(o -> o.getDateTime().toLocalDate().isBefore(to.plusDays(1)))
                                .filter(o -> o.getDateTime().toLocalDate().isAfter(from.minusDays(1)))
                                .flatMap(o -> o.getOrderDetails().stream())
                                .map(od -> od.getUnitPrice().multiply(BigDecimal.valueOf(od.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add))
                        .build())
                .filter(r -> r.getQuantity() > 0)
                .sorted(byQuantity ? Comparator.comparingInt(RankingClients::getQuantity).reversed() :
                        Comparator.comparing(RankingClients::getTotal).reversed())
                .limit(quantityRegisters)
                .toList();
    }

    public ProfitReport getProfit(LocalDate from, LocalDate to) {

        if (from.isAfter(to)) {
            throw new RuntimeException("La fecha desde debe ser posterior a la fecha hasta");
        }

        LocalDateTime fromLocalDateTime = from.atStartOfDay();
        LocalDateTime toLocalDateTime = to.atTime(23, 59, 59, 999_999_999);
        List<Order> orders = orderRepository.findAllByDateTimeBetweenOrderByDateTimeDesc(
                fromLocalDateTime, toLocalDateTime);

        BigDecimal cost = BigDecimal.ZERO;
        BigDecimal profit = BigDecimal.ZERO;

        for (Order order : orders) {
            if (!order.getStatus().equals(Status.CANCELLED)) {
                for (OrderDetail orderDetail : order.getOrderDetails()) {
                    cost = cost.add(orderDetail.getUnitCost().multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
                    profit = profit.add(orderDetail.getUnitPrice().subtract(orderDetail.getUnitCost()).multiply(BigDecimal.valueOf(orderDetail.getQuantity())));
                }
            }
        }

        BigDecimal holdingResult = BigDecimal.ZERO;

        List<Profit> profitList = profitRepository.findAllByDateTimeBetween(fromLocalDateTime, toLocalDateTime);
        for (Profit p : profitList) {
            holdingResult = holdingResult.add(p.getAmount());
        }

        return ProfitReport.builder()
                .profits(profit)
                .costs(cost)
                .holdingResults(holdingResult)
                .build();
    }

    public void generateRankingClientsExcel(HttpServletResponse response, LocalDate from, LocalDate to, int quantityRegisters, boolean byQuantity) {
        List<RankingClients> rankingClients = getRankingClients(from, to, quantityRegisters, byQuantity);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("RankingClientes");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 4000);
        XSSFRow headerRow = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);

        XSSFCell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Nombre");
        headerCell1.setCellStyle(headerStyle);
        XSSFCell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Apellido");
        headerCell2.setCellStyle(headerStyle);
        XSSFCell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("Cantidad de pedidos");
        headerCell3.setCellStyle(headerStyle);
        XSSFCell headerCell4 = headerRow.createCell(3);
        headerCell4.setCellValue("Importe total");
        headerCell4.setCellStyle(headerStyle);
        int currentRow = 1;

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        Locale arLocale = new Locale("es", "AR");
        NumberFormat arsFormat = NumberFormat.getCurrencyInstance(arLocale);

        for (RankingClients rc : rankingClients) {
            XSSFRow row = sheet.createRow(currentRow);
            row.createCell(0).setCellValue(rc.getName());
            row.createCell(1).setCellValue(rc.getLastName());
            row.createCell(2).setCellValue(rc.getQuantity());
            row.getCell(2).setCellStyle(cellStyle);
            row.createCell(3).setCellValue(arsFormat.format(rc.getTotal().doubleValue()));
            row.getCell(2).setCellStyle(cellStyle);
            currentRow++;
        }
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel");
        }
    }

    public void generateProfitsExcel(HttpServletResponse response, LocalDate from, LocalDate to) {
        ProfitReport profit = getProfit(from, to);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Ganancias");
        sheet.setColumnWidth(0, 8000);
        sheet.setColumnWidth(1, 4000);

        XSSFFont fontBold = ((XSSFWorkbook) workbook).createFont();
        fontBold.setBold(true);
        XSSFCellStyle boldStyle = workbook.createCellStyle();
        boldStyle.setFont(fontBold);

        XSSFRow headerRow = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);

        XSSFCell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Concepto");
        headerCell1.setCellStyle(headerStyle);
        XSSFCell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Importe");
        headerCell2.setCellStyle(headerStyle);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        Locale arLocale = new Locale("es", "AR");
        NumberFormat arsFormat = NumberFormat.getCurrencyInstance(arLocale);

        XSSFRow rowProfit = sheet.createRow(1);
        rowProfit.createCell(0).setCellValue("Ganancias");
        rowProfit.createCell(1).setCellValue(arsFormat.format(profit.getProfits()));

        XSSFRow rowCosts = sheet.createRow(2);
        rowCosts.createCell(0).setCellValue("Costos");
        rowCosts.createCell(1).setCellValue(arsFormat.format(profit.getCosts()));

        XSSFRow rowHoldingResults = sheet.createRow(3);
        rowHoldingResults.createCell(0).setCellValue("Resultados por tenencia");
        rowHoldingResults.createCell(1).setCellValue(arsFormat.format(profit.getHoldingResults()));

        XSSFRow rowTotal = sheet.createRow(4);
        rowTotal.createCell(0).setCellValue("Ganancia/Pérdida");
        rowTotal.createCell(1).setCellValue(arsFormat.format(profit.getProfits()
                .subtract(profit.getCosts())
                .add(profit.getHoldingResults())));
        rowTotal.getCell(0).setCellStyle(boldStyle);
        rowTotal.getCell(1).setCellStyle(boldStyle);

        try {
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel");
        }
    }
}
