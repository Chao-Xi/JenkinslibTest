#!groovy
@Library('jenkinslib@master') _

def build = new org.devops.buildtools()
def sonar = new org.devops.sonarqube()
def sonaradv = new org.devops.sonarqubeadv()
def sonarapi = new org.devops.sonarapi()

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
	            } 
	        }
	    }

        stage('Qa') {
	        steps {
	        	script {
                    
                    result = sonarapi.SearchProject("${JOB_NAME}")
                    println(result)

                    if(result == "false"){
                       println("${JOB_NAME} ---- The project doesn't exist ----- ${JOB_NAME}!") 
                       sonarapi.CreateProject("${JOB_NAME}")
                    } else {
                       println("${JOB_NAME} ---- The project already exist!")
                    }

                    qpName = "${JOB_NAME}".split("-")[0]
                    sonarapi.ConfigQualityProfiles("${JOB_NAME}","java",qpName)

                    sonarapi.ConfigQualityGates("${JOB_NAME}",qpName)
                    
	            	sonaradv.SonarScan("${JOB_NAME}","${JOB_NAME}","src" )

                    result = sonarapi.GetProjectStatus("${JOB_NAME}")
                    println(result)

                    if(result.toString().contains("ERROR")){
                        error "Quality gate failed, please re-check ur code!"
                    }else{
                        println(result)
                    }
	            } 
	        }
	    }    
    }
 }