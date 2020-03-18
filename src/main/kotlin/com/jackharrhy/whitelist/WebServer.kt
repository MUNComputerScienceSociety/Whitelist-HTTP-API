package com.jackharrhy.whitelist

import com.fasterxml.jackson.databind.ObjectMapper
import io.javalin.Javalin
import io.javalin.http.UnauthorizedResponse
import org.bukkit.Bukkit
import java.io.File
import java.lang.Integer.parseInt


class WebServer(plugin: Whitelist) {
    private data class User(val name: String)

    init {
        val port = parseInt(plugin.config.getString("port"))
        val bearerToken = plugin.config.getString("bearer_token")!!

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

        val whitelistPath = plugin.dataFolder.absolutePath + "/../../whitelist.json"
        app.get("/") { ctx ->
            val whitelist = File(whitelistPath).readText()
            val mapper = ObjectMapper()
            ctx.json(mapper.readTree(whitelist))
        }

        app.post("/") { ctx ->
            val name = ctx.body<User>().name
            Bukkit.getScheduler().callSyncMethod(plugin) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist add $name")
            }
            ctx.status(201).result("")
        }

        app.delete("/") { ctx ->
            val name = ctx.body<User>().name
            Bukkit.getScheduler().callSyncMethod(plugin) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist remove $name")
            }
            ctx.status(201).result("")
        }

        plugin.logger.info("WebServer now running on port $port")
    }
}