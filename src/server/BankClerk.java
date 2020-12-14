/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server; 
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

interface ActionResultDelegate {
    public void actionExecuted(boolean completed, String message);
}

/**
 *
 * @author ASUS
 */
    public class BankClerk {
    private Bank bank = new Bank();
    public ActionResultDelegate resultsDelegate;

    public void handleMessageFromClient(String message) {
        System.out.println(message);
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();

        try {
            JsonParser parser = factory.createParser(message);
            JsonNode jsonObject = mapper.readTree(parser);
            String action = jsonObject.get("action").textValue();
            System.out.println(action);

            if (action.equalsIgnoreCase("create_client")) {
                this.createClient(jsonObject);
                this.resultsDelegate.actionExecuted(true, "action_end, registro existoso.");
            }

            if (action.equalsIgnoreCase("get_balance")) {
                int balanceResult = this.getBalance(jsonObject);
                String actionMessage =  "tu saldo es: " + Integer.toString(balanceResult);
                this.resultsDelegate.actionExecuted(true, "action_end, " + actionMessage);
            }

            if (action.equalsIgnoreCase("execute_withdrawal")) {
                int updatedBalance = this.executeWithDrawal(jsonObject);
                String actionMessage =  "tu nuevo saldo es: " + Integer.toString(updatedBalance);
                this.resultsDelegate.actionExecuted(true, "action_end, " + actionMessage);
            }

            if (action.equalsIgnoreCase("execute_deposit")) {
                int updatedBalance = this.executeDeposit(jsonObject);
                String actionMessage =  "tu nuevo saldo es: " + Integer.toString(updatedBalance);
                this.resultsDelegate.actionExecuted(true, "action_end, " + actionMessage);
            }
        } catch (IOException e) {
            System.out.println("error parsing json");
            e.printStackTrace();
            this.resultsDelegate.actionExecuted(true, "action_end, error");
        }
    }

    private void createClient(JsonNode jsonObject){
        System.out.println("create account");
        JsonNode dataNode = jsonObject.get("data");
        String name = dataNode.get("name").textValue();
        String lastName = dataNode.get("lastName").textValue();
        String phone = dataNode.get("phone").textValue();
        String personId = dataNode.get("personId").textValue();
        String password = dataNode.get("password").textValue();
        int branchId = dataNode.get("branchId").intValue();
        this.bank.createUser(name, lastName, phone, personId, branchId, password);
    }

    private int getBalance(JsonNode jsonObject) {
        JsonNode dataNode = jsonObject.get("data");
        String personId = dataNode.get("personId").textValue();
        String password = dataNode.get("password").textValue();
        return this.bank.getBalance(personId, password);
    }

    private int executeWithDrawal(JsonNode jsonObject){
        JsonNode dataNode = jsonObject.get("data");
        String personId = dataNode.get("personId").textValue();
        String password = dataNode.get("password").textValue();
        int amount = dataNode.get("amount").intValue();
        return this.bank.executeWithDrawal(personId, password, amount);
    }

    private int executeDeposit(JsonNode jsonObject){
        JsonNode dataNode = jsonObject.get("data");
        String personId = dataNode.get("personId").textValue();
        String password = dataNode.get("password").textValue();
        int amount = dataNode.get("amount").intValue();
        return this.bank.executeDeposit(personId, password, amount);
    }
   
}
