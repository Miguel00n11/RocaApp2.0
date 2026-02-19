# Script de Diagnóstico Automático del Mapa
# Guarda este archivo como: diagnostico_mapa.ps1
# Ejecútalo con: .\diagnostico_mapa.ps1

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  Diagnóstico Automático - Google Maps RocaApp  " -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que adb esté disponible
$adbPath = Get-Command adb -ErrorAction SilentlyContinue
if (-not $adbPath) {
    Write-Host "❌ ERROR: adb no encontrado en PATH" -ForegroundColor Red
    Write-Host "   Asegúrate de tener Android SDK instalado y adb en tu PATH" -ForegroundColor Yellow
    Write-Host "   Ubicación típica: C:\Users\$env:USERNAME\AppData\Local\Android\Sdk\platform-tools" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ adb encontrado: $($adbPath.Source)" -ForegroundColor Green
Write-Host ""

# Verificar dispositivos conectados
Write-Host "Buscando dispositivos conectados..." -ForegroundColor Cyan
$devices = adb devices | Select-String -Pattern "device$"
if ($devices.Count -eq 0) {
    Write-Host "❌ No hay dispositivos conectados" -ForegroundColor Red
    Write-Host "   Conecta un dispositivo por USB o inicia un emulador" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ Dispositivo(s) conectado(s):" -ForegroundColor Green
adb devices
Write-Host ""

# Obtener SHA-1 del keystore de debug
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  1. SHA-1 del Keystore de Debug" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
$keystorePath = "$env:USERPROFILE\.android\debug.keystore"
if (Test-Path $keystorePath) {
    Write-Host "Obteniendo SHA-1..." -ForegroundColor Yellow
    $sha1Output = keytool -list -v -keystore $keystorePath -alias androiddebugkey -storepass android -keypass android 2>&1
    $sha1Line = $sha1Output | Select-String -Pattern "SHA1:"
    if ($sha1Line) {
        Write-Host "✅ SHA-1 encontrado:" -ForegroundColor Green
        Write-Host "   $sha1Line" -ForegroundColor White
        $sha1Value = ($sha1Line -split "SHA1:")[1].Trim()
        Write-Host ""
        Write-Host "📋 IMPORTANTE: Copia este SHA-1 y añádelo en Google Cloud Console:" -ForegroundColor Yellow
        Write-Host "   Package name: com.miguelrodriguez.rocaapp20" -ForegroundColor Cyan
        Write-Host "   SHA-1: $sha1Value" -ForegroundColor Cyan
    } else {
        Write-Host "⚠️  No se pudo extraer SHA-1" -ForegroundColor Yellow
    }
} else {
    Write-Host "❌ Keystore de debug no encontrado en: $keystorePath" -ForegroundColor Red
}
Write-Host ""
Write-Host "Presiona Enter para continuar con la captura de logs..." -ForegroundColor Yellow
Read-Host

# Limpiar logs anteriores
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  2. Captura de Logs (Logcat)" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "Limpiando logs anteriores..." -ForegroundColor Yellow
adb logcat -c

Write-Host "✅ Logs limpiados" -ForegroundColor Green
Write-Host ""
Write-Host "📱 AHORA: Abre la app y navega al mapa (botón 'Ubicación...')" -ForegroundColor Yellow -BackgroundColor DarkBlue
Write-Host "   Esperando logs por 30 segundos..." -ForegroundColor Yellow
Write-Host "   (Presiona Ctrl+C para detener antes si es necesario)" -ForegroundColor Gray
Write-Host ""

$outputFile = "logcat_diagnostico_$(Get-Date -Format 'yyyyMMdd_HHmmss').txt"

# Capturar logs filtrados por 30 segundos
$job = Start-Job -ScriptBlock {
    param($filter)
    adb logcat | Select-String -Pattern $filter
} -ArgumentList "MapPickerActivity|Google Maps|Maps Android|Authentication|GmsCore|Places"

Wait-Job $job -Timeout 30 | Out-Null
$logs = Receive-Job $job
Stop-Job $job
Remove-Job $job

# Guardar logs
if ($logs) {
    $logs | Out-File -FilePath $outputFile -Encoding UTF8
    Write-Host "✅ Logs capturados y guardados en: $outputFile" -ForegroundColor Green
    Write-Host ""

    # Analizar logs para encontrar problemas comunes
    Write-Host "==================================================" -ForegroundColor Cyan
    Write-Host "  3. Análisis Automático de Logs" -ForegroundColor Cyan
    Write-Host "==================================================" -ForegroundColor Cyan

    $hasOnCreate = $logs | Select-String -Pattern "onCreate started" -Quiet
    $hasPlayServices = $logs | Select-String -Pattern "Google Play Services available" -Quiet
    $hasOnMapReady = $logs | Select-String -Pattern "onMapReady called" -Quiet
    $hasMapLoaded = $logs | Select-String -Pattern "Map tiles loaded successfully" -Quiet
    $hasTimeout = $logs | Select-String -Pattern "did not load within timeout" -Quiet
    $hasAuthError = $logs | Select-String -Pattern "Authorization failure|API key" -Quiet

    Write-Host "Verificaciones:" -ForegroundColor White
    Write-Host "  [$(if($hasOnCreate){'✅'}else{'❌'})] Activity onCreate ejecutado" -ForegroundColor $(if($hasOnCreate){'Green'}else{'Red'})
    Write-Host "  [$(if($hasPlayServices){'✅'}else{'❌'})] Google Play Services disponible" -ForegroundColor $(if($hasPlayServices){'Green'}else{'Red'})
    Write-Host "  [$(if($hasOnMapReady){'✅'}else{'❌'})] Mapa inicializado (onMapReady)" -ForegroundColor $(if($hasOnMapReady){'Green'}else{'Red'})
    Write-Host "  [$(if($hasMapLoaded){'✅'}else{'❌'})] Tiles del mapa cargados" -ForegroundColor $(if($hasMapLoaded){'Green'}else{'Red'})
    Write-Host ""

    if ($hasTimeout) {
        Write-Host "⚠️  PROBLEMA DETECTADO: Timeout de carga de tiles" -ForegroundColor Yellow -BackgroundColor DarkRed
        Write-Host "   Posibles causas:" -ForegroundColor Yellow
        Write-Host "   - API key inválida o sin restricciones correctas" -ForegroundColor White
        Write-Host "   - Facturación no habilitada en Google Cloud" -ForegroundColor White
        Write-Host "   - APIs no habilitadas (Maps SDK for Android / Places API)" -ForegroundColor White
        Write-Host ""
    }

    if ($hasAuthError) {
        Write-Host "❌ ERROR DE AUTENTICACIÓN DETECTADO" -ForegroundColor Red -BackgroundColor Yellow
        Write-Host "   Revisa el archivo de logs para ver el mensaje completo" -ForegroundColor Yellow
        Write-Host "   Líneas con 'Authorization' o 'API key':" -ForegroundColor White
        $logs | Select-String -Pattern "Authorization|API key" | Select-Object -First 5
        Write-Host ""
    }

    if (-not $hasPlayServices) {
        Write-Host "❌ GOOGLE PLAY SERVICES NO DISPONIBLE" -ForegroundColor Red
        Write-Host "   Usa un dispositivo real o un emulador con Play Store" -ForegroundColor Yellow
        Write-Host ""
    }

    if ($hasMapLoaded) {
        Write-Host "✅ ¡Los tiles cargaron correctamente!" -ForegroundColor Green -BackgroundColor DarkGreen
        Write-Host "   Si aún ves pantalla en blanco, puede ser un problema visual/tema" -ForegroundColor Yellow
        Write-Host ""
    }

    Write-Host "==================================================" -ForegroundColor Cyan
    Write-Host "  Primeras 20 líneas de los logs:" -ForegroundColor Cyan
    Write-Host "==================================================" -ForegroundColor Cyan
    $logs | Select-Object -First 20
    Write-Host ""
    Write-Host "📄 Archivo completo: $outputFile" -ForegroundColor Cyan

} else {
    Write-Host "⚠️  No se capturaron logs relevantes" -ForegroundColor Yellow
    Write-Host "   Verifica que:" -ForegroundColor White
    Write-Host "   - La app esté ejecutándose" -ForegroundColor White
    Write-Host "   - Hayas abierto la pantalla del mapa" -ForegroundColor White
    Write-Host "   - El dispositivo esté conectado correctamente" -ForegroundColor White
}

Write-Host ""
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host "  Diagnóstico Completado" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "📋 Próximos pasos:" -ForegroundColor Yellow
Write-Host "   1. Revisa el análisis automático arriba" -ForegroundColor White
Write-Host "   2. Abre el archivo: $outputFile" -ForegroundColor White
Write-Host "   3. Si hay error de autenticación, verifica SHA-1 y API key en Google Cloud" -ForegroundColor White
Write-Host "   4. Lee el archivo DIAGNOSTICO_MAPA.md para más detalles" -ForegroundColor White
Write-Host ""
Write-Host "Presiona Enter para salir..."
Read-Host
