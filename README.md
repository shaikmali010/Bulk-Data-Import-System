# Bulk Data Import System

A robust and scalable Spring Boot REST API that imports bulk data from **CSV** and **Excel (.xlsx)** files, validates each record, stores import results, tracks import jobs, prevents duplicate file uploads using SHA-256 hashing, and provides asynchronous background processing.

---

# Table of Contents

- Overview
- Features
- Technology Stack
- Project Architecture
- Project Structure
- Design Patterns Used
- Project Workflow
- Database Schema
- API Endpoints
- Validation Rules
- Exception Handling
- Logging
- Performance Optimizations
- Setup & Installation
- Running the Application
- Swagger Documentation
- Testing
- Future Enhancements
- Learning Outcomes
- Author

---

# Overview

Bulk data import is a common requirement in enterprise applications where thousands of records need to be imported from CSV or Excel files.

This project provides a scalable REST API that:

- Uploads CSV and Excel files
- Parses file contents
- Validates each record
- Stores successful and failed records
- Tracks import jobs
- Prevents duplicate imports
- Supports asynchronous processing
- Provides import summaries
- Supports pagination and sorting

---

# Features

## File Upload

- CSV Upload
- Excel (.xlsx) Upload

---

## Parsing

- Apache Commons CSV
- Apache POI

---

## Validation

Dynamic validation for every record.

Supported validations include:

- Required fields
- Numeric validation
- Positive number validation
- Missing columns
- Invalid data formats

---

## Import Management

- Import Job Tracking
- Import Summary
- Record Level Status
- Error Messages

---

## Duplicate File Detection

Uses SHA-256 hashing.

Even if the filename changes,

```
employees.xlsx
```

to

```
employees_new.xlsx
```

the duplicate file is detected because the file content remains the same.

---

## Asynchronous Processing

Large imports are processed in the background using

```
@Async
```

Users receive the Job ID immediately while processing continues.

---

## Pagination

Supports

```
page
size
```

---

## Sorting

Supports

```
sortBy
direction
```

---

## Logging

Application logs important events including

- Upload started
- Parser selected
- Validation started
- Invalid records
- Records saved
- Import completed
- Import failed

---

## Swagger

Interactive API documentation using OpenAPI.

---

# Technology Stack

| Technology | Version |
|------------|----------|
| Java | 17 |
| Spring Boot | 3.x |
| Spring Data JPA | Latest |
| MySQL | 8 |
| Maven | Latest |
| Lombok | Latest |
| Apache Commons CSV | Latest |
| Apache POI | Latest |
| Swagger OpenAPI | Latest |
| SLF4J | Built-in |

---

# Project Architecture

```
                Client

                   │

                   ▼

             REST Controller

                   │

                   ▼

                Service

                   │

        ┌──────────┴──────────┐

        ▼                     ▼

 FileParserFactory      Validator

        │

        ▼

CSV Parser     Excel Parser

        │

        ▼

     ParsedFileDto

        │

        ▼

      Mapper

        │

        ▼

   Repository Layer

        │

        ▼

      MySQL Database
```

---

# Project Structure

```
src
│
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── factory
├── mapper
├── parser
│     ├── FileParser
│     └── impl
├── repository
├── service
│     ├── inter
│     └── impl
├── util
├── validator
└── BulkImportApplication
```

---

# Design Patterns Used

## Factory Pattern

Selects the appropriate parser.

```
CSV

↓

CsvFileParser
```

```
Excel

↓

ExcelFileParser
```

---

## Strategy Pattern

Different parser implementations implement a common interface.

```
FileParser

↓

CsvFileParserImpl

↓

ExcelFileParserImpl
```

---

## Mapper Pattern

Converts

- Entity → DTO
- DTO → Entity

---

## Layered Architecture

```
Controller

↓

Service

↓

Repository

↓

Database
```

---

# Project Workflow

```
Upload File

↓

Validate File

↓

Generate SHA-256 Hash

↓

Duplicate File Check

↓

Create Import Job

↓

Select Parser

↓

Parse File

↓

Validate Records

↓

Create Import Records

↓

Save Records

↓

Update Job Status

↓

Return Import Summary
```

---

# Database Schema

## import_job

| Column | Type |
|---------|------|
| id | BIGINT |
| file_name | VARCHAR |
| file_hash | VARCHAR |
| status | ENUM |
| created_at | DATETIME |

---

## import_record

| Column | Type |
|---------|------|
| id | BIGINT |
| job_id | BIGINT |
| data | TEXT |
| status | ENUM |
| error_message | TEXT |

---

# API Endpoints

## Upload File

```
POST
```

```
/api/import/upload
```

Request

```
form-data

file = employees.xlsx
```

---

## Get Import Job

```
GET
```

```
/api/import/{id}
```

---

## Get All Jobs

```
GET
```

```
/api/import
```

Supports

```
page

size

sortBy

direction
```

Example

```
/api/import?page=0&size=5&sortBy=createdAt&direction=desc
```

---

## Get Import Records

```
GET
```

```
/api/import/{jobId}/records
```

---

# Sample Response

```json
{
  "jobId": 10,
  "fileName": "employees.xlsx",
  "status": "COMPLETED",
  "totalRecords": 500,
  "successfulRecords": 497,
  "failedRecords": 3
}
```

---

# Validation Rules

| Field | Validation |
|---------|------------|
| Name | Required |
| Email | Valid Email |
| Mobile | Exactly 10 digits |
| Salary | Greater than 0 |
| Numeric Fields | Must contain numbers |

---

# Exception Handling

Custom exceptions

- InvalidFileException
- DuplicateFileException
- ImportJobNotFoundException

Global Exception Handler returns consistent JSON responses.

Example

```json
{
  "status":400,
  "message":"This file has already been imported."
}
```

---

# Logging

Uses SLF4J Logger.

Logs include

```
Upload Started

Parser Selected

Validation Started

Invalid Record

Records Saved

Import Completed

Import Failed
```

---

# Performance Optimizations

- Asynchronous Processing
- Batch Insert
- SHA-256 Duplicate Detection
- Pagination
- Sorting
- Layered Architecture

---

# Setup & Installation

Clone repository

```bash
git clone <repository-url>
```

Open project

```
Import as Maven Project
```

Create MySQL Database

```
bulk_import_db
```

Update

```
application.properties
```

Example

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bulk_import_db

spring.datasource.username=root

spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
```

Install dependencies

```bash
mvn clean install
```

Run application

```bash
mvn spring-boot:run
```

---

# Swagger Documentation

Open

```
http://localhost:8080/swagger-ui/index.html
```

Available APIs

- Upload File
- Get Import Job
- Get All Jobs
- Get Import Records

---

# Testing

Test using

- Postman
- Swagger UI

Recommended scenarios

- Upload valid CSV
- Upload valid Excel
- Upload invalid file
- Upload duplicate file
- Upload invalid records
- Test pagination
- Test sorting

---

# Future Enhancements

- Email notification after import
- Import cancellation
- Retry failed records
- Dashboard with charts
- Scheduled imports
- JSON support
- XML support
- Cloud storage integration
- Role Based Authentication

---

# Learning Outcomes

This project demonstrates practical experience with:

- Spring Boot
- REST API Development
- Layered Architecture
- Spring Data JPA
- MySQL
- File Upload
- CSV Processing
- Excel Processing
- Factory Pattern
- Strategy Pattern
- Mapper Pattern
- Exception Handling
- Logging
- Asynchronous Processing
- Pagination
- Sorting
- SHA-256 Hashing
- Clean Code Principles

---

# Author

**Shaik Mohammad Ali**

Backend Developer

Java | Spring Boot | MySQL | REST APIs

---

# License

This project is developed for learning purposes and backend development practice.