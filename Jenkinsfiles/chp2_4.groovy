pipeline{
    agent any
    environment {
        _version = createVersion(BUILD_NUMBER)
    }
    stages {
        stage('Build'){
            steps {
                echo "${_version}"
            }
        }
    }
}

def createVersion(String BUILD_NUMBER) {
	return new Date().format( 'yyMM' ) + "-${BUILD_NUMBER}"
}