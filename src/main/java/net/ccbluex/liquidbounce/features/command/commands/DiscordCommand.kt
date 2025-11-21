
package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.features.command.Command

class DiscordCommand : Command("Discord", arrayOf("Discord")) {
    override fun execute(args: Array<String>) {
        alert("discord.aqualantic.com")
    }
}