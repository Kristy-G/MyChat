package org.example.chat_server.auth;

import java.util.ArrayList;

public class PrimitiveInMemoryAuthService implements AuthService {
    private ArrayList<User> users = new ArrayList();

    public PrimitiveInMemoryAuthService() {
        DataBase.start();
        this.users.addAll(DataBase.getArrayList());
    }

    @Override
    public void start() {
        System.out.println("Auth service started");
    }

    @Override
    public void stop() {
        System.out.println("Auth service stopped");
    }

    @Override
    public String getUsernameByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) return user.getUsername();
        }
        return null;
    }

    @Override
    public String changeUsername(String oldName, String newName) {
        return null;
    }

    @Override
    public String changePassword(String username, String oldPassword, String newPassword) {
        return null;
    }
}
