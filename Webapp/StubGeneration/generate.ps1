# Download every time in case swagger.json changes.
Invoke-WebRequest -Uri "http://localhost:9000/assets/swagger.json" -OutFile swagger.json

if (!(Test-Path -Path ./swagger-codegen-cli.jar)) {
  Write-Output "Downloading swagger-codegen-cli.jar for client codegen"
  Invoke-WebRequest -Uri "https://repo1.maven.org/maven2/io/swagger/swagger-codegen-cli/2.4.18/swagger-codegen-cli-2.4.18.jar" -OutFile swagger-codegen-cli.jar
}

java -jar .\swagger-codegen-cli.jar generate `
  -i ./swagger.json `
  -l typescript-fetch `
  -o ./WebappStub `
  --additional-properties npmName="security_trader_api"

Copy-Item .\WebappStub\* ..\security_trader_api\ -Force 

# Build Fetch
Set-Location ..\security_trader_api
Write-Output Installing NPM Dependencies
npm install

Write-Output Packaging module
npm install url
npm run-script build

# TODO: Copy files using windows command
