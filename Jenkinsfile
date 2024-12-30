pipeline {
    agent any

    environment {
        JAVA_HOME = "C:/Program Files/Eclipse Adoptium/jdk-21.0.3.9-hotspot"
        GRADLE_HOME = "C:/Gradle/gradle-8.2.1"
        PATH = "${GRADLE_HOME}/bin:${env.PATH}"
    }

    tools {
        jdk 'JDK21'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    echo 'Cloning project from GitHub...'
                    checkout scm
                }
            }
        }

        stage('Setup Gradle') {
            steps {
                script {
                    echo 'Setting up Gradle Wrapper...'
                    sh './gradlew wrapper'
                }
            }
        }

        stage('Clean Build') {
            steps {
                script {
                    echo 'Running clean build...'
                    sh './gradlew clean build'
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    echo 'Running Unit Tests...'
                    sh './gradlew test'
                }
            }
        }

        stage('Assemble APK') {
            steps {
                script {
                    echo 'Building APK...'
                    sh './gradlew assembleDebug'
                }
            }
        }

        stage('Archive APK') {
            steps {
                script {
                    echo 'Archiving APK...'
                    archiveArtifacts artifacts: '**/build/outputs/**/*.apk', fingerprint: true
                }
            }
        }
    }

    post {
        success {
            echo 'Build and test successful!'
        }
        failure {
            echo 'Build or test failed!'
        }
    }
}
