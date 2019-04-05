package org.debatetool.io.iocontrollers.mongodb;

import com.mongodb.*;
import org.debatetool.io.accounts.AdminManager;
import org.debatetool.io.accounts.DBLock;
import org.debatetool.io.accounts.mongodb.MongoDBAdminManager;
import org.debatetool.io.accounts.mongodb.MongoDBLock;
import org.debatetool.io.componentio.ComponentIOManager;
import org.debatetool.io.componentio.mongodb.MongoDBComponentIOManager;
import org.debatetool.io.iocontrollers.IOController;
import org.debatetool.io.overlayio.OverlayIOManager;
import org.debatetool.io.overlayio.mongodb.MongoDBOverlayIOManager;
import org.debatetool.io.structureio.StructureIOManager;
import org.debatetool.io.structureio.mongodb.MongoDBStructureIOManager;
import java.io.IOException;

public class MongoDBIOController implements IOController {
    private MongoClient mongoClient;
    private ComponentIOManager componentIOManager;
    private StructureIOManager structureIOManager;
    private OverlayIOManager overlayIOManager;
    private AdminManager adminManager;
    private DBLock dbLock;

    public MongoDBIOController(){
        adminManager = new MongoDBAdminManager();
    }

    @Override
    public boolean attemptAuthenticate(String address, int port, String username, String password) {
        try {
            MongoCredential credential = MongoCredential.createCredential(username,
                    "UDT",
                    password.toCharArray());
            mongoClient = new MongoClient(new ServerAddress(address,port), credential, MongoClientOptions.builder().build());
            // setup if the database authenticated properly
            componentIOManager = new MongoDBComponentIOManager(mongoClient);
            structureIOManager = new MongoDBStructureIOManager(mongoClient);
            overlayIOManager = new MongoDBOverlayIOManager(mongoClient);
            dbLock = new MongoDBLock(mongoClient);
            return true;
        }catch (MongoSecurityException e){
            // -4 is error authenticating
            if (e.getCode()==-4){
                return false;
            }else {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public ComponentIOManager getComponentIOManager() {
        return componentIOManager;
    }

    @Override
    public StructureIOManager getStructureIOManager() {
        return structureIOManager;
    }

    @Override
    public OverlayIOManager getOverlayIOManager() {
        return overlayIOManager;
    }

    @Override
    public AdminManager getAdminManager(){
        return adminManager;
    }

    @Override
    public DBLock getDBLock(){
        return dbLock;
    }

    @Override
    public void close() throws IOException {
        dbLock.unlockAll();
        mongoClient.close();
    }
}
