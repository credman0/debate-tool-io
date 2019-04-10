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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * provides static methods that filter requests to all IO based on passed ComponentFilter objects
 */
public class Filter {

    private Filter(){}

    public enum Type{CONTAINS}
    private static List<ComponentFilter> componentFilters = new ArrayList<>();
    private static List<StructureFilter> structureFilters = new ArrayList<>();
    private static List<Object> filterIndexes = new ArrayList<>();
    private static Set<byte[]> matchingComponents;
    public static void addComponentFilter(ComponentFilter filter){
        componentFilters.add(filter);
    }

    public static void addStructureFilter(StructureFilter filter){
        structureFilters.add(filter);
    }

    public static void updateComponentFilter(){
        matchingComponents = null;
    }

    public static Bson generateFilters(){
        if (structureFilters.isEmpty()){
            return null;
        }
        List<Bson> filtersList = new ArrayList<>();
        for (StructureFilter filter:structureFilters){
            filtersList.add(filter.toBson());
        }
        return Filters.or(filtersList);
    }

    public static void addParsed(String filter, int index){
        StructureFilter newFilter = new StructureFilter("Path", filter, Type.CONTAINS);
        structureFilters.add(newFilter);
        filterIndexes.add(index, newFilter);
    }

    public static void removedParsed(int index){
        Object filter = filterIndexes.remove(index);
        structureFilters.remove(filter);
    }

    public static void resetFilters(){
        componentFilters.clear();
        structureFilters.clear();
        updateComponentFilter();
    }
}
