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

package org.debatetool.io.iocontrollers;

import org.debatetool.io.accounts.AdminManager;
import org.debatetool.io.accounts.DBLock;
import org.debatetool.io.componentio.ComponentIOManager;
import org.debatetool.io.initializers.IOInitializer;
import org.debatetool.io.iocontrollers.mongodb.MongoDBIOController;
import org.debatetool.io.overlayio.OverlayIOManager;
import org.debatetool.io.structureio.StructureIOManager;

import java.io.Closeable;
import java.io.IOException;

public abstract class IOController implements Closeable, AutoCloseable {
    private static IOController ioController = null;
    public static void setIoController(IOController controller){
        ioController = controller;
    }
    public static IOController getIoController(){
        return ioController;
    }

    public abstract boolean attemptInitialize(IOInitializer initializer) throws IOException;

    public abstract ComponentIOManager getComponentIOManager();
    public abstract StructureIOManager getStructureIOManager();
    public abstract OverlayIOManager getOverlayIOManager();

    public abstract AdminManager getAdminManager();

    public abstract DBLock getDBLock();
}
