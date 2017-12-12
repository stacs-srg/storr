/*
 * Copyright 2017 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module storr.
 *
 * storr is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * storr is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with storr. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.storr.impl;

import uk.ac.standrews.cs.storr.impl.exceptions.IllegalKeyException;
import uk.ac.standrews.cs.storr.impl.exceptions.LXPException;
import uk.ac.standrews.cs.storr.interfaces.IReferenceType;
import uk.ac.standrews.cs.storr.types.LXP_LIST;
import uk.ac.standrews.cs.storr.types.LXP_REF;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * Class to manage mappings between names and slot numbers in LXP.
 * Created by al on 24/11/17
 */
public class Metadata {

    private HashMap<String,Integer> field_name_to_slot = new HashMap<>();
    private HashMap<Integer,String> slot_to_field_name  = new HashMap<>();

    private IReferenceType type = null;

    public Metadata() {

    }

    public Metadata(Class c, String typename) throws Exception {

        initialiseMaps( c );
        type = Store.getInstance().getTypeFactory().createType(c, typename);
    }

    private void initialiseMaps( Class c ) throws Exception {
        Field[] fields = c.getDeclaredFields();
        for (Field f : fields) {

            if (Modifier.isStatic(f.getModifiers())) {

                if ( f.isAnnotationPresent(LXP_SCALAR.class) || f.isAnnotationPresent(LXP_REF.class) ||
                        f.isAnnotationPresent(LXP_LIST.class) ) {
                    try {
                        f.setAccessible(true);
                        String field_name = f.getName(); // the name of the field
                        Integer slot_value = f.getInt(null); // the value of the labelled Java field!
                        if( slot_to_field_name.containsKey( slot_value ) ) {
                            throw new Exception( "Duplicated slot value: " + slot_value );
                        }
                        if( slot_to_field_name.containsKey( field_name ) ) {
                            throw new Exception( "Duplicated field name: " + field_name );
                        }
                        field_name_to_slot.put( field_name,slot_value );
                        slot_to_field_name.put( slot_value,field_name );

                    } catch (IllegalAccessException e) {
                        throw new Exception("Illegal access for label: " + f.getName());
                    } catch (IllegalKeyException e) {
                        throw new Exception("Illegal key in label: " + f.getName());
                    }
                }

            }
        }
    }



    // Map Getters

    public HashMap<String,Integer> getFieldNamesToSlotNumbers() {
        return field_name_to_slot;
    }

    public HashMap<Integer,String> getSlotNumbersToFieldNames() {
        return slot_to_field_name;
    }

    // Map Lookup methods

    public Integer getSlot(String fieldname ) {
        return field_name_to_slot.get(fieldname);
    }

    public boolean containsLabel( String fieldname ) { return field_name_to_slot.containsKey(fieldname); }

    public String getFieldName(int slot ) {
        return slot_to_field_name.get(slot);
    }

    public Set<String> getFields() {
        return field_name_to_slot.keySet();
    }

    public int getFieldCount() {
        Set<String> fields = getFields();
        if (fields == null) {
            return 0;
        } else {
            return fields.size();
        }
    }

    // Type handling

    public IReferenceType getType() { return type; }

    public void setType(IReferenceType suppliedType) throws LXPException {
        if( type == null ) {
            // TODO put a modified call of Types.checkStructuralConsistency() in here to ensure type compatibility.
            // TODO need a similar call for dynamic creation of fields if tyoe ha been set.
            type = suppliedType;
        } else {
            throw new LXPException( "Type already defined" );
        }
    }
}
