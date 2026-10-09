package com.miguelrodriguez.rocaapp20.supervisiones

object CatalogoSupervision {

    data class ItemNorma(val descripcion: String, val clausula: String = "")
    data class Norma(val codigo: String, val titulo: String, val items: List<ItemNorma>)

    val NORMAS: List<Norma> = listOf(

        Norma("083", "NMX-C-083 — Resistencia a la compresión de cilindros de concreto", listOf(
            ItemNorma("Verificar que el laboratorio cuente con un informe de calibración vigente de la máquina de ensaye, en donde se especifique que el error de ésta es como máximo de ±3% de la carga aplicada."),
            ItemNorma("Verificar que el ensaye a compresión de los especímenes curados en húmedo se efectúe tan pronto como sea posible después de retirarlos de la pileta o del cuarto húmedo y una vez que el material de cabeceo haya adquirido la resistencia requerida."),
            ItemNorma("En el caso de especímenes curados en condiciones especiales, comprobar que estos son ensayados en las condiciones de humedad resultante del curado."),
            ItemNorma("Verificar que la determinación del diámetro se obtenga del promedio de dos diámetros perpendiculares entre sí a una altura media del espécimen y se reporte con una aproximación de 1mm."),
            ItemNorma("Verificar que en la determinación de la altura del espécimen se utiliza el promedio de dos alturas opuestas y se reporten con una aproximación de 1mm."),
            ItemNorma("Verificar que se limpien las superficies de las platinas superior e inferior y la cabeza del espécimen de prueba."),
            ItemNorma("Comprobar que se coloque el espécimen sobre la placa inferior alineando su eje cuidadosamente con el centro de la placa de carga con asiento esférico."),
            ItemNorma("Verificar que se baja la placa superior hacia el espécimen de tal manera que se tenga un contacto suave y uniforme."),
            ItemNorma("Verificar que se aplica carga con una velocidad uniforme (27 a 32.4 t/min) y continua sin producir impacto, ni pérdida de carga."),
            ItemNorma("Verificar que se registre la carga máxima."),
            ItemNorma("Solicitar que se lleve el espécimen hasta la ruptura y se indique el tipo de falla.")
        )),

        Norma("109", "NMX-C-109 — Cabeceo de especímenes cilíndricos", listOf(
            ItemNorma("Verificar que el área de cabeceo sea un sitio cubierto sin circulación de aire que enfríe el compuesto para cabeceo antes de obtener la adherencia con el concreto.", "109 (6.2.1)"),
            ItemNorma("Verificar que el compuesto de cabeceo alcance la resistencia especificada antes de usarse y que se eliminen de las bases del espécimen cualquier cosa que interfiera con la adherencia de la capa de cabeceo.", "109 (6.2.1)"),
            ItemNorma("Que se preparen 3 cubos de compuesto de cabeceo de 50mm ± 1mm, colados a una temperatura del azufre de entre 130 y 150°C.", "Mortero azufre 1"),
            ItemNorma("Se ensayen a la compresión aplicando la carga en dos de las caras laterales y se calcule su resistencia en MPa (kgf/cm²).", "Mortero azufre 2"),
            ItemNorma("La velocidad de aplicación sea tal que éste falle entre 20 y 80 segundos.", "Mortero azufre 3"),
            ItemNorma("De no adquirir la resistencia esperada, se dosifique nuevamente los materiales de la mezcla de los componentes del mortero de azufre y repetir el ensayo.", "Mortero azufre 4"),
            ItemNorma("Verificar que el compuesto de cabeceo sea reutilizado en base a la resistencia especificada.", "Mortero azufre 5"),
            ItemNorma("Verificar que a uno de cada diez especímenes se verifique la planicidad, perpendicularidad, adherencia del compuesto de cabeceo y se registre.", "Cabeceo 1"),
            ItemNorma("Verificar que se registra el espesor máximo de las capas de cabeceo y el espesor promedio.", "Cabeceo 2"),
            ItemNorma("Verificar que el compuesto de cabeceo sea fundido a una temperatura entre 403°K y 423°K (130°C y 150°C) y que cumpla con la Tabla 1.", "109.8.1"),
            ItemNorma("Verificar que antes de vaciar cada capa, se lubrique ligeramente el plato de cabeceo.", "109.8.1"),
            ItemNorma("Verificar que las bases de los especímenes estén suficientemente secos al momento del cabeceo para evitar que se formen burbujas.", "109.8.1"),
            ItemNorma("Verificar que los especímenes se mantienen húmedos.", "109.8.2"),
            ItemNorma("Verificar que los especímenes cabeceados no se ensayen hasta que el compuesto de cabeceo haya desarrollado la resistencia requerida.", "109.8.2"),
            ItemNorma("Verificar que para el cabeceo con compuesto de cabeceo se usan platos metálicos, cuyo diámetro sea por lo menos 5mm mayor que el del espécimen por cabecear y la superficie de asiento no se aparte de un plano en más de 0.05mm en 150mm.", "109.2 (4.1.2)")
        )),

        Norma("148", "NMX-C-148 — Cuartos de curado", listOf(
            ItemNorma("Verificar que tiene una temperatura de 296°K ± 2°K (23 ± 2°C).", "148.4 (3.1)"),
            ItemNorma("Verificar que tiene una humedad relativa mínima del 95%.", "148.4 (3.1)"),
            ItemNorma("Verificar que las superficies expuestas de los especímenes en el almacenamiento se vean húmedas y aparezcan mojadas en cualquier momento."),
            ItemNorma("Verificar que se registra la temperatura y humedad por lo menos 3 veces al día."),
            ItemNorma("Si los especímenes frescos se colocan sobre armazones, verificar que estos se encuentren a nivel."),
            ItemNorma("Verificar que el cuarto está construido de tal forma que se conserven las condiciones ambientales internas requeridas.", "148.5 (3.2)"),
            ItemNorma("Verificar que la temperatura ambiente en el cuarto está controlada con equipos que la puedan enfriar o calentar en ambos.", "148.5 (3.2)"),
            ItemNorma("Verificar que el elemento sensitivo de temperatura está colocado dentro del cuarto húmedo.", "148.5 (3.2)"),
            ItemNorma("Verificar que la humedad relativa especificada se mantiene en forma constante.", "148.5 (3.2)")
        )),

        Norma("156", "NMX-C-156 — Determinación del revenimiento del concreto fresco", listOf(
            ItemNorma("Verificar que antes de efectuar la prueba del revenimiento, se remezcla el concreto con pala o cucharón lo necesario para garantizar la uniformidad en la mezcla.", "156.6"),
            ItemNorma("Verificar que se humedezca el cono antes de realizar la prueba.", "156.4"),
            ItemNorma("Verificar que se coloca el cono en una superficie horizontal, plana, rígida, húmeda y no absorbente.", "156.6"),
            ItemNorma("Verificar que se llena el cono en tres capas aproximadamente de igual volumen (primera capa ~70mm, segunda capa ~150mm y la tercera al borde superior del molde).", "156.7"),
            ItemNorma("Verificar que cada capa es compactada con 25 penetraciones hechas con el extremo semiesférico de la varilla.", "156.8"),
            ItemNorma("Verificar que al momento de varillar, se distribuyan uniformemente las penetraciones sobre toda la sección de cada capa.", "156.9"),
            ItemNorma("Verificar que aproximadamente la mitad de las penetraciones se hagan cerca del perímetro del cono, inclinando ligeramente la varilla, después en forma de espiral verticalmente hacia el centro.", "156.10"),
            ItemNorma("Verificar que se compacta la segunda capa y la superior a través de todo su espesor, de manera que la varilla penetre en la capa anterior aproximadamente 20mm.", "156.11"),
            ItemNorma("Si como consecuencia de la compactación de la última capa, se asienta el concreto por debajo del borde superior del cono, verificar que se agrega concreto en la penetración 10 o 20.", "156.12"),
            ItemNorma("Verificar que después de terminada la compactación de la última capa, se enrasa el concreto mediante rodamiento con la varilla.", "156.13"),
            ItemNorma("Verificar que después de limpiar la superficie exterior de la base de asiento del cono, éste se levanta en dirección vertical en sus 30cm de altura en un tiempo de 5 ± 2s.", "156.14"),
            ItemNorma("Verificar que retirado el cono se mide el revenimiento determinando el asentamiento a partir del nivel original de la base superior del molde, midiendo en el centro desplazado de la superficie superior del concreto.", "156.15"),
            ItemNorma("Preguntar al evaluado las dimensiones del cono (200mm Ø inferior, 100mm Ø superior, 300mm altura, tolerancia ±3mm).", "156.4.1"),
            ItemNorma("Verificar que el molde esté provisto de dos estribos para apoyar los pies y dos asas para levantarlo; superficie interior lisa, libre de protuberancias, cuerpo libre de abolladuras.", "156.4.1"),
            ItemNorma("Varilla: barra de acero, sección circular, recta, lisa de 16mm de diámetro y ~600mm de longitud, con por lo menos un extremo de forma semiesférica.", "156.2 (4.2)"),
            ItemNorma("Equipo auxiliar: pala, cucharón, guantes y escala métrica.", "156.3 (4.3)"),
            ItemNorma("Precisión de un solo operador: desviación máxima 7mm; dos determinaciones del mismo operador no deben diferir en más de 20mm.", "156.6"),
            ItemNorma("Precisión de varios operadores: desviación máxima 12.5mm; dos determinaciones de diferentes operadores no deben diferir en más de 35mm.", "156.6"),
            ItemNorma("Verificar que el evaluado llene correctamente el registro de campo identificando claramente la localización del concreto muestreado.")
        )),

        Norma("159", "NMX-C-159 — Elaboración y curado de especímenes de ensayo (en obra)", listOf(
            ItemNorma("Preguntar al evaluado las características de los moldes cilíndricos, varillas, mazos, etc."),
            ItemNorma("Herramienta auxiliar: verificar que se cuente con palas, recipientes, llanas, enrasador, cucharones, reglas, guantes de hule, mazo con cabeza de hule y charolas de lámina."),
            ItemNorma("Verificar que los especímenes son moldeados inmediatamente después de obtenida y remezclada la muestra en un lapso de tiempo no mayor de 15 min."),
            ItemNorma("Lugar para el moldeo: verificar que los especímenes son elaborados sobre una superficie horizontal rígida, nivelada, libre de vibraciones y otras perturbaciones."),
            ItemNorma("Vaciado del concreto: verificar que el concreto es vaciado a los moldes con un cucharón."),
            ItemNorma("Verificar que se remezcle el concreto en la charola con una pala o cuchara para prevenir la segregación y obtener una muestra representativa."),
            ItemNorma("Verificar que el cucharón se mueve alrededor del borde superior del molde a medida que el concreto vaya descargándose, para asegurar una distribución uniforme."),
            ItemNorma("Verificar que se distribuye el concreto dentro del molde usando la varilla de compactación antes de iniciar la misma."),
            ItemNorma("Verificar que el evaluado, durante el colado de la capa final, en caso de que falte concreto lo agregue en la penetración número veinte en una sola ocasión."),
            ItemNorma("Verificar que para cilindros se llenen 3 capas de 100mm."),
            ItemNorma("Varillado de cilindros: verificar que se varille cada capa con 25 penetraciones con el extremo redondeado."),
            ItemNorma("Verificar que se varilla la capa inferior en todo su espesor (primera capa)."),
            ItemNorma("Verificar que se distribuyan las penetraciones uniformemente en toda la sección transversal del molde."),
            ItemNorma("Verificar que en la segunda y/o tercera capa, el varillado penetre aproximadamente 20mm cuando el espesor de la capa sea 10mm o más."),
            ItemNorma("Verificar que se golpean ligeramente las paredes del molde para eliminar las oquedades con el mazo."),
            ItemNorma("Acabado de cilindros: verificar que se enrase la superficie del concreto y quede a nivel de las orillas del molde."),
            ItemNorma("Protección inicial: verificar que los especímenes elaborados se cubran inmediatamente después de terminados, con una placa no absorbente y no reactiva o con una tela de plástico resistente, durable e impermeable."),
            ItemNorma("Curado inicial: verificar que durante las primeras 24h después del moldeo, todos los especímenes sean almacenados bajo las condiciones que prevalezcan en el lugar y se registre la temperatura ambiente al momento del moldeo."),
            ItemNorma("Curado de especímenes cilíndricos: verificar que los especímenes sean retirados de los moldes a las 24h (margen 20h-48h) y sean almacenados de inmediato en una condición húmeda."),
            ItemNorma("Verificar que el tratamiento de curado húmedo de los especímenes cumple con lo especificado en la NMX-C-148-ONNCCE vigente."),
            ItemNorma("Verificar que sea prevenido el secado de la superficie del espécimen al final del período, entre el momento de retiro del especímen de su curado y el inicio de la prueba."),
            ItemNorma("Traslado al laboratorio: verificar que durante el transporte se cuida que se mantenga la humedad de los especímenes (telas húmedas, arena húmeda, colchonetas, etc.)."),
            ItemNorma("Verificar que al recibir los especímenes en el laboratorio sean colocados inmediatamente en el cuarto de curado.")
        )),

        Norma("161", "NMX-C-161 — Concreto fresco - Muestreo", listOf(
            ItemNorma("Verificar que el recipiente es de capacidad adecuada (mínima de 15L), impermeable, limpio y no absorbente.", "161.1 (4.1)"),
            ItemNorma("Verificar que la charola o recipiente sea preferentemente de acero, limpio, impermeable y no absorbente, con la capacidad adecuada para el tamaño total de la muestra.", "161.2 (4.1)"),
            ItemNorma("Verificar que el cucharón sea impermeable, limpio y no absorbente, con capacidad aproximada de 1L y de forma adecuada que evite la pérdida de material por sus costados.", "161.3 (4.2)"),
            ItemNorma("Verificar que la muestra se toma hasta que se haya agregado toda el agua de mezclado y la mezcla esté homogénea.", "161.4 (6)"),
            ItemNorma("El muestreador humedece previamente el equipo que entrará en contacto con el concreto."),
            ItemNorma("Muestreo de mezcladoras estacionarias (fijas y basculantes): verificar que la muestra se obtiene interceptando el flujo completo de descarga o desviando el flujo sin segregar el concreto, aproximadamente a la mitad de la descarga.", "161.5 (6.1)"),
            ItemNorma("Muestreo de pavimentadoras: verificar que la muestra se toma con el cucharón del total del concreto descargado en por lo menos 5 puntos diferentes.", "161.6 (6.2)"),
            ItemNorma("Verificar que el concreto se integra en el recipiente de remezclado en una sola muestra, evitando la pérdida de agua o la contaminación con la superficie de contacto."),
            ItemNorma("Muestreo de olla camión en planta: verificar que se esperen 7 min a la velocidad de mezclado especificada, realizar despunte estimado de 10L mínimo y proceder a tomar la muestra interceptando el flujo del canalón.", "161.7 (6.3.1)"),
            ItemNorma("Muestreo de olla camión en obra: tomar muestra al inicio de la descarga después de despuntar 10L, verificar que el concreto esté homogéneo, interceptar el flujo completo del canalón.", "161.8 (6.3.2)"),
            ItemNorma("Después de que el concreto haya sido aceptado, tomar muestra entre el 15% y el 85% de la descarga; la velocidad de descarga se controla con el número de revoluciones de la olla."),
            ItemNorma("Muestreo de camiones caja, con o sin agitadores, de volteo u otros tipos: obtener muestra por el procedimiento que mejor aplique.", "161.9 (6.4)"),
            ItemNorma("Tamaño de la muestra: verificar que sea del volumen suficiente para realizar los ensayos programados, pudiendo tomar varias porciones para generar la muestra compuesta adecuada.", "161.10 (6.5)"),
            ItemNorma("Remezclado de la muestra: transportar sin pérdida de material al lugar de los ensayos y remezclar con el cucharón para asegurar que es homogénea.", "161.11 (6.6)"),
            ItemNorma("Tiempo: el intervalo entre la obtención de la primera y última porción de la muestra no debe exceder de 15 min. Una vez obtenida la muestra, protéjala del sol, el viento, la lluvia y otras fuentes que provoquen evaporación, contaminación o alteración.", "161.12 (6.7)"),
            ItemNorma("Informe: registrar los datos del concreto incluyendo revenimiento, TMA, resistencia de proyecto, ubicación, hora de muestreo, y en su caso indicar que el concreto fue cribado antes de los ensayes.", "161.13 (7)")
        ))
    )

    fun porCodigo(codigo: String): Norma? = NORMAS.find { it.codigo == codigo }
}
