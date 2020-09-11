pipeline{
    agent { node { label "master"}}

    stages{
        stage('test'){
            steps{
                script{
                    httpRequest authentication: 'api-token',
                            httpMode: 'POST',
                            responseHandle: 'NONE',
                            url: 'http://127.0.0.1:30080/job/test-devops-service/disable'
                }
            }
        }
    }
}