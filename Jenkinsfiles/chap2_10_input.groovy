def approvalMap 

pipeline { 
    agent any 
    stages { 
        stage('pre deploy') { 
            steps { 
                script { 
                    approvalMap = input( 
                        message: 'Ready to deploy to which env?' ,
                        ok: 'ok', 
                        parameters: [ 
                            choice(choices: 'dev\ntest\nprod', description: 'Deploy to which Env? ', name: 'ENV'),
                            string(defaultValue: '', description: '', name: 'myparam') 
                        ], 
                        submitter: 'admin,admin2,releaseGroup', 
                        submitterParameter: 'APPROVER' 
                    )
                }
            }
        }
        stage('deploy') { 
            steps { 
                echo "APPROVER is ${approvalMap['APPROVER']}" 
                echo "Deploy to the Env: ${approvalMap['ENV']}"
                echo "Self defined param: ${approvalMap['myparam']}" 
                }
            }
        }
}

