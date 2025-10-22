package me.quantiom.advancedvanish.hook.impl

import com.tcoded.folialib.wrapper.task.WrappedTask
import dev.esophose.playerparticles.api.PlayerParticlesAPI
import me.quantiom.advancedvanish.AdvancedVanish
import me.quantiom.advancedvanish.hook.IHook
import me.quantiom.advancedvanish.util.AdvancedVanishAPI

class PlayerParticlesHook : IHook {

    private var task: WrappedTask? = null

    override fun getID() = "PlayerParticles"

    override fun onEnable() {
        task = AdvancedVanish.scheduler?.runTimerAsync(
            Runnable {

            AdvancedVanishAPI.vanishedPlayers
                    .mapNotNull { PlayerParticlesAPI.getInstance().getPPlayer(it) }
                    .forEach { it.activeParticles.clear() }
            },
            0L,
            20L
        )
    }

    override fun onDisable() {
        task?.cancel()
    }
}
