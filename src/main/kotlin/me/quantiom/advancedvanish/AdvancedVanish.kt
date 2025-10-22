package me.quantiom.advancedvanish

import co.aikar.commands.PaperCommandManager
import com.tcoded.folialib.FoliaLib
import com.tcoded.folialib.impl.PlatformScheduler
import me.quantiom.advancedvanish.command.VanishCommand
import me.quantiom.advancedvanish.config.Config
import me.quantiom.advancedvanish.hook.HooksManager
import me.quantiom.advancedvanish.listener.VanishListener
import me.quantiom.advancedvanish.permission.PermissionsManager
import me.quantiom.advancedvanish.state.VanishStateManager
import me.quantiom.advancedvanish.sync.ServerSyncManager
import me.quantiom.advancedvanish.util.AdvancedVanishAPI
import org.bukkit.Bukkit
import java.util.logging.Level

object AdvancedVanish {
    var instance: AdvancedVanishPlugin? = null
    var folia: FoliaLib? = null
    var scheduler: PlatformScheduler? = null
    var commandManager: PaperCommandManager? = null

    fun log(level: Level, msg: String) {
        instance?.logger?.log(level, msg)
    }

    fun onEnable(plugin: AdvancedVanishPlugin) {
        instance = plugin

        folia = FoliaLib(plugin)
        scheduler = folia?.scheduler

        commandManager = PaperCommandManager(plugin).also {
            it.enableUnstableAPI("help")
            it.registerCommand(VanishCommand, true)
        }

        Config.reload()

        plugin.server.pluginManager.registerEvents(ServerSyncManager, plugin)
        plugin.server.pluginManager.registerEvents(VanishListener, plugin)

        PermissionsManager.setupPermissionsHandler()
        HooksManager.setupHooks()
    }

    fun onDisable() {
        ServerSyncManager.close()
        VanishStateManager.onDisable()

        AdvancedVanishAPI.vanishedPlayers
            .mapNotNull(Bukkit::getPlayer)
            .forEach { AdvancedVanishAPI.unVanishPlayer(it) }

        HooksManager.disableHooks()
        commandManager?.unregisterCommand(VanishCommand)
    }
}
