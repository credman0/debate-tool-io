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

package org.debatetool.io.structureio;

import org.debatetool.core.HashIdentifiedSpeechComponent;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public interface StructureIOManager extends Closeable, AutoCloseable {
    List<String> getChildren(List<String> path);

    List<String> getChildren(List<String> path, boolean filtered);

    List<HashIdentifiedSpeechComponent> getContent(List<String> path) throws IOException;
    List<String> getRoot();
    void addChild(List<String> path, String name);

    void addContent(List<String> path, HashIdentifiedSpeechComponent component);

    void renameDirectory(List<String> path, String name, String newName);
}
