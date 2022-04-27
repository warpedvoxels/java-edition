@file:OptIn(pbandk.PublicForGeneratedCode::class)

package org.hexalite.network.protobuf

@pbandk.Export
public data class Example(
    val id: Int = 0,
    val email: String = "",
    val name: String? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap(),
): pbandk.Message {
    override operator fun plus(other: pbandk.Message?): org.hexalite.network.protobuf.Example = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<org.hexalite.network.protobuf.Example> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }

    public companion object: pbandk.Message.Companion<org.hexalite.network.protobuf.Example> {
        public val defaultInstance: org.hexalite.network.protobuf.Example by lazy { org.hexalite.network.protobuf.Example() }
        override fun decodeWith(u: pbandk.MessageDecoder): org.hexalite.network.protobuf.Example = org.hexalite.network.protobuf.Example.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<org.hexalite.network.protobuf.Example> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<org.hexalite.network.protobuf.Example, *>>(3)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "name",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        jsonName = "name",
                        value = org.hexalite.network.protobuf.Example::name
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "id",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Int32(),
                        jsonName = "id",
                        value = org.hexalite.network.protobuf.Example::id
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "email",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "email",
                        value = org.hexalite.network.protobuf.Example::email
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "example.Example",
                messageClass = org.hexalite.network.protobuf.Example::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
@pbandk.JsName("orDefaultForExample")
public fun Example?.orDefault(): org.hexalite.network.protobuf.Example = this ?: Example.defaultInstance

private fun Example.protoMergeImpl(plus: pbandk.Message?): Example = (plus as? Example)?.let {
    it.copy(
        name = plus.name ?: name,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Example.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Example {
    var id = 0
    var email = ""
    var name: String? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> name = _fieldValue as String
            2 -> id = _fieldValue as Int
            3 -> email = _fieldValue as String
        }
    }
    return Example(id, email, name, unknownFields)
}
