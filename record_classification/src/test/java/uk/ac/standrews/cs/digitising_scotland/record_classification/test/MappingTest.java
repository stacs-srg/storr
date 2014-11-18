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
/*
 * | ______________________________________________________________________________________________ | Understanding
 * Scotland's People (USP) project. | | The aim of the project is to produce a linked pedigree for all publicly | |
 * available Scottish birth/death/marriage records from 1855 to the present day. | | | | Digitization of the records is
 * being carried out by the ESRC-funded Digitising | | Scotland project, run by University of St Andrews and National
 * Records of Scotland. | | | | The project is led by Chris Dibben at the Longitudinal Studies Centre at St Andrews. | |
 * The other project members are Lee Williamson (also at the Longitudinal Studies Centre) | | Graham Kirby, Alan Dearle
 * and Jamie Carson at the School of Computer Science at St Andrews; | | and Eilidh Garret and Alice Reid at the
 * Department of Geography at Cambridge. | | | |
 * ______________________________________________________________________________________________
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

/**
 * The Class MappingTest.
 */
public class MappingTest {

    private ArrayList<String> t;
    private List<Multiset.Entry<String>> wordCounts;
    private Multiset<String> words;
    private String phrase = "word word1 word2";
    private String[] theText;

    /**
     * Setup.
     */
    @Before
    public void setup() {

        t = new ArrayList<String>();
        phrase = "word0 word1 word2";
        theText = phrase.split(" ");

        for (int i = 0; i < theText.length; i++) {
            t.add(theText[i]);
        }

        words = HashMultiset.create(t);
        wordCounts = Lists.newArrayList(words.entrySet());
    }

    /**
     * Test size.
     */
    @Test
    public void testSize() {

        System.out.println(wordCounts.get(0));
        System.out.println(wordCounts.get(1));
        System.out.println(wordCounts.get(2));
        Assert.assertEquals(wordCounts.size(), 3);
    }

    /**
     * Test remove.
     */
    @Test
    public void testRemove() {

        wordCounts.remove(0);
        System.out.println(wordCounts.get(0));
        System.out.println(wordCounts.get(1));
        Assert.assertEquals(wordCounts.size(), 2);
    }

    /**
     * Test remove via array list.
     */
    @Test
    public void testRemoveViaArrayList() {

        List<Integer> a = new ArrayList<Integer>();
        a.add(1);
        wordCounts.remove(a.get(0).intValue());
        System.out.println(wordCounts.get(0));
        System.out.println(wordCounts.get(1));
        Assert.assertEquals(wordCounts.size(), 2);
    }

}
