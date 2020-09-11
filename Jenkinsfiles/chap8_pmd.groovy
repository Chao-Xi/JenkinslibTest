pipeline { 
    agent { node { label "hostmachine" }}
    stages { 
        stage('pmd') { 
            steps { 
                script{
                mvnHome = tool "m2"
                sh "${mvnHome}/bin/mvn pmd:pmd"
                }
            } 
        } 
    } 
    post { 
        always{ 
            pmd(canRunOnFailed: true, pattern: '**/target/pmd.xml') 
        } 
    } 
} 
