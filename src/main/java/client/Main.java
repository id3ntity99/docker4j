package client;

import client.nettyserver.WebsocketServer;

public class Main {
    public static void main(String[] args) throws Exception {
        WebsocketServer server = new WebsocketServer();
        server.start("localhost", 1111);
    }
}
