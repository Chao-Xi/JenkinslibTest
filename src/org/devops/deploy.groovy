package org.devops

//salt stack

def SaltDeploy(host,func){
    sh "sudo salt ${host} ${func}"
}

def AnsibleDeploy(host,func){
    sh "ansible ${host} ${func}"
}
