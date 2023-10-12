package integrations

import Config
import dev.fuxing.airtable.AirtableApi
import dev.fuxing.airtable.AirtableTable
import dev.fuxing.airtable.formula.AirtableFormula
import dev.fuxing.airtable.formula.LogicalOperator
import model.Person
import model.enums.PersonSchema
import model.enums.Role

private val AirTableApi = AirtableApi(Config.airtableToken)
val PersonsTable: AirtableApi.Table = AirTableApi.base(Config.airtableBaseName).table(Config.airtablePersonsTable)
val ScheduleTable: AirtableApi.Table = AirTableApi.base(Config.airtableBaseName).table(Config.airtableScheduleTable)

object AirTable {
    fun getPersonRole(tgId: Long): Role? {
        val res = PersonsTable.list(
            AirtableTable.QuerySpec.create().filterByFormula(
                LogicalOperator.EQ,
                AirtableFormula.Object.field(PersonSchema.ID.column),
                AirtableFormula.Object.value(tgId)
            )
        )
        return if (res.size == 0) null
        else Role.entries.find { it.tableValue == res.first().getFieldString(PersonSchema.ROLE.column) }
    }

    fun addPerson(person: Person) {
        PersonsTable.post(person.toRecord())
    }
}