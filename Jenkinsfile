pipeline {
    agent any

    environment {
        // Maven
        MAVEN_HOME = tool 'Maven'
        MAVEN_OPTS = "-Xmx1024m -Xms512m"

        // SonarQube
        SONAR_SERVER = "SonarQubeLocal"
        SONAR_PROJECT_KEY = "api-logistique"
        SONAR_PROJECT_NAME = "API Logistique"

        // Docker
        DOCKER_IMAGE = "hamzaboumanjel/api-logistique"
        DOCKER_TAG = "${env.BUILD_NUMBER}"
    }

    stages {

        /* ============================================================
           1) CHECKOUT
        ============================================================ */
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    env.GIT_BRANCH = env.BRANCH_NAME ?: "unknown"
                }
                echo "📌 Branch: ${env.GIT_BRANCH}, Commit: ${env.GIT_COMMIT_SHORT}"
            }
        }

        /* ============================================================
           2) CLEAN
        ============================================================ */
        stage('Clean') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean"
            }
        }

        /* ============================================================
           3) COMPILE
        ============================================================ */
        stage('Compile') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn compile -DskipTests"
            }
        }

        /* ============================================================
           4) UNIT TESTS + JACOCO
        ============================================================ */
        stage('Unit Tests & Coverage') {
            steps {
                sh """
                    ${MAVEN_HOME}/bin/mvn test \
                        -Djacoco.skip=false
                """
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'

                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/mapper/**,**/dto/**,**/*Config.java'
                    )
                }
            }
        }

        /* ============================================================
           5) SONARQUBE STATIC ANALYSIS
        ============================================================ */
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv("${SONAR_SERVER}") {
                    sh """
                        ${MAVEN_HOME}/bin/mvn sonar:sonar \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.projectName=${SONAR_PROJECT_NAME} \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                            -Dsonar.exclusions=**/dto/**,**/mapper/**,**/*Config.java \
                            -Dsonar.java.binaries=target/classes
                    """
                }
            }
        }

        /* ============================================================
           6) SONARQUBE QUALITY GATE
        ============================================================ */
        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error "❌ Quality Gate failed: ${qg.status}"
                        }
                        echo "✅ Quality Gate passed"
                    }
                }
            }
        }

        /* ============================================================
           7) PACKAGE JAR
        ============================================================ */
        stage('Package') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn package -DskipTests"
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                }
            }
        }

        /* ============================================================
           8) DOCKER BUILD
        ============================================================ */
        stage('Docker Build') {
            steps {
                sh """
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                """
            }
        }

        /* ============================================================
           9) DOCKER PUSH
        ============================================================ */
        stage('Docker Push') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                }
            }
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'docker-hub-creds',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {
                    sh """
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker push ${DOCKER_IMAGE}:latest
                        docker logout
                    """
                }
            }
        }
    }

    post {
        success {
            echo "🎉 BUILD SUCCESS"
            echo "📦 Docker Image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
        }
        failure {
            echo "❌ BUILD FAILED"
        }
    }
}
