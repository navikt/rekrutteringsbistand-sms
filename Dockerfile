FROM navikt/java:14
COPY nais/import-vault-token.sh /init-scripts
COPY nais/import-serviceuser.sh /init-scripts
COPY /target/rekrutteringsbistand-sms-0.0.1-SNAPSHOT.jar app.jar
