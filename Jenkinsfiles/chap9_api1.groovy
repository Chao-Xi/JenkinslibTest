#!groovy
@Library('jenkinslib@master') _

def nexus = new org.devops.nexus()
def nexusapi = new org.devops.nexusapi()

String artifactUrl = "${env.artifactUrl}"

pipeline{
    agent { node { label "hostmachine" }}
    parameters {  
        choice(name: 'updateType', choices: 'snapshot -> release\n', description: 'update type')
    }

    stages{
        stage("GetRepoComponents"){
            steps{
                script{
                    nexusapi.GetRepoComponents("maven-hosted")
                }
            }
        }
    }
}
