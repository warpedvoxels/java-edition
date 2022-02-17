// TODO: finish this test
//@Suppress("UNUSED_VARIABLE")
//class HelloCommand : StringSpec({
//    val hello = command("hello") {
//        val times by integer("times").required()
//        val player by player("player").optional()
//        runs {
//            val player = player ?: noPlayerFound()
//            repeat(times) {
//                player.sendMessage("Hello!")
//            }
//        }
//        permission = "example.command.hello"
//    }
//
//    val plugin = mockk<KrakenPlugin>()
//    val dispatcher = CommandDispatcher<CommandSourceStack>()
//    val brigadier = hello.buildLiteral()
//    dispatcher.root.addChild(brigadier)
//
//    "should execute the command just fine" {
//        shouldNotThrowAnyUnit {
//            val source = mockk<CommandSourceStack>()
//            dispatcher.run(source, "hello")
//        }
//    }
//})