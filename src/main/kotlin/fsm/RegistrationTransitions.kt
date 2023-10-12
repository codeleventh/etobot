package fsm

import Answer
import bot
import buildYnKeyboard
import com.elbekd.bot.model.toChatId
import integrations.AirTable
import model.Person
import model.enums.Role

val registrationTransitions = mapOf(
    State.REGISTRATION_START to Transition(
        { _ -> true },
        { msg, _, _ ->
            val role = AirTable.getPersonRole(msg.chat.id)
            when (role) {
                Role.BANNED -> {
                    bot.sendMessage(msg.chat.id.toChatId(), "Извините, ваша анкета не была одобрена")
                    null to false
                }

                Role.VOLUNTEER, Role.COORDINATOR, Role.ADMIN -> {
                    bot.sendMessage(msg.chat.id.toChatId(), "Вы уже были успешно зарегистрированы")
                    null to false
                }

                Role.NOBODY -> {
                    bot.sendMessage(
                        msg.chat.id.toChatId(),
                        "Ваша анкета ждет одобрения. Вы получите уведомление после того, как волонтеры рассмотрят её"
                    )
                    null to false
                }

                Role.LEGACY -> {
                    bot.sendMessage(msg.chat.id.toChatId(), "Извините, ваша анкета не была одобрена")
                    null to false
                }

                null -> {
                    bot.sendMessage(msg.chat.id.toChatId(), "Как вас зовут?")
                    State.REGISTRATION_SAVE_NAME to false
                }
            }
        }),
    State.REGISTRATION_SAVE_NAME to Transition({ _ -> true }, { msg, ctx, _ ->
        ctx.registrationName = msg.text
        State.REGISTRATION_ASK_SUBSCRIBE to true
    }),
    State.REGISTRATION_ASK_SUBSCRIBE to Transition({ _ -> true }, { msg, _, _ ->
        bot.sendMessage(
            msg.chat.id.toChatId(),
            "Иногда мы делаем рассылки (например, «срочно нужны трое человек на склад завтра»), вы хочите получать и их тоже?",
            replyMarkup = buildYnKeyboard(forState = State.REGISTRATION_SAVE_SUBSCRIBE)
        )
        State.REGISTRATION_SAVE_SUBSCRIBE to false
    }),
    State.REGISTRATION_SAVE_SUBSCRIBE to Transition({ _ -> true }, { _, ctx, answer ->
        ctx.registrationIsSubscribed = (answer == Answer.YES)
        State.REGISTRATION_REVIEW to true
    }),
    State.REGISTRATION_REVIEW to Transition(
        { ctx -> ctx.registrationName != null && ctx.registrationIsSubscribed != null },
        { msg, ctx, _ ->
            bot.sendMessage(
                msg.chat.id.toChatId(),
                "Давайте проверять!\n" +
                        "Имя: ${ctx.registrationName}\n" +
                        "Подписка: ${ctx.registrationIsSubscribed}\n" +
                        "Telegram id: ${msg.chat.id}\n" +
                        "Всё верно?",
                replyMarkup = buildYnKeyboard(
                    State.REGISTRATION_END,
                    "Да, верно",
                    "Нет, давай сначала"
                )
            )
            State.REGISTRATION_END to false
        }),
    State.REGISTRATION_END to Transition(
        { ctx -> ctx.registrationName != null && ctx.registrationIsSubscribed != null },
        { msg, ctx, answer ->
            if (answer == Answer.NO) {
                ctx.clear()
                State.REGISTRATION_START to true
            } else {
                AirTable.addPerson(
                    Person(
                        msg.chat.id,
                        msg.chat.username,
                        ctx.registrationName!!,
                        ctx.registrationIsSubscribed!!,
                        Role.NOBODY
                    )
                )
                bot.sendMessage(
                    msg.chat.id.toChatId(),
                    "Готово! Когда анкета будет одобрена, вы получите уведомление и в боте откроется расписание"
                )
                null to false
            }
        })
)