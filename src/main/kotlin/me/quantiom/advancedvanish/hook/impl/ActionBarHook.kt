package me.quantiom.advancedvanish.hook.impl

import com.tcoded.folialib.wrapper.task.WrappedTask
import me.quantiom.advancedvanish.AdvancedVanish
import me.quantiom.advancedvanish.config.Config
import me.quantiom.advancedvanish.event.PlayerUnVanishEvent
import me.quantiom.advancedvanish.event.PlayerVanishEvent
import me.quantiom.advancedvanish.hook.IHook
import me.quantiom.advancedvanish.util.AdvancedVanishAPI
import me.quantiom.advancedvanish.util.color
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class ActionBarHook : IHook, Listener {

    private var task: WrappedTask? = null

    override fun getID() = "ActionBar"

    override fun onEnable() {
        task = AdvancedVanish.scheduler!!.runTimerAsync(
            Runnable { updateActionBars() },
            0L,
            40L
        )

        Bukkit.getPluginManager().registerEvents(
            this,
            AdvancedVanish.instance!!
        )
    }

    override fun onDisable() {
        task?.cancel()
    }

    private fun updateActionBars() {
        AdvancedVanishAPI.vanishedPlayers
            .mapNotNull(Bukkit::getPlayer)
            .forEach(::sendActionBar)
    }

    private fun sendActionBar(player: Player) {
        val message = Config.getValueOrDefault(
            "messages.action-bar",
            "<red>You are in vanish."
        ).color()

        player.sendActionBar(message)
    }

    @EventHandler
    private fun onVanish(event: PlayerVanishEvent) {
        sendActionBar(event.player)
    }

    @EventHandler
    private fun onUnVanish(event: PlayerUnVanishEvent) {
        event.player.sendActionBar("")
    }
}
