package org.example;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CrptApi {

    public CrptApi(TimeUnit timeUnit, int requestLimit) {

    }


    public void createProductEntryDocument(RussianProductDocument productDocument) {

    }


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
