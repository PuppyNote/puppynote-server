# ARM64 (t4g.small) 지원 멀티아키텍처 이미지
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080
# G1GC, 컨테이너 메모리 제한 자동 감지 (t4g.small: 2GB RAM, 태스크 1GB 할당 기준)
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
