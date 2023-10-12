import com.elbekd.bot.Bot
import fsm.StateMachine
import model.enums.Command.REGISTRATION
import model.enums.Command.START

val bot = Bot.createPolling(Config.telegramToken)

fun main() {
    bot.onCommand(START.cmd) { (msg, _) ->
        StateMachine.initState(START, msg)
    }

    bot.onCommand(REGISTRATION.cmd) { (msg, _) ->
        StateMachine.initState(REGISTRATION, msg)
    }

    bot.onMessage { msg ->
        StateMachine.nextState(msg)
    }

    bot.onCallbackQuery { cb ->
        StateMachine.processCallback(cb)
    }

    bot.start()
}