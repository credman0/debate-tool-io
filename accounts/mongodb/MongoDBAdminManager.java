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
