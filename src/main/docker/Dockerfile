FROM eclipse-temurin:17.0.1_12-jre-alpine as builder
WORKDIR /application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN ls -la
COPY src/main/tesseract/eng.tar.xz tesseract/tessdata/eng.tar.xz

RUN java -Djarmode=layertools -jar application.jar extract \
		&& cd tesseract/tessdata/ \
        && zcat eng.tar.xz | tar -xvf - \
        && rm eng.tar.xz

#=================================================================================

FROM eclipse-temurin:17.0.1_12-jre-alpine
#RUN apk add --update --no-cache tesseract-ocr \
#      && addgroup --system javauser \ 
#      && adduser -S -s /bin/false -G javauser javauser

#RUN tesseract --version 
WORKDIR /application
COPY --from=builder application/tesseract/tessdata/ ./tesseract/tessdata/
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader ./
COPY --from=builder application/internal-dependencies ./
COPY --from=builder application/snapshot-dependencies ./
COPY --from=builder application/application/ ./

RUN apk add --update --no-cache tesseract-ocr dumb-init \
      && addgroup --system javauser \
      && adduser -S -s /bin/false -G javauser javauser \
      && chown -R javauser:javauser /application

USER javauser

ENTRYPOINT [\
               "dumb-init",\
               "java",\
               "--enable-preview",\
               "-Dspring.profiles.active=local",\
			   "-XX:+UseContainerSupport",\ 
			   "-XX:InitialRAMPercentage=40.0",\ 
			   "-XX:MinRAMPercentage=20.0",\ 
			   "-XX:MaxRAMPercentage=80.0",\
               "org.springframework.boot.loader.JarLauncher"\
]

EXPOSE 5000
EXPOSE 5090
