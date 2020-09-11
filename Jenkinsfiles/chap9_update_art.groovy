#!groovy
@Library('jenkinslib@master') _

def nexus = new org.devops.nexus()

String artifactUrl = "${env.artifactUrl}"

pipeline{
    agent { node { label "hostmachine" }}
    parameters {  
        choice(name: 'updateType', choices: 'snapshot -> release\n', description: 'update type')
    }

    stages{
        stage("UpdateArtifact"){
            steps{
                script{
                    println(artifactUrl)
                    updateType = "${env.updateType}"
                    println(updateType)
                    nexus.ArtifactUpdate(updateType,artifactUrl)
                }
            }
        }
    }
}
