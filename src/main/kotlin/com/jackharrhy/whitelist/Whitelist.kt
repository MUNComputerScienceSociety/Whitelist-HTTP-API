package com.jackharrhy.whitelist

import org.bukkit.plugin.java.JavaPlugin

class Whitelist : JavaPlugin() {
    override fun onEnable() {
        logger.info(description.name + " has been enabled")

        this.saveDefaultConfig()
        val bearerToken = this.config.getString("bearer_token")
        if (bearerToken.isNullOrEmpty() || bearerToken.equals("unset")) {
            logger.warning("bearer_token is unset, plugin useless!")
        } else {
            WebServer(this)
        }
    }

    override fun onDisable() {
        logger.info(description.name + " has been disabled")
    }
}
