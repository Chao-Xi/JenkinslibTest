#!groovy

@Library('jenkinslib@master') _

def k8s = new org.devops.kubernetes()
def gitlab = new org.devops.gitlab()
def build = new org.devops.buildtools()

pipeline {
    agent { node { label "vagrant-agent" }}

    parameters {
        string(name: 'srcUrl', defaultValue: 'http://192.168.33.1:30088/root/demo-npm-service.git', description: '') 
        choice(name: 'branchName', choices: 'master\nstage\ndev', description: 'Please chose your branch')
        // choice(name: 'buildType', choices: 'npm', description: 'build tool')
        // string(name: 'buildShell', defaultValue: 'install && npm run build', description: 'build tool')
    }

    stages{
        stage('Checkout') {
	        steps {
	        	script {
	            	checkout([$class: 'GitSCM', branches: [[name: "${branchName}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'gitlab-admin-user', url: "${srcUrl}"]]])
	            } 
	        }
	    }

        stage("Build&Test"){
            steps{
                script{
                    println("执行打包")
                    sh "cd demo-npm-service && npm install  --unsafe-perm=true && npm run build  && ls -l dist/"
                }
            }
        }

         //构建镜像
        stage("BuildImages"){
            steps{
                script{
                    println("构建上传镜像")
                    // env.serviceName = "${JOB_NAME}".split("_")[0]
                    env.serviceName = "${JOB_NAME}"

                    withCredentials([usernamePassword(credentialsId: 'docker-registry-admin', passwordVariable: 'password', usernameVariable: 'username')]) 
                    {
                        
                        env.dockerImage = "nyjxi/${serviceName}:${branchName}"
                        sh """
                            docker login -u ${username} -p ${password} 
                            docker build -t nyjxi/${serviceName}:${branchName} .
                            sleep 1
                            docker push nyjxi/${serviceName}:${branchName}
                            sleep 1
                            docker rmi nyjxi/${serviceName}:${branchName}
                        """
                }

            }
        }
    }   

        stage('Checkout-For-Master') {
            agent { node { label "master" }}
	        steps {
	        	script {
	            	checkout([$class: 'GitSCM', branches: [[name: "${branchName}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'gitlab-admin-user', url: "${srcUrl}"]]])
	            } 
	        }
	    }

        //发布
        stage("Deploy"){
            agent { node { label "master" }}

            steps{
                script{
                    println("发布应用")
                    
                    //获取旧镜像
                    yamlData = readYaml file: "demo-npm.yaml"
                    
                    println(yamlData[0])
                    println(yamlData[0]["spec"]["template"]["spec"]["containers"][0]["image"])
                    
                    oldImage = yamlData[0]["spec"]["template"]["spec"]["containers"][0]["image"]
                    
                    //替换镜像
                    sourceData = readFile file: 'demo-npm.yaml'
                    println(sourceData)
                    println(sourceData.getClass()) //returns the exact type of an object.
                    sourceData = sourceData.replace(oldImage,dockerImage)
                    println(sourceData)
                    
                    writeFile file: 'demo-npm.yaml', text: """${sourceData}"""
                    
                
                    sh """
                        #cat demo-npm.yaml
                        kubectl apply -f demo-npm.yaml
                    """
                    
                }
            }
        }

    }   
}