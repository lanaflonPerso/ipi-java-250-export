package com.example.demo.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

public class ExporterXlsx<T> {

    private ExporterConfig<T> exporter;

    public ExporterXlsx(ExporterConfig<T> exporter) {
        this.exporter = exporter;
    }

    public void createXlsx(OutputStream outputStream, List<T> objects) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet");

        Row rowHeader = sheet.createRow(0);
        int idxHeaderColumn = 0;
        for (String header : exporter.getHeaders()) {
            Cell cell = rowHeader.createCell(idxHeaderColumn);
            cell.setCellValue(header);
            idxHeaderColumn++;
        }

        int idxRow = 1;
        for (T object : objects) {
            Row row = sheet.createRow(idxRow);
            int idxDataColumn = 0;
            for (Function<T, String> function : exporter.getFunctions()) {
                String value = function.apply(object);
                Cell cell = rowHeader.createCell(idxDataColumn);
                cell.setCellValue(value);
                idxDataColumn++;
            }
            idxRow++;
        }
        workbook.close();
    }

}
