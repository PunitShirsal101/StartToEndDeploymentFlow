pipeline {
  agent any

  options {
    timestamps()
  }

  environment {
    AWS_REGION       = credentials('AWS_REGION')
    AWS_ACCOUNT_ID   = credentials('AWS_ACCOUNT_ID')
    ECR_REPOSITORY   = credentials('ECR_REPOSITORY')
    IMAGE_NAME       = 'start-to-end-deployment-flow'
    GIT_COMMIT_SHORT = "${env.GIT_COMMIT?.take(12) ?: 'local'}"
    IMAGE_TAG        = "${GIT_COMMIT_SHORT}"
    ECR_URI          = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}"
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        sh 'chmod +x mvnw || true'
        sh './mvnw -B -V clean verify'
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
        }
      }
    }

    stage('Build Docker image') {
      steps {
        script {
          sh "aws --version"
          sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
          sh "docker build -t ${IMAGE_NAME}:${IMAGE_TAG} -f Dockerfile ."
          sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${ECR_URI}:${IMAGE_TAG}"
          sh "docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${ECR_URI}:latest"
        }
      }
    }

    stage('Push to ECR') {
      steps {
        sh "aws ecr describe-repositories --repository-names ${ECR_REPOSITORY} --region ${AWS_REGION} || aws ecr create-repository --repository-name ${ECR_REPOSITORY} --region ${AWS_REGION}"
        sh "docker push ${ECR_URI}:${IMAGE_TAG}"
        sh "docker push ${ECR_URI}:latest"
      }
    }
  }

  post {
    success {
      echo "Pushed image: ${ECR_URI}:${IMAGE_TAG}"
      echo "Latest tag also updated: ${ECR_URI}:latest"
    }
    failure {
      echo 'Build failed.'
    }
  }
}
