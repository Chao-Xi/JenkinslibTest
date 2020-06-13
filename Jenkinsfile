#!groovy

@Library('jenkinslib') _
def tools = new org.devops.tools()


String workspace = "/home/vagrant/workspace" 

//Pipeline 
pipeline {
	agent { node { label "hostmachine"   //指定运行节点的标签或者名称
					 customWorkspace "${workspace}" //指定运行工作目录（可选）
			}
	}

	options { 
		timestamps() 	//日志会有时间
		skipDefaultCheckout()  //删除隐式checkout scm 语句
		disableConcurrentBuilds()  //静止并行
		timeout(time: 1, unit: 'HOURS')  //流水线超时设置1h
	}

	stages { 
	//下载代码
		stage("GetCode"){   //阶段名称
			steps{   //名称
				timeout(time:5, unit:"MINUTES"){   //步骤超时时间
					ansiColor('xterm') {
 						echo 'something that outputs ansi colored stuff'
					}
					script{	  //填写运行代码
						println('获取代码') 
						println("\033[31m Red \033[0m") 
						tools.PrintMes("获取代码",'blue')
						

					} 
				} 
			} 
		}
		
	//代码扫描
		stage("CodeScan"){
			steps{ 
				timeout(time:30, unit:"MINUTES"){ 
					script{ 
						print('代码扫描')
						tools.PrintMes("代码扫描",'red')
					} 
				}
			}
		}			
		
	   //构建
		stage("Build"){ 
			steps{
				timeout(time:20, unit:"MINUTES"){ 
					script{ 
						println('应用打包')
						tools.PrintMes("应用打包",'green')
						
						mvnHome = tool "m2"
                        println(mvnHome)
                        
                        sh "${mvnHome}/bin/mvn --version"
					}
				} 
			}
		}

		
	} 

	//构建后操作
	post {
		always { 
			script{ 
				println("always") 
			} 
		} 

		success {
			script{
				currentBuild.description += "\n 构建成功!" 
			} 
		}

		failure {
			script{ 
				currentBuild.description += "\n 构建失败!" 
			} 
		}

		aborted { 
			script{ 
				currentBuild.description += "\n 构建取消!" 
			} 
		} 
	} 
}
