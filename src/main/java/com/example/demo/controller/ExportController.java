package com.example.demo.controller;

import com.example.demo.entity.Client;
import com.example.demo.entity.Facture;
import com.example.demo.service.*;
import com.itextpdf.text.DocumentException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * Controlleur pour réaliser les exports.
 */
@Controller
@RequestMapping("/")
public class ExportController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private FactureService factureService;

    @Autowired
    private ClientFactureExportXlsx clientFactureExportXlsx;

    @Autowired
    private ExportPDFITextService exportPDFITextService;


    @GetMapping("/clients/csv")
    public void clientsCSV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"clients.csv\"");
        PrintWriter writer = response.getWriter();
        List<Client> allClients = clientService.findAllClients();
        LocalDate now = LocalDate.now();
        writer.println("Id;Nom;Prenom;Date de Naissance;Age");
       for (Client client : allClients) {
            writer.println(
                    client.getId() + ";"
                            + client.getNom() + ";"
                            + client.getPrenom() + ";"
                            + ";"
                            + (now.getYear() - client.getDateNaissance().getYear())
            );
        }

        // ExporterConfig<Client> exportConfig = new ExporterConfig<>();
        // exportConfig.addColumnLong("Id", c1 -> c1.getId());
        // exportConfig.addColumnString("Nom", c -> c.getNom());
        // exportConfig.addColumnString("Prénom", c -> c.getPrenom());
        // exportConfig.addColumnString("Date de naissance", c -> c.getDateNaissance().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        // exportConfig.addColumnInteger("Age", c -> now.getYear() - c.getDateNaissance().getYear());

        // ExporterCSV<Client> exporterCSV = new ExporterCSV<>(exportConfig);
        // exporterCSV.createCSV(response.getWriter(), allClients);

        // ExporterXlsx<Client> exporterXlsx = new ExporterXlsx<>(exportConfig);
        // exporterXlsx.createXlsx(response.getOutputStream(), allClients);

        // ExporterCSV<Article> exportArticle = new ExporterCSV<>();
        // exportArticle.addColumnString("Article", a -> a.getLibelle());
    }

    @GetMapping("/clients/xlsx")
    public void clientsXlsx(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"clients.xlsx\"");
        List<Client> allClients = clientService.findAllClients();
        LocalDate now = LocalDate.now();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clients");

        Row headerRow = sheet.createRow(0);

        Cell cellHeaderId = headerRow.createCell(0);
        cellHeaderId.setCellValue("Id");

        Cell cellHeaderPrenom = headerRow.createCell(1);
        cellHeaderPrenom.setCellValue("Prénom");

        Cell cellHeaderNom = headerRow.createCell(2);
        cellHeaderNom.setCellValue("Nom");

        Cell cellHeaderDateNaissance = headerRow.createCell(3);
        cellHeaderDateNaissance.setCellValue("Date de naissance");

        int i = 1;
        for (Client client : allClients) {
            Row row = sheet.createRow(i);

            Cell cellId = row.createCell(0);
            cellId.setCellValue(client.getId());

            Cell cellPrenom = row.createCell(1);
            cellPrenom.setCellValue(client.getPrenom());

            Cell cellNom = row.createCell(2);
            cellNom.setCellValue(client.getNom());

            Cell cellDateNaissance = row.createCell(3);
            Date dateNaissance = Date.from(client.getDateNaissance().atStartOfDay(ZoneId.systemDefault()).toInstant());
            cellDateNaissance.setCellValue(dateNaissance);

            CellStyle cellStyleDate = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
            cellDateNaissance.setCellStyle(cellStyleDate);

            i++;
        }

        workbook.write(response.getOutputStream());
        workbook.close();

    }

    /**
     * Export excel
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/factures/xlsx")
    public void facturesXlsx(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"factures.xlsx\"");
        clientFactureExportXlsx.facturesXlsx(response.getOutputStream());
    }

    @GetMapping("/factures/{id}/pdf")
    public void facturePDF(@PathVariable("id") Long factureId, HttpServletRequest request, HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"facture.pdf\"");
        Facture facture = factureService.findById(factureId);
        exportPDFITextService.export(response.getOutputStream(), facture);
    }

}
