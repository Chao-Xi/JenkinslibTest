#!groovy
@Library('jenkinslib@master') _

def build = new org.devops.buildtools()

pipeline {
 	agent { node { label "hostmachine" }}
 	parameters {
        string(name: 'srcUrl', defaultValue: 'http://192.168.33.1:30088/root/demo-interfacetest-service.git', description: '') 
        choice(name: 'branchName', choices: 'master\nstage', description: 'Please chose your branch')
        choice(name: 'buildType', choices: 'ant', description: 'build tool')
        choice(name: 'buildShell', choices: '-f build.xml ', description: 'build tool')
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

                     //展示测试报告
                    publishHTML([allowMissing: false, 
                                 alwaysLinkToLastBuild: false, 
                                 keepAll: false, 
                                 reportDir: 'result/htmlfile', 
                                 reportFiles: 'SummaryReport.html,DetailReport.html', 
                                 reportName: 'InterfaceTestReport', 
                                 reportTitles: ''])
	            } 
	        }
	    }
    }
    post {
        always{
            script{
                println("always")
            }
        }
        
        success{
            script{
                println("success")  
            }
        
        }
        failure{
            script{
                println("failure")
            }
        }
        
        aborted{
            script{
                println("aborted")
            }
        
        }
    }
 }

