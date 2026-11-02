# ---- build: gera o .war com Maven + JDK 21 ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# ---- runtime: Tomcat 10.1 (Jakarta EE 10) + JDK 21 ----
FROM tomcat:10.1-jdk21-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/Servlet-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# DB_URL, DB_USER e DB_PASSWORD vem das variaveis de ambiente do servico
# (o Dotenv do Conexao.java tambem le as variaveis do sistema).
ENV PORT=8080
EXPOSE 8080

# usa a porta que a plataforma (Render/Railway) define em $PORT
CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT}\\\"/\" conf/server.xml && exec catalina.sh run"]
