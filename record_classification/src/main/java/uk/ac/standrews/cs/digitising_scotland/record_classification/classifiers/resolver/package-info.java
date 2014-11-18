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
/**
 * Split into genericised code and project specific code. The main point of contact between this package
 * to the rest of the Digitising Scotland project is designed to be the class "ResolverPipelineTools"
 * which holds a collection of tools implemented in this package for use in data processing pipelines.
 * Created by fraserdunlop on 07/10/2014 at 17:21.
 */
package uk.ac.standrews.cs.digitising_scotland.record_classification.classifiers.resolver;

//TODO move ClassifierPipeline.classifyTokenSet into package?