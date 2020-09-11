#!groovy
@Library('jenkinslib@master') _

def k8s = new org.devops.kubernetes()
def gitlab = new org.devops.gitlab()

pipeline {
    agent { node { label "master" }}
    stages{
        stage("GetDeployment"){
            steps{
                script{
                    response = k8s.GetDeployment("demo-prod","demoapp")
                    response = response.content

                    //文件转换
                    base64Content = response.bytes.encodeBase64().toString()

                    //上传文件
                    gitlab.CreateRepoFile(3,"demo-prod%2f{versionName}-prod.yaml",base64Content)

                }  
            }
        }
    }
}