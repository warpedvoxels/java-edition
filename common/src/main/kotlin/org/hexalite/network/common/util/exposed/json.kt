// https://gist.github.com/qoomon/70bbbedc134fd2a149f1f2450667dc9d
package org.hexalite.network.common.util.exposed

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject

@Suppress("UNCHECKED_CAST")
@OptIn(InternalSerializationApi::class)
class JsonbColumnType<T: Any>(private val stringify: (T) -> String, private val parse: (String) -> T, override var nullable: Boolean = false): ColumnType() {
    companion object {
        const val SqlType: String = "jsonb"
    }

    override fun sqlType(): String = SqlType

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        stmt[index] = PGobject().apply {
            type = sqlType()
            this.value = value as String?
        }
    }

    override fun valueFromDB(value: Any): Any = if (value is PGobject) parse(value.value!!) else value

    override fun valueToString(value: Any?): String = if (value is Iterable<*>) nonNullValueToString(value) else super.valueToString(value)

    override fun notNullValueToDB(value: Any): Any = stringify(value as T)
}

fun <T: Any> Table.jsonb(name: String, nullable: Boolean = false, stringify: (T) -> String, parse: (String) -> T): Column<T> {
    return registerColumn(name, JsonbColumnType(stringify, parse, nullable))
}

// kotlinx.serialization
fun <T: Any> Table.jsonb(name: String, serializer: KSerializer<T>, nullable: Boolean = false, json: Json = Json.Default): Column<T> =
    jsonb(name, nullable, { json.encodeToString(serializer, it) }, { json.decodeFromString(serializer, it) })

// kotlinx.serialization - reified
@OptIn(InternalSerializationApi::class)
inline fun <reified T: Any> Table.jsonb(name: String, nullable: Boolean = false, json: Json = Json.Default): Column<T> =
    jsonb(name, T::class.serializer(), nullable, json)

class JsonValue<T>(val expr: Expression<*>, override val columnType: IColumnType, val jsonPath: List<String>): org.jetbrains.exposed.sql.Function<T>(columnType) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        val castJson = columnType.sqlType() != JsonbColumnType.SqlType
        if (castJson) {
            append('{')
        }
        append(expr)
        append(" #>")
        if (castJson) {
            append('}')
        }
        append(" '{${jsonPath.joinToString { escapeFieldName(it) }}}'")
        if (castJson) {
            append(")::${columnType.sqlType()}}")
        }
    }

    companion object {
        private fun escapeFieldName(value: String) = value.map {
            fieldNameCharactersToEscape[it] ?: it
        }.joinToString("").let { "\"$it\"" }

        private val fieldNameCharactersToEscape = mapOf(
            // '\"' to "\'\'", // no need to escape single quote as we put string in double quotes
            '\"' to "\\\"",
            '\r' to "\\r",
            '\n' to "\\n"
        )
    }
}

inline fun <reified T: Any> Column<*>.json(vararg jsonPath: String): JsonValue<T> {
    val type = when (T::class) {
        Boolean::class -> BooleanColumnType()
        Int::class -> IntegerColumnType()
        Float::class -> FloatColumnType()
        String::class -> TextColumnType()
        else -> JsonbColumnType({ error("Unexpected call") }, { error("Unexpected call") })
    }
    return JsonValue(this, type, jsonPath.toList())
}

class JsonContainsOp(expr1: Expression<*>, expr2: Expression<*>): ComparisonOp(expr1, expr2, "??")

infix fun <T> JsonValue<Any>.contains(t: T): JsonContainsOp =
    JsonContainsOp(this, SqlExpressionBuilder.run { this@contains.wrap(t) })

infix fun <T> JsonValue<Any>.contains(other: Expression<T>): JsonContainsOp =
    JsonContainsOp(this, other)
