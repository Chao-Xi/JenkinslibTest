@Library('jenkinslib@master') _

def jenkinsapi = new org.devops.jenkinsapi()

// String projectName = "${env.projectName}"
// String manageOpts = "${env.manageOpts}"

pipeline {
    
    agent { node {label "master"}}

    parameters {
        string(name: 'projectName', defaultValue: '', description: 'Please add your projectName')
        choice(name: 'manageOpts', choices: 'DisableProject\nEnableProject\nDeleteProject\nBuildProject\nCreateProject', description: 'Please chose your manageOpts')
    }
   
    
    stages{
        stage("test"){
            steps{
                script{
                    
                    if (manageOpts == "CreateProject"){
                        jenkinsapi.CreateProject(projectName)
                    } else {
                        jenkinsapi.Project(projectName,manageOpts)
                    }
                }
            }
        }
    }
}