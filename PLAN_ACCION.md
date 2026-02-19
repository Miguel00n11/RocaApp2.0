# ✅ PLAN DE ACCIÓN INMEDIATO

## EL PROBLEMA
Toast dice: "El mapa no cargó correctamente. Revisa la API key, permisos o conexión."

## LA SOLUCIÓN
Tu API key necesita ser autorizada con el SHA-1 correcto de tu keystore.

---

## 🚀 ACCIONES EN ORDEN

### 1️⃣ OBTÉN TU SHA-1 (2 minutos)

Abre PowerShell y ejecuta:

```powershell
keytool -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android | Select-String "SHA1:"
```

**Verás algo como:**
```
SHA1: AB:CD:EF:01:23:45:67:89:AB:CD:EF:01:23:45:67:89:AB:CD:EF:01
```

**COPIA SOLO ESTO (sin "SHA1:"):**
```
AB:CD:EF:01:23:45:67:89:AB:CD:EF:01:23:45:67:89:AB:CD:EF:01
```

---

### 2️⃣ VE A GOOGLE CLOUD CONSOLE (1 minuto)

**ABRE EN NAVEGADOR:**
```
https://console.cloud.google.com/apis/credentials
```

**Busca la key que empieza con:** `AIzaSyCr68ksKvBZI1nu0g...`

**Haz clic en ella**

---

### 3️⃣ EDITA LAS RESTRICCIONES (3 minutos)

#### SECCIÓN: "Application restrictions"

1. Selecciona: **"Android apps"**
2. Haz clic en: **"Add package name and fingerprint"**
3. 
   - **Package name:** `com.miguelrodriguez.rocaapp20`
   - **SHA-1 certificate fingerprint:** (el que copiaste en Paso 1)

4. Haz clic en **"Add"**

#### SECCIÓN: "API restrictions"

1. Selecciona: **"Restrict key"** (dropdown)
2. En la lista, busca y **marca:**
   - ✅ Maps SDK for Android
   - ✅ Places API

3. Haz clic en **"Save"**

---

### 4️⃣ ESPERA Y REINSTALA (5 minutos)

**Espera 2-5 minutos** (Google propaga cambios lentamente)

Luego, abre PowerShell y ejecuta:

```powershell
cd C:\Users\rooj_\AndroidStudioProjects\RocaApp2.0
.\gradlew.bat clean installDebug
```

---

### 5️⃣ PRUEBA (1 minuto)

- Abre la app
- Navega al mapa (botón "Ubicación...")
- **Si funciona:** ✅ problema resuelto
- **Si NO funciona:** Continúa leyendo ↓

---

## 🔴 SI SIGUE SIN FUNCIONAR

### Verifica ESTO:

#### A) ¿Las APIs están habilitadas?

Abre: https://console.cloud.google.com/apis/library

Busca:
- **Maps SDK for Android** → debe estar ENABLED (verde)
- **Places API** → debe estar ENABLED (verde)

Si no están, haz clic en cada una y presiona **"Enable"**

#### B) ¿Hay facturación?

Abre: https://console.cloud.google.com/billing

Debe haber una **cuenta activa** (verde)

Si no hay, crea una (Google Maps requiere facturación, aunque tienes $200 gratis/mes)

#### C) ¿El SHA-1 está exacto?

A veces se copian espacios extras o caracteres. 

Verifica **carácter por carácter** que coincida:
- El SHA-1 de tu keystore (paso 1)
- El SHA-1 que añadiste en Google Cloud (paso 3)

---

## 🆘 ÚLTIMA OPCIÓN (Si urgente)

Si no puedes acceder a Google Cloud o quieres probar rápido:

**Solución temporal:**

1. Crea nueva API key sin restricciones (SOLO PARA PRUEBAS)
2. Pega en `app/src/main/res/values/strings.xml`:

```xml
<string name="google_maps_key">YOUR_NEW_UNRESTRICTED_KEY_HERE</string>
```

3. Reinstala:
```powershell
.\gradlew.bat clean installDebug
```

**⚠️ ESTO ES TEMPORAL** - Después vuelve a hacer los pasos 1-5 con una key restringida correctamente.

---

## 📝 CHECKLIST

Marca cada paso completado:

- [ ] Obtuve mi SHA-1
- [ ] Entré a Google Cloud Console
- [ ] Edité la API key
- [ ] Añadí: Package name + SHA-1 en "Android apps"
- [ ] Seleccioné restricciones: Maps SDK + Places API
- [ ] Esperé 2-5 minutos
- [ ] Reinstalé con: `.\gradlew.bat clean installDebug`
- [ ] Probé el mapa

---

## ✅ SI YA FUNCIONA

¡Perfecto! Ya puedes:

✅ Abrir la app  
✅ Ir a: "Reporte de Muestreo de Material" → botón "Ubicación..."  
✅ Hacer click en el mapa para seleccionar un pin  
✅ Los valores de latitud y longitud se llenarán automáticamente  

---

## 🤔 ¿PREGUNTAS?

Si después de esto sigue sin funcionar, comparte:

1. ¿Qué SHA-1 obtuviste?
2. ¿Qué error aparece en logcat? (ejecuta `.\capturar_logs.ps1`)
3. ¿Ves el marcador azul al menos?

Con eso puedo ayudarte más específicamente.

---

**¡Comienza por el PASO 1 ahora!**
