#=================================================================================

ARG JAVA_VERSION=17.0.7_7-jre-alpine@sha256:dd8238c151293ae6a7c22898ef2f0df2af8a05786aef73ccd3248e73765969ed

FROM eclipse-temurin:${JAVA_VERSION} AS builder
WORKDIR /application

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

RUN ["java", "-Djarmode=layertools", "-jar", "application.jar", "extract"]

#=================================================================================

FROM eclipse-temurin:${JAVA_VERSION}

# install tesseract
RUN [\
	"apk",\ 
	"add",\
    "--update",\
    "--no-cache",\
    "tesseract-ocr",\
    "tesseract-ocr-data-eng"\
]

WORKDIR /application

# copy the application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader ./
COPY --from=builder application/internal-dependencies ./
COPY --from=builder application/snapshot-dependencies ./
COPY --from=builder application/application/ ./

# create usergroup 'javauser' and 'javauser' user     
RUN addgroup --system javauser \
      && adduser -S -s  /bin/false -G javauser javauser \
      && chown -R javauser:javauser /application

# user the user 'javauser'
USER javauser

# execute the application
ENTRYPOINT [\
    "java",\
	# Profiling
    # "-Dcom.sun.management.jmxremote.port=9010",\ 
    # "-Dcom.sun.management.jmxremote.ssl=false",\ 
    # "-Dcom.sun.management.jmxremote.authenticate=false",\
    # "-Dcom.sun.management.jmxremote.local.only=false",\
    # Debug
    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000",\
    # Spring  
    "-Dspring.profiles.active=local",\
    # VM
	"-XX:+UseContainerSupport",\ 
	"-XX:InitialRAMPercentage=40.0",\ 
	"-XX:MinRAMPercentage=20.0",\ 
	"-XX:MaxRAMPercentage=80.0",\
	# Exec
    "org.springframework.boot.loader.JarLauncher"\
]

# https://medium.com/@chamilad/lets-make-your-docker-image-better-than-90-of-existing-ones-8b1e5de950d
# https://docs.docker.com/develop/develop-images/dockerfile_best-practices/
# https://snyk.io/blog/10-best-practices-to-containerize-nodejs-web-applications-with-docker/
# https://github.com/label-schema/label-schema.org/issues/7

ARG BUILD_DATE=$now
LABEL org.image-converter-service.build-date=$BUILD_DATE \
     	"org.image-converter-service.vendor"="Nandao Software" \
        com.example.label-with-value="foo" \
        maintainer="team-fernando<team-fernando@gmail.com>" \
        version="1.0.0"

# HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 CMD [ "executable" ]
# HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 CMD curl --fail http://localhost/health || exit 1
HEALTHCHECK --start-period=1s CMD curl --fail https://localhost:8080/health | exit 1 

EXPOSE 8080 8000 9010
