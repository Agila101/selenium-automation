pipeline {
    agent any

    environment {
        JAVA_HOME = "/usr/lib/jvm/java-17-openjdk" // Adjust if needed
        PATH = "$JAVA_HOME/bin:$PATH"
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/yourusername/your-repo.git'
            }
        }

        stage('Build') {
            steps {
                sh './mvnw clean compile' // or 'mvn clean compile' if you don't have mvnw
            }
        }

        stage('Run Tests') {
            steps {
                sh './mvnw test -Denv=qa -Dsurefire.suiteXmlFiles=testng.xml'
            }
        }

        stage('Publish Reports') {
            steps {
                publishHTML(target: [
                    reportDir: 'ExtentReports',
                    reportFiles: 'ExtentReport.html',
                    reportName: 'Extent Report'
                ])
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
        }
    }
}