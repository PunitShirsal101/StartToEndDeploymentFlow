#Requires -Version 5.1
<#!
Run SonarQube/SonarCloud analysis for this project.

Prerequisites:
- A running SonarQube server (on-prem) or access to SonarCloud.
- Set environment variables before running:
    $env:SONAR_HOST_URL = "https://your-sonar-host"   # e.g., http://localhost:9000 or https://sonarcloud.io
    $env:SONAR_TOKEN    = "your-token-here"
- Either have 'sonar-scanner' available in PATH, or Maven with the Sonar plugin available on the runner.

This script will:
1) Run tests to generate surefire reports (used by Sonar).
2) Prefer using 'sonar-scanner' if available; otherwise fall back to 'mvn sonar:sonar'.

Note: Code coverage path is configured in sonar-project.properties as target/site/jacoco/jacoco.xml.
      If you want coverage, set up JaCoCo in your CI/build to produce that XML before analysis
      (e.g., by running 'mvn -DskipTests=false test jacoco:report' if the plugin is configured in your CI).
!#>

$ErrorActionPreference = 'Stop'

function Write-Header($text) {
    Write-Host "`n=== $text ===" -ForegroundColor Cyan
}

if (-not $env:SONAR_HOST_URL -or -not $env:SONAR_TOKEN) {
    Write-Warning "SONAR_HOST_URL or SONAR_TOKEN is not set. Please set them before running this script."
    Write-Host "Example:" -ForegroundColor Yellow
    Write-Host "  $env:SONAR_HOST_URL = 'http://localhost:9000'" -ForegroundColor Yellow
    Write-Host "  $env:SONAR_TOKEN = 'xxxxxxxxxxxxxxxxxxxx'" -ForegroundColor Yellow
    exit 1
}

Write-Header "Step 1: Run tests (Surefire reports)"
# Use Maven wrapper if available
$mvn = if (Test-Path -LiteralPath ".\mvnw.cmd") { ".\mvnw.cmd" } else { "mvn" }
& $mvn -DskipTests=false test

# Try to find sonar-scanner first
function Test-Command($cmd) {
    $old = $ErrorActionPreference; $ErrorActionPreference = 'SilentlyContinue'
    $null = Get-Command $cmd
    $found = $?
    $ErrorActionPreference = $old
    return $found
}

Write-Header "Step 2: Run Sonar analysis"
$sonarArgs = @(
    "-Dsonar.host.url=$($env:SONAR_HOST_URL)",
    # SonarCloud uses sonar.login, SonarQube supports sonar.token; most versions accept either
    "-Dsonar.login=$($env:SONAR_TOKEN)",
    "-Dsonar.token=$($env:SONAR_TOKEN)"
)

if (Test-Command "sonar-scanner") {
    Write-Host "Using sonar-scanner from PATH" -ForegroundColor Green
    sonar-scanner @sonarArgs
} else {
    Write-Host "sonar-scanner not found. Falling back to Maven sonar:sonar" -ForegroundColor Yellow
    & $mvn sonar:sonar @sonarArgs
}

Write-Header "Done"
Write-Host "If analysis succeeded, view the project on your Sonar server." -ForegroundColor Green
