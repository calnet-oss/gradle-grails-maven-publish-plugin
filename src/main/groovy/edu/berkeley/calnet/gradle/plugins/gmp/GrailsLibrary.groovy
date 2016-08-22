/*
 * Copyright (c) 2016, Regents of the University of California and
 * contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.berkeley.calnet.gradle.plugins.gmp

import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.Usage

// @formatter:off
/**
 * Adds components.grails.
 *
 * Usage:
 * <pre>{@code
 * publishing {
 *     publications {
 *         maven(MavenPublication) {
 *         grailsMavenPublish.addGrailsComponent(project)
 *         from components.grails
 *     }
 * }
 * }</pre>
 */
// @formatter:on
class GrailsLibrary implements SoftwareComponentInternal {
    /**
     * See Gradle's JavaLibrary for example
     */

    /**
     * maven-publish only supports runtime scope (as of 2.14.1).  See the
     * Grails code for DefaultMavenPublication.from() to see how runtime is
     * hard-coded.  So the Usage.getName() really doesn't mean anything in
     * terms of affecting functionality, nor does having more than one Usage
     * instance matter.  But we'll keep the Usages separate as I imagine the
     * maven-publish plugin will eventually get support for different scopes
     * that may someday make use of these separate Usages.  Instead, see the
     * MavenPublishPomFixer for how we support scopes other than the runtime
     * scope.
     */
    private final LinkedHashSet<Usage> usages = new LinkedHashSet<Usage>()
    private final LinkedHashSet<Dependency> addedDependencies = new LinkedHashSet<Dependency>()

    public GrailsLibrary(ConfigurationContainer configurations) {
        ["compile", "runtime", "provided"].each { String scope ->
            usages.add(createUsage(scope, configurations.getByName(scope).allDependencies))
        }
    }

    private Usage createUsage(final String name, final DependencySet dependencies) {
        final Collection<Dependency> notAddedYet = dependencies - addedDependencies
        addedDependencies.addAll(notAddedYet)
        return new Usage() {
            String getName() { return name }

            public Set<PublishArtifact> getArtifacts() { [] }

            public Set<Dependency> getDependencies() { notAddedYet }
        }
    }

    @Override
    String getName() {
        return "grails"
    }

    @Override
    Set<Usage> getUsages() {
        return usages
    }
}
