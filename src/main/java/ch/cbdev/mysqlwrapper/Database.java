package ch.cbdev.mysqlwrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private String host;
    private int port;
    private String username;
    private String password;
    private String database;

    public Database(String host, String username, String password, String database) {
        this.host = host;
        this.port = 3306;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public Database(String host, int port, String username, String password, String database){
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public Query table(String name){
        return new Query(this, name);
    }

    //Direct query
    public void execute(String query) {
        try {
            Connection connection = newConnection();
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query){
        try {
            Connection connection = newConnection();
            ResultSet resultSet = connection.createStatement().executeQuery(query);
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Connection newConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
