pipeline {

   agent any

   tools {
     jdk 'jdk21'
   }

   environment{
      SONARQUBE_SERVER = 'SonarQubeLocal'
      IMAGE_NAME = 'warehouse_management'
      IMAGE_TAG = '${env.BUILD_NUMBER}'
   }

   stages {
       stage('checkout'){
          steps {
            echo ' checking out the source code ...'
            checkout scm
          }
       }

       stage('Build & Run test'){
          steps{
            echo 'Running maven build and unit tests'
            sh './mvnw clean verify'
          }

          post{
           always{
             junit 'target/surefire-reports/*.xml'
           }
          }
       }

        stage('Code Coverage (JaCoCo)') {
            steps {
                echo 'Generating JaCoCo report...'
                sh './mvnw jacoco:report'
                publishHTML(target: [
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Coverage Report'
                ])
            }
        }

        stage('static analysing with (SonarQube)'){
          steps{
            echo 'Running sonarQube analysis'
            withSonarQubeEnv("${SONARQUBE_SERVER}"){
              sh './mvnw sonar:sonar  -Dsonar.projectKey=logistics-api'
            }
          }
        }

        stage('Quality Gate'){
         steps{
           timeout(time: 5, unit: 'MINUTES'){
             waitForQualityGate abortPipeline : true
           }
         }
        }

        stage('package'){
          when{
            expression{currentBuild.resultIsBetterOrEqualTo('SUCCESS')}
          }

          steps{
           echo 'packaging the project '
           sh './mvnw package -DskipTests'
           archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
          }
        }

//         stage('building docker image'){
//          when{
//            expression{fileExists('Dockerfile')
//          }
//
//          steps{
//            echo 'building docker image'
//            sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG}"
//          }
//         }
   }


   post{

     success{
       echo 'pipeline was done with success'
     }

     failure{
       echo 'something went wrong , pipeline fained '
     }

   }


}