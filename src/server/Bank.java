/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;
import com.mysql.cj.protocol.Resultset;

import java.sql.*;

/**
 *
 * @author ASUS
 */
public class Bank {
    Connection connection;

    void createUser(String name, String lastName, String phone, String personIdentifier, int branchId, String password) {
        try {
            this.createConnectionIfNeeded();
            Statement statement = connection.createStatement();
            this.connection.setAutoCommit(false);

            String clientValues = "VALUES('"+ name +"', '" + lastName + "', " + phone + ", " + personIdentifier +  ", '" + password + "');";
            String insertClientSql = "INSERT INTO CLIENTE (NOMBRE_CLIENTE, APELLIDO_CLIENTE, TELE_CLIENTE, CEDULA, CONTRASENA) " + clientValues;

            String selectClient = "(SELECT ID_CLIENTE FROM CLIENTE WHERE CEDULA = " +  personIdentifier + ")";
            String accountValues = "VALUES("+ selectClient +", 1, " + personIdentifier + ", 0);";
            String insertAccountSql = "INSERT INTO CUENTA(ID_CLIENTE, TIPO_CUENTA, NUMERO_CUENTA, SALDO) " + accountValues;
            statement.execute(insertClientSql);
            statement.execute(insertAccountSql);
            this.connection.commit();
            this.connection.setAutoCommit(true);
        }catch (ClassNotFoundException | SQLException e){
            System.out.println(e.toString());
        }
    }

    public int getBalance(String personId, String password){
        try {
            this.createConnectionIfNeeded();
            Statement statement = connection.createStatement();
            String query = "SELECT SALDO FROM CUENTA CU JOIN CLIENTE CL ON CL.ID_CLIENTE = CU.ID_CLIENTE WHERE CL.CEDULA = '"+ personId +"' AND CL.CONTRASENA = '"+ password +"'  ";
            System.out.println(query);
            ResultSet results = statement.executeQuery(query);
            //results.first();
            int ret = 0;
            if(results.next()){
                ret = results.getInt(1);
            }
            return ret;
        } catch (ClassNotFoundException | SQLException e){
            System.out.println(e.toString());
            return 0;
        }
    }

    public int executeWithDrawal(String personId, String password, int amount){
        try {
            this.createConnectionIfNeeded();
            Statement statement = connection.createStatement();
            String query = "UPDATE CUENTA SET SALDO = SALDO - "+ amount +" WHERE CUENTA.NUMERO_CUENTA = '"+ personId +"'";
            //System.out.println(query);
            statement.execute(query);
            return this.getBalance(personId, password);
        } catch (ClassNotFoundException | SQLException e){
            System.out.println(e.toString());
            return 0;
        }
    }

    public int executeDeposit(String personId, String password, int amount){
        try {
            this.createConnectionIfNeeded();
            Statement statement = connection.createStatement();
            String query = "UPDATE CUENTA SET SALDO = SALDO + "+ amount +" WHERE CUENTA.NUMERO_CUENTA = '"+ personId +"'";
            System.out.println(query);
            statement.execute(query);
            return this.getBalance(personId, password);
        } catch (ClassNotFoundException | SQLException e){
            System.out.println(e.toString());
            return 0;
        }
    }

    /*public boolean validateCredentials(String personIdentifier, String password) {
        try {
            this.createConnectionIfNeeded();
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT FROM cliente WHERE cedula = '"+ personIdentifier +"' AND contrasena = '"+ password +"' LIMIT 1");
            results.last();
            return results.getRow() > 0;
        } catch (ClassNotFoundException | SQLException e){
            System.out.println(e.toString());
            return false;
        }
    }*/

    static Connection dbConnection() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/banco_1?user=root"); 
            //Connection connection = DriverManager.getConnection("jdbc:mysql://us-cdbr-east-05.cleardb.net/heroku_95453670e0e8ec5?user=bf085ce6928c9e&password=09be3373");  ;
            //System.out.println("db connection succesful");
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            throw e;
        }
    }

    private void createConnectionIfNeeded() throws ClassNotFoundException, SQLException {
        if (this.connection == null) {
            this.connection = Bank.dbConnection();
        }
    }

    //close db connection when server is disconnected
}
