package org.devops

//封装HTTP请求
def HttpReq(reqType,reqUrl,reqBody){
    def jenkinsServer = 'http://127.0.0.1:30080'
    result = httpRequest authentication: 'api-token',
                        httpMode: reqType,
                        consoleLogResponseBody: true,
                        ignoreSslErrors: true, 
                        requestBody: reqBody,
                        url: "${jenkinsServer}/${reqUrl}"
                        //quiet: true
                        
}


//新建项目

def CreateProject(projectName){
    
    withCredentials([usernamePassword(credentialsId: 'api-token', passwordVariable: 'password', usernameVariable: 'username')]) {
       
        sh """
        
           curl -u ${username}:${password} -X GET 'http://127.0.0.10:30080/job/demo-project-manage/config.xml' -o config.xml
           ls -l 
       
           curl -u ${username}:${password} -X POST 'http://127.0.0.1:30080/createItem?name=${projectName}' -H 'Content-Type:text/xml' --data-binary @config.xml
    
    
        """
    }
}


//禁用项目

def Project(projectName,option){

    println(projectName)
    println(option)
    
    options = [ "DisableProject": "disable",
                "EnableProject":"enable",
                "DeleteProject":"doDelete",
                "BuildProject":"build"]
    
    result = HttpReq('POST',"job/${projectName}/${options[option]}",'')
    
}