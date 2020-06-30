package org.devops

// build tools
def Build(buildType, buildShell){
    def buildTools = ["mvn":"m2", "ant":"ANT","gradle":"GRADLE","npm":"NPM"]

    println("The current build tool is ${buildType}")
    buildHome = tool buildTools[buildType]

    sh "${buildHome}/bin/${buildType} ${buildShell}"
}
