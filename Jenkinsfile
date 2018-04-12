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
                sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install'
                sh 'mvn sonar:sonar -Dsonar.host.url=http://ec2-18-220-143-170.us-east-2.compute.amazonaws.com:9000/'
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
    