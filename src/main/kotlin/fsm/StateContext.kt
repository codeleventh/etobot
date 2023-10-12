package fsm

class StateContext(
    var registrationName: String? = null,
    var registrationIsSubscribed: Boolean? = null
) {
    fun clear() {
        registrationName = null
        registrationIsSubscribed = null
    }
}