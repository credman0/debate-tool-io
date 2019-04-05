package org.debatetool.io.accounts.mongodb;

import com.mongodb.*;
import org.debatetool.io.accounts.AdminManager;

public class MongoDBAdminManager implements AdminManager {

    MongoClient mongoClient;

    @Override
    public boolean authenticateAsAdmin(String address, int port, String username, String password) {
        MongoCredential credential = MongoCredential.createCredential(username,
                "admin",
                password.toCharArray());
        mongoClient = new MongoClient(new ServerAddress(address, port), credential, MongoClientOptions.builder().build());

        if (!checkIsAuthenticated()) {
            return  false;
        }else {
            return true;
        }
    }

    @Override
    public boolean checkIsAuthenticated() {
        try {
            mongoClient.getDatabase("admin").getCollection("System").find().first();
        }catch (MongoSecurityException e){
            // -4 is error authenticating
            if (e.getCode()==-4){
                return false;
            }else {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean createUser(String username, String password) {
        BasicDBObject createCommand = new BasicDBObject();
        createCommand.append("createUser", username);
        createCommand.append("pwd", password);
        String[] roles = { "readWrite"};
        createCommand.put("roles", roles);
        mongoClient.getDatabase("UDT").runCommand(createCommand);
        return true;
    }
}
