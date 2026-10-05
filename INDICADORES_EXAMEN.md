# 📋 Indicadores de Respuestas Correctas/Incorrectas en Exámenes

## 🎯 Funcionalidad Implementada

Se ha añadido la capacidad de visualizar indicadores visuales (✓ y ✗) cuando el usuario revisa un examen que ya ha completado anteriormente.

### ✨ Características Implementadas

#### 1. **Indicadores Visuales**
- ✓ (Paloma) en color **VERDE** - indica respuesta correcta
- ✗ (Tacha) en color **ROJO** - indica respuesta seleccionada pero incorrecta

#### 2. **Modo Revisión**
- Cuando se cargan respuestas guardadas, las opciones se deshabilitan
- El usuario no puede modificar las respuestas al revisar
- El botón "Enviar examen" cambia a "Revisar examen" y se deshabilita

#### 3. **Almacenamiento de Respuestas Correctas**
- Ahora se guardan tanto las respuestas seleccionadas como las respuestas correctas en Firebase
- Las respuestas correctas se usan al comparar con las seleccionadas

## 🔧 Cambios Realizados

### Archivo: `Examen17025GestionActivity.kt`

#### Cambio 1: Variable de Estado
```kotlin
private var esRevisionExamen = false
```
- Controla si se está revisando un examen anterior

#### Cambio 2: Función `guardarExamen()`
- Se añadió `respuestasCorrectasMap` para almacenar las respuestas correctas
- Ahora guarda en Firebase: `"respuestasCorrectas" to respuestasCorrectasMap`

#### Cambio 3: Función `cargarRespuestasGuardadas()`
- Lee las respuestas guardadas Y las respuestas correctas desde Firebase
- Compara ambas y agrega indicadores visuales:
  - Texto de la opción: `"${opcion} ✓"` o `"${opcion} ✗"`
  - Colores: Verde para correctas (Color.GREEN), Rojo para incorrectas (Color.RED)
- Deshabilita todos los RadioButtons
- Cambia el contexto del botón a "Revisar examen" y lo deshabilita

## 📊 Flujo de Funcionamiento

### Primera vez que el usuario abre el examen:
1. Se cargan las preguntas y opciones
2. Usuario responde todas las preguntas
3. Usuario presiona "Enviar examen"
4. Se guarda en Firebase:
   - respuestas (seleccionadas por el usuario)
   - respuestasCorrectas (respuestas correctas de cada pregunta)
   - calificación

### Cuando el usuario regresa a revisar:
1. Se cargan las respuestas guardadas
2. Se cargan las respuestas correctas guardadas
3. Se comparan y se mostrar indicadores:
   - ✓ en verde = opción seleccionada fue correcta
   - ✗ en rojo = opción seleccionada fue incorrecta (solo se muestra si es la que seleccionó)
   - La respuesta correcta siempre se muestra en verde
4. UI se coloca en modo "solo lectura" (RadioButtons deshabilitados)
5. Botón cambia a "Revisar examen" (deshabilitado)

## 🔍 Ejemplos de presentación al usuario

Cuando revisa un examen donde:
- Pregunta 1: Respondió correctamente
  ```
  📌 INACAL ✓ (Verde)
  - EMA
  - ICA
  - ONNCCE
  ```

- Pregunta 2: Respondió incorrectamente
  ```
  📌 Se aplica una no conformidad. ✓ (Verde) ← Respuesta correcta
  - Se notifica al cliente. ✗ (Rojo) ← Lo que el usuario seleccionó
  - Se capacita nuevamente al personal.
  ```

## 🛠️ Compatibilidad

- ✅ Funciona con exámenes anteriores (busca la respuesta correcta en el mapa local si no existe en Firebase)
- ✅ Compatible con Firebase Realtime Database
- ✅ Mantiene la calificación visible

## 📝 Notas Técnicas

- Los indicadores se agregan al texto del RadioButton original
- Los colores se aplican usando `setTextColor()`
- Los RadioButtons se deshabilitan con `isEnabled = false`
- La lógica de carga es asincrónica usando Firebase listeners
- Se utiliza `maxByOrNull` para obtener el examen más reciente del usuario

## 🚀 Próximas Mejoras Opcionales

- Agregar un botón "Rehacer examen" que limpie las respuestas y permita intentar de nuevo
- Mostrar un resumen visual de aciertos/errores por categoría
- Permitir ver solo las preguntas fallidas

