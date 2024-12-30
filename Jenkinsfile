pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))  // Simpan 5 build terakhir
    }
    environment {
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')  // Sesuaikan ID credentials
    }
    stages {
        stage('Updating local repository') {
            steps {
                dir('C:/Users/Sabil Aditia/AndroidStudioProjects/Shopping') {
                    script {
                        echo 'Pulling latest changes from GitHub...'
                        bat 'git config --global --add safe.directory C:/Users/Sabil Aditia/AndroidStudioProjects/Shopping'
                        bat 'git pull origin main'
                    }
                }
            }
        }
        stage('Build') {
            steps {
                dir('C:/Users/Sabil Aditia/AndroidStudioProjects/Shopping') {
                    script {
                        echo 'Building Docker image...'
                        bat 'docker build -t sabiladitia/shopping-apps:latest .'  // Sesuaikan nama image
                    }
                }
            }
        }
        stage('Login to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                    echo 'Logging in to Docker Hub...'
                    bat "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
                }
            }
        }
        stage('Push to Docker Hub') {
            steps {
                script {
                    echo 'Pushing Docker image to Docker Hub...'
                    bat 'docker push sabiladitia/shopping-apps:latest'  // Sesuaikan nama image
                }
            }
        }
    }
    post {
        always {
            node('master') {  // Tentukan label node yang digunakan di sini
                script {
                    echo 'Logging out from Docker Hub...'
                    bat 'docker logout'
                }
            }
        }
    }
}
