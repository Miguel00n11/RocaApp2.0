<?php
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");

$tablas = [
    "carretilla" => "inventario_carretillas",
    "varilla"    => "inventario_varillas",
    "mazo"       => "inventario_mazos",
    "flexometro" => "inventario_flexometro",
    "termometro" => "inventario_termometro",
    "cucharon"   => "inventario_cucharones",
    "placa"      => "inventario_placas",
    "enrasador"  => "inventario_enrasadores",
];

$persona = trim($_GET['persona'] ?? '');

if ($persona === '') {
    http_response_code(400);
    echo json_encode(["error" => "Se requiere el parámetro persona"]);
    exit;
}

$host   = "localhost";
$dbname = "fortastudio_roca";
$user   = "fortastudio_ali3d";
$pass   = "rumores3d";

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $user, $pass, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
    ]);

    $resultado = [];

    foreach ($tablas as $clave => $tabla) {
        // Obtiene el código del equipo más reciente asignado a esta persona
        $stmt = $pdo->prepare(
            "SELECT Codigo FROM `$tabla`
             WHERE Persona_asignada = ?
             ORDER BY ID DESC
             LIMIT 1"
        );
        $stmt->execute([$persona]);
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        $resultado[$clave] = $row ? $row['Codigo'] : null;
    }

    echo json_encode($resultado);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(["error" => "Error de base de datos"]);
}
