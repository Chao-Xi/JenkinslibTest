package org.showme

class Utils implements Serializable { 
	def getVersion(String BUILD_NUMBER ,String GIT_COMMIT){ 
		return new Date().format( 'yyMM' ) + "-${BUILD_NUMBER}" + "-${GIT_COMMIT}" 
		}
}