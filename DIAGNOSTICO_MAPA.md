# Diagnóstico del Problema del Mapa en Blanco

## ✅ Cambios Aplicados

He actualizado `MapPickerActivity.kt` con:

1. **Corrección del warning de permisos**: Ahora el código verifica explícitamente los permisos con `hasLocationPermission()` antes de llamar a `map.isMyLocationEnabled` y usa `@SuppressWarnings("MissingPermission")` en las funciones que requieren permisos.

2. **Logs detallados**: Añadí múltiples puntos de logging para rastrear exactamente qué está pasando:
   - `onCreate` verifica Google Play Services
   - `onMapReady` confirma la inicialización del mapa
   - `setOnMapLoadedCallback` detecta cuando los tiles cargan exitosamente
   - Timeout de 6 segundos que avisa si los tiles NO cargan
   - Logs en todas las operaciones críticas

3. **Verificación de Google Play Services**: El código ahora verifica que Google Play Services esté disponible y muestra un diálogo si no lo está.

## 🔍 Cómo Diagnosticar el Problema

### Paso 1: Ejecutar la app y observar los Toasts

Después de instalar la nueva versión y abrir el mapa, deberías ver UNO de estos dos toasts:

- ✅ **"Mapa cargado correctamente"** → Los tiles cargaron. El problema está en otro lugar (API key válida, pero quizás restricciones o tema visual).
- ❌ **"El mapa no cargó correctamente. Revisa la API key, permisos o conexión."** → Los tiles NO cargaron. Problema de API key, restricciones, facturación o red.

### Paso 2: Revisar Logcat

Conecta tu dispositivo/emulador y ejecuta en PowerShell (desde cualquier carpeta):

```powershell
# Limpiar logs anteriores
adb logcat -c

# Filtrar solo mensajes relevantes
adb logcat | Select-String -Pattern "MapPickerActivity|Google Maps|Maps Android|Authentication|API"
```

Busca estos mensajes clave en la salida:

#### ✅ Mensajes de ÉXITO:
```
MapPickerActivity: onCreate started
MapPickerActivity: Google Play Services available
MapPickerActivity: Places API initialized successfully
MapPickerActivity: Map fragment found, calling getMapAsync
MapPickerActivity: onMapReady called - Map object initialized
MapPickerActivity: Map tiles loaded successfully
```

#### ❌ Mensajes de ERROR más comunes:

**Error de API key:**
```
Google Maps Android API: Authorization failure. Please see https://developers.google.com/maps/documentation/android-api/start for how to correctly set up the map.
Google Maps Android API: In the Google Developer Console (https://console.developers.google.com)
  Ensure that the "Google Maps Android API v2" is enabled.
  Ensure that the following Android Key exists:
    API key: AIzaSyCr68ksKvBZI1nu0g9m8mwfUZYbLDYxIXU
    Android Application (<cert_fingerprint>;<package_name>): XX:XX:...:XX;com.miguelrodriguez.rocaapp20
```

**Error de Google Play Services:**
```
MapPickerActivity: Google Play Services not available. Result code: X
```

**Error de red/tiles:**
```
MapPickerActivity: Map tiles did not load within timeout. Check API key / Play Services / network.
```

### Paso 3: Verificar la API Key de Google Maps

#### 3.1. Obtener el SHA-1 de tu keystore de depuración

Ejecuta en PowerShell:

```powershell
$keypath = "$env:USERPROFILE\.android\debug.keystore"
keytool -list -v -keystore $keypath -alias androiddebugkey -storepass android -keypass android
```

**Copia el SHA-1** (algo como: `XX:XX:XX:XX:...:XX`).

#### 3.2. Ir a Google Cloud Console

1. Abre: https://console.cloud.google.com/
2. Selecciona tu proyecto (el que tiene la API key `AIzaSyCr68ksKvBZI1nu0g9m8mwfUZYbLDYxIXU`)
3. Ve a: **APIs & Services** → **Credentials**
4. Encuentra la API key `AIzaSyCr68ksKvBZI1nu0g9m8mwfUZYbLDYxIXU` y haz clic en ella

#### 3.3. Verificar Restricciones

En la configuración de la API key, verifica:

**Application restrictions:**
- Si está en "None": temporalmente OK para pruebas (pero inseguro para producción)
- Si está en "Android apps": asegúrate de que exista una entrada con:
  - Package name: `com.miguelrodriguez.rocaapp20`
  - SHA-1 certificate fingerprint: (el que obtuviste arriba)

**API restrictions:**
- Si está en "Don't restrict key": temporalmente OK para pruebas
- Si está en "Restrict key": asegúrate de que estén habilitadas:
  - ✅ Maps SDK for Android
  - ✅ Places API
  - ✅ Geocoding API (opcional, para direcciones)

#### 3.4. Verificar APIs Habilitadas

Ve a: **APIs & Services** → **Library** y busca:

1. **Maps SDK for Android** → debe decir "API enabled" (verde)
2. **Places API** → debe decir "API enabled" (verde)

Si no están habilitadas, haz clic en cada una y presiona "Enable".

#### 3.5. Verificar Facturación

Ve a: **Billing** en el menú lateral

- Debe haber una cuenta de facturación activa vinculada al proyecto
- Google Maps requiere facturación habilitada (aunque hay cuota gratuita de $200/mes)
- Si no hay cuenta de facturación, los tiles NO cargarán

### Paso 4: Prueba Rápida con API Key Sin Restricciones (Temporal)

Para aislar si el problema son las restricciones:

1. En Google Cloud Console, **crea una API key nueva temporal**
2. En "Application restrictions" selecciona: **None**
3. En "API restrictions" selecciona: **Don't restrict key**
4. Copia la nueva key
5. Pégala en `app/src/main/res/values/strings.xml`:
   ```xml
   <string name="google_maps_key">TU_NUEVA_KEY_TEMPORAL_AQUI</string>
   ```
6. Haz rebuild e instala: `.\gradlew.bat installDebug`
7. Abre el mapa

- ✅ Si ahora funciona → el problema eran las restricciones de la key original
- ❌ Si sigue sin funcionar → el problema es otro (Play Services, red, facturación)

**⚠️ IMPORTANTE:** No dejes esta key sin restricciones en producción. Es solo para diagnóstico.

## 🛠️ Soluciones Comunes

### Problema: "Google Play Services not available"
**Solución:**
- Usa un dispositivo real o un AVD con **Google Play** (no Google APIs)
- Actualiza Google Play Services en el dispositivo
- En Android Studio: Tools → AVD Manager → crea un AVD con "Play Store" (icono de Play Store visible)

### Problema: "Authorization failure" en logcat
**Solución:**
- Agrega el package name + SHA-1 correcto a las restricciones de la API key
- Espera 2-5 minutos después de cambiar restricciones (Google tarda en propagar)
- Reinstala la app después de cambiar la key

### Problema: Tiles no cargan pero no hay errores
**Solución:**
- Verifica conexión a internet del dispositivo/emulador
- Verifica que el proyecto tenga facturación habilitada en Google Cloud
- Prueba con una API key sin restricciones (temporal)

### Problema: Pantalla gris con controles pero sin mapa
**Solución:**
- Este es típico de API key inválida o restricciones incorrectas
- Sigue los pasos de "Verificar la API Key" arriba

## 📋 Checklist Final

Marca cada item que hayas verificado:

- [ ] La app muestra toast (¿cuál?: "cargado" o "error"?)
- [ ] Logcat muestra "onMapReady called"
- [ ] Logcat muestra "Map tiles loaded successfully" (o timeout)
- [ ] Google Play Services está disponible (logcat: "Google Play Services available")
- [ ] SHA-1 del keystore de debug coincide con el configurado en Google Cloud
- [ ] Package name en Google Cloud es exactamente: `com.miguelrodriguez.rocaapp20`
- [ ] APIs habilitadas: Maps SDK for Android + Places API
- [ ] Proyecto tiene facturación habilitada en Google Cloud
- [ ] Probé con una API key sin restricciones (temporal)

## 🆘 Qué hacer si sigue sin funcionar

Si después de verificar todo lo anterior el mapa sigue en blanco:

1. **Pega aquí las primeras 50 líneas de logcat** que contengan "MapPickerActivity" o "Google Maps":
   ```powershell
   adb logcat | Select-String -Pattern "MapPickerActivity|Google Maps" | Select-Object -First 50 | Out-File logcat_output.txt
   ```
   Y comparte el contenido de `logcat_output.txt`

2. **Confirma estos datos**:
   - ¿Dispositivo real o emulador? (nombre del dispositivo/AVD)
   - ¿Qué toast aparece? ("cargado" / "error" / ninguno)
   - ¿El marcador fallback azul aparece después de 6 segundos?

3. **Toma una captura** del error en logcat si ves mensajes de "Authorization failure" o "API key".

---

## 📝 Resumen de Archivos Modificados

✅ `app/src/main/java/com/miguelrodriguez/rocaapp20/mecanicas/MapPickerActivity.kt`
- Corrección de warning de permisos
- Añadidos logs detallados para diagnóstico
- Verificación de Google Play Services
- Timeout y marcador fallback para visualizar problemas

✅ Warnings corregidos:
- ❌ "Call requires permission which may be rejected by user"
- ✅ Ahora usa `hasLocationPermission()` + `@SuppressWarnings("MissingPermission")`

---

**Próximo paso**: Ejecuta la app, observa el toast, revisa logcat con el comando de arriba y comparte los resultados. Con eso podré identificar exactamente cuál es el problema.
