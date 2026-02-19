# 🔴 SOLUCIÓN RÁPIDA - El Mapa No Cargó

Has recibido este error:
```
"El mapa no cargó correctamente. Revisa la API key, permisos o conexión."
```

## 🎯 CAUSA 95% PROBABLE

Tu API key de Google Maps está configurada, pero **no está autorizada para tu package name + SHA-1**.

---

## 🚀 SOLUCIÓN EN 5 PASOS

### PASO 1: Obtén tu SHA-1 (copia exacto)

Ejecuta en PowerShell:

```powershell
keytool -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android | Select-String "SHA1:"
```

**Verás algo como:** `SHA1: AB:CD:EF:01:23:45:67:89:AB:CD:EF:01:23:45:67:89:AB:CD:EF:01`

**Copia solo la parte después de "SHA1: "** (sin "SHA1:")

### PASO 2: Ve a Google Cloud Console

Abre en tu navegador:
```
https://console.cloud.google.com/apis/credentials
```

### PASO 3: Busca tu API Key

- Busca la key que empieza con: `AIzaSyCr68ksKvBZI1nu0g...`
- Haz **clic en ella** para editar

### PASO 4: Configurar Restricciones

En la página de edición de la key:

#### 4a. Application restrictions
- Selecciona: **"Android apps"**
- Haz clic en: **"Add package name and fingerprint"**
- **Package name:** `com.miguelrodriguez.rocaapp20`
- **SHA-1 certificate fingerprint:** (el que copiaste en PASO 1)
- Haz clic en **"Add"**

#### 4b. API restrictions
- Selecciona: **"Restrict key"**
- En el dropdown, busca y selecciona:
  - ✅ **Maps SDK for Android**
  - ✅ **Places API**
- Haz clic en **"Save"**

### PASO 5: Espera y Reinstala

- **Espera 2-5 minutos** (Google tarda en propagar cambios)
- Reinstala la app:

```powershell
cd C:\Users\rooj_\AndroidStudioProjects\RocaApp2.0
.\gradlew.bat clean installDebug
```

- Abre la app y prueba el mapa

---

## ✅ Si Todavía No Funciona

Verifica TAMBIÉN esto:

### 1. ¿Las APIs están habilitadas?

Ve a: https://console.cloud.google.com/apis/library

Busca y habilita (si no lo están):
- ✅ **Maps SDK for Android**
- ✅ **Places API**
- ✅ **Geocoding API** (opcional)

### 2. ¿Hay Facturación?

Ve a: https://console.cloud.google.com/billing

Debe haber una **cuenta de facturación activa** vinculada al proyecto.

*(Google Maps requiere facturación, aunque tienes $200 gratis/mes)*

### 3. ¿El SHA-1 es exactamente el mismo?

A veces hay espacios o caracteres mal copiados. 

Compara el SHA-1 de estos dos comandos:

```powershell
# Desde Android Studio (directamente):
adb shell dumpsys package com.miguelrodriguez.rocaapp20 | Select-String "sha256"

# O desde el keystore (como hiciste antes):
keytool -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android | Select-String "SHA1:"
```

Deberían coincidir.

---

## 🆘 Prueba de Emergencia (Si urgente necesitas que funcione)

Si necesitas que funcione AHORA mientras arreglas Google Cloud:

**OPCIÓN A: Crea una key sin restricciones (TEMPORAL)**

1. En Google Cloud Console → Credentials → Create new API Key
2. Selecciona **"API keys"** (no "Service account")
3. En "Application restrictions" selecciona: **"None"**
4. En "API restrictions" selecciona: **"Don't restrict key"**
5. Copia la key
6. Pégala en `app/src/main/res/values/strings.xml`:

```xml
<string name="google_maps_key">TU_NEW_KEY_AQUI</string>
```

7. Reinstala:

```powershell
.\gradlew.bat clean installDebug
```

**⚠️ IMPORTANTE:** Esto es solo TEMPORAL. Cuando funcione, repite el proceso de restricciones con una key nueva y auténtica.

---

## 📋 Checklist de Verificación

Marca lo que hayas hecho:

- [ ] Obtuve mi SHA-1 con el comando keytool
- [ ] Entré a Google Cloud Console
- [ ] Edité la API key de Maps
- [ ] Añadí: Package name + SHA-1 en "Android apps"
- [ ] Seleccioné "Restrict key" y habilité Maps SDK + Places API
- [ ] Esperé 2-5 minutos
- [ ] Reinstalé con: `.\gradlew.bat clean installDebug`
- [ ] Probé el mapa en la app

---

## 🔍 Si Sigue Sin Funcionar - Captura Logs

Ejecuta este script para capturar logs de error:

```powershell
.\capturar_logs.ps1
```

El script:
1. Abre el archivo `logs_mapa_error.txt`
2. Busca "Authorization failure" o "API key"
3. Comparte esas líneas aquí

Con eso podré saber exactamente qué está rechazando Google.

---

## 🎯 Resumen Rápido

**Si el toast dice: "El mapa no cargó correctamente"**

→ Es 95% seguro que es la **API key con restricciones incorrectas**

Solución:
1. SHA-1 de keystore
2. Google Cloud Console
3. Editar API key
4. Añadir: Package name + SHA-1
5. Habilitar: Maps SDK + Places API
6. Esperar 2-5 minutos
7. Reinstalar app

---

**¿Cuál es tu SHA-1? Dímelo y te guío si hay algún paso que no entiendas.**
