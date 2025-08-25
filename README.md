# Start-To-End Deployment Flow

A small Customer app with a simple path from code to a running service.


## Quick Start
- Requirements: Docker Desktop
- Build (App): docker build -t start-to-end:latest -f dockerfile .
- Run (App): docker run -p 8080:8080 start-to-end:latest
- Try: <http://localhost:8080/swagger-ui/index.html>

## PostgreSQL with Docker
Build the PostgreSQL image:
- docker buildx build -t postgresql-16.10-alpine-img -f dockerfile.postgres .

Run the PostgreSQL container:
- docker run -d --name postgresql-16.10-alpine-cont -p 5432:5432 postgresql-16.10-alpine-img

Notes:
- Port 5432 is a PostgreSQL database port (not HTTP). Opening http://localhost:5432 in a browser will show an empty response; that is expected.
- The app now defaults to jdbc:postgresql://localhost:5432/mydb with username=postgre and password=postgre, matching this container. Just run the DB, then run the app image and visit http://localhost:8080/swagger-ui/index.html.

The DB container uses:
- POSTGRES_DB=mydb
- POSTGRES_USER=postgre
- POSTGRES_PASSWORD=postgre

Configure the app to use this database by setting environment variables when you run it:
- SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mydb
- SPRING_DATASOURCE_USERNAME=postgre
- SPRING_DATASOURCE_PASSWORD=postgre

## How it deploys (at a glance)
GitHub → Jenkins → Amazon ECR → AWS CodePipeline (ECR source) → AWS ECS (Fargate) → Running App


## CI/CD with Jenkins + AWS
This repository includes:
- Dockerfile for containerizing the Spring Boot app.
- Jenkinsfile for building, testing, containerizing, and pushing the image to Amazon ECR.

High-level flow:
1. Developer pushes to main branch on GitHub/CodeCommit.
2. Jenkins job is triggered by webhook or poll.
3. Jenkins runs Maven tests and builds the jar, builds the Docker image and pushes to ECR with two tags: short git SHA and latest.
4. AWS CodePipeline is configured with an ECR source. When a new image tag appears, it triggers deployment to ECS (Fargate) which pulls the image from ECR and runs the container.

Jenkins prerequisites:
- Jenkins agent with Docker CLI, Docker daemon access, and AWS CLI v2 installed.
- Jenkins credentials configured as Secret Text credentials with IDs:
  - AWS_REGION (e.g., us-east-1)
  - AWS_ACCOUNT_ID (12-digit account ID)
  - ECR_REPOSITORY (e.g., start-to-end-deployment-flow)
- The agent has IAM permissions for ECR: ecr:GetAuthorizationToken, ecr:BatchCheckLayerAvailability, ecr:CompleteLayerUpload, ecr:CreateRepository, ecr:DescribeRepositories, ecr:InitiateLayerUpload, ecr:PutImage, ecr:UploadLayerPart.
- Optional: Configure a webhook from GitHub to Jenkins or use Poll SCM.

Local test of container (after mvn package):
- docker build -t start-to-end:latest -f Dockerfile .
- docker run -p 8080:8080 --env SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/mydb --env SPRING_DATASOURCE_USERNAME=postgre --env SPRING_DATASOURCE_PASSWORD=postgre start-to-end:latest

AWS setup (outline):
- Create an ECR repository named as in ECR_REPOSITORY.
- Create an ECS Cluster and Fargate Service/Task Definition exposing port 8080 and pulling from ECR.
- Create a CodePipeline with ECR (repository + tag pattern) as Source, and ECS Deploy as the Deploy stage.

Notes:
- The application listens on port 8080 (exposed in Dockerfile). Ensure your ECS service security group allows inbound 80/8080 from the ALB or public as needed.
- Database connectivity is configured via environment variables; set them in the ECS Task Definition if you use a managed PostgreSQL (RDS) or another container.

## License
See the LICENSE file in this repository.


## IntelliJ IDEA: Create Data Source
Use these settings to add a PostgreSQL data source in IntelliJ IDEA’s Database tool window.

Prerequisite
- Ensure the PostgreSQL container is running:
  - docker run -d --name postgresql-16.10-alpine-cont -p 5432:5432 postgresql-16.10-alpine-img

Steps
1. Open View > Tool Windows > Database (or press Alt+1 then select the Database tab).
2. Click the + button > Data Source > PostgreSQL.
3. If prompted, allow IntelliJ to download the PostgreSQL driver.
4. Fill in the connection details:
   - Host: localhost
   - Port: 5432
   - Database: mydb
   - User: postgre
   - Password: postgre
   - URL (optional view): jdbc:postgresql://localhost:5432/mydb
   - SSL: Disabled (or leave default “Prefer” if it works in your environment)
5. Click Test Connection.
6. If successful, click OK/Apply. You can now browse schemas and run queries from IntelliJ.

Notes
- These values match the Docker image and the app’s defaults in application.yml.
- If you changed the container env vars (POSTGRES_DB/USER/PASSWORD), use those instead.

Troubleshooting
- Connection refused / timeout: Confirm the container is running (docker ps) and port 5432 isn’t blocked by a firewall.
- Port already in use: Stop the process using 5432 or map to another port (e.g., -p 5433:5432) and update Host/Port accordingly.
- Driver download failed: Check your proxy settings (File > Settings > Appearance & Behavior > System Settings > HTTP Proxy) and retry.
- Authentication failed: Verify user/password and that the database name is correct.
