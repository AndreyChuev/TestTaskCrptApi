package org.example;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.MINUTES, 30);
        CrptApi.RussianProductDocument emptyProductDocument = new CrptApi.RussianProductDocument();
        crptApi.createProductEntryDocument(emptyProductDocument);
    }
}