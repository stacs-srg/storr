package uk.ac.standrews.cs.digitising_scotland.tools;

import java.io.*;

/**
 * TODO test!
 * Should deep clone any serializable object.
 * For Map<K,V> make sure K and V serializable.
 * Created by fraserdunlop on 06/10/2014 at 10:17.
 */
public class DeepCloner implements Serializable{
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T deepClone(T o) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(o);
        out.flush();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
        return (T) o.getClass().cast(in.readObject());
    }
}
