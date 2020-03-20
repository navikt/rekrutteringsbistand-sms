package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.altinnvarsel

class AltinnException : RuntimeException {
    constructor(melding: String) : super(melding)
}
