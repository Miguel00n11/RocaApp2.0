# Script Rápido para Capturar Logs de Error del Mapa
# Guarda como: capturar_logs.ps1
# Ejecuta: .\capturar_logs.ps1

Write-Host "Limpiando logs anteriores..." -ForegroundColor Yellow
adb logcat -c

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "ABRE EL MAPA AHORA EN LA APP" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Cyan
Write-Host "Capturando logs por 20 segundos..." -ForegroundColor White
Write-Host ""

# Capturar logs para un archivo
$outputFile = "logs_mapa_error.txt"
$process = Start-Process adb -ArgumentList "logcat" -NoNewWindow -RedirectStandardOutput $outputFile -PassThru

# Esperar 20 segundos
Start-Sleep -Seconds 20

# Detener captura
Stop-Process -Id $process.Id -Force

Write-Host ""
Write-Host "✅ Logs capturados en: $outputFile" -ForegroundColor Green
Write-Host ""
Write-Host "=== ANÁLISIS DE LOGS ===" -ForegroundColor Cyan

$content = Get-Content $outputFile

# Buscar líneas importantes
Write-Host ""
Write-Host "1. Errores de AUTENTICACIÓN / API KEY:" -ForegroundColor Yellow
$authErrors = $content | Select-String -Pattern "Authorization failure|API key|Android Application|Auth"
if ($authErrors) {
    $authErrors | Select-Object -First 10
} else {
    Write-Host "   (no encontrados)" -ForegroundColor Gray
}

Write-Host ""
Write-Host "2. Logs de MapPickerActivity:" -ForegroundColor Yellow
$mapLogs = $content | Select-String -Pattern "MapPickerActivity"
if ($mapLogs) {
    $mapLogs | Select-Object -First 15
} else {
    Write-Host "   (no encontrados)" -ForegroundColor Gray
}

Write-Host ""
Write-Host "3. Errores de Google Maps:" -ForegroundColor Yellow
$mapErrors = $content | Select-String -Pattern "Maps|Google|Permission"
if ($mapErrors) {
    $mapErrors | Select-Object -First 10
} else {
    Write-Host "   (no encontrados)" -ForegroundColor Gray
}

Write-Host ""
Write-Host "================================" -ForegroundColor Cyan
Write-Host "Archivo completo guardado en: $outputFile" -ForegroundColor White
Write-Host "Abre ese archivo para ver todos los logs" -ForegroundColor White
Write-Host "================================" -ForegroundColor Cyan
