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

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class Helper {

    /**
     * Check that the repository name is legal.
     * A name is legal if:
     * - it exists and it has at least one character
     * - it is a valid file name for the file system
     *
     * TODO - consider limiting the size of the name to 31 characters for better compatability with old file systems?
     * @param name to be checked
     * @return true if the name is legal
     */
    public static boolean NameIsLegal(String name) {

        char[] invalidMac = new char[] {':'};
        char[] invalidLinux = new char[] {'/', '\0'};
        char[] invalidWindows = new char[] {'<', '>', ':', '"', '/', '\\', '|', '?', '*'};

        return name != null && !name.equals("")
                && !StringHasChar(name, invalidMac)
                && !StringHasChar(name, invalidLinux)
                && !StringHasChar(name, invalidWindows);
    }

    private static boolean StringHasChar(String string, char[] matches) {
        for(char m:matches) {
            if (string.indexOf(m) != -1) {
                return true;
            }
        }

        return false;
    }
}
