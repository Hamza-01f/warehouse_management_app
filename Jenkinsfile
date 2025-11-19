pipeline {
    agent any

    tools {
        maven 'Maven'
//         jdk 'JDK21'
    }

    environment {
        SONAR_SERVER = "SonarQubeLocal"
        SONAR_PROJECT_KEY = "api-logistique"
        SONAR_PROJECT_NAME = "API Logistique"

        DOCKER_IMAGE = "hamzaboumanjel/api-logistique"
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                }
                echo "📌 Branch: ${env.BRANCH_NAME}, Commit: ${env.GIT_COMMIT_SHORT}"
            }
        }

        /* =============================
           CLEAN
        ============================= */
        stage('Clean') {
            steps {
                withMaven(maven: 'Maven') {
                    sh "mvn clean"
                }
            }
        }

        /* =============================
           COMPILE
        ============================= */
        stage('Compile') {
            steps {
                withMaven(maven: 'Maven') {
                    sh "mvn compile -DskipTests"
                }
            }
        }

        /* =============================
           UNIT TESTS + JACOCO
        ============================= */
        stage('Unit Tests & Coverage') {
            steps {
                withMaven(maven: 'Maven') {
                    sh "mvn test -Djacoco.skip=false"
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/dto/**,**/mapper/**,**/*Config.java'
                    )
                }
            }
        }

        /* =============================
           SONARQUBE ANALYSIS
        ============================= */
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONAR_SERVER}") {
                    withMaven(maven: 'Maven') {
                        sh """
                           mvn sonar:sonar \
                               -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                               -Dsonar.projectName="${SONAR_PROJECT_NAME}" \
                               -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                               -Dsonar.exclusions=**/dto/**,**/mapper/**,**/*Config.java \
                               -Dsonar.java.binaries=target/classes
                        """
                    }
                }
            }
        }
        /* =============================
           PACKAGE
        ============================= */
        stage('Package') {
            steps {
                withMaven(maven: 'Maven') {
                    sh "mvn package -DskipTests"
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                }
            }
        }

        /* =============================
           DOCKER BUILD
        ============================= */
        stage('Docker Build') {
            steps {
                sh """
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} -f Docker/Pipeline/Dockerfile .
                    docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                """
            }
        }

                /* =============================
                   DOCKER PUSH
                ============================= */
        //         stage('Docker Push') {
        //             when {
        //                 anyOf { branch 'main'; branch 'master' }
        //             }
        //             steps {
        //                 withCredentials([
        //                     usernamePassword(
        //                         credentialsId: 'docker-hub-creds',
        //                         usernameVariable: 'DOCKER_USER',
        //                         passwordVariable: 'DOCKER_PASS'
        //                     )
        //                 ]) {
        //                     sh """
        //                         echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
        //                         docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
        //                         docker push ${DOCKER_IMAGE}:latest
        //                         docker logout
        //                     """
        //                 }
        //             }
        //         }
   }


    post {
        success {
            echo " BUILD SUCCESS"
            echo " Docker Image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
        }
        failure {
            echo "❌ BUILD FAILED"
        }
    }
}
