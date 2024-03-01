# CrptApi

Класс `CrptApi` предоставляет интерфейс для взаимодействия с API Честного знака.
Он позволяет создавать документы о вводе товаров в оборот.

## Использование

### Конструкторы
- `CrptApi(TimeUnit timeUnit, int requestLimit)`: Создает экземпляр `CrptApi` с использованием по умолчанию `OkHttpClient` и заданными параметрами времени и лимита запросов.
- `CrptApi(OkHttpClient okHttpClient, TimeUnit timeUnit, int requestLimit)`: Создает экземпляр `CrptApi` с пользовательским `OkHttpClient` и заданными параметрами времени и лимита запросов.

### Методы
- `createProductEntryDocument(RussianProductDocument productDocument)`: Создает документ о вводе товара в оборот.

## Классы
### `RussianProductDocument`
Представляет документ о товаре, произведенном в России для ввода в оборот.

#### Свойства
- `description`: Описание товара.
- `docId`: Идентификатор документа.
- `docStatus`: Статус документа.
- `docType`: Тип документа.
- `importRequest`: Показывает, является ли запрос импортом.
- `ownerInn`: ИНН владельца.
- `participantInn`: ИНН участника.
- `producerInn`: ИНН производителя.
- `productionDate`: Дата производства.
- `productionType`: Тип производства.
- `products`: Список российских продуктов.
- `regDate`: Дата регистрации.
- `regNumber`: Регистрационный номер.

### `RussianProduct`
Представляет российский продукт.

#### Свойства
- `certificateDocument`: Документ о сертификате.
- `certificateDocumentDate`: Дата документа о сертификате.
- `certificateDocumentNumber`: Номер документа о сертификате.
- `ownerInn`: ИНН владельца.
- `producerInn`: ИНН производителя.
- `productionDate`: Дата производства.
- `tnvedCode`: Код ТН ВЭД.
- `uidCode`: Код УИД.
- `uituCode`: Код УИТУ.

