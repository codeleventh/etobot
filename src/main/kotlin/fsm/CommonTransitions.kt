package fsm

import bot
import com.elbekd.bot.model.toChatId
import model.enums.Command

val commonTransitions = mapOf(
    State.START to Transition(
        { _ -> true },
        { msg, _, _ ->
            bot.sendMessage(
                msg.chat.id.toChatId(),
                "Привет!\n" +
                        "Это — бот.\n" +
                        "Для регистрации в нём используй команду ${Command.REGISTRATION.cmd}\n\n" +
                        "После одобрения откроется доступ к расписанию."
            )
            null to false
        },
    ),
    State.UNEXPECTED to Transition({ _ -> true }, { msg, _, _ ->
        bot.sendMessage(
            msg.chat.id.toChatId(),
            "Что-то пошло не так, нужно начать сначала. Отправь нужную команду снова!"
        )
        null to false
    }
    )
)
