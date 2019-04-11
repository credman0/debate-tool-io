/*
 *                               This program is free software: you can redistribute it and/or modify
 *                                it under the terms of the GNU General Public License as published by
 *                                the Free Software Foundation, version 3 of the License.
 *
 *                                This program is distributed in the hope that it will be useful,
 *                                but WITHOUT ANY WARRANTY; without even the implied warranty of
 *                                MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *                                GNU General Public License for more details.
 *
 *                                You should have received a copy of the GNU General Public License
 *                                along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *                                Copyright (c) 2019 Colin Redman
 */

package org.debatetool.io.iocontrollers.mongodb;

import com.mongodb.*;
import org.debatetool.io.accounts.AdminManager;
import org.debatetool.io.accounts.DBLock;
import org.debatetool.io.accounts.mongodb.MongoDBAdminManager;
import org.debatetool.io.accounts.mongodb.MongoDBLock;
import org.debatetool.io.componentio.ComponentIOManager;
import org.debatetool.io.componentio.mongodb.MongoDBComponentIOManager;
import org.debatetool.io.initializers.DatabaseInitializer;
import org.debatetool.io.initializers.IOInitializer;
import org.debatetool.io.iocontrollers.IOController;
import org.debatetool.io.overlayio.OverlayIOManager;
import org.debatetool.io.overlayio.mongodb.MongoDBOverlayIOManager;
import org.debatetool.io.structureio.StructureIOManager;
import org.debatetool.io.structureio.mongodb.MongoDBStructureIOManager;
import java.io.IOException;

public class MongoDBIOController extends IOController {
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
    public boolean attemptInitialize(IOInitializer initializer) {
        if (!(initializer instanceof DatabaseInitializer)){
            throw new IllegalArgumentException("Incorrect initializer type");
        }
        String address = ((DatabaseInitializer) initializer).address;
        int port = ((DatabaseInitializer) initializer).port;
        String username = ((DatabaseInitializer) initializer).username;
        String password = ((DatabaseInitializer) initializer).password;
        try {
            if (username == null || password==null){
                mongoClient = new MongoClient(new ServerAddress(address,port));
            }else{
                MongoCredential credential = MongoCredential.createCredential(username,
                        "UDT",
                        password.toCharArray());
                mongoClient = new MongoClient(new ServerAddress(address,port), credential, MongoClientOptions.builder().build());
            }
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
