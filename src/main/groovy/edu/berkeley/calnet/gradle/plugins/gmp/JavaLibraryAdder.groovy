/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This code derived from the Gradle project's JavaPlugin class and the
 * above license is duplicated from source code of that class as of Gradle
 * v3.2.1.
 */
package edu.berkeley.calnet.gradle.plugins.gmp

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.plugins.DefaultArtifactPublicationSet
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.internal.java.JavaLibrary
import org.gradle.api.tasks.SourceSet

abstract class JavaLibraryAdder {
    // see Gradle JavaPlugin.configureArchivesAndComponent() (v3.2.1)
    static void addJavaLibrary(final Project project) {
        JavaPluginConvention pluginConvention = project.getConvention().getPlugin(JavaPluginConvention.class)
        
        Jar jar = project.getTasks().create(JavaPlugin.JAR_TASK_NAME, Jar.class)
        jar.setDescription("Assembles a jar archive containing the main classes.")
        jar.from(pluginConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getOutput())
        
        ArchivePublishArtifact jarArtifact = new ArchivePublishArtifact(jar)
        Configuration runtimeConfiguration = project.getConfigurations().getByName(JavaPlugin.RUNTIME_CONFIGURATION_NAME)
        
        runtimeConfiguration.getArtifacts().add(jarArtifact)
        project.getExtensions().getByType(DefaultArtifactPublicationSet.class)addCandidate(jarArtifact)
        project.getComponents().add(new JavaLibrary(jarArtifact, runtimeConfiguration.getAllDependencies()))
    }    
}
