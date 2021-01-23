openapi-generator-cli.cmd generate `
    -i .\openapi.yaml `
    -g typescript-angular `
    -o ./ClientStub

openapi-generator-cli.cmd generate `
    -i .\openapi.yaml `
    -g scala-play-server `
    --additional-properties=java8=false `
    -o ./ServerStub

Write-Output Copying server items... 
Copy-Item ./ServerStub/app/* ../../Server/app/ -Force
Copy-Item ./ServerStub/conf/routes ../../Server/conf/routes -Force
Copy-Item ./ServerStub/public/openapi.json ../../Server/public/openapi.json 