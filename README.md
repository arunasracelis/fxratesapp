# FX Rates App by [Arūnas Račelis][arunasracelis]

  - Import a HTML file and watch it magically convert to Markdown
  - Drag and drop images (requires your Dropbox account be linked)

### Introduction

This web application fulfills the following functionality requirements:

* App has to show exchange rates using [Bank of Lithuania webservice][lblt]
* When user selects particular currency, app displays its rates history in a table format.
* App must include currency converter. When user enters amount and selects currency, the app displays the amount in foreign currency and exchange rate used.
* Exchange rates must be automatically retrieved everyday using Quartz, for example.
* Use H2 database.
* Use Java >= 1.7, Maven

### Frontend

* HTML
* CSS
* JS
* Bootsrap

### Backend

* Java 1.8
* Maven
* Spring Boot
* Quartz
* Thymeleaf
* H2
* Lombok

[arunasracelis]: <http://www.arunasracelis.com>
[lblt]: <http://www.lb.lt/webservices/FxRates/>
