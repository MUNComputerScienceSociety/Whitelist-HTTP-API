package com.jackharrhy.whitelist

import org.bukkit.plugin.java.JavaPlugin

class Whitelist : JavaPlugin() {
    override fun onEnable() {
        logger.info(description.name + " has been enabled")
    }

    override fun onDisable() {
        logger.info(description.name + " has been enabled")
    }
}
