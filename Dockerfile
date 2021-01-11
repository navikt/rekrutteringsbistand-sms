FROM navikt/java:14
COPY import-vault-token.sh /init-scripts
COPY import-serviceuser.sh /init-scripts
COPY /target/rekrutteringsbistand-sms-0.0.1-SNAPSHOT.jar app.jar
