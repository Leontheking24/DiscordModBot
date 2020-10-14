package de.leontheking24.discordbot;

import java.util.HashMap;

public class ServerLists {

    private final HashMap<Long, ServerManager> servers;

    public ServerLists() {
        servers = new HashMap<>();
    }

    public ServerManager getServerManager(long serverId) {
        return servers.get(serverId);
    }

    public void addServer(ServerManager serverManager) {
        servers.put(serverManager.getServerId(), serverManager);
    }

    public void removeServer(long serverId) {
        servers.remove(serverId);
    }

}
