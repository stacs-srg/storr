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

import uk.ac.standrews.cs.storr.impl.exceptions.LXPException;
import uk.ac.standrews.cs.storr.interfaces.IReferenceType;
import uk.ac.standrews.cs.storr.types.LXP_LIST;
import uk.ac.standrews.cs.storr.types.LXP_REF;
import uk.ac.standrews.cs.storr.types.LXP_SCALAR;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Class to manage mappings between names and slot numbers in LXP.
 */
public class LXPMetadata extends PersistentMetaData {

    private final Map<String, Integer> field_name_to_slot = new HashMap<>();
    private final Map<Integer, String> slot_to_field_name = new HashMap<>();

    private IReferenceType type = null;

    LXPMetadata() {
        super();
    }

    public LXPMetadata(final Class metadata_class, final String type_name) {

        super(metadata_class,type_name);
        initialiseMaps(metadata_class);
    }

    private void initialiseMaps(final Class c) {

        final Field[] fields = c.getDeclaredFields();
        int next_slot = 0;

        for (final Field field : fields) {

            if (isStaticLXPField(field)) {

                try {
                    field.setAccessible(true);

                    final int slot_value = next_slot++;
                    final String field_name = field.getName();

                    checkDuplicates(field_name, slot_value);
                    field.setInt(null, slot_value);

                    field_name_to_slot.put(field_name, slot_value);
                    slot_to_field_name.put(slot_value, field_name);


                } catch (final IllegalAccessException e) {
                    throw new RuntimeException("Illegal access for label: " + field.getName());
                }
            }
        }
    }

    private void checkDuplicates(final String field_name, final int slot_value) {

        if (slot_to_field_name.containsKey(slot_value)) {
            throw new RuntimeException("Duplicated slot value: " + slot_value);
        }

        if (slot_to_field_name.containsValue(field_name)) {
            throw new RuntimeException("Duplicated field name: " + field_name);
        }
    }

    private static boolean isStaticLXPField(final Field field) {

        return isStatic(field) && isAnnotatedAsLXP(field);
    }

    private static boolean isAnnotatedAsLXP(final Field field) {

        return field.isAnnotationPresent(LXP_SCALAR.class) || field.isAnnotationPresent(LXP_REF.class) || field.isAnnotationPresent(LXP_LIST.class);
    }

    private static boolean isStatic(final Field field) {

        return Modifier.isStatic(field.getModifiers());
    }

    public Map<String, Integer> getFieldNamesToSlotNumbers() {
        return field_name_to_slot;
    }

    public Map<Integer, String> getSlotNumbersToFieldNames() {
        return slot_to_field_name;
    }

    public Integer getSlot(final String field_name) {
        return field_name_to_slot.get(field_name);
    }

    public boolean containsLabel(final String field_name) {

        return field_name_to_slot.containsKey(field_name);
    }

    public String getFieldName(final int slot) {
        return slot_to_field_name.get(slot);
    }

    public List<String> getFieldNamesInSlotOrder() {

        final List<String> result = new ArrayList<>();

        final int count = field_name_to_slot.keySet().size();
        for (int i = 0; i < count; i++) {
            result.add(slot_to_field_name.get(i));
        }

        return result;
    }

    public Set<String> getFields() {
        return field_name_to_slot.keySet();
    }

    public Set<Integer> getSlots() {
        return slot_to_field_name.keySet();
    }

    public int getFieldCount() {

        return field_name_to_slot.keySet().size();
    }

    public void setType(final IReferenceType suppliedType) throws LXPException {

        if (type == null) {
            // TODO put a modified call of Types.checkStructuralConsistency() in here to ensure type compatibility.
            // TODO need a similar call for dynamic creation of fields if type has been set.
            type = suppliedType;
        } else {
            throw new LXPException("Type already defined");
        }
    }
}
