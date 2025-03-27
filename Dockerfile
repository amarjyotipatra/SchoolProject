# syntax=docker/dockerfile:1

# Stage 1: Builder
FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /build
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle . # Include settings.gradle if you have one
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon
COPY src src
RUN ./gradlew bootJar --no-daemon

################################################################################
# Stage 2: Final image
FROM eclipse-temurin:21-jre-jammy AS final
WORKDIR /app
ARG UID=10001
RUN adduser --disabled-password --gecos "" --home "/nonexistent" --shell "/sbin/nologin" --no-create-home --uid "${UID}" appuser
COPY --from=builder /build/build/libs/*.jar app.jar
USER root
RUN chown appuser:appuser /app/app.jar
USER appuser

# --- CHANGE HERE ---
# Expose the port specified in application.properties
EXPOSE 8081

ENTRYPOINT [ "java", "-jar", "app.jar" ]