package no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.mock

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import no.altinn.schemas.services.serviceengine.notification._2015._06.SendNotificationResultList
import no.altinn.schemas.services.serviceengine.standalonenotificationbe._2009._10.StandaloneNotificationBEList
import no.altinn.services.serviceengine.notification._2010._10.INotificationAgencyExternalBasic
import no.nav.rekrutteringsbistand.sms.rekrutteringsbistandsms.utils.log

class AltinnVarselMock : INotificationAgencyExternalBasic {
    override fun sendStandaloneNotificationBasicV3(p0: String?, p1: String?, p2: StandaloneNotificationBEList?): SendNotificationResultList {
        runBlocking { delay(500) }
        log.info("Sending av SMS er slått av i dev. Faker utsending...")
        return SendNotificationResultList()
    }

    override fun sendStandaloneNotificationBasicV2(p0: String?, p1: String?, p2: StandaloneNotificationBEList?): String {
        return ""
    }

    override fun sendStandaloneNotificationBasic(p0: String?, p1: String?, p2: StandaloneNotificationBEList?) {}

    override fun test() {}
}
