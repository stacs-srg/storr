/**
 * Copyright 2014 Digitising Scotland project:
 * <http://digitisingscotland.cs.st-andrews.ac.uk/>
 *
 * This file is part of the module util.
 *
 * util is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * util is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with util. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.digitising_scotland.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManipulation {

    public static synchronized Properties getProperties(final String properties_path_string) throws IOException {

        try (InputStream stream = PropertiesManipulation.class.getClassLoader().getResourceAsStream(properties_path_string)) {

            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        }
    }
}
