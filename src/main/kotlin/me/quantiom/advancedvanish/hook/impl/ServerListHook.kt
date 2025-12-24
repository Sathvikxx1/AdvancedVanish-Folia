package me.quantiom.advancedvanish.hook.impl

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.wrapper.status.server.WrapperStatusServerResponse
import com.google.gson.JsonArray
import me.quantiom.advancedvanish.hook.IHook
import me.quantiom.advancedvanish.util.AdvancedVanishAPI

// https://minecraft.wiki/w/Java_Edition_protocol/Server_List_Ping
class ServerListHook : IHook {

    private val packetListener = object : PacketListenerAbstract() {
        
        override fun onPacketSend(event: PacketSendEvent) {
            if (event.packetType != PacketType.Status.Server.RESPONSE) {
                return
            }

            val response = WrapperStatusServerResponse(event)
            val root = response.component

            val players = root.getAsJsonObject("players") ?: return

            val visibleOnline = players.get("online").asInt - AdvancedVanishAPI.vanishedPlayers.size
            players.addProperty("online", visibleOnline.coerceAtLeast(0))

            val sample = players.getAsJsonArray("sample") ?: return
            val filteredSample = JsonArray()

            for (element in sample) {
                val player = element.asJsonObject
                val uuid = player.get("id").asString

                if (AdvancedVanishAPI.vanishedPlayers.none { it.toString() == uuid }) {
                    filteredSample.add(player)
                }
            }

            players.add("sample", filteredSample)
            response.component = root
        }
    }

    override fun getID(): String = "ServerList"

    override fun onEnable() {
        PacketEvents.getAPI().eventManager.registerListener(packetListener)
    }

    override fun onDisable() {
        PacketEvents.getAPI().eventManager.unregisterListener(packetListener)
    }
}
