This is a Gradle plugin that is meant to be used in conjunction with the
`grails` and `maven-publish` Gradle plugins.  It adds support for Grails
publishing using `maven-publish`.

This plugin does two things:
* It allows the usage of `from components.grails`, which tells the
  `maven-publish` plugin which dependencies should be listed in the
  generated Maven POM.  The `compile`, `runtime` and `provided` dependencies
  are placed in the component for the POM.
* It sets the proper `scope` and `type` values in the generated Maven POM.

Notes: 
* We use this plugin with version `2.2.0.RC1` of the `grails-gradle-plugin`
  and Grails 2.5.  Usage with Grails 3 is untested.  We also tend to use
  this plugin for deploying Grails plugins (not applications) to Maven.
* As of this writing, we use this plugin with Gradle 2.14.1.  Future
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

publish.dependsOn assemble
publishToMavenLocal.dependsOn assemble
publishing {
    publications {
        maven(MavenPublication) {
            // add components.grails
            grailsMavenPublish.addGrailsComponent(project)
            from components.grails
            // fix the pom scopes
            grailsMavenPublish.fixPom(configurations, pom)
        }
    }
}
```

## Known issues

* For Grails plugins, Grails core dependencies (and the groovy-all
  dependency) will appear in the POM file.  `grails-gradle-plugin` adds
  these core Grails dependencies.  This shouldn't be much of a problem
  unless you're trying to use a Grails plugin configured for a later version
  of Grails than your Grails application is actually using.
