#!/bin/bash

Download every time in case swagger.json changes.
curl "http://localhost:9000/assets/swagger.json" -o ./swagger.json

if [ -f ./swagger-codegen-cli.jar ]; 
then
  echo "Downloading swagger-codegen-cli.jar for client codegen"
  curl "https://repo1.maven.org/maven2/io/swagger/swagger-codegen-cli/2.4.18/swagger-codegen-cli-2.4.18.jar" -o swagger-codegen-cli.jar
fi

rm WebappStub -rf

java -jar ./swagger-codegen-cli.jar generate \
  -i ./swagger.json \
  -l typescript-fetch \
  -o ./WebappStub \
  --additional-properties npmName="security_trader_api"

# Build Fetch
cd ./WebappStub
echo Installing NPM Dependencies
npm install

echo Packaging module
npm install url
npm run-script build

cp ./dist/* ../../webapp/src/app/fetch/api/
cp ./custom.d.ts ../../webapp/src/app/fetch/custom.d.ts