#!groovy
@Library('jenkinslib@master') _

def k8s = new org.devops.kubernetes()
def gitlab = new org.devops.gitlab()

// String updateType = "${env.updateType}"
// String releaseVersion = "${env.releaseVersion}"

pipeline{
    agent { node { label "master" }}

    parameters {
        choice(name: 'releaseVersion', choices: '1.0\n1.1\n1.2\n1.3', description: 'Please chose your versionName')
        choice(name: 'updateType', choices: 'UAT -> STAGE\nSTAGE -> PROD', description: 'Please chose your updateType')
    }

    stages{
        stage("UAT->STAGE"){
            when {
                environment name: 'updateType', value: 'UAT -> STAGE' 
            }
            
            steps {
                script{
                    println("UAT -> STAGE")
                
                   //获取UAT文件中的images
                    response = gitlab.GetRepoFile(3,"demo-uat%2f${releaseVersion}-uat.yaml")
                    fileData = readYaml text: """${response}"""
                    uatImage = fileData["spec"]["template"]["spec"]["containers"][0]["image"]
                    println("UAT IMAGES --> ${uatImage}")

                   //获取最新STAG环境的deployment
                    stagResponse = k8s.GetDeployment("demo-stage","demoapp")
                    stagResponse = stagResponse.content
                    
                    //获取镜像和version
                    stagfileData = readYaml text: """${stagResponse}"""
                    stagOldImage = stagfileData["spec"]["template"]["spec"]["containers"][0]["image"]
                    stagOldVersion = stagfileData["metadata"]["resourceVersion"]
                    
                    //更新镜像和version
                    println("STAG OLD IMAGES --> ${stagOldImage}")
                    stagResponse = stagResponse.replace(stagOldImage,uatImage)
                    stagResponse = stagResponse.replace(stagOldVersion,"")

                    //生成最新的STAGE版本文件
                    //文件转换
                    base64Content = stagResponse.bytes.encodeBase64().toString()
                    //上传文件
                    try {
                        gitlab.CreateRepoFile(3,"demo-stage%2f${releaseVersion}-stage.yaml",base64Content)
                    } catch(e){
                        gitlab.UpdateRepoFile(3,"demo-stage%2f${releaseVersion}-stage.yaml",base64Content)
                    }

                }
            }
        }
    
    
    
    stage("STAG->PROD"){
            when {
                environment name: 'updateType', value: 'STAGE -> PROD' 
            }
            
            steps {
                script{
                    println("STAGE -> PROD")
                   
                   //获取STAG文件中的images
                    response = gitlab.GetRepoFile(3,"demo-stage%2f${releaseVersion}-stage.yaml")
                    fileData = readYaml text: """${response}"""
                    stagImage = fileData["spec"]["template"]["spec"]["containers"][0]["image"]
                    println("STAGE IMAGES --> ${stagImage}")

                   //获取最新PROD环境的deployment
                    prodResponse = k8s.GetDeployment("demo-prod","demoapp")
                    prodResponse = prodResponse.content
                    
                    //获取镜像和version
                    prodfileData = readYaml text: """${prodResponse}"""
                    prodOldImage = prodfileData["spec"]["template"]["spec"]["containers"][0]["image"]
                    prodOldVersion = prodfileData["metadata"]["resourceVersion"]
                    
                    //更新镜像和version
                    println("PROD OLD IMAGES --> ${prodOldImage}")
                    prodResponse = prodResponse.replace(prodOldImage,stagImage)
                    prodResponse = prodResponse.replace(prodOldVersion,"")

                    //生成最新的PROD版本文件
                    //文件转换
                    base64Content = prodResponse.bytes.encodeBase64().toString()
                    //上传文件
                    try {
                        gitlab.CreateRepoFile(3,"demo-prod%2f${releaseVersion}-prod.yaml",base64Content)
                    } catch(e){
                        gitlab.UpdateRepoFile(3,"demo-prod%2f${releaseVersion}-prod.yaml",base64Content)
                    }

                }
            }
        }
    }
}