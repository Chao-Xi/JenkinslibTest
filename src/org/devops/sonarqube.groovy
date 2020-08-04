package org.devops


//scan
def SonarScan(projectName,projectDesc,projectPath){
    def scannerHome = "/usr/local/sonar-scanner/"
    //定义服务器列表
    def sonarServers = "http://192.168.33.1:32314"
    def sonarDate = sh  returnStdout: true, script: 'date  +%Y%m%d%H%M%S'
    sonarDate = sonarDate - "\n"
    
    sh """ 
         ${scannerHome}/bin/sonar-scanner -Dsonar.host.url=${sonarServers} \
        -Dsonar.projectKey=${projectName} \
        -Dsonar.projectName=${projectName} \
        --define sonar.login=admin \
        --define sonar.password=admin \
        --define sonar.projectVersion=${sonarDate} \
        --define sonar.ws.timeout=30 \
        --define sonar.projectDescription=${projectDesc} \
        --define sonar.links.homepage=http://www.baidu.com  \
        --define sonar.sources=${projectPath} \
        --define sonar.sourceEncoding=UTF-8 \
        --define sonar.java.binaries=target/classes \
        --define sonar.java.test.binaries=target/test—classes \
        --define sonar.java.surefire.report=target/surefire—reports
    """
    }
}