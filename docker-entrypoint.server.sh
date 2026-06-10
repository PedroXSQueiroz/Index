#!/bin/bash
set -e

echo "[index] Instalando index.concept..."
mvn install -f index.concept/pom.xml -DskipTests --no-transfer-progress -q

echo "[index] Instalando index.embedding..."
mvn install -f index.embedding/pom.xml -DskipTests --no-transfer-progress -q

echo "[index] Instalando index.repo..."
mvn install -f index.repo/pom.xml -DskipTests --no-transfer-progress -q

echo "[index] Iniciando index.server (dev, debug na porta ${DEBUG_PORT:-5005}, aguardando debugger)..."
exec mvn spring-boot:run -f index.server/pom.xml \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${DEBUG_PORT:-5005}"
