# image-converter-service

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Project status](https://img.shields.io/badge/Project%20status-Maintenance-orange.svg)](https://img.shields.io/badge/Project%20status-Maintenance-orange.svg)

## Project status

I use this project to learn new technologies related to microservices. In this case, the image convert. So it'll get new things all time.

# About

A project that converts images with text into simple text using diverse technologies.  
I used [tesseract](https://github.com/tesseract-ocr/tesseract) for it and exposes it as a web service using spring boot.

# Model

It's very simple application, just a controller and a service:
![Model](https://github.com/fernando-romulo-silva/image-converter-service/blob/master/doc/class-diagram.png)

# Technologies

- Java
- Maven
- Spring Frameworks (boot, data, security, etc)
- Tesseract
- Docker

# Start

To start, clone it:

```bash
$ git clone https://github.com/fernando-romulo-silva/image-converter-service
```

# How to Execute

To execute, please folow these steps:

## Build application

```bash
$ mvn package -DskipTests
```

## Using Docker

First check your docker version:

```bash
$ docker version
```

Then build the image:

```bash 
$ export DOCKER_BUILDKIT=1

$ docker build --file src/main/docker/Dockerfile --tag image-service-converter-iso .
```

To run the project:

```bash 
$ docker run --publish 8080:8080 --detach --name mage-converter-service-1 --env-file src/main/docker/Dockerfile.env image-converter-service-iso
```

## Using Java Local

Requirements: 

1) Java 17

```bash
$ java -version 
```

2) Maven 3

```bash
$ mvn --version
```

3) Ant 1.10 (optional)

```bash
$ ant -version
```

4) Tesseract >= 4
 
```bash
$ tesseract --version
```

Tesseract needs a dictionary and the application use the English dictionary called 'eng.traineddata.'

The dictionary can be found in /src/main/tesseract on this project.

You have to define at least the dictionary folder on environment variable or edit application-local.yml file:

```bash
$ export TESSERACT_FOLDER=/home/YourUser/tools/tessdata/
```

Then execute:

```bash
$ mvn spring-boot:run -Dspring.profiles.active=local
```

# API Documentation

To access the API's documentation:

```url
http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
```
