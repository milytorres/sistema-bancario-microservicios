-- ============================================================
-- BaseDatos.sql
-- Sistema Bancario - Microservicios Cliente/Persona y Cuenta/Movimiento
-- Motor: PostgreSQL
-- Cada microservicio tiene su propia base de datos (sin FK cruzada entre ellas).
-- ============================================================

-- ------------------------------------------------------------
-- Microservicio: Cliente / Persona -> base de datos "banco_cliente"
-- Estrategia de herencia: SINGLE_TABLE (tabla "persona" con discriminador)
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS persona (
    id              BIGSERIAL PRIMARY KEY,
    tipo_persona    VARCHAR(31)  NOT NULL,
    nombre          VARCHAR(120) NOT NULL,
    genero          VARCHAR(20),
    edad            INTEGER,
    identificacion  VARCHAR(30)  NOT NULL,
    direccion       VARCHAR(200),
    telefono        VARCHAR(20),
    cliente_id      VARCHAR(50)  UNIQUE,
    contrasena      VARCHAR(255),
    estado          BOOLEAN
);

-- Datos de ejemplo (Sección 7.1 del PDF).
-- "contrasena" almacena el hash BCrypt de la contraseña original del PDF (1234/5678/1245),
-- nunca texto plano. Ejemplo: 'Jose Lema' -> hash de '1234'.
INSERT INTO persona (tipo_persona, nombre, genero, edad, identificacion, direccion, telefono, cliente_id, contrasena, estado)
VALUES
    ('CLIENTE', 'Jose Lema',           'M', 35, '0102030405', 'Otavalo sn y principal', '098254785', 'CLI-001', '$2b$10$YE2ZB612KCGTMQK3h4d7Veyk7Olzkgo7MTdlplNbv.B0J5JPSZ2fq', TRUE),
    ('CLIENTE', 'Marianela Montalvo',  'F', 32, '0203040506', 'Amazonas y NNUU',        '097548965', 'CLI-002', '$2b$10$j6XD8vF6C0gEm9w6Vwok9OPX0ojaVhD58Prpz9QUuLxdomH5KJw9.', TRUE),
    ('CLIENTE', 'Juan Osorio',         'M', 40, '0304050607', '13 junio y Equinoccial', '098874587', 'CLI-003', '$2b$10$fFqH9.C9wgvcSkfpLB0QRukrT5NNPwOoK1ZoUO257yMqfPYkJsVUK', TRUE)
ON CONFLICT (cliente_id) DO NOTHING;

-- ------------------------------------------------------------
-- Microservicio: Cuenta / Movimiento -> base de datos "banco_cuenta"
-- Se crea una base de datos separada y se cambia la conexión.
-- ------------------------------------------------------------

CREATE DATABASE banco_cuenta;

\c banco_cuenta

CREATE TABLE IF NOT EXISTS cuenta (
    id                BIGSERIAL PRIMARY KEY,
    numero_cuenta     VARCHAR(20)    NOT NULL UNIQUE,
    tipo_cuenta       VARCHAR(20)    NOT NULL,
    saldo_inicial     NUMERIC(15,2)  NOT NULL,
    saldo_disponible  NUMERIC(15,2)  NOT NULL,
    estado            BOOLEAN        NOT NULL,
    cliente_id        VARCHAR(50)    NOT NULL -- referencia lógica al clienteId del microservicio Cliente (sin FK de BD entre servicios)
);

CREATE TABLE IF NOT EXISTS movimiento (
    id               BIGSERIAL PRIMARY KEY,
    fecha            TIMESTAMP      NOT NULL,
    tipo_movimiento  VARCHAR(20)    NOT NULL,
    valor            NUMERIC(15,2)  NOT NULL,
    saldo            NUMERIC(15,2)  NOT NULL,
    cuenta_id        BIGINT         NOT NULL REFERENCES cuenta(id) -- FK válida: cuenta y movimiento viven en el mismo microservicio/BD
);

-- Datos de ejemplo: cuentas (Secciones 7.2 y 7.3 del PDF), con saldo_disponible
-- ya reflejando los movimientos de la Sección 7.4 aplicados.
INSERT INTO cuenta (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id)
VALUES
    ('478758', 'AHORRO',    2000, 1425, TRUE, 'CLI-001'),
    ('225487', 'CORRIENTE', 100,  700,  TRUE, 'CLI-002'),
    ('495878', 'AHORRO',    0,    150,  TRUE, 'CLI-003'),
    ('496825', 'AHORRO',    540,  0,    TRUE, 'CLI-002'),
    ('585545', 'CORRIENTE', 1000, 1000, TRUE, 'CLI-001')
ON CONFLICT (numero_cuenta) DO NOTHING;

-- Datos de ejemplo: movimientos (Sección 7.4 del PDF).
-- Fechas de 225487 y 496825 tomadas del reporte de ejemplo (Sección 7.5);
-- las de 478758 y 495878 no se especifican en el PDF, se asumen dentro del mismo rango.
INSERT INTO movimiento (fecha, tipo_movimiento, valor, saldo, cuenta_id)
VALUES
    ('2022-02-09 10:00:00', 'RETIRO',    -575, 1425, (SELECT id FROM cuenta WHERE numero_cuenta = '478758')),
    ('2022-02-10 10:00:00', 'DEPOSITO',  600,  700,  (SELECT id FROM cuenta WHERE numero_cuenta = '225487')),
    ('2022-02-09 11:00:00', 'DEPOSITO',  150,  150,  (SELECT id FROM cuenta WHERE numero_cuenta = '495878')),
    ('2022-02-08 10:00:00', 'RETIRO',    -540, 0,    (SELECT id FROM cuenta WHERE numero_cuenta = '496825'));
