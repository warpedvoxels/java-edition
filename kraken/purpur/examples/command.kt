fun CommandRegisteringScope.hello() = command("hello") {
    class Arguments : CommandArgumentsScope(this) {
        val player by player("player")
        val kind by enumeration<GreetingKind>("kind")
        val times by integer("times") { suggest(1, 2, 3) }
    }
    runs(Arguments()) { args ->
        val greeting = when(args!!.kind) {
            GreetingKind.Hello -> "Hello, "
            GreetingKind.Hi -> "Hi, "
        }
        repeat(args.times) { count ->
            val component = Component.text("[$count] $greeting, ${args.player.name}!")
            args.player.sendMessage(component)
        }
    }
}
