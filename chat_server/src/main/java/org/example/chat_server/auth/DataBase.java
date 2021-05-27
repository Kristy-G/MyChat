package org.example.chat_server.auth;

import java.sql.*;
import java.util.*;

public class DataBase {
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement ps;
    private static String insert = "insert into users (username, login, password) values (?, ?, ?);";

    private static ArrayList<User> arrayList = new ArrayList();

    public static void start() {
        try {
            connect();
            dropTable();
            createTable();
            insertUsers();
            simpleRead();
        }
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            disconnect();
        }
    }

    private static void dropTable() throws SQLException {
        statement.execute("drop table if exists users;");
    }

    private static void createTable() throws SQLException {
        statement.execute("create table if not exists users (id integer primary key autoincrement, username text, login text, password text);");
    }

    private static void insertUsers() throws SQLException {
        statement.executeUpdate("insert into users (username, login, password) values ('User1', 'Log1', 'pass1'), ('User2', 'Log2', 'pass2'), ('User3', 'Log3', 'pass3')");
    }

    private static void simpleRead() throws SQLException {
        try (ResultSet rs = statement.executeQuery("select username, login, password from users;")) {

            while (rs.next()) {
                arrayList.add(new User(rs.getString("username"), rs.getString("login"), rs.getString("password")));
                for (User user: arrayList) {
                    System.out.print(user.getUsername() + " " + user.getLogin() + " " + user.getPassword() + ", ");
                }
                System.out.println();
            }
        }
    }

    public static List getArrayList() { return arrayList; }

    private static void connect() throws SQLException, ClassNotFoundException {
//        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:lesson.db");
        statement = connection.createStatement();
//        ps = connection.prepareStatement(insert);
//        CallableStatement cs = connection.prepareCall(call);
    }

    private static void disconnect() {
        try {
            if (statement != null) statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if (ps != null) ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if (connection != null) connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
