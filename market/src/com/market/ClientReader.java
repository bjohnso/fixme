package com.market;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;

public class ClientReader implements Callable {

    private Socket client;

    private Message message;

    public ClientReader(Socket client) {
        this.client = client;

        try {
            this.client.setSoTimeout(2000);
        } catch (SocketException e){
            System.out.println("Cannot set Timeout on this Socket");
        }
    }

    public Socket getClient() {
        return client;
    }

    @Override
    public Object call() throws Exception {
        String message = "";
        BufferedReader in = null;
        PrintWriter out = null;

        //Blocking Socket call
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            message = in.readLine();
        } catch (SocketTimeoutException e){
            return null;
        } catch (IOException e){
            System.out.println("Read from Client Failed");
            return null;
        }

        if (message == null){
            return null;
        }

        out = new PrintWriter(this.client.getOutputStream(), true);

        if (!message.equalsIgnoreCase("c")) {
            String split[] = message.split("\\|");
            this.message = new Message(Integer.parseInt(split[0]), Integer.parseInt(split[1]), split[2], client);
        }
        System.out.printf("New Message From Client : %S | Recipient : %S | Message %S\n", this.message.getSender(), this.message.getRecipient(), this.message.getMessage());
        return this.message;
    }
}