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

package org.debatetool.io.filesystemio;

import org.debatetool.io.accounts.AdminManager;
import org.debatetool.io.accounts.DBLock;
import org.debatetool.io.accounts.DBLockResponse;
import org.debatetool.io.componentio.ComponentIOManager;
import org.debatetool.io.initializers.FileSystemInitializer;
import org.debatetool.io.initializers.IOInitializer;
import org.debatetool.io.iocontrollers.IOController;
import org.debatetool.io.overlayio.OverlayIOManager;
import org.debatetool.io.structureio.StructureIOManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileSystemIOController extends IOController {
    private Path root;
    private FileSystemComponentIOManager componentIOManager;
    private FileSystemStructureIOManager structureIOManager;
    private FileSystemOverlayIOManager overlayIOManager;

    @Override
    public boolean attemptInitialize(IOInitializer initializer) throws IOException {
        if (!(initializer instanceof FileSystemInitializer)){
            throw new IllegalArgumentException("Incorrect initializer type");
        }
        root = ((FileSystemInitializer) initializer).root;
        root.toFile().mkdirs();
        Path componentDir = Paths.get(root.toString(), "components");
        if (!componentDir.toFile().exists()){
            componentDir.toFile().mkdir();
        }
        Path overlayDir = Paths.get(root.toString(), "overlays");
        if (!overlayDir.toFile().exists()){
            overlayDir.toFile().mkdir();
        }
        Path rootDir = Paths.get(root.toString(), "root");
        if (!rootDir.toFile().exists()){
            rootDir.toFile().mkdir();
        }
        componentIOManager = new FileSystemComponentIOManager(componentDir);
        structureIOManager = new FileSystemStructureIOManager(rootDir);
        overlayIOManager = new FileSystemOverlayIOManager(overlayDir);
        return true;
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
    public AdminManager getAdminManager() {
        return null;
    }

    @Override
    public DBLock getDBLock() {
        return new DBLock() {
            @Override
            public DBLockResponse tryLock(byte[] hash) {
                return new DBLockResponse(null, DBLockResponse.ResultType.SUCCESS);
            }

            @Override
            public void unlock(byte[] hash) {

            }

            @Override
            public void unlockAll() {

            }

            @Override
            public void unlockAllExcept(byte[] hash) {

            }

            @Override
            public void unlockAllExcept(byte[]... hashes) {

            }
        };
    }

    @Override
    public void close() throws IOException {
        componentIOManager.close();
        structureIOManager.close();
        overlayIOManager.close();
    }
}
