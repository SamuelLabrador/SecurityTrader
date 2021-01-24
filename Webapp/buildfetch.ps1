Set-Location .\security_trader_api
Write-Output Installing NPM Dependencies
npm install

Write-Output Packaging module
npm install url
npm run-script build

Write-Output Linking module
npm link

Set-Location ..\webapp
npm link security_trader_api