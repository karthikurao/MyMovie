@echo off
set MAVEN_OPTS=-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true -Dtrust_all_cert=true -Dcom.sun.net.ssl.checkRevocation=false -Djavax.net.ssl.trustStore=NUL -Djavax.net.ssl.trustStoreType=Windows-ROOT

echo Starting Maven with SSL bypass options...
mvn clean spring-boot:run
