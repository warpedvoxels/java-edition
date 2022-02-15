internal fun HelloCommand() = command("hello") {
    val times by integer("times").required()
    val player by player("player").optional()

    executes(root = false) {
        val player = player ?: noPlayerFound()
        repeat(times) {
            reply("Hello, ${player.name}!")
        }
        reply("Successfully sent $times hello messages to ${player.name}!")
    }
}