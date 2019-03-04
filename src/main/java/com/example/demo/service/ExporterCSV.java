package com.example.demo.service;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.function.Function;

public class ExporterCSV<T> {

    private ExporterConfig<T> exporter;

    public ExporterCSV(ExporterConfig<T> exporter) {
        this.exporter = exporter;
    }

    public void createCSV(Writer printWriter, List<T> objects) throws IOException {
        for (String header : exporter.getHeaders()) {
            printWriter.write(header);
            printWriter.write(";");
        }
        printWriter.write("\n");
        for (T object : objects) {
            for (Function<T, String> function : exporter.getFunctions()) {
                String value = function.apply(object);
                printWriter.write(value);
                printWriter.write(";");
            }
            printWriter.write("\n");
        }
    }

}
