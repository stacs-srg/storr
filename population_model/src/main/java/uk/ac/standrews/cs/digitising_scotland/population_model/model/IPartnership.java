package uk.ac.standrews.cs.digitising_scotland.population_model.model;

import java.util.List;

/**
 * Created by graham on 09/06/2014.
 */
public interface IPartnership extends Comparable<IPartnership> {

    int getId();

    int getPartner(int p);

    int getPartner1();

    int getPartner2();

    boolean includesChild(int p);

    int getMarriageDate();

    List<Integer> getChildren();

    boolean isMarked();

    void setMarked();
}
