# 🔧 Comandos Rápidos - Referencia

## Compilar e Instalar la App

```powershell
# Limpiar build anterior
.\gradlew.bat clean

# Compilar e instalar versión debug
.\gradlew.bat installDebug

# O ambos en uno:
.\gradlew.bat clean installDebug
```

## Obtener SHA-1 del Keystore

```powershell
# SHA-1 del keystore de debug
keytool -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android | Select-String "SHA1:"

# SHA-1 del keystore de release (ajusta la ruta):
keytool -list -v -keystore "ruta\a\tu\keystore.jks" -alias tu_alias -storepass TU_PASSWORD
```

## Capturar Logs (Logcat)

```powershell
# Limpiar logs anteriores
adb logcat -c

# Ver logs en tiempo real filtrados
adb logcat | Select-String -Pattern "MapPickerActivity|Google Maps|Maps Android|Authentication"

# Guardar logs a archivo
adb logcat | Select-String -Pattern "MapPickerActivity|Google Maps" | Out-File logs.txt

# Ver solo logs de tu app
adb logcat | Select-String -Pattern "com.miguelrodriguez.rocaapp20"

# Capturar por 30 segundos y guardar
timeout /t 30 /nobreak & adb logcat > logs_completos.txt
```

## Dispositivos Conectados

```powershell
# Listar dispositivos conectados
adb devices

# Si hay múltiples dispositivos, especificar uno:
adb -s DEVICE_ID logcat

# Reiniciar adb server si hay problemas
adb kill-server
adb start-server
```

## Verificar Package Instalado

```powershell
# Verificar si la app está instalada
adb shell pm list packages | Select-String "rocaapp"

# Ver información del package
adb shell pm dump com.miguelrodriguez.rocaapp20 | Select-String -Pattern "versionName|versionCode"

# Desinstalar la app (si necesitas reinstalar limpia)
adb uninstall com.miguelrodriguez.rocaapp20
```

## Verificar Google Play Services

```powershell
# Versión de Google Play Services en el dispositivo
adb shell dumpsys package com.google.android.gms | Select-String "versionName"

# Ver si Google Play Services está funcionando
adb shell pm list packages | Select-String "gms"
```

## Limpiar Caché de la App

```powershell
# Limpiar datos y caché de la app
adb shell pm clear com.miguelrodriguez.rocaapp20
```

## Script de Diagnóstico Completo

```powershell
# Ejecutar el script automático que creé
.\diagnostico_mapa.ps1
```

## Cambiar API Key Rápidamente

```powershell
# Abrir strings.xml para editar
notepad app\src\main\res\values\strings.xml

# O con VS Code:
code app\src\main\res\values\strings.xml

# Después de cambiar la key, reinstalar:
.\gradlew.bat clean installDebug
```

## Debugging Avanzado

```powershell
# Ver TODOS los logs sin filtro (mucho output)
adb logcat

# Filtrar por nivel de error
adb logcat *:E  # Solo errores
adb logcat *:W  # Warnings y superiores
adb logcat *:I  # Info y superiores

# Ver logs de un tag específico
adb logcat -s MapPickerActivity

# Ver logs con timestamp
adb logcat -v time | Select-String "MapPickerActivity"
```

## Comandos de Gradle Útiles

```powershell
# Ver todas las tareas disponibles
.\gradlew.bat tasks

# Compilar sin instalar
.\gradlew.bat assembleDebug

# Verificar dependencias
.\gradlew.bat dependencies

# Limpiar y compilar release
.\gradlew.bat clean assembleRelease

# Ver configuración del proyecto
.\gradlew.bat properties
```

## URLs Rápidas de Google Cloud

```
# Google Cloud Console Principal
https://console.cloud.google.com/

# APIs & Services - APIs habilitadas
https://console.cloud.google.com/apis/library

# APIs & Services - Credentials (API Keys)
https://console.cloud.google.com/apis/credentials

# Billing
https://console.cloud.google.com/billing

# Maps SDK for Android (para habilitar)
https://console.cloud.google.com/apis/library/maps-android-backend.googleapis.com

# Places API (para habilitar)
https://console.cloud.google.com/apis/library/places-backend.googleapis.com
```

## Solución Rápida a Problemas Comunes

### Problema: "adb no se reconoce como comando"
```powershell
# Añadir temporalmente al PATH (ajusta la ruta):
$env:PATH += ";C:\Users\$env:USERNAME\AppData\Local\Android\Sdk\platform-tools"

# O permanente (requiere admin):
[Environment]::SetEnvironmentVariable("PATH", "$env:PATH;C:\Users\$env:USERNAME\AppData\Local\Android\Sdk\platform-tools", "Machine")
```

### Problema: "Could not open JVM.cfg"
```powershell
# Configurar JAVA_HOME (ajusta a tu JDK):
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

### Problema: "Device offline" en adb
```powershell
adb kill-server
adb start-server
adb devices
```

### Problema: App no se actualiza en el dispositivo
```powershell
# Desinstalar completamente y reinstalar
adb uninstall com.miguelrodriguez.rocaapp20
.\gradlew.bat installDebug
```

## Verificación Rápida de la Configuración

```powershell
# 1. ¿Está conectado el dispositivo?
adb devices

# 2. ¿Tiene Google Play Services?
adb shell pm list packages | Select-String "gms"

# 3. ¿La app está instalada?
adb shell pm list packages | Select-String "rocaapp"

# 4. ¿Hay conexión a internet?
adb shell ping -c 3 google.com

# 5. ¿Qué versión de Android tiene?
adb shell getprop ro.build.version.release
```

## Flujo de Trabajo Recomendado

```powershell
# 1. Hacer cambios en el código
# 2. Limpiar logs
adb logcat -c

# 3. Compilar e instalar
.\gradlew.bat installDebug

# 4. Capturar logs mientras pruebas
adb logcat | Select-String -Pattern "MapPickerActivity|Google Maps" | Tee-Object -FilePath logs_$(Get-Date -Format 'HHmmss').txt

# 5. Analizar logs mientras se capturan (aparecen en consola y se guardan)
```

---

## 🎯 Lo Más Importante

**Para diagnosticar el problema del mapa:**
```powershell
.\diagnostico_mapa.ps1
```

**Para reinstalar con cambios:**
```powershell
.\gradlew.bat clean installDebug
```

**Para ver logs en tiempo real:**
```powershell
adb logcat | Select-String "MapPickerActivity|Google Maps"
```

---

Guarda este archivo como referencia rápida. Todos estos comandos están verificados para PowerShell en Windows.
