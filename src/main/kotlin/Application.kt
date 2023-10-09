import com.elbekd.bot.Bot
import com.elbekd.bot.model.toChatId


fun main(args: Array<String>) {
    val bot = Bot.createPolling(Config.token)

    bot.onCommand("/start") { (msg, _) ->
        bot.sendMessage(msg.chat.id.toChatId(), "Eto bot!")
    }
    bot.start()
}