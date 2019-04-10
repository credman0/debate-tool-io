/*
 *                               This program is free software: you can redistribute it and/or modify
 *                               it under the terms of the GNU General Public License as published by
 *                                the Free Software Foundation, either version 3 of the License, or
 *                                (at your option) any later version.
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

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.conversions.Bson;
import org.debatetool.io.accounts.DBLock;
import org.bson.Document;
import org.debatetool.io.accounts.DBLockResponse;
import org.debatetool.io.filters.Filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MongoDBLock implements DBLock {
    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> collection;

    public MongoDBLock(MongoClient mongoClient){
        this.mongoClient = mongoClient;
        database = mongoClient.getDatabase("UDT");
        collection = database.getCollection("Locks");
        collection.createIndex(Indexes.hashed("Hash"));
        // TODO don't drop indexes on every start
        collection.dropIndexes();
        // TODO prevent this from cleaning up locks when still connected
        collection.createIndex(Indexes.ascending("time"),
                new IndexOptions().expireAfter(10L, TimeUnit.MINUTES));
    }

    @Override
    public DBLockResponse tryLock(byte[] hash) {
        String username = mongoClient.getCredential().getUserName();
        Document setOnInsert = new Document();
        setOnInsert.put("time", new Date());
        setOnInsert.put("username", username);
        Document update = new Document("$setOnInsert", setOnInsert);
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);
        options.upsert(true);
        Document document = collection.findOneAndUpdate(Filters.eq("Hash", hash), update, options);
        if (document.get("username").equals(username)){
            return new DBLockResponse("", DBLockResponse.ResultType.SUCCESS);
        }else{
            return new DBLockResponse(username, DBLockResponse.ResultType.FAILURE_LOCKEDBY);
        }
    }

    @Override
    public void unlock(byte[] hash) {
        collection.findOneAndDelete(Filters.and(Filters.eq("Hash", hash),Filters.eq("username", mongoClient.getCredential().getUserName())));
    }

    @Override
    public void unlockAll() {
        collection.deleteMany(Filters.eq("username", mongoClient.getCredential().getUserName()));
    }

    @Override
    public void unlockAllExcept(byte[] hash) {
        collection.deleteMany(Filters.and(Filters.eq("username", mongoClient.getCredential().getUserName()), Filters.not(Filters.eq("Hash", hash))));
    }

    @Override
    public void unlockAllExcept(byte[]... hashes) {
        List<Bson> excludeFiltersList = new ArrayList<>();
        for (byte[] hash:hashes){
            excludeFiltersList.add(Filters.eq("Hash", hash));
        }
        Bson excludeFilter = Filters.nor(excludeFiltersList);
        collection.deleteMany(Filters.and(Filters.eq("username", mongoClient.getCredential().getUserName()), Filters.not(excludeFilter)));
    }
}
