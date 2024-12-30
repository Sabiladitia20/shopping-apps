pipeline {
    agent any

    environment {
        // Java configuration - using double backslashes for Windows paths
        JAVA_HOME = 'C:\\Program Files\\Java\\jdk-17'
        // Android SDK configuration
        ANDROID_HOME = 'C:\\Users\\Sabil Aditia\\AppData\\Local\\Android\\Sdk'
        // Update PATH to include Java and Android tools
        PATH = "${JAVA_HOME}\\bin;${ANDROID_HOME}\\platform-tools;${ANDROID_HOME}\\tools;${ANDROID_HOME}\\tools\\bin;${env.PATH}"
    }

    stages {
        stage('Environment Check') {
            steps {
                // Verify Java installation
                bat 'java -version'
                bat 'echo %JAVA_HOME%'
                
                // Verify Android SDK
                bat 'echo %ANDROID_HOME%'
                
                // Verify Gradle installation
                bat '.\\gradlew --version'
            }
        }

        stage('Checkout') {
            steps {
                // Checkout code from GitHub repository
                git branch: 'main', url: 'https://github.com/Sabiladitia20/shopping-apps.git'
            }
        }

        stage('Clean Project') {
            steps {
                // Clean the project using gradlew
                bat 'gradlew clean'
            }
        }

        stage('Run Tests') {
            steps {
                // Run all tests
                bat 'gradlew test'
            }
            post {
                always {
                    // Publish test results
                    junit allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                }
            }
        }

        stage('Code Analysis') {
            steps {
                // Run lint check
                bat 'gradlew lint'
            }
            post {
                always {
                    // Archive lint results
                    archiveArtifacts allowEmptyArchive: true, artifacts: '**/build/reports/lint-results-debug.html'
                }
            }
        }

        stage('Build Debug APK') {
            steps {
                // Build debug APK
                bat 'gradlew assembleDebug'
            }
            post {
                success {
                    // Archive the APK
                    archiveArtifacts '**/build/outputs/apk/debug/*.apk'
                }
            }
        }

        stage('Build Release APK') {
            steps {
                // Build release APK
                bat 'gradlew assembleRelease'
            }
            post {
                success {
                    // Archive the release APK
                    archiveArtifacts '**/build/outputs/apk/release/*.apk'
                }
            }
        }
    }

    post {
        always {
            // Clean workspace after build
            cleanWs()
        }
        success {
            echo 'Build completed successfully!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
