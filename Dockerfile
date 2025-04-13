# Используем официальный образ JDK
FROM eclipse-temurin:17-jdk-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем build.gradle и pom.xml, если используешь Maven или Gradle
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Кэшируем зависимости
RUN ./mvnw dependency:go-offline

# Копируем остальной проект
COPY src ./src

# Собираем проект
RUN ./mvnw clean package -DskipTests

# Запускаем приложение
CMD ["java", "-jar", "target/radio-station-0.0.1-SNAPSHOT.jar"]
