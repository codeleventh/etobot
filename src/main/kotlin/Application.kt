import com.elbekd.bot.Bot
import fsm.State
import fsm.StateMachine
import integrations.AirTable
import model.enums.Command.REGISTRATION
import model.enums.Command.START
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.server.Undertow
import org.http4k.server.asServer

val bot = Bot.createPolling(Config.telegramToken)

fun main() {
    bot.onCommand(START.cmd) { (msg, _) ->
        StateMachine.initState(State.START, msg)
    }

    bot.onCommand(REGISTRATION.cmd) { (msg, _) ->
        StateMachine.initState(State.REGISTRATION_START, msg)
    }

    bot.onMessage { msg ->
        StateMachine.nextState(msg)
    }

    bot.onCallbackQuery { cb ->
        StateMachine.processCallback(cb)
    }

    bot.start()
}