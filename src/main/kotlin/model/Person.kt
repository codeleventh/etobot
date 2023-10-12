package model

import dev.fuxing.airtable.AirtableRecord
import model.enums.PersonSchema
import model.enums.Role

data class Person(
    val tgId: Long,
    val tgUsername: String?,
    val name: String,
    val isSubscribed: Boolean,
    val role: Role
) {
    fun toRecord(): AirtableRecord {
        val record = AirtableRecord()
        record.putField(PersonSchema.ID.column, tgId)
        record.putField(PersonSchema.USERNAME.column, this.tgUsername)
        record.putField(PersonSchema.NAME.column, this.name)
        record.putField(PersonSchema.ROLE.column, this.role.tableValue)
        record.putField(PersonSchema.SUBSCRIBED.column, this.isSubscribed)
        return record
    }
}
