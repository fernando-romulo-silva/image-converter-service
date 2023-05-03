
# only when your application has modularization
#FROM openjdk:17-alpine as builder

FROM eclipse-temurin:17.0.1_12-jre-alpine as builder
WORKDIR /application

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

ADD src/main/tesseract/eng.tar.xz tesseract/tessdata/eng.tar.xz

RUN ["java", "-Djarmode=layertools", "-jar", "application.jar", "extract"]

# only when your application has modularization
# find JDK dependencies dynamically from jar
# RUN jdeps \
#       # dont worry about missing modules
# 	  --ignore-missing-deps \
#       # suppress any warnings printed to console
#       -q \
#       # java release version targeting
#       --multi-release 17 \
#       # output the dependencies at end of run
#       --print-module-deps \
#       # specify the the dependencies for the jar
#       --class-path dependencies/BOOT-INF/lib/* \
#       # pipe the result of running jdeps on the app jar to file
#       application.jar > jre-deps.info

# new jre
# RUN $JAVA_HOME/bin/jlink --verbose \
#        --compress 2 \
#        --strip-java-debug-attributes \
#        --no-header-files \
#        --no-man-pages \
#        --output jre \
#        --add-modules $(cat jre-deps.info)

#=================================================================================

# take a smaller runtime image for the final output
# only when your application has modularization
# FROM alpine:3.15.0

FROM eclipse-temurin:17.0.1_12-jre-alpine
WORKDIR /application

# copy the custom JRE produced from jlink
# only when your application has modularization
# ENV JAVA_HOME=/opt/java/openjdk
# ENV PATH "${JAVA_HOME}/bin:${PATH}"
# COPY --from=builder application/jre/ $JAVA_HOME

# copy the application
COPY --from=builder application/tesseract/tessdata/ ./tesseract/tessdata/
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
    "tesseract-ocr=4.1.3-r0",\
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
    # Preview Features
    # "--enable-preview",\ 
	# Profiling
    # "-Dcom.sun.management.jmxremote.port=9010",\ 
    # "-Dcom.sun.management.jmxremote.ssl=false",\ 
    # "-Dcom.sun.management.jmxremote.authenticate=false",\
    # "-Dcom.sun.management.jmxremote.local.only=false",\
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

# HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 CMD [ "executable" ]
# HEALTHCHECK CMD curl --fail http://localhost/health || exit 1

EXPOSE 8080
EXPOSE 9010
