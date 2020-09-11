pipeline { 
	agent any 
	triggers { 
		GenericTrigger(
			genericVariables: [ 
				[key: 'ref', value: '$.ref']
			],
			token: 'secret',
			causeString: 'Triggered on $ref',
			printContributedVariables: true,
			printPostContent: true
        )
	}
	stages { 
		stage('Some step')  { 
			steps { 
			sh "echo $ref"
			sh "printenv"
		}
	}
  }
} 