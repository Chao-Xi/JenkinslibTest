pipeline {
    agent { label 'hostmachine'}
    stages {
        stage('Example') {
            steps {
                echo 'Hello World'

                script {
                    def browsers = ['chrome', 'firefox']
                    for (int i = 0; i < browsers.size(); ++i) {
                        echo "Testing the ${browsers[i]} browser"
                    }
                
                }
            }
        }
        
        stage('Docker') {
            steps { 
	            script{ 
		            def t = tool name: 'docker', type: 'org.jenkinsci.plugins.docker.commons.tools.DockerTool'
		            echo "${t}" // 打印 /var/lib/docker 
	            } 
            }
        }
    }
}