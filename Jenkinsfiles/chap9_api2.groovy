#!groovy
@Library('jenkinslib@master') _

def nexus = new org.devops.nexus()
def nexusapi = new org.devops.nexusapi()


pipeline{
    agent { node { label "hostmachine" }}
    parameters {  
         string(name: 'pkgVersion', defaultValue: "1.1-20200813.190212-1", description: '') 
    }

    stages{
        stage("GetSingleComponents"){
            steps{
                script{
                    pkgVersion = "${env.pkgVersion}"
                    nexusapi.GetRepoComponents("maven-hosted")
                    nexusapi.GetSingleComponents("maven-hosted","com.mycompany.app","my-app",pkgVersion)
                    
                }
            }
        }
    }
}
