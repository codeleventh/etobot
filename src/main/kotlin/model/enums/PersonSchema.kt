package model.enums

enum class PersonSchema(val column: String) {
    ID("Telegram Id"),
    USERNAME("Ник в Telegram"),
    NAME("Имя"),
    SUBSCRIBED("Подписан на рассылки"),
    ROLE("Роль")
}