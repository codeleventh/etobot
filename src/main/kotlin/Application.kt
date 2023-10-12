import com.elbekd.bot.Bot
import com.elbekd.bot.model.toChatId
import dev.fuxing.airtable.AirtableApi


val api = AirtableApi(Config.airtableToken)
val personsTable = api.base(Config.airtableBaseName).table(Config.airtablePersonsTable)
val scheduleTable = api.base(Config.airtableBaseName).table(Config.airtableScheduleTable)

    val bot = Bot.createPolling(Config.telegramToken)

    bot.onCommand("/start") { (msg, _) ->
        bot.sendMessage(msg.chat.id.toChatId(), RegistrationFlow.register(msg))
    }

    bot.start()
}