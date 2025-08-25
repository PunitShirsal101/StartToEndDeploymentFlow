# Start-To-End Deployment Flow

A small Customer app with a simple path from code to a running service.

## Quick Start
- Requirements: Docker Desktop
- Build (App): docker build -t start-to-end:latest -f dockerfile .
- Run (App): docker run -p 8080:8080 start-to-end:latest
- Try: <http://localhost:8080/swagger-ui/index.html>

## PostgreSQL with Docker
Build the PostgreSQL image (from the postgres stage in the main dockerfile):
- docker build -t postgresql-16.10-alpine-img -f dockerfile --target postgres .

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
GitHub → AWS CodePipeline → AWS CodeBuild → Amazon ECR → AWS ECS → Running App

### CI/CD Artifacts in this repo
- Dockerfile: builds a container from target/CICD.jar and exposes 8080 with a container HEALTHCHECK to /actuator/health.
- docker-compose.yml: local PostgreSQL + app for development.
- buildspec.yml: CodeBuild script to build, tag, and push Docker image to ECR, and output imagedefinitions.json for ECS deploy action.
- ecs-taskdef.json: sample ECS task definition (Fargate) with container health check. Update ARNs, repo URI, and DB endpoint as needed.
- appspec.yaml: for ECS Blue/Green deployments via CodeDeploy.

### ECS Health Checks
- Spring Boot Actuator /actuator/health is enabled (pom includes spring-boot-starter-actuator; application.yml exposes health).
- Dockerfile defines a container-level HEALTHCHECK using curl.
- For Load Balancer target group, configure health check path: /actuator/health and port 8080.

### Required AWS variables (examples)
- In CodeBuild: AWS_REGION, IMAGE_REPO_NAME, IMAGE_TAG (or let CodePipeline set it).
- ECR: create repository matching IMAGE_REPO_NAME.
- ECS Service: use container name "app" and port 8080, compatible with imagedefinitions.json.
- Database: set SPRING_DATASOURCE_URL/USERNAME/PASSWORD in ECS Task Definition or via Secrets.


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

## License
See the LICENSE file in this repository.