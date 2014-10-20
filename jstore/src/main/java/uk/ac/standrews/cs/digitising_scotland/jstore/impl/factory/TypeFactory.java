package uk.ac.standrews.cs.digitising_scotland.jstore.impl.factory;


import uk.ac.standrews.cs.digitising_scotland.jstore.impl.TypeLabel;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.IBucket;
import uk.ac.standrews.cs.digitising_scotland.jstore.interfaces.ITypeLabel;

import java.util.HashMap;

/**
 * Created by al on 12/09/2014.
 */
public class TypeFactory {

    private HashMap<String,ITypeLabel> types_map = new HashMap<>();
    private static final TypeFactory instance = new TypeFactory();

    private TypeFactory() {

    }

    public ITypeLabel createType(String json_encoded_type_descriptor_file_name, String type_name, IBucket types) {
        ITypeLabel newtype = TypeLabel.createNewTypeLabel(json_encoded_type_descriptor_file_name, types);
        types_map.put(type_name, newtype);
        return newtype;
    }

    public static TypeFactory getInstance() {
        return instance;
    }

    public ITypeLabel typeWithname( String name ) {
        return types_map.get(name);
    }
}
