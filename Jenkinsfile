pipeline {
    agent any

    environment {
        // Set JAVA_HOME to Temurin 17
        JAVA_HOME = '/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    tools {
        // Make sure you configure Maven in Jenkins global tools as 'Maven3'
        maven 'Maven3'
    }

    stages {

        stage('Checkout SCM') {
            steps {
                // Checkout from Git
                git url: 'https://github.com/Agila101/selenium-automation.git', branch: 'main'
            }
        }

        stage('Build') {
            steps {
                // Use Maven to clean and build
                sh 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build JAR') {
            steps {
                sh 'mvn package'
            }
        }

        stage('Send Email') {
            steps {
                echo "Skipping email for now. Configure SMTP to enable this step."
            }
        }
    }

    post {
        failure {
            echo "Build failed. Check the logs."
        }
        success {
            echo "Build succeeded!"
        }
        always {
                    // Archive TestNG/Surefire test results
                    junit '**/target/surefire-reports/*.xml'

                    // Archive Allure results for reporting
                    archiveArtifacts artifacts: 'target/allure-results/**/*', allowEmptyArchive: true
                }
    }
}
