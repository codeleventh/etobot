import com.typesafe.config.ConfigFactory

object Config {
    private val config = ConfigFactory.load("secrets.properties")

    val token = config.getString("telegram.token") ?: throw RuntimeException("Cannot get Telegram token")
}