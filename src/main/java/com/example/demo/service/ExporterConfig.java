package com.example.demo.service;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ExporterConfig<T> {

    private List<String> headers = new ArrayList<>();

    private List<Function<T, String>> functions = new ArrayList<>();

    public void addColumnString(String headerName, Function<T, String> function) {
        headers.add(headerName);
        functions.add(function);
    }

    public void addColumnLong(String headerName, Function<T, Long> function) {
        headers.add(headerName);
        functions.add(function.andThen(integerValue -> integerValue == null ? "" : integerValue.toString()));
    }

    public void addColumnInteger(String headerName, Function<T, Integer> function) {
        headers.add(headerName);
        functions.add(function.andThen(integerValue -> integerValue == null ? "" : integerValue.toString()));
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<Function<T, String>> getFunctions() {
        return functions;
    }

}
