@Library('jenkins-pipeline@work/PBLEID-15605') _

pipelineWithMavenAndDocker {
    stagingEnvironment = 'statistics-staging'
    stagingEnvironmentType = 'docker'
    productionEnvironment = 'statistics'
    productionEnvironmentType = 'docker'
    stackName = 'idporten'
    gitSshKey = 'ssh.github.com'
}
