<#
Fetch the maven-wrapper JAR used by the Maven Wrapper scripts.
Usage (PowerShell):
  .\fetch-maven-wrapper.ps1
#>
$ErrorActionPreference = 'Stop'

$outDir = Join-Path $PSScriptRoot '.mvn\wrapper'
if (-not (Test-Path $outDir)) { New-Item -ItemType Directory -Path $outDir -Force | Out-Null }

$jarUrl = 'https://repo1.maven.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar'
$dest = Join-Path $outDir 'maven-wrapper.jar'

Write-Host "Downloading maven-wrapper.jar from $jarUrl to $dest"
Invoke-WebRequest -Uri $jarUrl -OutFile $dest -UseBasicParsing
Write-Host 'Download complete.'
