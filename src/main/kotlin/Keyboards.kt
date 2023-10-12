import com.elbekd.bot.types.InlineKeyboardButton
import com.elbekd.bot.types.InlineKeyboardMarkup
import fsm.State

const val SEPARATOR = ':'

object Answer {
    const val YES = "yes"
    const val NO = "no"
}

private val buildData: (State, String) -> String =
    { state: State, answer: String -> "${state.ordinal}$SEPARATOR$answer" }
val splitData: (String) -> Pair<State, String> = { data ->
    data.split(SEPARATOR).take(2).let { (fst, snd) -> (State.entries[fst.toInt()] to snd) }
}

fun buildYnKeyboard(forState: State, acceptText: String = "Да", refuseText: String = "Нет") = InlineKeyboardMarkup(
    listOf(
        listOf(
            InlineKeyboardButton(text = acceptText, callbackData = buildData(forState, Answer.YES)),
            InlineKeyboardButton(text = refuseText, callbackData = buildData(forState, Answer.NO)),
        )
    )
)