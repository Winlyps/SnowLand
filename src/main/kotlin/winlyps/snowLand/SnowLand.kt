package winlyps.snowLand

import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class SnowLand : JavaPlugin(), Listener {

    private val snowPlayers = HashSet<UUID>()

    override fun onEnable() {
        getCommand("snowland")?.setExecutor(SnowLandCommand())
        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    inner class SnowLandCommand : CommandExecutor {
        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
            if (sender is Player) {
                if (snowPlayers.contains(sender.uniqueId)) {
                    snowPlayers.remove(sender.uniqueId)
                    sender.sendMessage("Snow effect disabled.")
                } else {
                    snowPlayers.add(sender.uniqueId)
                    sender.sendMessage("Snow effect enabled.")
                }
            } else {
                sender.sendMessage("This command can only be run by a player.")
            }
            return true
        }
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (!snowPlayers.contains(event.player.uniqueId)) {
            return
        }

        val player = event.player
        val location = player.location
        val world = location.world ?: return

        for (x in -2..2) {
            for (z in -2..2) {
                val block = world.getBlockAt(location.blockX + x, location.blockY - 1, location.blockZ + z)
                if (block.type.isSolid && block.type != Material.SNOW) {
                    val above = block.getRelative(BlockFace.UP)
                    if (above.type.isAir || above.type == Material.TALL_GRASS || above.type == Material.SHORT_GRASS ) {
                        if (above.type != Material.AIR) {
                            above.breakNaturally()
                        }
                        above.type = Material.SNOW
                    }
                }
            }
        }
    }
}
