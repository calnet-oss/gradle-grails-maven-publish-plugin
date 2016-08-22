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
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.publish.maven.MavenPom

// @formatter:off
/**
 * Fixes the scope in maven-publish pom objects.
 *
 * Usage:
 * <pre>{@code
 * publishing {
 *   publications {
 *       maven(MavenPublication) {
 *           grailsMavenPublish.addGrailsComponent(project)
 *           from components.grails
 *           grailsMavenPublish.fixPom(configurations, pom)
 *       }
 *   }
 * }
 * }</pre>
 */
// @formatter:on
class MavenPublishPomFixer {
    ConfigurationContainer configurations
    MavenPom pom

    void fixPom() {
        pom.withXml {
            dependenciesParent((NodeList) asNode().get('dependencies'))
        }
        pom.withXml { println it }
    }

    private void dependenciesParent(NodeList dependenciesParentList) {
        if (dependenciesParentList.size() > 0) {
            dependencies(dependenciesParentList.get(0).children())
        }
    }

    private void dependencies(NodeList dependenciesList) {
        dependenciesList.each { Node dependency ->
            String groupId = dependency.get('groupId').text()
            String artifactId = dependency.get('artifactId').text()
            String version = dependency.get('version').text()
            assert groupId
            assert artifactId

            String shouldBeScope = getScopeForDependency(groupId, artifactId, version)
            if (shouldBeScope) {
                // replace the Pom scope with what it should actually be
                if (!dependency.get('scope')) {
                    dependency.appendNode("scope", shouldBeScope)
                } else {
                    def scope = dependency.get('scope').get(0)
                    scope.value = shouldBeScope
                }
            }

            ResolvedArtifact ra = getResolvedArtifact(groupId, artifactId, version)
            if (ra) {
                // set the Pom type
                if (!dependency.get("type")) {
                    dependency.appendNode("type", ra.type)
                } else {
                    def type = dependency.get('type').get(0)
                    type.value = ra.type
                }
            }
        }
    }

    private DependencySet getDependencies(String scope) {
        return configurations.getByName(scope)?.allDependencies
    }

    private ResolvedArtifact getResolvedArtifact(String groupId, String artifactId, String version) {
        for (String scope in ["compile", "runtime", "provided"]) {
            ResolvedArtifact resolvedArtifact = configurations.getByName(scope).resolvedConfiguration.resolvedArtifacts.find { ResolvedArtifact ra ->
                // version is optional
                ra.moduleVersion.id.group == groupId &&
                        ra.moduleVersion.id.name == artifactId &&
                        (version ? ra.moduleVersion.id.version == version : true)
            }
            if (resolvedArtifact) {
                return resolvedArtifact
            }
        }
        return null
    }

    private String getScopeForDependency(String groupId, String artifactId, String version) {
        for (String scope in ["compile", "runtime", "provided"]) {
            Dependency dependencyObject = getDependencies(scope).find { Dependency d ->
                // version is optional
                d.group == groupId && d.name == artifactId && (version ? d.version == version : true)
            }
            if (dependencyObject) {
                return scope
            }
        }
        return null
    }
}
