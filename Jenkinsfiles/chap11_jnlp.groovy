import com.nirima.jenkins.plugins.docker.DockerCloud
import com.nirima.jenkins.plugins.docker.DockerTemplate
import com.nirima.jenkins.plugins.docker.DockerTemplateBase
//import com.nirima.jenkins.plugins.docker.launcher.AttachedDockerComputerLauncher
import com.nirima.jenkins.plugins.docker.launcher.DockerComputerJNLPLauncher
//import io.jenkins.docker.connector.DockerComputerAttachConnector
import io.jenkins.docker.connector.DockerComputerJNLPConnector
import jenkins.model.Jenkins
import hudson.slaves.JNLPLauncher;
// parameters

//Docker Agent 基本参数
def dockerTemplateBaseParameters = [
  bindAllPorts:       false,
  bindPorts:          '',
  cpuShares:          null,
  dnsString:          '',
  dockerCommand:      '',
  environmentsString: '',
  extraHostsString:   '',
  hostname:           '',
  image:              'jenkins/inbound-agent:latest',   //镜像
  macAddress:         '',
  memoryLimit:        null,
  memorySwap:         null,
  network:            '',
  privileged:         false,
  pullCredentialsId:  '',
  sharedMemorySize:   null,
  tty:                true,
  volumesFromString:  '',
  volumesString:      ''
]


//Docker Agnet 参数
def DockerTemplateParameters = [
  instanceCapStr: '10',    //实例数
  labelString:    'jenkins-agent-test',   //节点标签
  remoteFs:       '/home/jenkins'      //主目录

]


def dockerCloudParameters = [
  connectTimeout:   60,       //链接超时
  containerCapStr:  '10',     //实例数量
  credentialsId:    '',
  dockerHostname:   '',
  name:             'docker.local.test',     
  readTimeout:      60,
  serverUrl:        'tcp://192.168.33.11:2375',
  version:          ''
]

// https://github.com/jenkinsci/docker-plugin/blob/docker-plugin-1.1.2/src/main/java/com/nirima/jenkins/plugins/docker/DockerTemplateBase.java
DockerTemplateBase dockerTemplateBase = new DockerTemplateBase(
  dockerTemplateBaseParameters.image,
  dockerTemplateBaseParameters.pullCredentialsId,
  dockerTemplateBaseParameters.dnsString,
  dockerTemplateBaseParameters.network,
  dockerTemplateBaseParameters.dockerCommand,
  dockerTemplateBaseParameters.volumesString,
  dockerTemplateBaseParameters.volumesFromString,
  dockerTemplateBaseParameters.environmentsString,
  dockerTemplateBaseParameters.hostname,
  dockerTemplateBaseParameters.memoryLimit,
  dockerTemplateBaseParameters.memorySwap,
  dockerTemplateBaseParameters.cpuPeriod,
  dockerTemplateBaseParameters.cpuQuota,
  dockerTemplateBaseParameters.cpuShares,
  dockerTemplateBaseParameters.sharedMemorySize,
  dockerTemplateBaseParameters.bindPorts,
  dockerTemplateBaseParameters.bindAllPorts,
  dockerTemplateBaseParameters.privileged,
  dockerTemplateBaseParameters.tty,
  dockerTemplateBaseParameters.macAddress,
  dockerTemplateBaseParameters.extraHostsString
)

// https://github.com/jenkinsci/docker-plugin/blob/docker-plugin-1.1.2/src/main/java/com/nirima/jenkins/plugins/docker/DockerTemplate.java
DockerTemplate dockerTemplate = new DockerTemplate(
  dockerTemplateBase,
  //new DockerComputerAttachConnector(),
  new DockerComputerJNLPConnector(new JNLPLauncher(null, null)).withUser("jenkins")
                        .withJenkinsUrl("http://192.168.33.11:8080"),
  DockerTemplateParameters.labelString,
  DockerTemplateParameters.remoteFs,
  DockerTemplateParameters.instanceCapStr
)



// https://github.com/jenkinsci/docker-plugin/blob/docker-plugin-1.1.2/src/main/java/com/nirima/jenkins/plugins/docker/DockerCloud.java
DockerCloud dockerCloud = new DockerCloud(
  dockerCloudParameters.name,
  [dockerTemplate],
  dockerCloudParameters.serverUrl,
  dockerCloudParameters.containerCapStr,
  dockerCloudParameters.connectTimeout,
  dockerCloudParameters.readTimeout,
  dockerCloudParameters.credentialsId,
  dockerCloudParameters.version,
  dockerCloudParameters.dockerHostname
)

// get Jenkins instance
Jenkins jenkins = Jenkins.getInstance()

// add cloud configuration to Jenkins
jenkins.clouds.add(dockerCloud)

// save current Jenkins state to disk
jenkins.save()