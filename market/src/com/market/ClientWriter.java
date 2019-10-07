package com.market;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;

public class ClientWriter implements Callable {

    private Socket client;
    private String message;
    private PrintWriter out;

    public ClientWriter(Socket client, String message){
        this.client = client;
        this.message = message;
    }

    public Socket getClient() {
        return client;
    }

    @Override
    public Object call() throws Exception {
        PrintWriter out = null;
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            out.println(message);
            System.out.println("Response Sent To Client");
        } catch (IOException e){
            System.out.println("Write to Client Failed");
            return null;
        }
        return message;
    }
}