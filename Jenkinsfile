pipeline {
    agent any

    environment {
        ANDROID_HOME = "$HOME/Android/Sdk"
        GRADLE_HOME = "$HOME/.gradle"
        PATH = "/usr/local/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$GRADLE_HOME/bin:$PATH"
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    echo 'Checking out project...'
                    git url: 'https://github.com/Sabiladitia20/shopping-apps.git', branch: 'main'
                }
            }
        }

        stage('Setup') {
            steps {
                script {
                    echo 'Setting up Android SDK and dependencies...'
                    sh './gradlew dependencies'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo 'Building APK...'
                    sh './gradlew assembleDebug'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    echo 'Running tests...'
                    sh './gradlew testDebugUnitTest'
                }
            }
        }

        stage('Archive') {
            steps {
                script {
                    echo 'Archiving APK...'
                    archiveArtifacts artifacts: '**/build/outputs/apk/debug/*.apk', fingerprint: true
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo 'Deploying APK to device/emulator...'
                    sh 'adb install -r app/build/outputs/apk/debug/app-debug.apk'
                }
            }
        }
    }

    post {
        success {
            echo 'Build and deployment successful!'
        }
        failure {
            echo 'Build failed. Please check the logs.'
        }
    }
}
