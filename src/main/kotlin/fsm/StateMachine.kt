package fsm

import bot
import com.elbekd.bot.model.toChatId
import com.elbekd.bot.types.CallbackQuery
import com.elbekd.bot.types.Message
import com.elbekd.bot.types.ParseMode
import dev.fuxing.airtable.exceptions.AirtableApiException
import model.enums.Command
import model.enums.Error
import splitData

enum class State {
    START,
    REGISTRATION_START,
    REGISTRATION_SAVE_NAME,
    REGISTRATION_ASK_SUBSCRIBE,
    REGISTRATION_SET_SUBSCRIBE,
    REGISTRATION_SAVE_SUBSCRIBE,
    REGISTRATION_REVIEW,
    REGISTRATION_END,
    UNEXPECTED,
}

val transitions = commonTransitions + registrationTransitions

object StateMachine {
    // Using specific chat id as the key
    private val store: MutableMap<Long, Pair<State, StateContext>> = mutableMapOf()

    suspend fun processCallback(cb: CallbackQuery) {
        val msg = cb.message!!
        if (store[msg.chat.id] == null) {
            bot.answerCallbackQuery(cb.id, Error.UNEXPECTED_EVENT.message, showAlert = true)
        } else {
            nextState(msg, cb.data)
            bot.answerCallbackQuery(cb.id)
        }
    }

    suspend fun initState(cmd: Command, msg: Message) {
        val stateCmd = when (cmd) {
            Command.START -> State.START
            Command.REGISTRATION -> State.REGISTRATION_START
            else -> State.UNEXPECTED
        }

        val chatId = msg.chat.id
        store[chatId] = stateCmd to StateContext()
        nextState(msg)
    }

    suspend fun nextState(msg: Message, cbData: String? = null) {
        val chatId = msg.chat.id

        try {
            store[chatId] ?: return // no context was ever created

            val (state, context) = store[chatId]!!
            if (state == State.UNEXPECTED) {
                bot.sendMessage(chatId.toChatId(), Error.UNEXPECTED_EVENT.message)
                context.clear()
                return
            }

            val transition = transitions[state]!!

            val data = cbData?.let(splitData)
            val dataState = data?.first
            val dataAnswer = data?.second
            if ((dataState != null && dataState != state) || !transition.validationFn(context)) {
                bot.sendMessage(chatId.toChatId(), Error.UNEXPECTED_EVENT.message)
                context.clear()
                return
            }

            val res = transition.transitionFn(msg, context, dataAnswer)
            if (res.first == null) {
                store.remove(chatId)
                context.clear()
                return
            } else {
                store[chatId] = res.first!! to context
            }

            // The next state is marked as capable of auto-starting
            if (res.second) {
                nextState(msg)
                return
            }
        } catch (e: AirtableApiException) {
            bot.sendMessage(
                chatId.toChatId(),
                "Ошибка интеграции с AirTable:\n`${e.message}`",
                parseMode = ParseMode.Markdown
            )
        } catch (e: Exception) {
            bot.sendMessage(chatId.toChatId(), Error.UNEXPECTED_EVENT.message)
        }
    }
}
