
FROM eclipse-temurin:17.0.7_7-jre-alpine as builder
WORKDIR /application

ARG JAR_FILE=target/*.jar
ADD ${JAR_FILE} application.jar

RUN ["java", "-Djarmode=layertools", "-jar", "application.jar", "extract"]

#=================================================================================

FROM eclipse-temurin:17.0.7_7-jre-alpine
WORKDIR /application

# copy the application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader ./
COPY --from=builder application/internal-dependencies ./
COPY --from=builder application/snapshot-dependencies ./
COPY --from=builder application/application/ ./

# install tesseract
RUN [\
	"apk",\ 
	"add",\
    "--update",\
    "--no-cache",\
    "tesseract-ocr",\
    "dumb-init"\      
]

# create usergroup 'javauser' and 'javauser' user     
RUN [ "addgroup", "--system", "javauser" ]
RUN [ "adduser", "-S", "-s", "/bin/false", "-G", "javauser", "javauser" ]
RUN [ "chown", "-R", "javauser:javauser", "/application" ]

# user the user 'javauser'
USER javauser

# execute the application
ENTRYPOINT [\
    "dumb-init",\
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
ARG BUILD_DATE
LABEL org.label-schema.build-date=$BUILD_DATE

LABEL "com.example.vendor"="ACME Incorporated"
LABEL com.example.label-with-value="foo"
LABEL maintainer="team-fernando<team-fernando@gmail.com>"
LABEL version="1.0.0"

# HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 CMD [ "executable" ]
# HEALTHCHECK CMD curl --fail http://localhost/health || exit 1

EXPOSE 8000
EXPOSE 8080
EXPOSE 9010
