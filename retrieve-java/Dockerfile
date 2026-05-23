FROM maven:3.9.9-eclipse-temurin-8 AS fastdfs-build
WORKDIR /opt

RUN apt-get update \
    && apt-get install -y --no-install-recommends git ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Use a Maven mirror for better resilience during Docker builds.
RUN mkdir -p /root/.m2 && cat > /root/.m2/settings.xml <<'SETTINGS'
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>*</mirrorOf>
      <name>Aliyun Maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
SETTINGS

RUN git clone https://github.com/happyfish100/fastdfs-client-java.git
WORKDIR /opt/fastdfs-client-java
# The upstream project still targets Java 6 in some revisions. Force Java 8 for build compatibility.
RUN sed -ri 's#<source>1?\.6</source>#<source>1.8</source>#g; s#<target>1?\.6</target>#<target>1.8</target>#g' pom.xml || true
RUN mvn -B -q -s /root/.m2/settings.xml \
    -Dmaven.wagon.http.retryHandler.count=5 \
    -DskipTests \
    -Dmaven.compiler.source=1.8 \
    -Dmaven.compiler.target=1.8 \
    clean install

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

RUN mkdir -p /root/.m2 && cat > /root/.m2/settings.xml <<'SETTINGS'
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>aliyunmaven</id>
      <mirrorOf>*</mirrorOf>
      <name>Aliyun Maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
SETTINGS

COPY --from=fastdfs-build /root/.m2 /root/.m2
COPY pom.xml ./
RUN mvn -B -q -s /root/.m2/settings.xml -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B -q -s /root/.m2/settings.xml -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
