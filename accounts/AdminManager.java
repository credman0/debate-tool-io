package org.debatetool.io.accounts;

public interface AdminManager {
    boolean authenticateAsAdmin(String address, int port, String username, String password);
    boolean checkIsAuthenticated();
    boolean createUser(String username, String password);
}
