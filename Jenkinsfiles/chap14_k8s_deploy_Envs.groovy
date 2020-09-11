#!groovy

@Library('jenkinslib@master') _

// String stackName = "${env.stackName}"
// String releaseVersion = "${env.releaseVersion}"

def gitlab = new org.devops.gitlab()
def k8s = new org.devops.kubernetes()

pipeline{
   agent { node { label "master" }}

   parameters {
        choice(name: 'stackName', choices: '\nUAT\nSTAGE\nPROD', description: 'Please chose your stackName')
        choice(name: 'releaseVersion', choices: '1.0\n1.1\n1.2\n1.3', description: 'Please chose your versionName')
    }
   
   stages{
   
       stage("Deploy"){
            steps{
                script{
                
                    //获取版本文件
                    stack = "${stackName}".toLowerCase()
                    response = gitlab.GetRepoFile(3,"demo-${stack}%2f${releaseVersion}-${stack}.yaml")
                    //发布应用
                    k8s.UpdateDeployment("demo-${stack}","demoapp",response)
 
                }  
           }
       }
   }
}