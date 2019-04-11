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

import org.bson.types.Binary;
import org.debatetool.core.CardOverlay;
import org.debatetool.io.overlayio.OverlayIOManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class FileSystemOverlayIOManager implements OverlayIOManager {

    Path root;
    public FileSystemOverlayIOManager(Path root) throws IOException {
        this.root = root;
    }

    @Override
    public HashMap<String, List<CardOverlay>> getOverlays(byte[] cardHash) {
        return null;
    }

    @Override
    public void saveOverlays(byte[] cardHash, List<CardOverlay> overlays, String type) {

    }

    @Override
    public HashMap<Binary, HashMap<String, List<CardOverlay>>> getAllOverlays(List<byte[]> cardHashes) {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
