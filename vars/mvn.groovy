//var/mvn.groovy

def call(mvnExec) { 
	configFileProvider([configFile(fileId: 'maven-global-settings', variable: 'MAVEN_GLOBAL_ENV')]) { 
	mvnExec("${MAVEN_GLOBAI_ENV}")
	}
} 