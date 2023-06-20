package com.lacodigoneta.elbuensabor.controllers;

import com.lacodigoneta.elbuensabor.dto.order.ProfitReport;
import com.lacodigoneta.elbuensabor.dto.order.RankingClients;
import com.lacodigoneta.elbuensabor.dto.order.RankingProduct;
import com.lacodigoneta.elbuensabor.services.ReportsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@AllArgsConstructor
public class ReportController {

    private final ReportsService service;

    @GetMapping("/rankingProducts")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<RankingProduct>> getRankingProducts(@RequestParam LocalDate from,
                                                                   @RequestParam LocalDate to,
                                                                   @RequestParam int quantityRegisters) {

        return ResponseEntity.ok(service.getRankingProducts(from, to, quantityRegisters));
    }

    @GetMapping(value = "/rankingProductsExcel", produces = "application/octet-stream")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void getRankingProductsExcel(HttpServletResponse response,
                                        @RequestParam LocalDate from,
                                        @RequestParam LocalDate to,
                                        @RequestParam int quantityRegisters,
                                        @RequestParam boolean drinks) {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename='ranking_productos.xlsx'");
        service.generateRankingProductsExcel(response, from, to, quantityRegisters, drinks);
    }

    @GetMapping("/rankingClients")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<RankingClients>> getRankingClients(@RequestParam LocalDate from,
                                                                  @RequestParam LocalDate to,
                                                                  @RequestParam int quantityRegisters,
                                                                  @RequestParam boolean byQuantity) {

        return ResponseEntity.ok(service.getRankingClients(from, to, quantityRegisters, byQuantity));
    }

    @GetMapping(value = "/rankingClientsExcel", produces = "application/octet-stream")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void getRankingClientsExcel(HttpServletResponse response,
                                       @RequestParam LocalDate from,
                                       @RequestParam LocalDate to,
                                       @RequestParam int quantityRegisters,
                                       @RequestParam boolean byQuantity) {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename='ranking_clientes.xlsx'");
        service.generateRankingClientsExcel(response, from, to, quantityRegisters, byQuantity);
    }

    @GetMapping("/profits")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ProfitReport> getProfits(@RequestParam LocalDate from,
                                                   @RequestParam LocalDate to) {

        return ResponseEntity.ok(service.getProfit(from, to));
    }

    @GetMapping(value = "/profitsExcel", produces = "application/octet-stream")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void getProfitsExcel(HttpServletResponse response,
                                @RequestParam LocalDate from,
                                @RequestParam LocalDate to) {

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename='ganancias.xlsx'");
        service.generateProfitsExcel(response, from, to);
    }
}
