# 🖼️ GUÍA VISUAL - Configurar API Key en Google Cloud

## PASO 1: Ve a Google Cloud Console

**URL:** https://console.cloud.google.com/apis/credentials

**Verás una página similar a esta:**

```
┌─────────────────────────────────────────────┐
│ APIs & Services > Credentials               │
├─────────────────────────────────────────────┤
│                                             │
│  API Keys                                   │
│  ┌──────────────────────────────────────┐  │
│  │ Name          │ API Restrictions   │  │
│  ├──────────────────────────────────────┤  │
│  │ Android Key   │ Maps SDK for ...   │  │
│  │ AIzaSyCr...u  │ Places API ...     │  │
│  │               │                    │  │
│  └──────────────────────────────────────┘  │
│                                             │
└─────────────────────────────────────────────┘
```

---

## PASO 2: Haz clic en tu API Key

Busca la que empieza con: `AIzaSyCr68ksKvBZI1nu0g...`

Haz clic en ella.

**Verás esta pantalla de edición:**

```
┌─────────────────────────────────────────────┐
│ Edit API Key                                │
├─────────────────────────────────────────────┤
│                                             │
│ Name:                                       │
│ [Android Key                            ]  │
│                                             │
│ API Key:                                    │
│ [AIzaSyCr68ksKvBZI1nu0g9m8mwfUZYbLDYxIXU] │
│                                             │
│ RESTRICCIONES DE APLICACIÓN                │
│ ┌──────────────────────────────────────┐   │
│ │ ○ None                               │   │
│ │ ○ Android apps                       │ ← SELECCIONA ESTO
│ │ ○ HTTP referrers                     │   │
│ │ ○ IP addresses                       │   │
│ └──────────────────────────────────────┘   │
│                                             │
└─────────────────────────────────────────────┘
```

---

## PASO 3: Selecciona "Android apps"

Cuando selecciones "Android apps", verás:

```
┌─────────────────────────────────────────────┐
│ RESTRICCIONES DE APLICACIÓN                 │
│ ○ Android apps (seleccionado)              │
│                                             │
│ Añadir package name and fingerprint         │
│ [Add Package Name and Fingerprint Button] │  ← HACA CLIC AQUÍ
│                                             │
│ Paquetes registrados:                       │
│ ┌──────────────────────────────────────┐   │
│ │ (vacío - aún no hay)                 │   │
│ └──────────────────────────────────────┘   │
│                                             │
└─────────────────────────────────────────────┘
```

---

## PASO 4: Añade Package + SHA-1

Se abrirá un diálogo:

```
┌────────────────────────────────────────────┐
│ Add Android Application                    │
├────────────────────────────────────────────┤
│                                            │
│ Package name:                              │
│ [com.miguelrodriguez.rocaapp20          ] │
│                                            │
│ SHA-1 certificate fingerprint:             │
│ [AB:CD:EF:01:23:45:67:89:AB:CD:EF:01   ] │
│                                            │
│ [Add]  [Cancel]                            │
│                                            │
└────────────────────────────────────────────┘
```

**VALORES A PONER:**
- **Package name:** `com.miguelrodriguez.rocaapp20`
- **SHA-1:** El que obtuviste con `keytool` (ejemplo: `AB:CD:EF:01:23:45:67:89:AB:CD:EF:01:23:45:67:89:AB:CD:EF:01`)

---

## PASO 5: Haz clic en "Add"

Después de hacer clic, verás:

```
┌─────────────────────────────────────────────┐
│ RESTRICCIONES DE APLICACIÓN                 │
│ ○ Android apps                              │
│                                             │
│ Paquetes registrados:                       │
│ ┌──────────────────────────────────────┐   │
│ │ com.miguelrodriguez.rocaapp20       │   │
│ │ AB:CD:EF:01:23:45:67:89:AB:...      │   │
│ │ [Remove]                             │   │
│ └──────────────────────────────────────┘   │
│                                             │
│ [Add package name and fingerprint]         │
│                                             │
└─────────────────────────────────────────────┘
```

✅ **Perfecto**, tu package ya está registrado.

---

## PASO 6: Configura API Restrictions

Baja en la misma página hasta "API Restrictions":

```
┌─────────────────────────────────────────────┐
│ RESTRICCIONES DE API                        │
│ ○ Don't restrict key                        │
│ ○ Restrict key (selecciona ESTO)            │
│                                             │
│ Cuando selecciones "Restrict key":          │
│ ┌──────────────────────────────────────┐   │
│ │ Dropdown: Select APIs                │   │
│ │ ┌──────────────────────────────────┐ │   │
│ │ │ □ Geocoding API                  │ │   │
│ │ │ □ Maps Embed API                 │ │   │
│ │ │ ✓ Maps SDK for Android           │ │   │  MARCA
│ │ │ □ Maps JavaScript API            │ │   │  ESTO
│ │ │ ✓ Places API                     │ │   │  ESTO
│ │ │ ...                              │ │   │
│ │ └──────────────────────────────────┘ │   │
│ └──────────────────────────────────────┘   │
│                                             │
└─────────────────────────────────────────────┘
```

✅ Asegúrate de que estén marcados:
- ✓ **Maps SDK for Android**
- ✓ **Places API**

---

## PASO 7: Guarda

Baja a la parte inferior y haz clic en:

```
┌─────────────────────────────────────────────┐
│                                             │
│                              [Cancel] [Save]│
│                                             │
└─────────────────────────────────────────────┘
```

Haz clic en **[Save]**

---

## PASO 8: Verifica que se Guardó

Deberías ver un mensaje:

```
✅ API Key updated successfully
```

---

## ✅ LISTO

Ahora:

1. **Espera 2-5 minutos** (Google propaga cambios)

2. **Reinstala la app:**
   ```powershell
   cd C:\Users\rooj_\AndroidStudioProjects\RocaApp2.0
   .\gradlew.bat clean installDebug
   ```

3. **Prueba el mapa**
   - Abre la app
   - Ve a "Reporte de Muestreo"
   - Click en "Ubicación..."
   - El mapa debería mostrar tiles ahora

---

## 🔴 NOTA IMPORTANTE

Si no ves el dropdown de APIs en el paso 6:

- Haz clic en **"Select APIs"** o en el campo dropdown
- Se desplegará una lista
- Busca "Maps SDK for Android"
- Haz clic en él (✓ aparecerá)
- Busca "Places API"
- Haz clic en él (✓ aparecerá)

---

## 📸 REFERENCIA RÁPIDA

| Paso | Acción | Valor |
|------|--------|-------|
| 1 | Abre | https://console.cloud.google.com/apis/credentials |
| 2 | Busca y abre | API key AIzaSyCr68ksKvBZI1nu0g... |
| 3 | Selecciona | "Android apps" |
| 4 | Añade Package | com.miguelrodriguez.rocaapp20 |
| 4 | Añade SHA-1 | AB:CD:EF:01:23:45:67:89... (tu SHA-1) |
| 6 | Habilita APIs | Maps SDK for Android + Places API |
| 7 | Guarda | Click [Save] |
| - | Espera | 2-5 minutos |
| - | Reinstala | .\gradlew.bat clean installDebug |

---

## 🆘 SI NO ENCUENTRAS ALGO

Captura de pantalla y comparte:
- ¿Qué ves en "APIs & Services > Credentials"?
- ¿Aparece tu API key?
- ¿En qué paso te atascas?

Con eso podré guiarte paso a paso.
