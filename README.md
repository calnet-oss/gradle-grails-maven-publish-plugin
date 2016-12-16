This is a Gradle plugin that is meant to be used in conjunction with the
`grails` and `maven-publish` Gradle plugins.  It adds support for Grails
publishing using `maven-publish`.

This plugin does three things:
* It allows the usage of `from components.grails`, which tells the
  `maven-publish` plugin which dependencies should be listed in the
  generated Maven POM.  The `compile`, `runtime` and `provided` dependencies
  are placed in the component for the POM.
* It allows the usage of 'from components.jar' for building standard class
  and source JAR files.
* It sets the proper `scope` and `type` values in the generated Maven POM.

Notes: 
* We use this plugin with version `2.2.0.RC1` of the `grails-gradle-plugin`
  and Grails 2.5.  Usage with Grails 3 is untested.  We also tend to use
  this plugin for deploying Grails plugins (not applications) to Maven.
* As of this writing, we use this plugin with Gradle 3.2.1.  Future
  versions of `maven-publish` may change or offer more features that aid in
  how dependencies are calculated for POM creation.

## License

License is [BSD two-clause](LICENSE.txt).

## Plugin usage

```
buildscript {
    dependencies {
        classpath "org.grails:grails-gradle-plugin:2.2.0.RC1"
        classpath "edu.berkeley.calnet.gradle.plugins:gradle-grails-maven-publish-plugin:[latestVersion]"
    }
}
apply plugin: 'grails'
apply plugin: 'edu.berkeley.calnet.grails-maven-publish'
apply plugin: 'maven-publish'

grailsMavenPublish.addJavaComponent(project)

jar {
    metaInf {
        from "plugin.xml"
        rename "plugin.xml", "grails-plugin.xml"
    }
}

task sourceJar(type: Jar) {
    from "src/java"
    from "src/groovy"
    from "scripts"
    metaInf {
        from "plugin.xml"
        rename "plugin.xml", "grails-plugin.xml"
    }
}

// publishing with './gradlew publish'
publish.dependsOn assemble
publishToMavenLocal.dependsOn assemble
publishing {
    publications {
        // zip file
        mavenGrails(MavenPublication) {
            // add components.grails
            grailsMavenPublish.addGrailsComponent(project)
            from components.grails
            // fix the pom scopes
            grailsMavenPublish.fixPom(configurations, pom)
        }
        // jar and sources
        mavenJava(MavenPublication) {
            from components.java
            grailsMavenPublish.fixPom(configurations, pom)
            artifact sourceJar { classifier "sources" }
        }
    }
}
```
