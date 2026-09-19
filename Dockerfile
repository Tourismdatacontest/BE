# ---- Build stage ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x gradlew
RUN ./gradlew clean bootJar -x test --no-daemon

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# ⚠️ 카카오 로그인이 curl 서브프로세스로 카카오 서버와 통신하는 임시 우회 구조라
# (RestTemplate/JDK HttpClient가 카카오 서버에서 거부당하는 문제 우회, KakaoLoginClient
# 참고) curl 설치가 필수. 근본 원인 해결되면 이 Dockerfile 자체를 걷어내고
# nixpacks/railpack 자동 빌드로 되돌려도 됨.
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/contest-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
