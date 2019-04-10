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

package org.debatetool.io.componentio;

import org.debatetool.core.HashIdentifiedSpeechComponent;
import org.bson.types.Binary;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface ComponentIOManager extends Closeable, AutoCloseable {
    HashIdentifiedSpeechComponent retrieveSpeechComponent(byte[] hash) throws IOException;
    HashMap<Binary, HashIdentifiedSpeechComponent> retrieveSpeechComponents(List<byte[]> hashes) throws IOException;
    void storeSpeechComponent(HashIdentifiedSpeechComponent speechComponent) throws IOException;
    void deleteSpeechComponent(byte[] hash) throws IOException;
    void loadAll(HashIdentifiedSpeechComponent component) throws IOException;
}
