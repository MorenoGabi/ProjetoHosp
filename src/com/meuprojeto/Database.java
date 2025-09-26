package com.meuprojeto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/hospdb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";   // seu usuário MySQL
    private static final String PASS = "root";   // sua senha MySQL

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // garante driver JDBC
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}





