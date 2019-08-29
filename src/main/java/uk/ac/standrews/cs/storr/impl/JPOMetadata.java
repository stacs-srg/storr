/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr-expt.
 *
 * storr-expt is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr-expt is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr-expt. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package main.java.toy.test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to manage mappings between names and slot numbers in LXP.
 */
public class JPOMetadata {

    private final Map<String,StorrField> storr_fields = new HashMap<>();

    private Class metadata_class = null;

    public JPOMetadata(Class metadata_class) {

        this.metadata_class = metadata_class;

        while( metadata_class != null ) {
            initialiseMaps( metadata_class );
            metadata_class = metadata_class.getSuperclass();
        }
    }

    private void initialiseMaps(final Class c) {

        final Field[] fields = c.getDeclaredFields();

        for (final Field field : fields) {

            field.setAccessible(true);

            if(! Modifier.isStatic(field.getModifiers() ) && field.isAnnotationPresent(STORR.class) ) {

                String name = field.getName();
                Class type = field.getType();

                boolean is_list = List.class.isAssignableFrom( type );
                boolean store_ref = type.equals( StoreReference.class );

                System.out.println( name + " : "+ type + " list? " + is_list + " store ref? " + store_ref);
                storr_fields.put( name, new StorrField( name,type,is_list,store_ref ) );
            }
        }
    }

    public Collection<StorrField> getStorrFields() {
        return storr_fields.values();
    }

    public StorrField get( String key ) {
        return storr_fields.get(key);
    }

    public Class getMetadataClass() {
        return metadata_class;
    }
}
