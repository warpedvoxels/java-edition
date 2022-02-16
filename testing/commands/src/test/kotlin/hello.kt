import com.mojang.brigadier.CommandDispatcher
import io.kotest.assertions.throwables.shouldNotThrowAnyUnit
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import net.minecraft.commands.CommandSourceStack
import org.hexalite.network.kraken.KrakenPlugin
import org.hexalite.network.kraken.command.argument.integer
import org.hexalite.network.kraken.command.argument.optional
import org.hexalite.network.kraken.command.argument.player
import org.hexalite.network.kraken.command.argument.required
import org.hexalite.network.kraken.command.brigadier.bukkit.run
import org.hexalite.network.kraken.command.dsl.command
import org.hexalite.network.kraken.extension.noPlayerFound

// TODO: finish this test
@Suppress("UNUSED_VARIABLE")
class HelloCommand : StringSpec({
    val hello = command("hello") {
        val times by integer("times").required()
        val player by player("player").optional()
        runs {
            val player = player ?: noPlayerFound()
            repeat(times) {
                player.sendMessage("Hello!")
            }
        }
        permission = "example.command.hello"
    }

    val plugin = mockk<KrakenPlugin>()
    val dispatcher = CommandDispatcher<CommandSourceStack>()
    val brigadier = hello.buildLiteral()
    dispatcher.root.addChild(brigadier)

    "should execute the command just fine" {
        shouldNotThrowAnyUnit {
            val source = mockk<CommandSourceStack>()
            dispatcher.run(source, "hello")
        }
    }
})