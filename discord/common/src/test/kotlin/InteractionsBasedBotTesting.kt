//import dev.kord.core.Kord
//import dev.kord.core.behavior.interaction.respondEphemeral
//import kotlinx.coroutines.runBlocking
//import org.hexalite.discord.common.DiscordCommonData
//import org.hexalite.discord.common.InteractionRegistry
//import org.hexalite.discord.common.command.slash.options.SlashCommandArguments
//import org.hexalite.discord.common.command.slash.options.optionalUser
//import org.hexalite.discord.common.command.slashCommand
//import org.hexalite.network.common.settings.HexaliteSettings
//import kotlin.test.Test
//
//class InteractionsBasedBotTesting {
//    @Test
//    fun `should run a bot with a test command just fine`() = runBlocking {
//        val settings = HexaliteSettings.Discord(
//            token = System.getenv("TESTING_TOKEN").also(::println),
//            id = System.getenv("TESTING_BOT_ID")?.toLong()!!.also(::println),
//            publicKey = System.getenv("TESTING_PUBLIC_KEY").also(::println),
//            secret = System.getenv("TESTING_SECRET").also(::println),
//            guildIds = listOf(System.getenv("TESTING_GUILD_ID")?.toLong()!!.also(::println))
//        )
//        val kord = Kord(settings.token)
//        val registry = InteractionRegistry(kord, DiscordCommonData(settings))
//
//        class HelloArguments: SlashCommandArguments() {
//            val user by optionalUser("user", "The user to be greeted.")
//        }
//
//        val hello = slashCommand("hello", "\uD83D\uDC4B Greets the given user.", ::HelloArguments) {
//            execute {
//                val user = arguments.user ?: interaction.user
//                val message = "`\uD83D\uDC4B` Hello, ${user.mention} (${user.tag})"
//                interaction.respondEphemeral { content = message }
//            }
//        }
//        registry.register(hello)
//        registry.start()
//    }
//}