# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copia apenas o pom.xml primeiro para baixar as dependências (cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Agora copia o código e gera o .jar
COPY src ./src
RUN mvnw clean package -DskipTests

# Stage 2: Imagem final leve
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia apenas o .jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]