FROM eclipse-temurin:21-jre-alpine

ARG BROUTER_VERSION=1.7.8

# Install required tools for downloading and extracting
RUN apk add --no-cache wget unzip

# Download and extract BRouter release from GitHub
RUN wget -q "https://github.com/abrensch/brouter/releases/download/v${BROUTER_VERSION}/brouter-${BROUTER_VERSION}.zip" -O /tmp/brouter.zip && \
    unzip -q /tmp/brouter.zip -d /opt && \
    mv /opt/brouter-${BROUTER_VERSION} /opt/brouter && \
    rm /tmp/brouter.zip && \
    apk del wget unzip

# Create directories for segments and custom profiles
RUN mkdir -p /opt/brouter/segments4 /opt/brouter/customprofiles

WORKDIR /opt/brouter

# Environment variables for configuration
ENV JAVA_OPTS="-Xmx512M -Xms512M -DmaxRunningTime=60"
ENV BROUTER_PORT=17777
ENV BROUTER_THREADS=1
ENV BROUTER_BINDADDRESS=""

EXPOSE 17777

# Run BRouter server
CMD java $JAVA_OPTS -cp brouter-*-all.jar btools.server.RouteServer \
    segments4 profiles2 customprofiles $BROUTER_PORT $BROUTER_THREADS $BROUTER_BINDADDRESS
