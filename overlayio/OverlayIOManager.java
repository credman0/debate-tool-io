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

package org.debatetool.io.overlayio;

import org.debatetool.core.CardOverlay;
import org.bson.types.Binary;

import java.io.Closeable;
import java.util.HashMap;
import java.util.List;

public interface OverlayIOManager extends Closeable, AutoCloseable {
    HashMap<String,List<CardOverlay>> getOverlays(byte[] cardHash);

    void saveOverlays(byte[] cardHash, List<CardOverlay> overlays, String type);
    HashMap<Binary, HashMap<String,List<CardOverlay>>> getAllOverlays(List<byte[]> cardHashes);
}
