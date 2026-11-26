# Stage 1: Compilación con Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime con GlassFish
FROM eclipse-temurin:17-jdk
RUN apt-get update && apt-get install -y curl unzip && rm -rf /var/lib/apt/lists/*

# Descargar GlassFish 7
ENV GLASSFISH_VERSION=7.0.26
RUN curl -sL https://github.com/eclipse-ee4j/glassfish/releases/download/${GLASSFISH_VERSION}/glassfish-${GLASSFISH_VERSION}.zip -o /tmp/glassfish.zip \
    && unzip -q /tmp/glassfish.zip -d /opt \
    && rm /tmp/glassfish.zip

ENV GLASSFISH_HOME=/opt/glassfish7
ENV PATH=$GLASSFISH_HOME/bin:$PATH

# Desplegar el WAR
COPY --from=build /app/target/*.war $GLASSFISH_HOME/glassfish/domains/domain1/autodeploy/

# Inicializar GlassFish (crear dominio por defecto)
RUN $GLASSFISH_HOME/bin/asadmin start-domain \
    && $GLASSFISH_HOME/bin/asadmin stop-domain

EXPOSE 8080

CMD ["sh", "-c", "$GLASSFISH_HOME/bin/asadmin start-domain && tail -f $GLASSFISH_HOME/glassfish/domains/domain1/logs/server.log"]
