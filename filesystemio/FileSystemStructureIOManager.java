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

import org.bson.Document;
import org.bson.types.Binary;
import org.debatetool.core.HashIdentifiedSpeechComponent;
import org.debatetool.core.StateRecoverableComponent;
import org.debatetool.io.IOUtil;
import org.debatetool.io.iocontrollers.IOController;
import org.debatetool.io.structureio.StructureIOManager;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FileSystemStructureIOManager implements StructureIOManager {

    Path root;
    public FileSystemStructureIOManager(Path root) throws IOException {
        this.root = root;
    }

    @Override
    public List<String> getChildren(List<String> path) {
        return getChildren(path, true);
    }

    @Override
    public List<String> getChildren(List<String> path, boolean filtered) {
        Path folderPath = Paths.get(root.toString(), path.stream().toArray(String[]::new));
        String[] childrenArr = folderPath.toFile().list((current, name) -> new File(current, name).isDirectory());
        return Arrays.asList(childrenArr);
    }

    @Override
    public List<HashIdentifiedSpeechComponent> getContent(List<String> path) throws IOException {
        Path folderPath = Paths.get(root.toString(), path.stream().toArray(String[]::new));
        File[] contentFiles = folderPath.toFile().listFiles((current, name) -> !(new File(current, name).isDirectory()));
        List<byte[]> byteList = new ArrayList<>(contentFiles.length);
        for (File contentFile:contentFiles){
            byteList.add(IOUtil.decodeString(contentFile.getName()));
        }
        List<HashIdentifiedSpeechComponent> contents = new ArrayList<>();
        HashMap<Binary, HashIdentifiedSpeechComponent> components = IOController.getIoController().getComponentIOManager().retrieveSpeechComponents(byteList);
        for (File contentFile:contentFiles) {
            byte[] hashBytes = IOUtil.decodeString(contentFile.getName());
            HashIdentifiedSpeechComponent content = components.get(new Binary(hashBytes));
            if (content instanceof StateRecoverableComponent) {
                try(BufferedReader reader = new BufferedReader(new FileReader(contentFile))){
                    content.restoreState(reader.readLine());
                }
            }
            contents.add(content);
        }
        return contents;

    }

    @Override
    public List<String> getRoot() {
        return getChildren(new ArrayList<>());
    }

    @Override
    public void addChild(List<String> path, String name) {
        List<String> fullpath = new ArrayList<>(path);
        fullpath.add(name);
        Path contentPath = Paths.get(root.toString(), fullpath.stream().toArray(String[]::new));
        contentPath.toFile().mkdir();
    }

    @Override
    public void removeNode(List<String> path) {
        Path folderPath = Paths.get(root.toString(), (String[]) path.toArray());
        if (!folderPath.toFile().isDirectory()){
            throw new IllegalArgumentException("Deletion argument is a file");
        }
        folderPath.toFile().delete();
    }

    @Override
    public void addContent(List<String> path, HashIdentifiedSpeechComponent component) throws IOException {
        List<String> fullpath = new ArrayList<>(path);
        fullpath.add(IOUtil.encodeString(component.getHash()));
        Path contentPath = Paths.get(root.toString(), fullpath.stream().toArray(String[]::new));
        contentPath.toFile().createNewFile();
        if (component instanceof StateRecoverableComponent) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(contentPath.toFile()))) {
                writer.write(component.getStateString());
            }
        }
    }

    @Override
    public void removeContent(List<String> path, byte[] id) {
        Path contentPath = Paths.get(root.toString(), path.stream().toArray(String[]::new));
        if (contentPath.toFile().isDirectory()){
            throw new IllegalArgumentException("Deletion argument is a folder");
        }
        contentPath.toFile().delete();
    }

    @Override
    public void renameDirectory(List<String> path, String name, String newName) {
        List<String> fullpath = new ArrayList<>(path);
        fullpath.add(name);
        Path folderPath = Paths.get(root.toString(), fullpath.stream().toArray(String[]::new));
        if (!folderPath.toFile().isDirectory()){
            throw new IllegalArgumentException("Rename argument is a file");
        }
        List<String> newListPath = new ArrayList<>(path);
        newListPath.add(newName);
        Path newPath = Paths.get(root.toString(), newListPath.stream().toArray(String[]::new));
        folderPath.toFile().renameTo(newPath.toFile());
    }

    @Override
    public void close() throws IOException {

    }
}
