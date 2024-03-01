package org.example;

import okhttp3.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class CrptApiTest {

    @Test
    void createProductEntryDocument() throws IOException {
        OkHttpClient mockClient = Mockito.mock(OkHttpClient.class);
        Call mockCall = Mockito.mock(Call.class);

        CrptApi crptApi = new CrptApi(mockClient, TimeUnit.MINUTES, 30);

        String responseBody = "{\"message\": \"Document created successfully\"}";
        ResponseBody body = ResponseBody.create(responseBody, MediaType.get("application/json"));
        Response response = new Response.Builder()
                .request(new Request.Builder().url("https://ismp.crpt.ru/api/v3/lk/documents/create").build())
                .protocol(Protocol.HTTP_1_1)
                .code(201)
                .message("Created")
                .body(body)
                .build();

        Mockito.when(mockCall.execute()).thenReturn(response);
        Mockito.when(mockClient.newCall(Mockito.any(Request.class))).thenReturn(mockCall);


        final LocalDate localDate = LocalDate.of(2020, Month.JANUARY, 23);

        CrptApi.RussianProductDocument russianProductDocument = new CrptApi.RussianProductDocument()
                .setDescription(new CrptApi.RussianProductDocument.Description()
                        .setParticipantInn("string"))
                .setDocId("string")
                .setDocStatus("string")
                .setImportRequest(true)
                .setOwnerInn("string")
                .setParticipantInn("string")
                .setProducerInn("string")
                .setProductionDate(localDate)
                .setProductionType("string")
                .setProducts(new ArrayList<>() {{
                    add(new CrptApi.RussianProductDocument.RussianProduct()
                            .setCertificateDocument("string")
                            .setCertificateDocumentDate(localDate)
                            .setCertificateDocumentNumber("string")
                            .setOwnerInn("string")
                            .setProducerInn("string")
                            .setProductionDate(localDate)
                            .setTnvedCode("string")
                            .setUidCode("string")
                            .setUituCode("string"));
                }})
                .setRegDate(localDate)
                .setRegNumber("string");

        Assertions.assertDoesNotThrow(() -> crptApi.createProductEntryDocument(russianProductDocument));
    }
}