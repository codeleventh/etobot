import com.typesafe.config.ConfigFactory

object Config {
    private val config = ConfigFactory.load("secrets.properties")

    val airtableBaseName = config.getString("airtable.baseid") ?: throw RuntimeException("Cannot get Airtable base name")
    val airtablePersonsTable =
        config.getString("airtable.tableid.persons") ?: throw RuntimeException("Cannot get Airtable table name")
    val airtableScheduleTable =
        config.getString("airtable.tableid.schedule") ?: throw RuntimeException("Cannot get Airtable table name")

    val airtableToken = config.getString("airtable.token") ?: throw RuntimeException("Cannot get Airtable token")
    val telegramToken = config.getString("telegram.token") ?: throw RuntimeException("Cannot get Telegram token")
}