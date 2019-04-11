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
import org.debatetool.core.*;
import org.debatetool.io.IOUtil;
import org.debatetool.io.Pair;
import org.debatetool.io.componentio.ComponentIOManager;
import org.debatetool.io.iocontrollers.IOController;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class FileSystemComponentIOManager implements ComponentIOManager {
    Path componentDir;
    public FileSystemComponentIOManager(Path componentDir) throws IOException {
        this.componentDir = componentDir;
    }

    @Override
    public HashIdentifiedSpeechComponent retrieveSpeechComponent(byte[] hash) throws IOException {
        String hashEncoded = IOUtil.encodeString(hash);
        Path componentPath = Paths.get(componentDir.toString(), hashEncoded);
        if (!componentPath.toFile().exists()){
            throw new FileNotFoundException();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(componentPath.toFile()))){
            FileSystemDocument document = (FileSystemDocument) in.readObject();
            return HashIdentifiedSpeechComponent.createFromLabelledLists(document.type, document.labels, document.values, document.hash);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public HashMap<Binary, HashIdentifiedSpeechComponent> retrieveSpeechComponents(List<byte[]> hashes) throws IOException {
        HashMap <Binary, HashIdentifiedSpeechComponent> map = new HashMap<>();
        for (byte[] hash:hashes){
            map.put(new Binary(hash), retrieveSpeechComponent(hash));
        }
        return map;
    }

    @Override
    public void storeSpeechComponent(HashIdentifiedSpeechComponent speechComponent) throws IOException {
        String hashEncoded = IOUtil.encodeString(speechComponent.getHash());
        Path componentPath = Paths.get(componentDir.toString(), hashEncoded);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(componentPath.toFile()))){
            List<String>[] labelledLists = speechComponent.toLabelledLists();
            FileSystemDocument document = new FileSystemDocument(speechComponent.getClass().getName(), labelledLists[0], labelledLists[1], speechComponent.getHash());
            out.writeObject(document);
        }
    }

    @Override
    public void deleteSpeechComponent(byte[] hash) throws IOException {
        String hashEncoded = IOUtil.encodeString(hash);
        Path componentPath = Paths.get(componentDir.toString(), hashEncoded);
        componentPath.toFile().delete();
    }

    @Override
    public void loadAll(HashIdentifiedSpeechComponent component) throws IOException {
        if (Block.class.isInstance(component)) {
            Block block = (Block) component;
            ArrayList<byte[]> cardHashes = new ArrayList<>(block.size());
            ArrayList<Card> cards = new ArrayList<>(block.size());
            appendBlockCards(block,cardHashes,cards);
            loadCards(cardHashes,cards);
        }else if (Speech.class.isInstance(component)) {
            Speech speech = (Speech) component;
            ArrayList<byte[]> cardHashes = new ArrayList<>(speech.size());
            ArrayList<Card> cards = new ArrayList<>(speech.size());
            for (int i = 0; i < speech.size(); i++){
                SpeechComponent speechComponent = speech.getComponent(i);
                if (Card.class.isInstance(speechComponent)){
                    cardHashes.add(((HashIdentifiedSpeechComponent)speechComponent).getHash());
                    cards.add((Card) speechComponent);
                }else if (Block.class.isInstance(speechComponent)){
                    appendBlockCards(((Block) speechComponent), cardHashes,cards);
                }else{
                    throw new IllegalStateException("Illegal component in speech: "+speechComponent.getClass());
                }
            }
            loadCards(cardHashes,cards);
        } else {
            throw new IllegalArgumentException("Unrecognized type: " + component.getClass());
        }
    }

    private void appendBlockCards(Block block, List<byte[]> cardHashes, List<Card> cards){
        for (int i = 0; i < block.size(); i++){
            SpeechComponent blockComponent = block.getComponent(i);
            if (Card.class.isInstance(blockComponent)){
                cardHashes.add(((HashIdentifiedSpeechComponent)blockComponent).getHash());
                cards.add((Card) blockComponent);
            }else if (Analytic.class.isInstance(blockComponent)){
                // nothing to load
            }else{
                throw new IllegalStateException("Illegal component in block: "+blockComponent.getClass());
            }
        }
    }

    private void loadCards(List<byte[]> cardHashes, List<Card> cards) throws IOException {
        HashMap<Binary, HashIdentifiedSpeechComponent> loadedComponents = retrieveSpeechComponents(cardHashes);
        HashMap<Binary, HashMap<String, List<CardOverlay>>> allOverlays = IOController.getIoController().getOverlayIOManager().getAllOverlays(cardHashes);
        for (Card cardToLoad:cards){
            Binary cardBinaryHash = new Binary(cardToLoad.getHash());
            cardToLoad.setTo((Card) loadedComponents.get(cardBinaryHash));
            HashMap<String, List<CardOverlay>> cardOverlays = allOverlays.get(cardBinaryHash);
            if (cardOverlays!=null) {
                cardToLoad.assignOverlaysFromMap(cardOverlays);
            }
        }
    }

    @Override
    public void close() throws IOException {

    }
}
