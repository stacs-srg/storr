/*
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module record_classification.
 *
 * record_classification is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * record_classification is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with record_classification. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver.Interfaces;

/**
 * Assesses the validity of c given criterion t.
 * Created by fraserdunlop on 06/10/2014 at 16:35.
 */
public interface ValidityAssessor<C, T> {

    /**
     * Assesses the validity of c given criterion t.
     * @param c object whose validity is to be assessed.
     * @param t criterion for validity assessment.
     * @return boolean, true if valid, false if not.
     */
    boolean assess(C c, T t);
}
