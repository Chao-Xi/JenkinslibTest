#!groovy
@Library('jenkinslib@master') _

def build = new org.devops.buildtools()

pipeline {
 	agent { node { label "hostmachine" }}
 	parameters {
        string(name: 'srcUrl', defaultValue: 'http://192.168.33.1:30088/root/demo-maven-service.git', description: '') 
        choice(name: 'branchName', choices: 'jacoco\nmaster\ndev\npmd', description: 'Please chose your branch')
        choice(name: 'buildType', choices: 'mvn', description: 'build tool')
        choice(name: 'buildShell', choices: 'clean package -DskipTest\n--version', description: 'build tool')
	}

 	stages{
        stage('Checkout') {
	        steps {
	        	script {
	            	checkout([$class: 'GitSCM', branches: [[name: "${branchName}"]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'gitlab-admin-user', url: "${srcUrl}"]]])
	            } 
	        }
	    }
        
		stage('Build') {
	        steps {
                sh "/opt/maven/bin/mvn ${buildShell}"
                jacoco(
                    // 代码覆盖率统计文件位置，Ant 风格路径表达式
                    execPattern: 'target/**/*.exec',
                    // classes 文件位置，Ant 风格路径表达式
                    classPattern: 'target/classes',
                    //源码文件位置，Ant 风格路径表达式
                    sourcePattern: 'src/main/java',
                    //排除分析的位置，Ant 风格路径表达式
                    exclusionPattern: 'src/test*',
                    //是否禁用每行覆盖率的源文件显示
                    skipCopyOfSrcFiles: false,
                    // 如果为 true，则对各维度的覆盖率进行比较。如果任何一个维度的当前覆盖率小于最小覆盖率阈值，则构建状态为失败;
                    // 如果当前覆盖率在最大阈值和最小阈值之间，则当前构建状态为不稳定；
                    // 如果当前覆盖率大于最大阈值，则构建成功；
                    changeBuildStatus: true,
                    // 字节码指令覆盖率
                    minimumInstructionCoverage: '30', maximumInstructionCoverage: '70',
                    // 行覆盖率
                    minimumLineCoverage: '30', maximumLineCoverage: '70',
                    // 圈复杂度覆盖率
                    minimumComplexityCoverage: '30', maximumComplexityCoverage: '70',
                    // 方法覆盖率
                    minimumMethodCoverage: '30', maximumMethodCoverage: '70',
                    // 类覆盖率
                    minimumClassCoverage: '30', maximumClassCoverage: '70',
                    // 分支覆盖率
                    minimumBranchCoverage: '30', maximumBranchCoverage: '70',
                    // 如果为 true, 则只有所有维度的覆盖率变化量的绝对值小于相应的变化量阈值时，构建结果才为成功
                    buildOverBuild: true,
                    // 以下是各个维度覆盖率的变化量阈值
                    deltaInstructionCoverage: '80', deltaLineCoverage: '80',
                    deltaMethodCoverage: '80', deltaClassCoverage: '80',
                    deltaComplexityCoverage: '80', deltaBranchCoverage: '80'
                )
            }
	    }
    }
 }