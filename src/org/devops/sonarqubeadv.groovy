package org.devops


//scan
def SonarScan(projectName,projectDesc,projectPath){
        
    withSonarQubeEnv("sonarqube"){
        def scannerHome = "/usr/local/sonar-scanner/"
        def sonarServers = "http://192.168.33.1:32314"
        def sonarDate = sh  returnStdout: true, script: 'date  +%Y%m%d%H%M%S'
        sonarDate = sonarDate - "\n"
    

        sh """ 
            ${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=${projectName} \
            -Dsonar.projectName=${projectName} \
            --define sonar.projectVersion=${sonarDate} \
            --define sonar.ws.timeout=30 \
            --define sonar.projectDescription=${projectDesc} \
            --define sonar.links.homepage=http://www.baidu.com \
            --define sonar.sources=${projectPath} \
            --define sonar.sourceEncoding=UTF-8 \
            --define sonar.java.binaries=target/classes \
            --define sonar.java.test.binaries=target/test-classes \
            --define sonar.java.surefire.report=target/surefire-reports
        """
    }
    
    //def qg = waitForQualityGate()
    //if (qg.status != 'OK') {
        //error "Pipeline aborted due to quality gate failure: ${qg.status}"
    //}
}
