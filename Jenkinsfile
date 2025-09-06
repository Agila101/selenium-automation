pipeline {
    agent any

    environment {
        CI = "true" // BaseTest checks this to run headless browsers in Jenkins
        MAVEN_HOME = tool name: 'Maven', type: 'maven' // make sure Maven is configured in Jenkins
        JAVA_HOME = tool name: 'JDK 21', type: 'jdk' // make sure JDK is configured in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/Agila101/selenium-automation.git'
            }
        }

        stage('Build') {
            steps {
                withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin", "JAVA_HOME=${JAVA_HOME}"]) {
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Run Tests') {
            steps {
                withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin", "JAVA_HOME=${JAVA_HOME}"]) {
                    sh 'mvn test'
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    publishHTML(target: [
                        reportDir: 'ExtentReports',
                        reportFiles: 'ExtentReport.html',
                        reportName: 'Extent Test Report'
                    ])
                }
            }
        }

        stage('Build JAR') {
            steps {
                withEnv(["PATH+MAVEN=${MAVEN_HOME}/bin", "JAVA_HOME=${JAVA_HOME}"]) {
                    sh 'mvn package'
                }
            }
        }

        stage('Send Email') {
            steps {
                mail bcc: '', body: "Build ${env.BUILD_NUMBER} completed.\nCheck reports: ${env.BUILD_URL}",
                    from: 'jenkins@yourdomain.com',
                    replyTo: 'jenkins@yourdomain.com',
                    subject: "Build ${env.BUILD_NUMBER} Status",
                    to: 'agila.sri21@gmail.com'
            }
        }
    }

    post {
        failure {
            mail bcc: '', body: "Build ${env.BUILD_NUMBER} failed.\nCheck logs: ${env.BUILD_URL}",
                from: 'jenkins@yourdomain.com',
                replyTo: 'jenkins@yourdomain.com',
                subject: "Build ${env.BUILD_NUMBER} Failed",
                to: 'agila.sri21@gmail.com'
        }
    }
}
