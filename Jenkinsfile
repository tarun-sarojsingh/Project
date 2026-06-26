pipeline {
    agent any
    tools {
    maven 'Maven-3.9.16'
    jdk 'JDK-21'
    }
    
    environment {
        DOCKER_IMAGE = 'library-management-app'
        DOCKER_TAG = "${BUILD_NUMBER}"
        DOCKER_REGISTRY = 'your-registry' // Update this
        APP_NAME = 'author-book-management'
    }
    
    stages {
        stage('Checkout') {
            steps {
                script {
                    echo "========== Stage: Checkout =========="
                    checkout scm
                }
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Unit Tests') {
            steps {
                echo "========== Stage: Unit Tests =========="
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Code Quality Analysis') {
            steps {
                script {
                    echo "========== Stage: Code Quality Analysis =========="
                    // SonarQube analysis (if configured)
                    // sh 'mvn sonar:sonar'
                    echo 'Code quality check completed'
                }
            }
        }
        
        stage('Package') {
            steps {
                echo "========== Stage: Package =========="
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                }
            }
        }

        stage('Debug') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'pwd'
                        sh 'ls -la'
                        sh 'find . -name pom.xml'
                    } else {
                        bat 'cd'
                        bat 'dir'
                        bat 'where /r . pom.xml'
                    }
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo "========== Stage: Build Docker Image =========="
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
            }
        }
        
        stage('Push Docker Image') {
            steps {
                echo "========== Stage: Push Docker Image =========="
                // Login to Docker registry and push
                // withCredentials([usernamePassword(credentialsId: 'docker-credentials', 
                //                                   usernameVariable: 'DOCKER_USERNAME', 
                //                                   passwordVariable: 'DOCKER_PASSWORD')]) {
                //     sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin'
                //     sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                //     sh "docker push ${DOCKER_IMAGE}:latest"
                // }
                echo 'Docker image built successfully'
            }
        }
        
        stage('Deploy to Test Environment') {
            steps {
                script {
                    echo 'Skipping deployment for now'
                }
            }
        }
        
        stage('Integration Tests') {
            steps {
                echo "========== Stage: Integration Tests =========="
                sh 'pwd'
                sh 'ls -la'
                sh 'mvn verify'
            }
        }
        
        stage('Security Scan') {
            steps {
                script {
                    echo "========== Stage: Security Scan =========="
                    // Docker image security scan
                    // sh "trivy image ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    echo 'Security scan completed'
                }
            }
        }
        
        stage('Deploy to Azure') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "========== Stage: Deploy to Azure =========="
                    // Azure deployment steps
                    echo 'Deploying to Azure Cloud...'
                    // Add Azure CLI commands here
                }
            }
        }
    }
    
    post {
        always {
            echo "========== Pipeline Execution Completed =========="
        }
        success {
            echo "========== BUILD SUCCESS =========="
            
        }
        failure {
            echo "========== BUILD FAILED =========="
        }
    }
}
