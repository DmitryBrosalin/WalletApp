# WalletApp

REST API, которое позволяет создавать кошельки, проводить операции с их балансом, а также выводить информацию о них.

## Стек технологий

> - Java 17
> - SpringBoot
> - PostgreSql
> - Spring Data JPA
> - Hibernate ORM
> - REST API
> - Maven
> - Docker
> - Liquibase
> - JUnit
> - MockMvc

## Запуск приложения

Для запуска приложения, находясь в корневой папке проекта, введите в командную строку:

```
docker compose up -d
```

## Тестирование

Для приложения написаны интеграционные тесты. Будьте внимательны, перед тестированием необходимо запустить контейнер с базой данных!

Если вы не запустили приложение с помощью команды выше, вы можете запустить отдельно контейнер с базой данных с помощью команды:

```
docker run -p 5432:5432 -d --name wallet-db -e POSTGRES_USER=wallet -e POSTGRES_PASSWORD=wallet -e POSTGRES_DB=wallet postgres:16.1
```

## Эндпоинты

### Добавление нового кошелька

> POST /api/v1/wallet/create-new-wallet


> Response Body:
> {
> 
> > "uuid": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
> >
> > "balance": 0
> 
> } 
---

### Операция по изменению баланса кошелька

> POST /api/v1/wallet

> Request Body:
> {
>
> > "walletId": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
> >
> > "operationType": "DEPOSIT" or "WITHDRAW"
> >
> > "amount": X (positive long type number)
>
> }

Производится валидация полей запроса: 

- длина walletId (36 символов)
- наличие кошелька с walletId в базе данных
- корректность введенного operationType 
- знак суммы операции (amount должна быть положительной)

> Response Body:
> {
>
> > "uuid": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
> >
> > "balance": updated balance
>
> }
---

### Получение информации о кошельке по UUID

> GET /api/v1/wallets/{walletUUID}

> Response Body:
> {
>
> > "uuid": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx",
> >
> > "balance": balance
>
> } 
