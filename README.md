# Whitelist HTTP API

Enable Minecraft whitelist management from an exposed HTTP endpoint

---

`GET / -> [] of {uuid: "...", name: "..."}` - Returns the `whitelist.json` of the server.
`POST / ({name: "..."}) -> 201` - Adds a user to the whitelist via username.
`DELETE / ({name: "..."}) -> 201` - Removes a user from the whitelist via username.

---

- Interfacing with the whitelist via Bukkit's API doesn't seem to work properly, so this plugin just calls the `whitelist` command directly, as you would via the server console.
- Even if the server fails to whitelist a player, the response will still be a `201`, so it is assumed you hand the server a valid Minecraft username.
- Default port is 7500, configurable via `<server dir>/plugins/<plugin dir>/config.yml`.
- Everything is `application/json`.
- Authentication is done via Bearer Tokens, any request must contain a valid Bearer Token to be served anything but a 403.

> `<server dir>/plugins/<plugin dir>/config.yml`:
>
> ```
> bearer_token: <token>
> ```
>
> Example HTTP request w/valid header:
>
> ```
> GET / HTTP/x.x
> Content-Type: application/json
> Authorization: WHA <token>
> Host: <host>:<port>
> ```
