package org.hexalite.discord.common.modal.options

interface ModalOptionBuilder {
    var actionRowNumber: Int
    var value: String?

    fun validate() {
        if (actionRowNumber !in 0..4)
            error("The actionRowNumber value exceeds the ranger")
    }
}

interface TextInputOptionBuilder : ModalOptionBuilder {
    var allowedLength: ClosedRange<Int>?
    var placeholder: String?
}