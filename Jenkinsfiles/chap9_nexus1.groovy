#!groovy
@Library('jenkinslib@master') _

def build = new org.devops.buildtools()
def sonar = new org.devops.sonarqube()

pipeline {
    agent { node { label "hostmachine" }}
    parameters {
        string(name: 'srcUrl', defaultValue: 'http://192.168.33.1:30088/root/demo-maven-service.git', description: '') 
        choice(name: 'branchName', choices: 'master\nstage\ndev', description: 'Please chose your branch')
        choice(name: 'buildType', choices: 'mvn', description: 'build tool')
        choice(name: 'buildShell', choices: 'clean package -DskipTest\n--version', description: 'build tool')
    }
    stages{
        stage('Checkout') {
            steps {
                script {
                    checkout([$class: 'GitSCM', branches: [[name: "${branchName}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'gitlab-admin-user', url: "${srcUrl}"]]])
                } 
            }
        }

        stage('Build') {
            steps {
                script {
                    build.Build(buildType,buildShell)

                    def jarName = sh returnStdout: true, script: "cd target; ls *.jar"
                    jarName = jarName - "\n"

                    def pom =readMavenPom file: 'pom.xml'
                    pomVersion = "${pom.version}"
                    pomArtifact = "${pom.artifactId}"
                    pomPackaging = "${pom.packaging}"
                    pomGroupId = "${pom.groupId}"

                    println("${pomGroupId}-${pomArtifact}-${pomVersion}-${pomPackaging}")

                    def mvnHome = tool "m2"
                    sh """
                        cd target/
                        ${mvnHome}/bin/mvn deploy:deploy-file -Dmaven.test.skip=true -Dfile=${jarName} -DgroupId=${pomGroupId} -DartifactId=${pomArtifact} -Dversion=${pomVersion} -Dpackaging=${pomPackaging} -DrepositoryId=maven-releases -Durl=http://192.168.33.1:32000/repository/maven-releases/
                    """
                } 
            }
        }
    }
 }

