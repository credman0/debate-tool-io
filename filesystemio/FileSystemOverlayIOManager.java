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
import org.debatetool.io.IOUtil;
import org.debatetool.io.overlayio.OverlayIOManager;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FileSystemOverlayIOManager implements OverlayIOManager {
    Path root;
    public FileSystemOverlayIOManager(Path root) throws IOException {
        this.root = root;
    }

    @Override
    public HashMap<String, List<CardOverlay>> getOverlays(byte[] cardHash) {
        String hashEncoded = IOUtil.encodeString(cardHash);
        Path componentDirPath = Paths.get(root.toString(), hashEncoded);
        File[] overlayFiles = componentDirPath.toFile().listFiles();
        HashMap<String, List<CardOverlay>> overlays = new HashMap<>();
        if (overlayFiles == null){
            return overlays;
        }
        for (File file : overlayFiles){
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))){
                List<CardOverlay> overlayList = (List<CardOverlay>) in.readObject();
                overlays.put(file.getName(), overlayList);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return overlays;
    }

    @Override
    public void saveOverlays(byte[] cardHash, List<CardOverlay> overlays, String type) {
        String hashEncoded = IOUtil.encodeString(cardHash);
        Path folderPath = Paths.get(root.toString(), hashEncoded);
        folderPath.toFile().mkdir();
        Path componentPath = Paths.get(root.toString(), hashEncoded,type);
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(componentPath.toFile()))){
            // List isnt always serializable, so we convert to something that is
            out.writeObject(new ArrayList<>(overlays));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<Binary, HashMap<String, List<CardOverlay>>> getAllOverlays(List<byte[]> cardHashes) {
        HashMap<Binary, HashMap<String, List<CardOverlay>>> allOverlays = new HashMap<>();
        for (byte[]cardHash:cardHashes){
            allOverlays.put(new Binary(cardHash), getOverlays(cardHash));
        }
        return allOverlays;
    }

    @Override
    public void close() throws IOException {
    }
}
