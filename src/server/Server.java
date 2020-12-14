/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.*;
import java.io.*;

public class Server implements ActionResultDelegate {
    private Socket client = null;
    private ServerSocket server = null;
    private PrintWriter outputWriter;
    private BankClerk clerk = new BankClerk();
    BufferedReader inputBufferReader;

    public Server(int port) {
        try {
            this.server = new ServerSocket(port);
            this.clerk.resultsDelegate = this;
            this.resetClientConnection();
        } catch(IOException i) {
            System.out.println(i);
        }
    }

    private void resetClientConnection() throws IOException {
        System.out.println("esperando conexion");
        this.client = this.server.accept();
        System.out.println("cliente conectado");

        this.inputBufferReader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        this.outputWriter = new PrintWriter(this.client.getOutputStream(), true);
        String incomingMessage = "";

        //this.sendInfo("start");

        while ((incomingMessage = this.inputBufferReader.readLine()) != null) {
            incomingMessage = incomingMessage.trim();
            this.clerk.handleMessageFromClient(incomingMessage);
            break;
            /*if (incomingMessage == "close_server") {
                System.out.println("Closing server");
                this.inputStream.close();
                this.inputBufferReader.close();
                this.outputStream.close();
                this.client.close();
                break;
            }*/
        }
    }

    public void sendInfo(String info) {
        System.out.println(info);
        this.outputWriter.println(info);
    }

    @Override
    public void actionExecuted(boolean completed, String message) {
        this.sendInfo(message);
        try {
            /*this.inputBufferReader.close();
            this.outputWriter.close();
            this.client.close();*/
            this.inputBufferReader = null;
            this.outputWriter = null;
            this.client = null;
            this.resetClientConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

