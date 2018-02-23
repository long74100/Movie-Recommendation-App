pipeline {
   agent {
       docker {
           image 'maven:3-alpine'
           args '-v /root/.m2:/root/.m2'
       }
   }

   stages {
       stage('Build') {
           steps {
               echo "Building"
               sh 'mvn -f project/workspace-sts-3.9.2.RELEASE/cs4500-spring2018-team47/pom.xml compile'
               sh 'mvn -f project/workspace-sts-3.9.2.RELEASE/cs4500-spring2018-team47/pom.xml package'
           }
       }
       stage('Test'){
           steps {
               echo "Testing"
               sh 'mvn -f project/workspace-sts-3.9.2.RELEASE/cs4500-spring2018-team47/pom.xml test'
           }
       }
       stage('SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                        sh 'mvn -f project/workspace-sts-3.9.2.RELEASE/cs4500-spring2018-team47/pom.xml clean install'
                        sh 'mvn -f project/workspace-sts-3.9.2.RELEASE/cs4500-spring2018-team47/pom.xml sonar:sonar'
                }
            }
        }
            
        stage('Quality') {
            steps {
              sh 'sleep 30'
              timeout(time: 10, unit: 'SECONDS') {
                retry(5) {
                  script {
                    def qg = waitForQualityGate()
                    if (qg.status != 'OK') {
                      error "Pipeline aborted due to quality gate failure: ${qg.status}"
                    }
                  }
                }
              }
            }
        }

      }
}
    