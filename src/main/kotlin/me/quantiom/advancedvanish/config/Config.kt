package me.quantiom.advancedvanish.config

import co.aikar.commands.Locales
import co.aikar.commands.MessageKeys
import co.aikar.locales.MessageKeyProvider
import com.google.common.collect.Maps
import me.quantiom.advancedvanish.AdvancedVanish
import me.quantiom.advancedvanish.state.VanishStateManager
import me.quantiom.advancedvanish.sync.ServerSyncManager
import me.quantiom.advancedvanish.util.applyPlaceholders
import me.quantiom.advancedvanish.util.color
import me.quantiom.advancedvanish.util.colorLegacy
import me.quantiom.advancedvanish.util.sendComponentMessage
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import java.io.File
import java.util.logging.Level

object Config {
    var savedConfig: FileConfiguration? = null
    var usingPriorities = false
    private var messages: MutableMap<String, List<String>> = Maps.newHashMap()

    fun reload() {
        val plugin = AdvancedVanish.instance!!
        val configFile = File(plugin.dataFolder, "config.yml")
        if (!configFile.exists()) {
            plugin.saveDefaultConfig()
        }

        plugin.reloadConfig()
        this.savedConfig = plugin.config

        this.reloadMessages()
        this.reloadCommandHandlerMessages()
        ServerSyncManager.setup()
        VanishStateManager.onConfigReload()
        this.usingPriorities = this.getValueOrDefault("priority.enable", false)
        AdvancedVanish.log(Level.INFO, "Config reloaded successfully.")
    }

    inline fun <reified T> getValue(key: String): T? {
        val value = this.savedConfig!!.get(key)
        if (value is T) return value
        return when (T::class) {
            Boolean::class -> false as T
            String::class -> "" as T
            Int::class, Double::class -> 0 as T
            else -> null
        }
    }

    inline fun <reified T> getValueOrDefault(key: String, default: T): T {
        val value = this.savedConfig!!.get(key)
        if (value is T) return value
        return default
    }

    private fun getMessage(key: String): List<String> {
        return messages[key] ?: listOf("<gray>Message not found for <red>$key<gray>.")
    }

    private fun getMessage(key: String, vararg pairs: Pair<String, String>): List<String> {
        return messages[key]?.applyPlaceholders(*pairs) ?: listOf("<gray>Message not found for <red>$key<gray>.")
    }

    fun sendMessage(player: CommandSender, key: String) {
        var prefix = ""
        if (this.getValueOrDefault("messages.prefix.enabled", false)) {
            prefix = this.getValueOrDefault("messages.prefix.value", "<red>[AdvancedVanish]<white> ")
        }
        this.getMessage(key).filter { it.isNotEmpty() }
            .forEach { player.sendComponentMessage(prefix.color().append(it.color())) }
    }

    fun sendMessage(sender: CommandSender, key: String, vararg pairs: Pair<String, String>) {
        var prefix: Component = Component.text("")
        if (this.getValueOrDefault("messages.prefix.enabled", false)) {
            prefix = this.getValueOrDefault("messages.prefix.value", "<red>[AdvancedVanish]<white> ").color()
        }
        this.getMessage(key, *pairs).filter { it.isNotEmpty() }
            .forEach { sender.sendComponentMessage(prefix.append(it.color())) }
    }

    private fun reloadMessages() {
        messages.clear()
        this.savedConfig?.getConfigurationSection("messages")?.let {
            it.getKeys(false).forEach { key ->
                if (it.isString(key)) {
                    messages[key] = listOf(it.getString(key)!!)
                } else if (it.isList(key)) {
                    messages[key] = it.getList(key)!!.filterIsInstance<String>()
                }
            }
        }
    }

    private fun reloadCommandHandlerMessages() {
        val commandHandlerMessages: MutableMap<String, String> = Maps.newHashMap()
        this.savedConfig?.getConfigurationSection("command-handler-messages")?.let {
            it.getKeys(false).forEach { key ->
                if (it.isString(key)) {
                    commandHandlerMessages[key] = it.getString(key)!!
                }
            }
        }

        val prefix =
            if (this.savedConfig?.getConfigurationSection("command-handler-messages")
                    ?.getBoolean("use-prefix") == true
            ) {
                this.getValueOrDefault("messages.prefix.value", "<red>[AdvancedVanish]<white> ")
            } else {
                ""
            }

        val getOrDefault: (String, String) -> String = { key, default ->
            prefix + if (commandHandlerMessages.containsKey(key)) commandHandlerMessages[key]!! else default
        }

        val messages: MutableMap<MessageKeyProvider, String> = Maps.newHashMap()

        messages[MessageKeys.UNKNOWN_COMMAND] = getOrDefault("unknown-command", "Invalid arguments.")
            .color().colorLegacy()

        messages[MessageKeys.INVALID_SYNTAX] = getOrDefault("invalid-syntax", "Usage: %command% %syntax%")
            .applyPlaceholders(
                "%command%" to "{command}",
                "%syntax%" to "{syntax}"
            )
            .color().colorLegacy()

        messages[MessageKeys.ERROR_PERFORMING_COMMAND] =
            getOrDefault("error-performing-command", "There was an error performing this command.")
                .color().colorLegacy()

        messages[MessageKeys.COULD_NOT_FIND_PLAYER] =
            getOrDefault("could-not-find-player", "Couldn't find a player by the name of <red>%search%<white>.")
                .applyPlaceholders("%search%" to "{search}")
                .color().colorLegacy()

        messages[MessageKeys.ERROR_PREFIX] = getOrDefault("generic-error", "Error: <red>%error%")
            .applyPlaceholders("%error%" to "{message}")
            .color().colorLegacy()

        AdvancedVanish.commandManager!!.locales.addMessages(Locales.ENGLISH, messages)
    }
}
