pipeline { 
	agent any 
	environment {
		CC = 'clang'
	} 
 stages { 
	 	stage('Example') { 
	 		environment {
	 			DEBUG_FLAGS = '-g'
	 		}
	 		steps { 
	 			echo "${CC} ${DEBUG_FLAGS}" 
	 			sh 'printenv' 
	 		} 
		} 
	} 
} 