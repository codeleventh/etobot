package model.enums

enum class Role(val tableValue: String) {
    LEGACY(""),
    NOBODY("Анкета на рассмотрении"),
    BANNED("Не допущен"),
    VOLUNTEER("Волонтер"),
    COORDINATOR("Координатор"),
    ADMIN("Администратор")

}