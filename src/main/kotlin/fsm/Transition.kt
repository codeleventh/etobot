package fsm

import com.elbekd.bot.types.CallbackQuery
import com.elbekd.bot.types.Message

class Transition(
    val validationFn: (StateContext) -> Boolean,
    val transitionFn: suspend (Message, StateContext, CallbackQuery?) -> Pair<State?, Boolean>
    // The second parameter in the resulting pair indicates that next state is not required for user input to start
)