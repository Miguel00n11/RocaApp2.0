package com.miguelrodriguez.rocaapp20

import com.google.firebase.database.DatabaseReference

/**
 * Contenido textual de las normas, extraído de los PDFs oficiales.
 * Se sube una sola vez a Realtime Database y luego se consulta desde ahí.
 */
object ContenidoNormas {

    fun tieneContenido(numero: String) = bancos.containsKey(numero)

    /** Sube el contenido de una norma a Firebase si aún no existe. */
    fun subirSiAusente(
        numero: String,
        db: DatabaseReference,
        onListo: () -> Unit
    ) {
        val data = bancos[numero] ?: run { onListo(); return }
        val ref = db.child("Capacitaciones").child("Material").child(numero)

        ref.child("titulo").get().addOnSuccessListener { snap ->
            if (snap.exists()) {
                onListo()
            } else {
                ref.setValue(data).addOnCompleteListener { onListo() }
            }
        }.addOnFailureListener { onListo() }
    }

    // ── Banco de contenido ────────────────────────────────────────────────────

    private val bancos: Map<String, Map<String, Any>> = mapOf(
        "109" to norma109(),
        "17025" to norma17025(),
        "008" to norma008(),
        "083" to norma083(),
        "148" to norma148(),
        "156" to norma156(),
        "159" to norma159(),
        "161" to norma161()
    )

    private fun norma17025(): Map<String, Any> = mapOf(
        "titulo" to "NMX-EC-17025 — Requisitos para Laboratorios de Ensayo",
        "secciones" to mapOf(
            "0" to mapOf(
                "titulo" to "Objetivo y Alcance",
                "contenido" to """
OBJETIVO
Establece los requisitos generales para la competencia, imparcialidad e implementación consistente de los laboratorios de ensayo y de calibración.

ALCANCE
• Aplicable a todos los laboratorios independientemente del número de personal o extensión de sus actividades.
• Incluye laboratorios de primera, segunda y tercera parte.
• Abarca muestreo, ensayos, calibraciones y verificaciones.
• Aplica a laboratorios que desarrollan métodos estándar, no estándar y desarrollados internamente.

IMPORTANCIA EN LABORATORIOS DE CONCRETO
Un laboratorio de ensayo de concreto acreditado bajo esta norma demuestra que:
• Opera con competencia técnica demostrable.
• Produce resultados técnicamente válidos.
• Sus resultados son reconocidos nacional e internacionalmente.
""".trim()
            ),
            "1" to mapOf(
                "titulo" to "Imparcialidad y Confidencialidad",
                "contenido" to """
IMPARCIALIDAD
• El laboratorio debe comprometerse con la imparcialidad en todas sus actividades.
• Las actividades NO deben estar comprometidas por presiones comerciales, financieras o de otro tipo.
• Identificar y documentar riesgos a la imparcialidad de forma continua.
• Si se identifica un riesgo, el laboratorio debe demostrar cómo lo elimina o minimiza.

Ejemplo de riesgo: un laboratorio que también fabrica el concreto que ensaya podría tener conflicto de interés.

CONFIDENCIALIDAD
• La información obtenida de los clientes es propiedad del cliente.
• Solo se revela con autorización escrita del cliente.
• Excepción: cuando la ley o autoridad competente lo requiera.
• El personal debe firmar acuerdos de confidencialidad.
""".trim()
            ),
            "2" to mapOf(
                "titulo" to "Requisitos de Estructura",
                "contenido" to """
ENTIDAD LEGAL
• El laboratorio debe ser una persona jurídica o parte definida de una persona jurídica.
• Debe documentar su estructura organizacional.

RESPONSABILIDADES
• Definir responsabilidades, autoridades e inter-relaciones del personal.
• Procedimientos para asegurar imparcialidad y protección de la confidencialidad.

PERSONAL CLAVE (mínimo requerido)
1. Director Técnico — responsable de las operaciones técnicas.
2. Personal técnico de ensayo — realiza las actividades de ensayo.
3. Personal de calidad — asegura el sistema de gestión.

COBERTURA TÉCNICA
• El laboratorio debe tener personal que cubra todas sus actividades acreditadas.
• Cuando sea parte de una organización mayor: demostrar que las presiones comerciales no afectan la imparcialidad.
""".trim()
            ),
            "3" to mapOf(
                "titulo" to "Recursos: Personal",
                "contenido" to """
REQUISITOS DEL PERSONAL
• El personal que afecta las actividades del laboratorio debe actuar imparcialmente.
• Competencia demostrada en educación, calificaciones, entrenamiento, habilidades técnicas y experiencia.
• Autorizar formalmente al personal para actividades específicas (ensayos, operación de equipos, emisión de informes).

REGISTROS QUE DEBE MANTENER EL LABORATORIO
• Competencia de cada integrante del personal.
• Autorizaciones para actividades específicas.
• Firmas autorizadas (para validar informes y certificados).
• Programa de entrenamiento y evaluaciones.

PARA PERSONAL DE CONCRETO
El técnico de ensayo de concreto debe demostrar competencia en:
• Elaboración de cilindros (NMX-C-159)
• Cabeceo (NMX-C-109)
• Ensayo de compresión (NMX-C-083)
• Muestreo del concreto
• Calibración básica de equipos
""".trim()
            ),
            "4" to mapOf(
                "titulo" to "Recursos: Instalaciones y Condiciones Ambientales",
                "contenido" to """
REQUISITOS DE LAS INSTALACIONES
• Las instalaciones no deben afectar adversamente la validez de los resultados.
• Condiciones ambientales requeridas: temperatura, humedad, iluminación, vibración, suministro eléctrico.

CONTROL AMBIENTAL EN CUARTO DE CURADO (Concreto)
• Temperatura: 23°C ± 2°C (rango 21°C a 25°C)
• Humedad relativa: ≥ 95%
• Los especímenes deben estar cubiertos de agua o mantenerse húmedos continuamente
• REGISTRAR temperatura y humedad al menos una vez al día

SEPARACIÓN DE ÁREAS
• Separar áreas donde se realizan actividades incompatibles.
• Controlar acceso a áreas que afecten la calidad de los ensayos.
• Limpiar y mantener las instalaciones según procedimientos documentados.
""".trim()
            ),
            "5" to mapOf(
                "titulo" to "Recursos: Equipos",
                "contenido" to """
REQUISITOS DE LOS EQUIPOS
• El laboratorio debe tener acceso a todos los equipos necesarios.
• Cada equipo debe tener identificación única.
• Lista maestra de equipos actualizada.

CALIBRACIÓN Y VERIFICACIÓN
• Establecer programas de calibración antes del uso.
• Solo usar equipos calibrados cuyo estado esté vigente.
• Etiqueta de estado de calibración en cada equipo (fecha de calibración y próxima fecha).
• Registros de calibración: fecha, resultados, criterios de aceptación, ajustes realizados.

EQUIPOS CRÍTICOS PARA LABORATORIO DE CONCRETO
• Máquina de compresión: calibrar según NMX o ASTM E4 anualmente.
• Balanzas: verificar con pesas patrón certificadas.
• Termómetros: calibrar o verificar periódicamente.
• Vernier/calibradores: verificar con patrones trazables.

CUANDO UN EQUIPO FALLA
• Retirar de servicio inmediatamente.
• Identificar con etiqueta 'FUERA DE SERVICIO'.
• Evaluar el impacto en ensayos previos realizados con ese equipo.
• Notificar al cliente si los resultados pueden haber sido afectados.
""".trim()
            ),
            "6" to mapOf(
                "titulo" to "Trazabilidad Metrológica",
                "contenido" to """
DEFINICIÓN
Propiedad de un resultado de medición mediante la cual dicho resultado puede relacionarse con una referencia mediante una cadena ininterrumpida y documentada de calibraciones, cada una de las cuales contribuye a la incertidumbre de medición.

REQUISITOS
• Los resultados de medición deben ser trazables al SI (Sistema Internacional de Unidades).
• Calibración realizada por organismos acreditados (CENAM, EMA) o laboratorios de metrología nacional.

CADENA DE TRAZABILIDAD
SI → Laboratorio Nacional de Metrología (CENAM) → Laboratorio Acreditado → Su Laboratorio

PATRONES DE REFERENCIA
• Intervalos de calibración definidos según el uso y estabilidad del instrumento.
• Verificar antes y después de transportar patrones.
• Procedimientos para protección y almacenamiento.

CUANDO LA TRAZABILIDAD DIRECTA AL SI NO ES POSIBLE
• Demostrar equivalencia de resultados con otros laboratorios mediante comparaciones interlaboratorios.
• Participar en programas de ensayos de aptitud (proficiency testing).
""".trim()
            ),
            "7" to mapOf(
                "titulo" to "Selección y Verificación de Métodos",
                "contenido" to """
SELECCIÓN DE MÉTODOS
Prioridad (de mayor a menor):
1. Métodos publicados en normas internacionales (ISO, ASTM, EN)
2. Métodos publicados en normas nacionales (NMX, NOM)
3. Métodos de organizaciones técnicas reconocidas
4. Métodos científicos publicados
5. Métodos desarrollados internamente (requieren validación completa)

VERIFICACIÓN DE MÉTODOS
Antes de usar un método estándar, verificar que el laboratorio puede aplicarlo correctamente:
• Verificar que los resultados del laboratorio son comparables con los requisitos del método.
• Documentar la verificación.

VALIDACIÓN (para métodos no estándar)
• Confirmar que los requisitos para el uso específico son cumplidos.
• Registrar los resultados de validación.

ESTIMACIÓN DE LA INCERTIDUMBRE
• Para todos los métodos de ensayo: identificar componentes de incertidumbre y hacer una estimación.
• Incluir en el informe cuando es relevante para la interpretación del resultado.
""".trim()
            ),
            "8" to mapOf(
                "titulo" to "Muestreo y Manipulación de Ítems",
                "contenido" to """
MUESTREO
• Cuando el laboratorio realiza muestreo: registrar fecha, ubicación, condiciones ambientales, técnica.
• Plan de muestreo estadísticamente válido y basado en métodos específicos.

RECEPCIÓN DE ÍTEMS
• Registrar la condición del ítem al recibirlo (fotografía si hay daños).
• Asignar identificación única que permanece durante toda su estancia en el laboratorio.
• Registrar: fecha de recepción, identificación del cliente, descripción, condición.

CILINDROS DE CONCRETO — recepción
• Verificar que la etiqueta del cilindro corresponde a la boleta de muestreo.
• Registrar si llega con daños (fracturas, cabeceo dañado).
• Colocar inmediatamente en cuarto de curado si es muestreo en campo.

ALMACENAMIENTO
• Condiciones apropiadas para el tipo de ítem.
• Prevenir deterioro, contaminación, pérdida o daño.
• Mantener trazabilidad durante todo el proceso.
""".trim()
            ),
            "9" to mapOf(
                "titulo" to "Registros Técnicos e Incertidumbre",
                "contenido" to """
REGISTROS TÉCNICOS
• Suficientes para reproducir el ensayo en condiciones tan cercanas al original como sea posible.
• Incluir: identificación del personal, equipo usado, fecha y condiciones del ensayo.

CORRECCIONES EN REGISTROS
• Tachar el error con una línea sencilla (nunca borrar).
• Escribir el valor correcto al lado.
• Firmar y fechar la corrección.
• Para registros electrónicos: dejar trazabilidad de la corrección.

INCERTIDUMBRE DE MEDICIÓN
Componentes principales en ensayo de compresión de concreto:
• Tipo A (estadístico): repetibilidad de la máquina, variabilidad de especímenes similares.
• Tipo B (no estadístico): calibración de la máquina, resolución del indicador, condiciones del cabeceo.

CUÁNDO REPORTAR INCERTIDUMBRE
• Cuando el cliente la requiere.
• Cuando es relevante para conformidad con especificaciones.
• Cuando la incertidumbre afecta la interpretación del resultado.
""".trim()
            ),
            "10" to mapOf(
                "titulo" to "Informe de Resultados",
                "contenido" to """
CONTENIDO MÍNIMO DEL INFORME DE ENSAYO
• Título e identificación única (número de informe, paginación).
• Nombre y dirección del laboratorio.
• Nombre y dirección del cliente.
• Identificación del método utilizado (clave de la norma).
• Descripción, condición e identificación del ítem.
• Fecha de recepción y fecha(s) de ensayo.
• Resultados con unidades de medida.
• Nombre, función y firma del responsable.
• Declaración de que los resultados se refieren solo a los ítems ensayados.

DECLARACIONES DE CONFORMIDAD
Cuando el informe incluye declaración de conformidad con especificación:
• Documentar qué regla de decisión se aplicó.
• Considerar la incertidumbre de medición.

MODIFICACIONES AL INFORME
• Solo puede modificarse emitiendo un nuevo informe.
• El nuevo informe debe referir al original que reemplaza.
• Dejar registro del motivo de la modificación.
""".trim()
            ),
            "11" to mapOf(
                "titulo" to "Sistema de Gestión",
                "contenido" to """
OPCIONES DE IMPLEMENTACIÓN

OPCIÓN A — Requisitos de gestión propios de la norma
• Documentación: política de calidad, objetivos, manual de calidad.
• Control de documentos: aprobar, revisar, actualizar y controlar documentos.
• Control de registros: identificar, almacenar, proteger, recuperar, retener y disponer.
• Acciones para abordar riesgos y oportunidades.
• Mejora: identificar y tratar no conformidades, acciones correctivas.
• Auditorías internas: planificadas, independientes, a intervalos definidos.
• Revisiones de la dirección: al menos anualmente.

OPCIÓN B — ISO 9001
Cumplir con todos los requisitos de ISO 9001 (Sistema de Gestión de la Calidad) en todas las actividades del laboratorio que estén dentro del alcance del sistema de gestión.

QUEJAS Y TRABAJO NO CONFORME
• Proceso documentado para recibir y tratar quejas de clientes.
• Cuando se identifica trabajo no conforme: detener, retener, notificar al cliente, evaluar impacto.
• Acciones correctivas para prevenir recurrencia.
""".trim()
            )
        )
    )

    private fun norma008(): Map<String, Any> = mapOf(
        "titulo" to "LIC y NOM-008-SE-2021 — Sistema General de Unidades de Medida",
        "secciones" to mapOf(
            "0" to mapOf(
                "titulo" to "Objetivo y Campo de Aplicación",
                "contenido" to """
OBJETIVO
Establecer las características y definiciones de las unidades de medida del Sistema General de Unidades de Medida (SGUM) de México, basado en el Sistema Internacional de Unidades (SI).

CAMPO DE APLICACIÓN
• De observancia obligatoria en todo el territorio nacional.
• Aplica a transacciones comerciales, publicidad, documentos técnicos y de ingeniería.
• Aplica en laboratorios de ensayo, calibración y metrología.

FUNDAMENTO
El SI fue establecido en 1960 por la Conferencia General de Pesas y Medidas (CGPM). México adoptó este sistema mediante la Ley Federal sobre Metrología y Normalización.

IMPORTANCIA EN INGENIERÍA CIVIL Y LABORATORIO
El uso correcto de unidades garantiza:
• Comparabilidad de resultados entre laboratorios.
• Cumplimiento de normas nacionales e internacionales.
• Trazabilidad metrológica de las mediciones.
""".trim()
            ),
            "1" to mapOf(
                "titulo" to "Las 7 Unidades Base del SI",
                "contenido" to """
El SI está construido sobre 7 unidades base independientes entre sí:

1. METRO (m) — Longitud
   Uso en construcción: dimensiones de estructuras, espaciado de refuerzo.

2. KILOGRAMO (kg) — Masa
   Uso en construcción: masa de materiales, dosificación de mezclas.

3. SEGUNDO (s) — Tiempo
   Uso en laboratorio: tiempos de curado, velocidad de carga en ensayos.

4. AMPERE (A) — Corriente eléctrica
   Uso en laboratorio: equipos eléctricos de medición.

5. KELVIN (K) — Temperatura termodinámica
   Uso en laboratorio: temperatura absoluta. 0 K = -273.15 °C.

6. MOL (mol) — Cantidad de sustancia
   Uso en química de concreto: reacciones de hidratación.

7. CANDELA (cd) — Intensidad luminosa
   Uso en construcción: nivel de iluminación de espacios.
""".trim()
            ),
            "2" to mapOf(
                "titulo" to "Unidades Derivadas con Nombre Propio",
                "contenido" to """
Las unidades derivadas se forman combinando las 7 unidades base. Las más importantes para ingeniería civil:

NEWTON (N) — Fuerza
  N = kg · m/s²
  Equivalencia: 1 N = 0.101972 kgf | 1 kgf = 9.80665 N
  Uso: capacidad de carga, ensayos de compresión.

PASCAL (Pa) — Presión y Esfuerzo
  Pa = N/m² = kg/(m·s²)
  Múltiplos usados:
  • kPa (kilopascal) = 10³ Pa
  • MPa (megapascal) = 10⁶ Pa = N/mm²
  • GPa (gigapascal) = 10⁹ Pa
  Uso: resistencia del concreto, esfuerzos en estructuras.

JOULE (J) — Energía y Trabajo
  J = N·m = kg·m²/s²
  Uso: energía absorbida en ensayos de impacto.

WATT (W) — Potencia
  W = J/s = kg·m²/s³
  Uso: potencia de equipos de laboratorio.

HERTZ (Hz) — Frecuencia
  Hz = 1/s
  Uso: frecuencia de vibración en ensayos dinámicos.

GRADO CELSIUS (°C) — Temperatura
  T(°C) = T(K) - 273.15
  Uso cotidiano de temperatura; el kelvin es la unidad base.
""".trim()
            ),
            "3" to mapOf(
                "titulo" to "Prefijos del SI",
                "contenido" to """
Los prefijos del SI se colocan antes del símbolo de la unidad para indicar múltiplos o submúltiplos de potencias de 10:

MÚLTIPLOS (valores grandes)
• Tera  (T) = 10¹²  → 1 Tm = 1 000 000 000 000 m
• Giga  (G) = 10⁹  → 1 GPa = 1 000 MPa
• Mega  (M) = 10⁶  → 1 MPa = 1 000 kPa = 1 N/mm²
• kilo  (k) = 10³  → 1 km = 1 000 m | 1 kN = 1 000 N
• hecto (h) = 10²  → 1 hPa = 100 Pa
• deca  (da)= 10¹  → 1 dam = 10 m

SUBMÚLTIPLOS (valores pequeños)
• deci  (d) = 10⁻¹ → 1 dm = 0.1 m
• centi (c) = 10⁻² → 1 cm = 0.01 m
• mili  (m) = 10⁻³ → 1 mm = 0.001 m
• micro (μ) = 10⁻⁶ → 1 μm = 0.000001 m
• nano  (n) = 10⁻⁹ → 1 nm = 10⁻⁹ m

REGLAS DE USO DE PREFIJOS
• Solo se usa UN prefijo a la vez (no mmμg, sino ng).
• El prefijo y la unidad forman una palabra indivisible.
• Nunca usar prefijo para kelvin: K no tiene prefijo.
• El kilogramo (kg) ya lleva 'kilo'; sus múltiplos usan g como base: Mg (no kkg).
""".trim()
            ),
            "4" to mapOf(
                "titulo" to "Unidades Permitidas fuera del SI",
                "contenido" to """
Estas unidades no pertenecen al SI pero son aceptadas por ser de uso generalizado:

UNIDADES DE TIEMPO
• minuto (min) = 60 s
• hora    (h)   = 3 600 s = 60 min
• día     (d)   = 86 400 s = 24 h
  Uso en laboratorio: tiempos de curado (28 días, 7 días).

UNIDADES DE ÁNGULO PLANO
• grado sexagesimal (°) = π/180 rad ≈ 0.017453 rad
  Uso en topografía y levantamientos.

LITRO (L)
  1 L = 1 dm³ = 10⁻³ m³ = 1 000 cm³
  Uso: volumen de agua en dosificación de concreto.

TONELADA MÉTRICA (t)
  1 t = 1 000 kg = 10³ kg
  Uso: masa de materiales en grandes cantidades.

NO SON ACEPTADAS EN DOCUMENTOS TÉCNICOS MEXICANOS
• Libra (lb) — usar kg
• Pie (ft) y pulgada (in) — usar m o mm
• Kgf/cm² — preferir MPa (aunque continúa en uso por costumbre)
""".trim()
            ),
            "5" to mapOf(
                "titulo" to "Reglas de Escritura de Símbolos",
                "contenido" to """
REGLAS PARA SÍMBOLOS DE UNIDADES
• NO llevan punto al final (excepto al terminar una oración).
  Correcto: 25 kg | Incorrecto: 25 kg.
• NO se pluralizan.
  Correcto: 5 kg | Incorrecto: 5 kgs
• En minúsculas, salvo cuando el símbolo deriva de nombre propio:
  Correcto: m, kg, s, N, Pa, Hz, K
  Incorrecto: KG, KP, hz
• Dejar UN espacio entre el número y el símbolo:
  Correcto: 25 kg, 100 MPa | Incorrecto: 25kg, 100MPa
  Excepción: temperatura y ángulo pueden escribirse sin espacio: 25°C, 45°
• No mezclar símbolos con nombres escritos:
  Correcto: 30 kg/m² o 30 kilogramos por metro cuadrado
  Incorrecto: 30 kilogramos por m²

REGLAS PARA NÚMEROS
• Separador decimal: en México se usa el punto (.) según el SI.
  Ejemplo: 1.5 MPa (no 1,5 MPa)
• Los números entre -1 y 1 deben llevar cero antes del decimal:
  Correcto: 0.85 | Incorrecto: .85
""".trim()
            ),
            "6" to mapOf(
                "titulo" to "Unidades en Laboratorio de Concreto",
                "contenido" to """
RESISTENCIA A LA COMPRESIÓN
Unidad oficial: MPa (megapascal) = N/mm²
Conversión con kg/cm² (unidad tradicional en México):
  1 MPa = 10.197 kgf/cm²
  1 kgf/cm² = 0.098066 MPa ≈ 0.1 MPa

Ejemplos comunes:
  f'c = 200 kgf/cm² = 19.61 MPa ≈ 20 MPa
  f'c = 250 kgf/cm² = 24.52 MPa ≈ 25 MPa
  f'c = 300 kgf/cm² = 29.42 MPa ≈ 30 MPa

FUERZA APLICADA
  kN (kilonewton) = 1 000 N
  La máquina de compresión reporta la carga en kN.
  Para obtener resistencia:
    f = F (N) / A (mm²)  → resultado en MPa
    f = F (kN) × 1000 / A (mm²) → MPa

DIMENSIONES
  mm para dimensiones del espécimen (150 mm Ø)
  cm y m son aceptables en campo
  No usar pulgadas en reportes técnicos nacionales

TEMPERATURA
  °C para condiciones ambientales y de curado
  K para cálculos termodinámicos
  Temperatura de curado: 23°C ± 2°C = 296 K ± 2 K

VOLUMEN
  L (litros) para agua de mezclado
  m³ para volúmenes grandes de concreto
  1 m³ = 1 000 L
""".trim()
            ),
            "7" to mapOf(
                "titulo" to "Conversiones Comunes en Laboratorio",
                "contenido" to """
PRESIÓN / ESFUERZO
1 MPa = 1 N/mm² = 1 000 kPa = 10.197 kgf/cm²
1 kgf/cm² = 0.098066 MPa
1 bar = 0.1 MPa = 100 kPa
1 atm = 101.325 kPa = 0.101325 MPa

FUERZA
1 kN = 1 000 N = 101.972 kgf
1 kgf = 9.80665 N ≈ 9.81 N
1 tf (tonelada fuerza) = 9.80665 kN

LONGITUD
1 m = 100 cm = 1 000 mm = 39.3701 in = 3.28084 ft
1 in (pulgada) = 25.4 mm = 2.54 cm
1 ft (pie) = 304.8 mm = 30.48 cm

MASA / PESO
1 kg = 1 000 g = 2.20462 lb
1 lb (libra) = 0.453592 kg = 453.592 g
1 t (tonelada) = 1 000 kg

TEMPERATURA
°C = K - 273.15  (ej: 23°C = 296.15 K)
K = °C + 273.15
°C = (°F - 32) × 5/9
°F = °C × 9/5 + 32

VOLUMEN
1 m³ = 1 000 L = 1 000 000 cm³
1 L = 0.001 m³ = 1 000 cm³
1 ft³ = 28.3168 L = 0.0283168 m³

ÁREA
1 m² = 10 000 cm² = 1 000 000 mm²
1 cm² = 100 mm²
1 in² = 645.16 mm²
""".trim()
            ),
            "8" to mapOf(
                "titulo" to "Uso Correcto en Informes Técnicos",
                "contenido" to """
REPORTE DE RESISTENCIA A LA COMPRESIÓN
Formato correcto en un informe de laboratorio:
  Resistencia a la compresión: 22.5 MPa
  O bien: 22.5 MPa (229.6 kgf/cm²) si se necesita la equivalencia para el cliente.

CARGA APLICADA
  Carga máxima: 397.4 kN
  No reportar: '397 400 N' o '40 527 kgf' (no son prácticos)

REPORTE DE DIMENSIONES
  Diámetro del espécimen: 150.2 mm
  Altura del espécimen: 301.5 mm
  Área de sección transversal: 17 709 mm²

DECIMALES Y CIFRAS SIGNIFICATIVAS
• Resistencia: reportar con 1 decimal (22.5 MPa, no 22.512 MPa)
• Dimensiones: reportar con 1 decimal en mm (150.2 mm)
• Masas: reportar con la resolución de la balanza usada

ERRORES COMUNES A EVITAR
• Mezclar unidades: 'resistencia de 22 MPa (con una carga de 40 kgf)' — incorrecto
• Usar unidades de masa como fuerza: 'carga de 40 000 kg' en vez de '40 kN'
• Omitir la unidad: 'resistencia de 22.5' — incompleto
• Abreviar mal: 'Mpa' en vez de 'MPa' (la M es mayúscula, la p es minúscula)
""".trim()
            )
        )
    )

    private fun norma083(): Map<String, Any> = mapOf(
        "titulo" to "NMX-C-083 — Resistencia a Compresión de Especímenes de Concreto",
        "secciones" to mapOf(
            "0" to mapOf(
                "titulo" to "Objetivo y Alcance",
                "contenido" to """
OBJETIVO
Determinar la resistencia a la compresión de especímenes cilíndricos, corazones y cubos de concreto con masa unitaria mayor de 900 kg/m³.

CAMPO DE APLICACIÓN
• Cilindros moldeados según NMX-C-159
• Núcleos extraídos según NMX-C-169
• Cubos de concreto hidráulico
• NO aplica a concreto con masa unitaria ≤ 900 kg/m³
""".trim()
            ),
            "1" to mapOf(
                "titulo" to "Equipo: Máquina de Compresión",
                "contenido" to """
COMPONENTES REQUERIDOS
• Bloque superior con asiento esférico de acero
• Bloque inferior rígido (espesor mínimo 22.5 mm)

REQUISITOS DEL ASIENTO ESFÉRICO
• Planitud: no más de 0.05 mm en 150 mm
• Centro de la esfera: dentro del ±5% del radio del bloque
• Diámetro de la esfera: al menos 75% del diámetro del espécimen
• La porción móvil debe girar libremente al menos 4° en cualquier dirección

INDICADOR DE CARGA
• Escala ≥ 310 mm de capacidad; división mínima 1 mm
• Error de exactitud: máximo ±3%

CALIBRACIÓN
• Antes de poner en operación, luego cada año o cada 40,000 ensayos
""".trim()
            ),
            "2" to mapOf(
                "titulo" to "Preparación y Tolerancias",
                "contenido" to """
PREPARACIÓN DE SUPERFICIES
• Bases perpendiculares: desviación máxima 0.5° (3 mm en 300 mm)
• Irregularidades: no más de 0.05 mm
• Si no cumple: cabecear (NMX-C-109) o usar neopreno (NMX-C-469)

DIMENSIONES
• 2 medidas de diámetro perpendiculares + 2 alturas; aproximación 1 mm
• Si h/d < 1.8: aplicar factor de corrección (Tabla 1)
• h/d = 1.00 → factor 0.91 | h/d = 2.00 → factor 1.00
• NO ensayar si h/d < 1:1

TOLERANCIAS DE EDAD
• 24 h → ±0.5 h | 7 días → ±6 h | 28 días → ±20 h | 90 días → ±48 h
""".trim()
            ),
            "3" to mapOf(
                "titulo" to "Procedimiento y Tipos de Falla",
                "contenido" to """
VELOCIDAD DE CARGA
• 0.25 MPa/s ± 0.05 MPa/s de forma CONTINUA
• Cilindros 15 cm: entre 3.5 kN/s y 5.3 kN/s
• En 1 de cada 10 especímenes: observar tipo de falla completo

TIPOS DE FALLA EN CILINDROS
• Tipo 1: doble cono (ambos extremos) — DESEABLE
• Tipo 2: cono en un extremo con grietas menores de 25 mm
• Tipo 3: fracturas verticales columnares sin conos
• Tipo 4: diagonal sin agrietamiento en extremos
• Tipo 5 y 6: fracturas en lados — común con tapas no adheridas

CÁLCULO
• fc = F / A | resultado: promedio de mínimo 2 especímenes
• Aproximación: 100 kPa (1 kg/cm²)
• Repetibilidad: 2.9% | Reproducibilidad: 5.0%
""".trim()
            )
        )
    )

    private fun norma148(): Map<String, Any> = mapOf(
        "titulo" to "NMX-C-148 — Gabinetes, Cuartos Húmedos y Tanques para Curado",
        "secciones" to mapOf(
            "0" to mapOf(
                "titulo" to "Definiciones y Condiciones",
                "contenido" to """
DEFINICIONES
• Cuarto húmedo: habitación donde se puede transitar; control de T y HR
• Gabinete húmedo: no permite transitar en su interior
• Tanque de almacenamiento: pileta con agua a temperatura controlada

CONDICIONES REQUERIDAS
• Temperatura: 296 K ± 2 K (23°C ± 2°C) — cuartos, gabinetes Y agua del tanque
• Humedad relativa: mínimo 95%
• Especímenes: deben verse con brillo acuoso en su superficie
""".trim()
            ),
            "1" to mapOf(
                "titulo" to "Verificación y Equipo",
                "contenido" to """
LECTURAS DIARIAS
• Laboratorio: 3 lecturas aleatorias durante el día laboral
• Obra: mínimo 2 lecturas durante el día
• Si termómetro no fijo: introducir al menos 15 min antes de leer

EQUIPO DE MEDICIÓN
• Termómetro: precisión mínima 1 K (1°C)
• Higrómetro o psicrómetro: precisión mínima 1 K y 1% de HR
""".trim()
            ),
            "2" to mapOf(
                "titulo" to "Tanque de Almacenamiento",
                "contenido" to """
REQUISITOS DEL TANQUE
• Separación entre especímenes: mínimo 1 cm
• Separación a paredes: mínimo 3 cm
• Tirante de agua sobre especímenes: mínimo 2 cm
• Elemento calefactor: mínimo 10 cm de los especímenes

AGUA SATURADA DE CAL
• Mínimo 3.0 g de Ca(OH)₂ por litro (3 kg/m³)
• Mezclar el agua: intervalos no mayores de 1 mes
• Limpiar el tanque: máximo cada 12 meses
• NO usar agua corriente continua ni desmineralizada
""".trim()
            )
        )
    )

    private fun norma156(): Map<String, Any> = mapOf(
        "titulo" to "NMX-C-156 — Determinación del Revenimiento del Concreto Fresco",
        "secciones" to mapOf(
            "0" to mapOf(
                "titulo" to "Alcance y Equipo",
                "contenido" to """
ALCANCE
• Revenimientos de 2 cm a 20 cm; TMN < 50 mm
• NO aplica a concreto autoconsolidable (SCC)

MOLDE (CONO DE ABRAMS)
• Base mayor: 20 cm Ø | Base menor: 10 cm Ø | Altura: 30 cm
• Tolerancia en todas las dimensiones: ±3 mm
• 2 estribos en la base + 2 asas

VARILLA
• Acero circular liso, 16 mm Ø, ~600 mm longitud, extremos semiesféricos
""".trim()
            ),
            "1" to mapOf(
                "titulo" to "Procedimiento",
                "contenido" to """
LLENADO EN 3 CAPAS
• Capa 1: ~7 cm | Capa 2: ~15 cm | Capa 3: hasta el borde
• 25 penetraciones por capa
• ~Mitad del perímetro con varilla inclinada; resto en espiral al centro
• 2ª y 3ª capas: penetrar ~2 cm en la capa anterior

LEVANTAMIENTO
• Enrasar con movimiento de rodamiento
• Levantar en 5 s ± 2 s; dirección vertical sin lateral ni torsional
• Tiempo total máximo desde inicio de llenado: 2.5 min

DESCARTAR si: concreto cae a un lado | 2 fallas consecutivas = no aplicable
""".trim()
            ),
            "2" to mapOf(
                "titulo" to "Tolerancias e Informe",
                "contenido" to """
TOLERANCIAS DE REVENIMIENTO
• < 50 mm → ±15 mm | 50-100 mm → ±25 mm | > 100 mm → ±35 mm

PRECISIÓN
• Un operador: desv. estándar máx. 7 mm; diferencia máxima 20 mm (d2s)
• Varios operadores: desv. estándar máx. 12.5 mm; diferencia máxima 35 mm (d2s)

INFORME
• Revenimiento obtenido (1 cm aproximación) | Revenimiento de proyecto
• TMN del agregado | Identificación del concreto
""".trim()
            )
        )
    )

    private fun norma159(): Map<String, Any> = mapOf(
        "titulo" to "NMX-C-159 — Elaboración y Curado de Especímenes de Concreto",
        "secciones" to mapOf(
            "0" to mapOf(
                "titulo" to "Moldes y Equipo",
                "contenido" to """
MOLDES — REQUISITOS GENERALES
• No absorbentes, no reactivos, impermeables y estancos
• Dimensión menor ≥ 3 × TMN del agregado grueso
• Revestir con aceite mineral antes de usar

CILÍNDRICOS: longitud = 2 × diámetro; Ø mínimo 5 cm; ningún diámetro difiere >2%
CÚBICOS: lados perpendiculares (desv. máx. 0.5°); variación máx. 1% de dimensión nominal
PRISMÁTICOS: longitud ≥ 50 mm + 3 × peralte; viga estándar 150×150 mm sección

VARILLA LARGA: 16 mm ±1.5 mm, 600 mm ±30 mm
VARILLA CORTA: 10 mm ±1 mm, 300 mm ±15 mm
VIBRADOR DE INMERSIÓN: ≥9,000 vib/min; cabezal ≤ ¼ del Ø del molde
""".trim()
            ),
            "1" to mapOf(
                "titulo" to "Compactación",
                "contenido" to """
SELECCIÓN DEL MÉTODO
• Revenimiento > 8 cm → VARILLADO
• Revenimiento 3-8 cm → varillado O vibrado
• Revenimiento < 3 cm → VIBRADO

CILINDROS — CAPAS Y PENETRACIONES (varillado)
• Ø 75-100 mm: 2 capas, 25 penet/capa, varilla 10 mm
• Ø 150 mm: 3 capas, 25 penet/capa, varilla 16 mm
• Ø 200 mm: 3 capas, 50 penet/capa, varilla 16 mm
• Varilla penetra ~20 mm en capa inferior; golpear con mazo de hule las paredes

VIBRADO INTERNO: 3 inserciones/capa; penetra 20 mm en capa anterior
NO usar vibración interna en cilindros ≤ 10 cm de diámetro
""".trim()
            ),
            "2" to mapOf(
                "titulo" to "Curado",
                "contenido" to """
CUBIERTA INICIAL
• Cubrir INMEDIATAMENTE con placa no absorbente o membrana plástica

DESCIMBRADO
• Entre 20 h y 48 h después de la elaboración

CURADO CILINDROS Y CUBOS
• Retirar molde preferiblemente a las 24 h (entre 20-48 h)
• INMEDIATAMENTE a curado húmedo a 296 K ±2 K (23°C ±2°C)

CURADO VIGAS (aceptación)
• Descimbrar entre 24 h y 48 h; curar en agua saturada de Ca(OH)₂ a 23°C ±2°C
• Mínimo 20 h en agua antes del ensayo; PREVENIR secado desde retiro hasta inicio del ensayo

TRANSPORTE
• No antes de 20 h; proteger con material amortiguador; envolver en plástico o telas húmedas
""".trim()
            )
        )
    )

    private fun norma161(): Map<String, Any> = mapOf(
        "titulo" to "NMX-C-161 — Muestreo de Concreto Fresco",
        "secciones" to mapOf(
            "0" to mapOf(
                "titulo" to "Equipo y Requisitos Generales",
                "contenido" to """
EQUIPO
• Recipiente: cubeta, charola o carretilla; impermeable, no reactivo, capacidad ≥ 15 L
• Cucharón: impermeable, capacidad adecuada para evitar pérdida de material

REQUISITOS GENERALES
• Muestrear SOLO cuando todos los componentes han sido agregados y la mezcla es homogénea
• Tiempo entre primera y última porción: ≤ 15 min
• Proteger del sol, viento, lluvia y fuentes de evaporación o contaminación
• Remezclar la muestra con el cucharón antes de los ensayos
""".trim()
            ),
            "1" to mapOf(
                "titulo" to "Procedimientos por Tipo de Fuente",
                "contenido" to """
MEZCLADORA ESTACIONARIA
• Interceptar el FLUJO COMPLETO aproximadamente a la MITAD de la descarga

PAVIMENTADORAS
• Tomar en mínimo 5 puntos diferentes
• Integrar en un recipiente de remezclado en UNA sola muestra compuesta

CAMIÓN EN PLANTA
• Esperar 7 min a velocidad de mezclado especificada
• Despunte mínimo 10 L
• Interceptar totalmente el flujo del canal con el recipiente

CAMIÓN EN OBRA (aceptación/rechazo)
• Etapa 1: muestra al INICIO (después de despuntar 10 L); verificar homogeneidad y aceptar/rechazar
• Etapa 2: si se acepta → muestra entre 15% y 85% de la descarga
• Controlar velocidad de descarga con número de revoluciones, NO con abertura de compuerta
""".trim()
            ),
            "2" to mapOf(
                "titulo" to "Informe de Muestreo",
                "contenido" to """
DATOS OBLIGATORIOS EN EL INFORME
• Revenimiento obtenido (cm)
• TMN del agregado (mm)
• Resistencia de proyecto (MPa o kg/cm²)
• Ubicación en obra
• Hora de muestreo
• Si fue cribado: designación de la malla

DATOS OPCIONALES (si se solicitan)
• Temperatura del concreto (°C)
• Masa unitaria (kg/m³)
• Contenido de aire (%)
""".trim()
            )
        )
    )

    private fun norma109(): Map<String, Any> = mapOf(
        "titulo" to "NMX-C-109 — Cabeceo de Especímenes de Concreto",
        "secciones" to mapOf(
            "0" to mapOf(
                "titulo" to "Objetivo y Campo de Aplicación",
                "contenido" to """
OBJETIVO
Esta norma determina los procedimientos de cabeceo en especímenes con el fin de obtener la planicidad y perpendicularidad en sus bases para su ensayo a compresión.

CAMPO DE APLICACIÓN
Aplicable a:
• Especímenes de concreto hidráulico moldeados o extraídos.
• Especímenes prefabricados: tabiques, blocks, bloques, adoquines, ladrillos, etc.

El cabeceo garantiza que las bases sean planas y perpendiculares al eje del espécimen, dentro de una tolerancia de ± 0.05 mm en 150 mm, para asegurar distribución uniforme de la carga durante el ensayo.
""".trim()
            ),
            "1" to mapOf(
                "titulo" to "Definiciones",
                "contenido" to """
CABECEO
Preparación de las bases de los especímenes para obtener la planicidad y perpendicularidad requerida para su ensayo.

COMPUESTO PARA CABECEO (MORTERO DE AZUFRE)
Mezcla de material de azufre con adiciones de otros materiales para mejorar sus propiedades de:
• Resistencia a compresión
• Adherencia
• Plasticidad

Los materiales adicionales pueden ser: cenizas, silicios, puzolanas, etc.
""".trim()
            ),
            "2" to mapOf(
                "titulo" to "Equipo Necesario",
                "contenido" to """
PLACAS CABECEADORAS — para pasta de cemento
• Placa de vidrio o metal pulida: mínimo 6 mm de espesor.
• Acrílico, plástico, granito u otro material pétreo pulido: mínimo 12 mm de espesor.

PLATOS METÁLICOS — para compuesto de azufre
• Diámetro: al menos 5 mm mayor que el del espécimen.
• Planicidad: no debe diferir más de 0.05 mm en 150 mm.
• Espesor mínimo para cilindros de 15 cm Ø: 11 mm.
• Altura del borde recomendada: aprox. 12 mm.

ALINEADOR PARA CABECEO CILÍNDRICO
• Asegura que la capa no se aparte de la perpendicularidad más de 0.5° (aprox. 3 mm en 300 mm).

RECIPIENTE PARA FUNDIR AZUFRE
• Con dispositivos de control automático de temperatura.
• No debe ser reactivo con el compuesto fundido.

EQUIPO AUXILIAR
• Escuadra metálica de 90°.

PLACA CABECEADORA — especímenes prismáticos
• Metálica, mínimo 11 mm de espesor.
• Con dos fronteras fijas y dos desmontables.
• Superficie libre de ranuras o depresiones > 0.25 mm.
• Planidad: no más de 0.05 mm en 150 mm.

DISPOSITIVOS DE ALINEAMIENTO — prismáticos
• Barras de guía o niveles de burbuja.
• Perpendicularidad dentro de 0.5° (1 mm vertical por cada 100 mm horizontales).

MOLDES PARA CUBOS DE MORTERO
• Tres moldes cúbicos de 50 mm ± 1 mm por lado.
• Con placa base y cubierta perforada.
""".trim()
            ),
            "3" to mapOf(
                "titulo" to "Materiales Auxiliares",
                "contenido" to """
• ACEITE MINERAL
  Lubrica el interior de los moldes y el plato antes de verter el compuesto de azufre, evitando adherencia al plato.

• PAÑO
  Para limpieza y protección de superficies cabeceadas.

• TAPA O PLACA DE POLIETILENO
  Se coloca sobre el espécimen tras el cabeceo para evitar pérdida de humedad durante el curado.
  No debe estar en contacto directo con la superficie cabeceada; sobre ella se coloca un paño húmedo.
""".trim()
            ),
            "4" to mapOf(
                "titulo" to "Preparación de Muestras",
                "contenido" to """
ESPECÍMENES CILÍNDRICOS RECIÉN MOLDEADOS
La superficie superior se cubre con pasta de cemento Portland siguiendo el procedimiento de la sección 8.3.

ESPECÍMENES CILÍNDRICOS ENDURECIDOS (curados en húmedo)
• Bases con desviación > 0.05 mm deben ser cortadas, pulidas o cabeceadas.
• Verificar perpendicularidad con escuadra metálica de 90° con muesca, en dos lados opuestos.
• Tolerancia: no más de 0.5° (aprox. 3 mm en 300 mm de altura).

REQUISITOS DEL COMPUESTO PARA CABECEO — Tabla 1

Resistencia del concreto 3.5 a 50 MPa:
  → Resistencia mín. del compuesto: 35 MPa (o la del concreto, la mayor)
  → Espesor promedio: 6 mm | Máximo en cualquier punto: 8 mm

Resistencia del concreto > 50 MPa:
  → Resistencia mín. del compuesto: no menor que la del concreto
  → Espesor promedio: 3 mm | Máximo en cualquier punto: 5 mm

• El compuesto NO debe reutilizarse si su resistencia baja del mínimo requerido.
• Verificar espesor en 1 de cada 10 especímenes después del ensayo.
""".trim()
            ),
            "5" to mapOf(
                "titulo" to "Determinación de Resistencia del Compuesto",
                "contenido" to """
Procedimiento para verificar que el compuesto de azufre cumple la resistencia mínima:

1. Preparar 3 cubos de 50 mm ± 1 mm por lado.
2. Precalentar el molde (temperatura que permita retirarlo con la mano).
3. Lubricar el interior con aceite mineral.
4. Colar los cubos con el compuesto fundido entre 130°C y 150°C (403–423 K).
5. Rellenar los huecos generados por contracción.
6. Desmoldar una vez solidificado; eliminar sobrantes; verificar superficies.
7. Ensayar a la compresión aplicando carga en las caras laterales.
   → El fallo debe ocurrir entre 20 s y 80 s.
8. Si no se alcanza la resistencia requerida: reajustar dosificación y repetir.

Frecuencia de verificación: después de cada operación diaria de ensayo de compresión.
""".trim()
            ),
            "6" to mapOf(
                "titulo" to "Condiciones Ambientales",
                "contenido" to """
• Realizar el cabeceo en un sitio CUBIERTO sin cambios bruscos de clima.
• Evitar corrientes de aire que provoquen enfriamiento desigual del compuesto.
• Mantener temperatura ambiente estable durante el proceso.

⚠ ADVERTENCIA DE SEGURIDAD
Calentar el azufre con flama directa es PELIGROSO.
Punto de ignición del azufre: 227°C.
El mortero puede encenderse por sobrecalentamiento.

Cuando se trabaje en espacios con poca ventilación, usar campana de captación de gases.
""".trim()
            ),
            "7" to mapOf(
                "titulo" to "Procedimiento: Cabeceo con Compuesto de Azufre",
                "contenido" to """
PREPARACIÓN
1. Calentar el compuesto a 140°C ± 10°C.
2. Mantener el compuesto fundido ALEJADO de la humedad (varía su resistencia y comportamiento plástico).
3. Precalentar ligeramente el plato y los dispositivos para evitar choques térmicos.
4. Aceitar ligeramente el plato antes de cada capa para evitar adherencia.

ESPECÍMENES CURADOS EN HÚMEDO
• Las bases deben estar suficientemente secas para evitar burbujas de vapor que impidan la adherencia.

PROCEDIMIENTO (dispositivo vertical)
1. Verter el compuesto fundido sobre el plato cabeceador.
2. Levantar el cilindro por encima del plato.
3. Colocar los lados del cilindro con las guías de alineamiento.
4. Deslizar el cilindro por las guías manteniendo contacto constante.
5. Mantener el cilindro en reposo hasta que el mortero endurezca.
6. Usar suficiente material para cubrir el extremo completo del cilindro.

VERIFICACIÓN
• Golpear con elemento metálico en toda la superficie para revisar adherencia.
• Si el cabeceo no cumple con planicidad o tiene áreas sin adherencia: REMOVER Y REHACER.

POST-CABECEO (especímenes húmedos)
• Mantener en condiciones húmedas entre el cabeceo y el ensayo.
• NO ensayar hasta que el compuesto haya desarrollado la resistencia requerida.
""".trim()
            ),
            "8" to mapOf(
                "titulo" to "Procedimiento: Cabeceo con Pasta de Cemento",
                "contenido" to """
8.3 — ESPECÍMENES CILÍNDRICOS RECIÉN MOLDEADOS
1. Cabecear entre 2 h y 4 h después del moldeado.
2. Retirar el agua de sangrado antes de aplicar la pasta.
3. Aplicar capa tan delgada como sea posible.
4. Relación agua/cemento: aprox. 0.25 a 0.35.
5. A los 30 min de su aplicación, enrasar con la placa cabeceadora.
6. Cubrir con tapa de polietileno + paño húmedo (sin contacto directo con la superficie cabeceada).

Alternativa: Espolvorear cemento sobre la superficie fresca y enrasar después de 1 a 2 h (sin retirar el agua de sangrado previamente).

────────────────────────────────────────────

8.4 — ESPECÍMENES CILÍNDRICOS DE CONCRETO ENDURECIDO
⚠ SOLO para especímenes que se curarán en húmedo de forma continua hasta el ensayo.

1. Asegurar que las caras a cabecear estén saturadas de agua.
2. Aplicar pasta de cemento (relación agua/cemento: 0.25 a 0.35).
3. Enrasar a los 30 min con la placa cabeceadora (humedecida).
4. Una vez endurecido (~24 h), regresar al curado húmedo.
5. Cubrir y mantener en condiciones húmedas en todo momento.
6. Mínimo 7 DÍAS antes de ensayar. Las capas de cemento pueden agrietarse por secado.

Alternativas de preparación:
• Pulir la superficie del espécimen (sin necesidad de cabeceo).
• Usar placas o almohadillas de neopreno.
""".trim()
            ),
            "9" to mapOf(
                "titulo" to "Procedimiento: Cabeceo de Especímenes Prismáticos",
                "contenido" to """
Para tabiques, blocks, bloques, adoquines, ladrillos, etc.

1. Colocar la placa cabeceadora en superficie horizontal, firme y plana.
2. Nivelar en AMBOS sentidos.
3. Si se usa compuesto de azufre: precalentar la placa.
4. Colocar el material de cabeceo sobre la placa.
5. Colocar el espécimen de ensayo sobre el material.
6. Cuidar que el material no se salga por las uniones del cabeceador.
7. Garantizar PERPENDICULARIDAD de la superficie cabeceada con el eje vertical.

REQUISITOS DE CALIDAD
• Superficies planas dentro de ± 0.05 mm en 150 mm, en dos direcciones ortogonales.

VERIFICACIÓN (1 espécimen de cada lote)
• Regla rígida de bordes rectos + calibradores de laminillas.
• Mínimo 2 lecturas en cada dirección ortogonal.
• Las superficies no deben apartarse más de 0.05 mm de un plano.
""".trim()
            ),
            "10" to mapOf(
                "titulo" to "Verificación General y Recomendaciones",
                "contenido" to """
VERIFICACIÓN DE PLANICIDAD (durante el procedimiento)
En 1 de cada 10 especímenes:
• Regla rígida de bordes rectos + calibradores de laminillas.
• Mínimo 3 lecturas en diámetros diferentes.
• Tolerancia: no más de 0.05 mm en 150 mm.

PREPARACIÓN PREVIA AL CABECEO
• Eliminar depósitos de cera, material aceitoso o exceso de agua en las bases.
  Estos depósitos interfieren con la adherencia de la capa de cabeceo.

RECOMENDACIONES GENERALES
• Usar campana de captación de gases al fundir azufre en espacios poco ventilados.
• ⚠ Riesgo: punto de ignición del azufre = 227°C. No usar flama directa.
• Dureza Rockwell C-48 mínimo para los platos de cabeceo (mayor durabilidad).

CUBOS DE COMPUESTO PARA CABECEO — calidad
• Placa de baquelita de 3 mm entre cubierta y molde para disminuir velocidad de enfriamiento.
• Huecos de la placa troncocónicos para facilitar el desmolde.
• Rellenar con compuesto fundido para evitar huecos de contracción.
• Si la resistencia obtenida es significativamente menor a la esperada: inspeccionar el interior de los cubos.
""".trim()
            )
        )
    )
}
