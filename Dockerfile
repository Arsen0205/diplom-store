# 1️⃣ Этап сборки
FROM maven:latest AS build
WORKDIR /app

# Копируем pom.xml и загружаем зависимости (кэширование)
COPY pom.xml .
RUN mvn dependency:resolve

# Копируем весь проект и собираем его
COPY . .
RUN mvn clean package -DskipTests

# 2️⃣ Этап выполнения
FROM openjdk:17-jdk-slim
WORKDIR /app

# Копируем собранный JAR из предыдущего этапа
COPY --from=build /app/target/*.jar app.jar

# Оптимизация запуска JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Запускаем приложение
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
