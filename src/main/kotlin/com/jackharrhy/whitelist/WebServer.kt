package com.jackharrhy.whitelist

import io.javalin.Javalin
import io.javalin.http.BadRequestResponse
import io.javalin.http.UnauthorizedResponse
import org.bukkit.Bukkit
import java.util.*
import kotlin.collections.HashSet

class WebServer(plugin: Whitelist, bearerToken: String) {
    private data class User(val uuid: UUID)

    init {
        val port = 7500

        val classLoader = Thread.currentThread().contextClassLoader
        Thread.currentThread().contextClassLoader = Whitelist::class.java.classLoader
        val app = Javalin.create().start(port)
        Thread.currentThread().contextClassLoader = classLoader

        app.before { ctx ->
            val auth = ctx.header("authorization")

            if (auth.isNullOrBlank()) {
                throw UnauthorizedResponse()
            } else {
                if (!(auth.startsWith("WHA") && auth.endsWith(bearerToken))) {
                    throw UnauthorizedResponse("Invalid Token")
                }
            }
        }

        app.get("/") { ctx ->
            val players = HashSet<UUID>()
            for (player in Bukkit.getWhitelistedPlayers()) {
                players.add(player.uniqueId)
            }
            ctx.json(players)
        }

        app.post("/") { ctx ->
            val uuid = ctx.body<User>().uuid
            val player = Bukkit.getOfflinePlayer(uuid)
            if (player.isWhitelisted) {
                throw BadRequestResponse("Already whitelisted!")
            } else {
                player.isWhitelisted = true
                ctx.status(201).result("")
            }
        }

        app.delete("/") { ctx ->
            val uuid = ctx.body<User>().uuid
            val player = Bukkit.getOfflinePlayer(uuid)
            if (player.isWhitelisted) {
                player.isWhitelisted = false
                ctx.status(201).result("")
            } else {
                throw BadRequestResponse("Not whitelisted!")
            }
        }

        plugin.logger.info("WebServer now running on port $port")
    }
}