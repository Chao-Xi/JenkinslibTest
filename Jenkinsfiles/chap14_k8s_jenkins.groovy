#!groovy
@Library('jenkinslib@master') _

def k8s = new org.devops.kubernetes()
def gitlab = new org.devops.gitlab()
def build = new org.devops.buildtools()


pipeline {
    agent { node { label "vagrant-agent" }}
    parameters {
        string(name: 'srcUrl', defaultValue: 'http://192.168.33.1:30088/root/demo-maven-service.git', description: '') 
        choice(name: 'versionName', choices: '1.0\n1.1\n1.2\n1.3', description: 'Please chose your versionName')
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

        // 构建镜像
           stage("BuildImages"){
                steps{
                    script{
                        env.serviceName = "${JOB_NAME}".split("_")[0]
                       
                        withCredentials([usernamePassword(credentialsId: 'docker-registry-admin', passwordVariable: 'password', usernameVariable: 'username')]) {
                           
                           env.dockerImage = "nyjxi/${serviceName}:${branchName}"
                           sh """
                               docker login -u ${username} -p ${password} 
                               docker build -t nyjxi/${serviceName}:${branchName} .
                               sleep 1
                               docker push nyjxi/${serviceName}:${branchName}
                               sleep 1
                               #docker rmi nyjxi/devopstest/${serviceName}:${branchName}
                            """
                        }
                    }
                }
            }
        
        stage("GetDeployment"){
            agent { node { label "master" }}
            steps{
                script{
                    response = k8s.GetDeployment("demo-prod","demoapp")
                    response = response.content

                    //文件转换
                    base64Content = response.bytes.encodeBase64().toString()

                    //上传文件
                    // gitlab.CreateRepoFile(3,"demo-prod%2f${versionName}-prod.yaml",base64Content)

                }  
            }
        }


    }
}