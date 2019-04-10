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

package org.debatetool.io.filters;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

public class StructureFilter {
    private String field;
    private String value;
    private Filter.Type type;
    public StructureFilter(String field, String value, Filter.Type type) {
        this.field = field;
        this.value = value;
        this.type = type;
    }
    public Bson toBson(){
        switch (type){
            case CONTAINS:
                return Filters.regex(field, value);
            default:
                throw new IllegalStateException("Type not recognized: " + type);
        }
    }
}
