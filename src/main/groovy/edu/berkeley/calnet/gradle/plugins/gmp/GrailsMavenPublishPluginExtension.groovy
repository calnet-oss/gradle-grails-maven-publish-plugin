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

import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.publish.maven.MavenPom

// @formatter:off
/**
 * Plugin usage:
 *
 * <pre>{@code
 * buildscript {
 *     dependencies {
 *         classpath "org.grails:grails-gradle-plugin:2.2.0.RC1"
 *         classpath "edu.berkeley.calnet.gradle.plugins:gradle-grails-maven-publish-plugin:[latestVersion]"
 *     }
 * }
 * apply plugin: 'grails'
 * apply plugin: 'edu.berkeley.calnet.grails-maven-publish'
 * apply plugin: 'maven-publish'
 *
 * grailsMavenPublish.addJavaComponent(project)
 *
 * publish.dependsOn assemble
 * publishToMavenLocal.dependsOn assemble
 * publishing {
 *     publications {
 *         mavenGrails(MavenPublication) {
 *             grailsMavenPublish.addGrailsComponent(project)
 *             from components.grails
 *             grailsMavenPublish.fixPom(configurations, pom)
 *         }
 *         mavenJava(MavenPublication) {
 *             from components.java
 *         }
 *     }
 * }
 * }</pre>
 */
// @formatter:on
class GrailsMavenPublishPluginExtension {
    void addGrailsComponent(Project project) {
        project.components.add(new GrailsLibrary(project.name, project.configurations))
    }

    void addJavaComponent(Project project) {
        JavaLibraryAdder.addJavaLibrary(project)
    }

    void fixPom(ConfigurationContainer configurations, MavenPom pom) {
        new MavenPublishPomFixer(configurations: configurations, pom: pom).fixPom()
    }
}
