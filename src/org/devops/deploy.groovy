package org.devops

//salt stack

def SaltDeploy(host,func){
    sh "salt ${host} ${func}"
}
