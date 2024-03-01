package org.example;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * Класс CrptApi предоставляет интерфейс для взаимодействия с API Честного знака.
 * Он позволяет создавать документы о вводе товаров в оборот.
 */
public class CrptApi {

    private final OkHttpClient okHttpClient;
    private final ApiEndpoints endpoints;
    private final RateLimiter rateLimiter;


    /**
     * Конструктор класса CrptApi, использующий по умолчанию OkHttpClient.
     * Создает экземпляр CrptApi с переданными параметрами времени и ограничения запросов.
     *
     * @param timeUnit     единица времени для ограничения запросов.
     * @param requestLimit максимальное количество запросов за период времени.
     */
    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this(new OkHttpClient(), timeUnit, requestLimit);
    }


    /**
     * Конструктор класса CrptApi, принимающий пользовательский OkHttpClient.
     * Создает экземпляр CrptApi с переданным клиентом OkHttpClient и параметрами времени и ограничения запросов.
     *
     * @param okHttpClient пользовательский OkHttpClient для выполнения HTTP-запросов.
     * @param timeUnit     единица времени для ограничения запросов.
     * @param requestLimit максимальное количество запросов за период времени.
     */
    public CrptApi(OkHttpClient okHttpClient, TimeUnit timeUnit, int requestLimit) {
        this.okHttpClient = Objects.requireNonNull(okHttpClient);
        endpoints = new ApiEndpoints();
        rateLimiter = new RateLimiter(timeUnit, requestLimit);
    }

    /**
     * Создает документ о вводе товара в оборот.
     * @param productDocument документ о товаре для создания.
     * @throws HttpRequestException если произошла ошибка при выполнении HTTP-запроса.
     */
    public synchronized void createProductEntryDocument(RussianProductDocument productDocument) {
        rateLimiter.acquire();
        try {
            endpoints.createProduct(JsonMapper.toJson(productDocument));
        } catch (HttpRequestException e) {
            rateLimiter.deleteLastRequest();
            throw e;
        }
    }


    /**
     * Класс ApiEndpoints предоставляет методы для выполнения HTTP-запросов к API.
     * Он использует OkHttpClient для создания и выполнения запросов.
     */
    private class ApiEndpoints {

        private static final int STATUS_CREATED_CODE = 201;

        /**
         * Тип контента JSON для запросов.
         */
        static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json");


        /**
         * Отправляет POST-запрос для создания продукта в API.
         * @param jsonDocument JSON-документ, содержащий информацию о продукте.
         */
        private void createProduct(String jsonDocument) {
            RequestBody body = RequestBody.create(jsonDocument, JSON_MEDIA_TYPE);

            Request request = new Request.Builder()
                    .url("https://ismp.crpt.ru/api/v3/lk/documents/create")
                    .post(body)
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()){
                if (response.code() != STATUS_CREATED_CODE) {
                    throw new HttpRequestException(response.message());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Внутренний класс, предоставляющий методы для преобразования объектов в JSON.
     */
    private static class JsonMapper {

        private static final ObjectMapper MAPPER = new ObjectMapper()
                .findAndRegisterModules();

        /**
         * Преобразует объект в его JSON-представление.
         * @param obj объект для преобразования.
         * @return JSON-представление объекта.
         */
        static String toJson(Object obj) {
            try {
                return MAPPER.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

    }


    /**
     * Внутренний класс, реализующий механизм ограничения частоты запросов.
     */
    private static class RateLimiter {

        private final int requestLimit;
        private final long period;

        private final LinkedList<Long> requests = new LinkedList<>();


        /**
         * Конструктор класса RateLimiter.
         * @param timeUnit единица времени для ограничения запросов.
         * @param requestLimit максимальное количество запросов за период времени.
         */
        private RateLimiter(TimeUnit timeUnit, int requestLimit) {

            if (timeUnit == null) {
                throw new IllegalArgumentException("TimeUnit can't be null!");
            }

            if (requestLimit <= 0) {
                throw new IllegalArgumentException("requestLimit must be positive!");
            }

            period = timeUnit.toNanos(1);
            this.requestLimit = requestLimit;
        }

        /**
         * Захватывает разрешение для выполнения запроса.
         */
        private synchronized void acquire() {
            long now = System.nanoTime();

            requests.add(now);

            long waitTime = calculateWaitTime(now);

            if (waitTime > 0) {
                try {
                    TimeUnit.NANOSECONDS.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        /**
         * Удаляет последний запрос из очереди.
         */
        private void deleteLastRequest() {
            if (!requests.isEmpty()) {
                requests.removeLast();
            }
        }

        /**
         * Рассчитывает время ожидания до следующего запроса.
         * @param now текущее время.
         * @return время ожидания в наносекундах.
         */
        private long calculateWaitTime(long now) {
            trimRequests(now);

            synchronized (requests) {
                if (!requests.isEmpty() && requests.size() > requestLimit) {
                    return period - (now - requests.getFirst());
                }
            }

            return 0;
        }

        /**
         * Удаляет устаревшие запросы из очереди.
         * @param now текущее время.
         */
        private void trimRequests(long now) {
            Iterator<Long> requestsIterator = requests.iterator();

            while (requestsIterator.hasNext()) {
                long requestNanoTime = requestsIterator.next();

                if ((now - requestNanoTime) >= period) {
                    requestsIterator.remove();
                } else {
                    break;
                }
            }
        }

    }

    /**
     * Исключение, выбрасываемое при ошибке HTTP-запроса.
     */
    public static class HttpRequestException extends RuntimeException {

        public HttpRequestException(String message) {
            super(message);
        }
    }


    /**
     * Класс, представляющий документ о товаре произведенном в России, для ввода в оборот.
     */
    public static class RussianProductDocument {

        private Description description;

        @JsonProperty("doc_id")
        private String docId;

        @JsonProperty("doc_status")
        private String docStatus;

        @JsonProperty("doc_type")
        private final String docType = "LP_INTRODUCE_GOODS";

        @JsonProperty("109 \"importRequest\"")
        private boolean importRequest;

        @JsonProperty("owner_inn")
        private String ownerInn;

        @JsonProperty("participant_inn")
        private String participantInn;

        @JsonProperty("producer_inn")
        private String producerInn;

        @JsonProperty("production_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate productionDate;

        @JsonProperty("production_type")
        private String productionType;

        private List<RussianProduct> products;

        @JsonProperty("reg_date")
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate regDate;

        @JsonProperty("reg_number")
        private String regNumber;


        public Description getDescription() {
            return description;
        }

        public RussianProductDocument setDescription(Description description) {
            this.description = description;
            return this;
        }

        public String getDocId() {
            return docId;
        }

        public RussianProductDocument setDocId(String docId) {
            this.docId = docId;
            return this;
        }

        public String getDocStatus() {
            return docStatus;
        }

        public RussianProductDocument setDocStatus(String docStatus) {
            this.docStatus = docStatus;
            return this;
        }

        public String getDocType() {
            return docType;
        }

        public boolean isImportRequest() {
            return importRequest;
        }

        public RussianProductDocument setImportRequest(boolean importRequest) {
            this.importRequest = importRequest;
            return this;
        }

        public String getOwnerInn() {
            return ownerInn;
        }

        public RussianProductDocument setOwnerInn(String ownerInn) {
            this.ownerInn = ownerInn;
            return this;
        }

        public String getParticipantInn() {
            return participantInn;
        }

        public RussianProductDocument setParticipantInn(String participantInn) {
            this.participantInn = participantInn;
            return this;
        }

        public String getProducerInn() {
            return producerInn;
        }

        public RussianProductDocument setProducerInn(String producerInn) {
            this.producerInn = producerInn;
            return this;
        }

        public LocalDate getProductionDate() {
            return productionDate;
        }

        public RussianProductDocument setProductionDate(LocalDate productionDate) {
            this.productionDate = productionDate;
            return this;
        }

        public String getProductionType() {
            return productionType;
        }

        public RussianProductDocument setProductionType(String productionType) {
            this.productionType = productionType;
            return this;
        }

        public List<RussianProduct> getProducts() {
            return products;
        }

        public RussianProductDocument setProducts(List<RussianProduct> products) {
            this.products = products;
            return this;
        }

        public LocalDate getRegDate() {
            return regDate;
        }

        public RussianProductDocument setRegDate(LocalDate regDate) {
            this.regDate = regDate;
            return this;
        }

        public String getRegNumber() {
            return regNumber;
        }

        public RussianProductDocument setRegNumber(String regNumber) {
            this.regNumber = regNumber;
            return this;
        }

        public static class Description {

            private String participantInn;

            public String getParticipantInn() {
                return participantInn;
            }

            public Description setParticipantInn(String participantInn) {
                this.participantInn = participantInn;
                return this;
            }
        }


        public static class RussianProduct {

            @JsonProperty("certificate_document")
            private String certificateDocument;

            @JsonProperty("certificate_document_date")
            @JsonFormat(pattern = "yyyy-MM-dd")
            private LocalDate certificateDocumentDate;

            @JsonProperty("certificate_document_number")
            private String certificateDocumentNumber;

            @JsonProperty("owner_inn")
            private String ownerInn;

            @JsonProperty("producer_inn")
            private String producerInn;

            @JsonProperty("production_date")
            @JsonFormat(pattern = "yyyy-MM-dd")
            private LocalDate productionDate;

            @JsonProperty("tnved_code")
            private String tnvedCode;

            @JsonProperty("uid_code")
            private String uidCode;

            @JsonProperty("uitu_code")
            private String uituCode;


            public String getCertificateDocument() {
                return certificateDocument;
            }

            public RussianProduct setCertificateDocument(String certificateDocument) {
                this.certificateDocument = certificateDocument;
                return this;
            }

            public LocalDate getCertificateDocumentDate() {
                return certificateDocumentDate;
            }

            public RussianProduct setCertificateDocumentDate(LocalDate certificateDocumentDate) {
                this.certificateDocumentDate = certificateDocumentDate;
                return this;
            }

            public String getCertificateDocumentNumber() {
                return certificateDocumentNumber;
            }

            public RussianProduct setCertificateDocumentNumber(String certificateDocumentNumber) {
                this.certificateDocumentNumber = certificateDocumentNumber;
                return this;
            }

            public String getOwnerInn() {
                return ownerInn;
            }

            public RussianProduct setOwnerInn(String ownerInn) {
                this.ownerInn = ownerInn;
                return this;
            }

            public String getProducerInn() {
                return producerInn;
            }

            public RussianProduct setProducerInn(String producerInn) {
                this.producerInn = producerInn;
                return this;
            }

            public LocalDate getProductionDate() {
                return productionDate;
            }

            public RussianProduct setProductionDate(LocalDate productionDate) {
                this.productionDate = productionDate;
                return this;
            }

            public String getTnvedCode() {
                return tnvedCode;
            }

            public RussianProduct setTnvedCode(String tnvedCode) {
                this.tnvedCode = tnvedCode;
                return this;
            }

            public String getUidCode() {
                return uidCode;
            }

            public RussianProduct setUidCode(String uidCode) {
                this.uidCode = uidCode;
                return this;
            }

            public String getUituCode() {
                return uituCode;
            }

            public RussianProduct setUituCode(String uituCode) {
                this.uituCode = uituCode;
                return this;
            }
        }
    }
}
