-- =============================================================================
-- SISTEMA DE TICKETING MUNDIAL 2026 -- VERSION 3
-- Base de Datos II - Universidad Católica del Uruguay
-- Integrantes: Facundo Banchero, Gaston Puyares
-- Motor: PostgreSQL
--
-- Cambios respecto a v1 (devolución de la docente):
--   - Se elimina evento_sector como intermediario de entrada
--   - ENTRADA se vincula directo a EVENTO y a SECTOR
--   - Se incorpora FASE y FASE_SECTOR para el manejo de precios
--   - VALIDACION se remodela como relación ternaria (validacion_ternaria)
--   - FUNCIONARIO_SECTOR se redirige de evento_sector a sector
--   - Se agrega columna password a usuario (hash BCrypt) para soportar
--     autenticación JWT en el backend Spring Boot
-- =============================================================================

DROP SCHEMA IF EXISTS ticketing CASCADE;
CREATE SCHEMA ticketing;
SET search_path = ticketing;

-- =============================================================================
-- 1. USUARIO
-- =============================================================================
CREATE TABLE usuario (
    id              SERIAL          PRIMARY KEY,
    mail            VARCHAR(255)    NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    pais_doc        VARCHAR(100)    NOT NULL,
    tipo_doc        VARCHAR(50)     NOT NULL,
    numero_doc      VARCHAR(100)    NOT NULL,
    pais_dir        VARCHAR(100)    NOT NULL,
    localidad       VARCHAR(150)    NOT NULL,
    calle           VARCHAR(200)    NOT NULL,
    numero_dir      VARCHAR(20)     NOT NULL,
    codigo_postal   VARCHAR(20)     NOT NULL,
    rol             VARCHAR(20)     NOT NULL,

    CONSTRAINT uq_usuario_mail UNIQUE (mail),
    CONSTRAINT uq_usuario_documento UNIQUE (pais_doc, tipo_doc, numero_doc),
    CONSTRAINT chk_usuario_rol
        CHECK (rol IN ('ADMIN_PAIS', 'FUNCIONARIO', 'USUARIO_GENERAL'))
);

-- =============================================================================
-- 2. TELEFONO
-- =============================================================================
CREATE TABLE telefono (
    id          SERIAL          PRIMARY KEY,
    usuario_id  INTEGER         NOT NULL,
    numero      VARCHAR(30)     NOT NULL,

    CONSTRAINT fk_telefono_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- =============================================================================
-- 3. ADMIN_PAIS
-- =============================================================================
CREATE TABLE admin_pais (
    usuario_id          INTEGER         PRIMARY KEY,
    pais_jurisdiccion   VARCHAR(100)    NOT NULL,
    fecha_asignacion    DATE            NOT NULL,

    CONSTRAINT fk_admin_pais_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- =============================================================================
-- 4. FUNCIONARIO
-- =============================================================================
CREATE TABLE funcionario (
    usuario_id      INTEGER         PRIMARY KEY,
    numero_legajo   VARCHAR(50)     NOT NULL,

    CONSTRAINT uq_funcionario_legajo UNIQUE (numero_legajo),
    CONSTRAINT fk_funcionario_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- =============================================================================
-- 5. USUARIO_GENERAL
-- =============================================================================
CREATE TABLE usuario_general (
    usuario_id          INTEGER         PRIMARY KEY,
    fecha_registro      DATE            NOT NULL DEFAULT CURRENT_DATE,
    estado_verificacion VARCHAR(30)     NOT NULL DEFAULT 'NO_VERIFICADO',

    CONSTRAINT chk_usuario_general_estado
        CHECK (estado_verificacion IN ('NO_VERIFICADO', 'PENDIENTE', 'VERIFICADO')),
    CONSTRAINT fk_usuario_general_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- =============================================================================
-- 6. DISPOSITIVO
-- =============================================================================
CREATE TABLE dispositivo (
    id              SERIAL          PRIMARY KEY,
    funcionario_id  INTEGER         NOT NULL,
    device_uid      VARCHAR(100)    NOT NULL,

    CONSTRAINT uq_dispositivo_uid UNIQUE (device_uid),
    CONSTRAINT fk_dispositivo_funcionario
        FOREIGN KEY (funcionario_id) REFERENCES funcionario(usuario_id) ON DELETE RESTRICT
);

-- =============================================================================
-- 7. ESTADIO
-- =============================================================================
CREATE TABLE estadio (
    id          SERIAL          PRIMARY KEY,
    admin_id    INTEGER         NOT NULL,
    nombre      VARCHAR(200)    NOT NULL,
    pais        VARCHAR(100)    NOT NULL,
    ciudad      VARCHAR(150)    NOT NULL,

    CONSTRAINT fk_estadio_admin
        FOREIGN KEY (admin_id) REFERENCES admin_pais(usuario_id) ON DELETE RESTRICT
);

-- =============================================================================
-- 8. SECTOR
-- =============================================================================
CREATE TABLE sector (
    id                  SERIAL          PRIMARY KEY,
    estadio_id          INTEGER         NOT NULL,
    codigo              VARCHAR(10)     NOT NULL,
    capacidad_maxima    INTEGER         NOT NULL,

    CONSTRAINT uq_sector_estadio_codigo UNIQUE (estadio_id, codigo),
    CONSTRAINT chk_sector_capacidad CHECK (capacidad_maxima > 0),
    CONSTRAINT fk_sector_estadio
        FOREIGN KEY (estadio_id) REFERENCES estadio(id) ON DELETE CASCADE
);

-- =============================================================================
-- 9. FASE  (NUEVA)
-- Etapas del torneo: fase de grupos, octavos, cuartos, semifinal, final.
-- =============================================================================
CREATE TABLE fase (
    id      SERIAL          PRIMARY KEY,
    nombre  VARCHAR(100)    NOT NULL,
    orden   SMALLINT        NOT NULL,

    CONSTRAINT uq_fase_nombre UNIQUE (nombre),
    CONSTRAINT uq_fase_orden UNIQUE (orden),
    CONSTRAINT chk_fase_orden CHECK (orden > 0)
);

-- =============================================================================
-- 10. FASE_SECTOR  (NUEVA, reemplaza a evento_sector)
-- Precio de referencia de cada sector según la fase del torneo.
-- =============================================================================
CREATE TABLE fase_sector (
    id          SERIAL          PRIMARY KEY,
    fase_id     INTEGER         NOT NULL,
    sector_id   INTEGER         NOT NULL,
    precio      NUMERIC(10,2)   NOT NULL,

    CONSTRAINT uq_fase_sector UNIQUE (fase_id, sector_id),
    CONSTRAINT chk_fase_sector_precio CHECK (precio > 0),

    CONSTRAINT fk_fase_sector_fase
        FOREIGN KEY (fase_id) REFERENCES fase(id) ON DELETE RESTRICT,
    CONSTRAINT fk_fase_sector_sector
        FOREIGN KEY (sector_id) REFERENCES sector(id) ON DELETE CASCADE
);

-- =============================================================================
-- 11. EVENTO  (MODIFICADA: agrega fase_id)
-- =============================================================================
CREATE TABLE evento (
    id                  SERIAL          PRIMARY KEY,
    estadio_id          INTEGER         NOT NULL,
    admin_id            INTEGER         NOT NULL,
    fase_id             INTEGER         NOT NULL,
    equipo_local        VARCHAR(100)    NOT NULL,
    equipo_visitante    VARCHAR(100)    NOT NULL,
    fecha_hora          TIMESTAMP       NOT NULL,

    CONSTRAINT chk_evento_equipos_distintos CHECK (equipo_local <> equipo_visitante),

    CONSTRAINT fk_evento_estadio
        FOREIGN KEY (estadio_id) REFERENCES estadio(id) ON DELETE RESTRICT,
    CONSTRAINT fk_evento_admin
        FOREIGN KEY (admin_id) REFERENCES admin_pais(usuario_id) ON DELETE RESTRICT,
    CONSTRAINT fk_evento_fase
        FOREIGN KEY (fase_id) REFERENCES fase(id) ON DELETE RESTRICT
);

-- =============================================================================
-- 12. VENTA
-- =============================================================================
CREATE TABLE venta (
    id              SERIAL          PRIMARY KEY,
    usuario_id      INTEGER         NOT NULL,
    fecha           TIMESTAMP       NOT NULL DEFAULT NOW(),
    estado          VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE',
    monto_total     NUMERIC(10,2)   NOT NULL DEFAULT 0,
    tasa_comision   NUMERIC(5,4)    NOT NULL DEFAULT 0.0500,

    CONSTRAINT chk_venta_estado CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'PAGA')),
    CONSTRAINT chk_venta_monto CHECK (monto_total >= 0),
    CONSTRAINT chk_venta_tasa CHECK (tasa_comision >= 0 AND tasa_comision <= 1),

    CONSTRAINT fk_venta_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario_general(usuario_id) ON DELETE RESTRICT
);

-- =============================================================================
-- 13. ENTRADA  (MODIFICADA: FK directa a evento y sector, agrega precio)
-- =============================================================================
CREATE TABLE entrada (
    id                          SERIAL          PRIMARY KEY,
    venta_id                    INTEGER         NOT NULL,
    evento_id                   INTEGER         NOT NULL,
    sector_id                   INTEGER         NOT NULL,
    propietario_actual_id       INTEGER         NOT NULL,
    precio                      NUMERIC(10,2)   NOT NULL,
    estado                      VARCHAR(20)     NOT NULL DEFAULT 'ACTIVA',
    transferencias_realizadas   SMALLINT        NOT NULL DEFAULT 0,

    CONSTRAINT chk_entrada_estado CHECK (estado IN ('ACTIVA', 'TRANSFERIDA', 'CONSUMIDA')),
    CONSTRAINT chk_entrada_transferencias
        CHECK (transferencias_realizadas >= 0 AND transferencias_realizadas <= 3),
    CONSTRAINT chk_entrada_precio CHECK (precio > 0),

    CONSTRAINT fk_entrada_venta
        FOREIGN KEY (venta_id) REFERENCES venta(id) ON DELETE RESTRICT,
    CONSTRAINT fk_entrada_evento
        FOREIGN KEY (evento_id) REFERENCES evento(id) ON DELETE RESTRICT,
    CONSTRAINT fk_entrada_sector
        FOREIGN KEY (sector_id) REFERENCES sector(id) ON DELETE RESTRICT,
    CONSTRAINT fk_entrada_propietario
        FOREIGN KEY (propietario_actual_id) REFERENCES usuario(id) ON DELETE RESTRICT
);

-- =============================================================================
-- 14. TRANSFERENCIA
-- =============================================================================
CREATE TABLE transferencia (
    id          SERIAL          PRIMARY KEY,
    entrada_id  INTEGER         NOT NULL,
    origen_id   INTEGER         NOT NULL,
    destino_id  INTEGER         NOT NULL,
    fecha_hora  TIMESTAMP       NOT NULL DEFAULT NOW(),
    estado      VARCHAR(20)     NOT NULL DEFAULT 'PENDIENTE',

    CONSTRAINT chk_transferencia_estado CHECK (estado IN ('PENDIENTE', 'ACEPTADA', 'RECHAZADA')),
    CONSTRAINT chk_transferencia_distintos CHECK (origen_id <> destino_id),

    CONSTRAINT fk_transferencia_entrada
        FOREIGN KEY (entrada_id) REFERENCES entrada(id) ON DELETE RESTRICT,
    CONSTRAINT fk_transferencia_origen
        FOREIGN KEY (origen_id) REFERENCES usuario(id) ON DELETE RESTRICT,
    CONSTRAINT fk_transferencia_destino
        FOREIGN KEY (destino_id) REFERENCES usuario(id) ON DELETE RESTRICT
);

-- =============================================================================
-- 15. TOKEN_QR
-- =============================================================================
CREATE TABLE token_qr (
    id              SERIAL          PRIMARY KEY,
    entrada_id      INTEGER         NOT NULL,
    codigo          VARCHAR(500)    NOT NULL,
    generado_en     TIMESTAMP       NOT NULL DEFAULT NOW(),
    expira_en       TIMESTAMP       NOT NULL,
    activo          BOOLEAN         NOT NULL DEFAULT FALSE,

    CONSTRAINT uq_token_qr_codigo UNIQUE (codigo),
    CONSTRAINT chk_token_qr_expiracion CHECK (expira_en > generado_en),

    CONSTRAINT fk_token_qr_entrada
        FOREIGN KEY (entrada_id) REFERENCES entrada(id) ON DELETE CASCADE
);

-- =============================================================================
-- 16. VALIDACION_TERNARIA  (MODIFICADA: relación ternaria, reemplaza a validacion)
-- PK compuesta por las tres entidades participantes.
-- =============================================================================
CREATE TABLE validacion_ternaria (
    funcionario_id  INTEGER         NOT NULL,
    dispositivo_id  INTEGER         NOT NULL,
    entrada_id      INTEGER         NOT NULL,
    token_qr_id     INTEGER         NOT NULL,
    fecha_hora      TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_validacion_ternaria
        PRIMARY KEY (funcionario_id, dispositivo_id, entrada_id),

    CONSTRAINT uq_validacion_entrada UNIQUE (entrada_id),
    CONSTRAINT uq_validacion_token UNIQUE (token_qr_id),

    CONSTRAINT fk_validacion_funcionario
        FOREIGN KEY (funcionario_id) REFERENCES funcionario(usuario_id) ON DELETE RESTRICT,
    CONSTRAINT fk_validacion_dispositivo
        FOREIGN KEY (dispositivo_id) REFERENCES dispositivo(id) ON DELETE RESTRICT,
    CONSTRAINT fk_validacion_entrada
        FOREIGN KEY (entrada_id) REFERENCES entrada(id) ON DELETE RESTRICT,
    CONSTRAINT fk_validacion_token
        FOREIGN KEY (token_qr_id) REFERENCES token_qr(id) ON DELETE RESTRICT
);

-- =============================================================================
-- 17. FUNCIONARIO_SECTOR  (MODIFICADA: ahora apunta directo a sector)
-- =============================================================================
CREATE TABLE funcionario_sector (
    funcionario_id  INTEGER     NOT NULL,
    sector_id       INTEGER     NOT NULL,

    CONSTRAINT pk_funcionario_sector PRIMARY KEY (funcionario_id, sector_id),

    CONSTRAINT fk_funcionario_sector_funcionario
        FOREIGN KEY (funcionario_id) REFERENCES funcionario(usuario_id) ON DELETE CASCADE,
    CONSTRAINT fk_funcionario_sector_sector
        FOREIGN KEY (sector_id) REFERENCES sector(id) ON DELETE CASCADE
);


-- =============================================================================
-- TRIGGERS
-- =============================================================================

-- -----------------------------------------------------------------------------
-- T1: Máximo 5 entradas por venta
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_check_max_entradas_por_venta()
RETURNS TRIGGER AS $$
DECLARE
    v_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM ticketing.entrada
    WHERE venta_id = NEW.venta_id;

    IF v_count >= 5 THEN
        RAISE EXCEPTION 'Una venta no puede tener más de 5 entradas. Venta ID: %', NEW.venta_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_max_entradas_por_venta
    BEFORE INSERT ON entrada
    FOR EACH ROW
    EXECUTE FUNCTION fn_check_max_entradas_por_venta();

-- -----------------------------------------------------------------------------
-- T2: No superposición de eventos en el mismo estadio (ventana de 2 horas)
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_check_superposicion_eventos()
RETURNS TRIGGER AS $$
DECLARE
    v_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_count
    FROM ticketing.evento
    WHERE estadio_id = NEW.estadio_id
      AND id <> COALESCE(NEW.id, -1)
      AND (
          NEW.fecha_hora < (fecha_hora + INTERVAL '2 hours')
          AND (NEW.fecha_hora + INTERVAL '2 hours') > fecha_hora
      );

    IF v_count > 0 THEN
        RAISE EXCEPTION 'Ya existe un evento en el estadio % en ese horario.', NEW.estadio_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_superposicion_eventos
    BEFORE INSERT OR UPDATE ON evento
    FOR EACH ROW
    EXECUTE FUNCTION fn_check_superposicion_eventos();

-- -----------------------------------------------------------------------------
-- T3 (NUEVO en v3): el sector de la entrada debe pertenecer al mismo
-- estadio que el evento al que corresponde la entrada.
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_check_sector_pertenece_estadio_evento()
RETURNS TRIGGER AS $$
DECLARE
    v_estadio_evento  INTEGER;
    v_estadio_sector  INTEGER;
BEGIN
    SELECT estadio_id INTO v_estadio_evento
    FROM ticketing.evento WHERE id = NEW.evento_id;

    SELECT estadio_id INTO v_estadio_sector
    FROM ticketing.sector WHERE id = NEW.sector_id;

    IF v_estadio_evento <> v_estadio_sector THEN
        RAISE EXCEPTION
            'El sector % no pertenece al estadio del evento % (sector del estadio %, evento del estadio %).',
            NEW.sector_id, NEW.evento_id, v_estadio_sector, v_estadio_evento;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sector_pertenece_estadio_evento
    BEFORE INSERT OR UPDATE ON entrada
    FOR EACH ROW
    EXECUTE FUNCTION fn_check_sector_pertenece_estadio_evento();

-- -----------------------------------------------------------------------------
-- T4 (NUEVO en v3): completar automáticamente el precio de la entrada
-- tomando el valor vigente en fase_sector según la fase del evento y el sector.
-- Solo completa si no fue provisto explícitamente (precio = 0 o NULL antes del insert).
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_completar_precio_entrada()
RETURNS TRIGGER AS $$
DECLARE
    v_precio NUMERIC(10,2);
    v_fase_id INTEGER;
BEGIN
    IF NEW.precio IS NULL THEN
        SELECT fase_id INTO v_fase_id FROM ticketing.evento WHERE id = NEW.evento_id;

        SELECT precio INTO v_precio
        FROM ticketing.fase_sector
        WHERE fase_id = v_fase_id AND sector_id = NEW.sector_id;

        IF v_precio IS NULL THEN
            RAISE EXCEPTION
                'No existe precio definido en fase_sector para fase % y sector %.',
                v_fase_id, NEW.sector_id;
        END IF;

        NEW.precio := v_precio;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_completar_precio_entrada
    BEFORE INSERT ON entrada
    FOR EACH ROW
    EXECUTE FUNCTION fn_completar_precio_entrada();

-- -----------------------------------------------------------------------------
-- T5: Un solo token activo por entrada
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_desactivar_token_anterior()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.activo = TRUE THEN
        UPDATE ticketing.token_qr
        SET activo = FALSE
        WHERE entrada_id = NEW.entrada_id
          AND id <> NEW.id
          AND activo = TRUE;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_un_token_activo_por_entrada
    BEFORE INSERT OR UPDATE ON token_qr
    FOR EACH ROW
    EXECUTE FUNCTION fn_desactivar_token_anterior();

-- -----------------------------------------------------------------------------
-- T6: Marcar entrada como CONSUMIDA al validarla (ahora sobre validacion_ternaria)
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_consumir_entrada_al_validar()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE ticketing.entrada
    SET estado = 'CONSUMIDA'
    WHERE id = NEW.entrada_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_consumir_entrada
    AFTER INSERT ON validacion_ternaria
    FOR EACH ROW
    EXECUTE FUNCTION fn_consumir_entrada_al_validar();

-- -----------------------------------------------------------------------------
-- T7: Al aceptar una transferencia, actualizar propietario y contador
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_aplicar_transferencia()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.estado = 'ACEPTADA' AND OLD.estado = 'PENDIENTE' THEN
        UPDATE ticketing.entrada
        SET propietario_actual_id = NEW.destino_id,
            transferencias_realizadas = transferencias_realizadas + 1,
            estado = 'TRANSFERIDA'
        WHERE id = NEW.entrada_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_aplicar_transferencia
    AFTER UPDATE ON transferencia
    FOR EACH ROW
    EXECUTE FUNCTION fn_aplicar_transferencia();

-- -----------------------------------------------------------------------------
-- T8: Verificar que la entrada no esté consumida ni supere transferencias
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_check_entrada_transferible()
RETURNS TRIGGER AS $$
DECLARE
    v_estado         VARCHAR(20);
    v_transferencias SMALLINT;
BEGIN
    SELECT estado, transferencias_realizadas
    INTO v_estado, v_transferencias
    FROM ticketing.entrada
    WHERE id = NEW.entrada_id;

    IF v_estado = 'CONSUMIDA' THEN
        RAISE EXCEPTION 'La entrada % ya fue consumida y no puede transferirse.', NEW.entrada_id;
    END IF;

    IF v_transferencias >= 3 THEN
        RAISE EXCEPTION 'La entrada % alcanzó el máximo de 3 transferencias.', NEW.entrada_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_entrada_transferible
    BEFORE INSERT ON transferencia
    FOR EACH ROW
    EXECUTE FUNCTION fn_check_entrada_transferible();

-- -----------------------------------------------------------------------------
-- T9: Verificar consistencia de rol al insertar en subtipos
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_check_rol_admin_pais()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT rol FROM ticketing.usuario WHERE id = NEW.usuario_id) <> 'ADMIN_PAIS' THEN
        RAISE EXCEPTION 'El usuario % no tiene rol ADMIN_PAIS.', NEW.usuario_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_rol_admin_pais
    BEFORE INSERT ON admin_pais
    FOR EACH ROW
    EXECUTE FUNCTION fn_check_rol_admin_pais();

CREATE OR REPLACE FUNCTION fn_check_rol_funcionario()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT rol FROM ticketing.usuario WHERE id = NEW.usuario_id) <> 'FUNCIONARIO' THEN
        RAISE EXCEPTION 'El usuario % no tiene rol FUNCIONARIO.', NEW.usuario_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_rol_funcionario
    BEFORE INSERT ON funcionario
    FOR EACH ROW
    EXECUTE FUNCTION fn_check_rol_funcionario();

CREATE OR REPLACE FUNCTION fn_check_rol_usuario_general()
RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT rol FROM ticketing.usuario WHERE id = NEW.usuario_id) <> 'USUARIO_GENERAL' THEN
        RAISE EXCEPTION 'El usuario % no tiene rol USUARIO_GENERAL.', NEW.usuario_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_rol_usuario_general
    BEFORE INSERT ON usuario_general
    FOR EACH ROW
    EXECUTE FUNCTION fn_check_rol_usuario_general();


-- =============================================================================
-- ÍNDICES
-- =============================================================================
CREATE INDEX idx_entrada_propietario ON entrada(propietario_actual_id);
CREATE INDEX idx_entrada_venta ON entrada(venta_id);
CREATE INDEX idx_entrada_evento ON entrada(evento_id);
CREATE INDEX idx_entrada_sector ON entrada(sector_id);
CREATE INDEX idx_venta_usuario ON venta(usuario_id);
CREATE INDEX idx_transferencia_entrada ON transferencia(entrada_id);
CREATE INDEX idx_transferencia_destino_estado ON transferencia(destino_id, estado);
CREATE INDEX idx_token_qr_entrada_activo ON token_qr(entrada_id, activo);
CREATE INDEX idx_evento_estadio_fecha ON evento(estadio_id, fecha_hora);
CREATE INDEX idx_evento_fase ON evento(fase_id);
CREATE INDEX idx_fase_sector_fase ON fase_sector(fase_id);
CREATE INDEX idx_funcionario_sector_funcionario ON funcionario_sector(funcionario_id);
CREATE INDEX idx_validacion_funcionario ON validacion_ternaria(funcionario_id);
CREATE INDEX idx_validacion_dispositivo ON validacion_ternaria(dispositivo_id);


-- =============================================================================
-- VISTAS UTILES
-- =============================================================================

-- Vista: entradas activas con datos del evento y propietario actual
CREATE VIEW v_entradas_activas AS
SELECT
    e.id                        AS entrada_id,
    u.mail                      AS propietario_mail,
    ev.equipo_local,
    ev.equipo_visitante,
    ev.fecha_hora               AS fecha_evento,
    est.nombre                  AS estadio,
    s.codigo                    AS sector,
    f.nombre                    AS fase,
    e.precio,
    e.transferencias_realizadas,
    e.estado
FROM entrada e
JOIN usuario u   ON u.id = e.propietario_actual_id
JOIN evento ev   ON ev.id = e.evento_id
JOIN sector s    ON s.id = e.sector_id
JOIN estadio est ON est.id = ev.estadio_id
JOIN fase f      ON f.id = ev.fase_id
WHERE e.estado = 'ACTIVA';

-- Vista: ranking de eventos por entradas vendidas
CREATE VIEW v_ranking_eventos AS
SELECT
    ev.id                AS evento_id,
    ev.equipo_local,
    ev.equipo_visitante,
    ev.fecha_hora,
    est.nombre            AS estadio,
    f.nombre              AS fase,
    COUNT(en.id)           AS total_entradas_vendidas,
    SUM(en.precio)         AS recaudacion_total
FROM evento ev
JOIN estadio est ON est.id = ev.estadio_id
JOIN fase f      ON f.id = ev.fase_id
LEFT JOIN entrada en ON en.evento_id = ev.id
GROUP BY ev.id, ev.equipo_local, ev.equipo_visitante, ev.fecha_hora, est.nombre, f.nombre
ORDER BY total_entradas_vendidas DESC;

-- Vista: ranking de mayores compradores
CREATE VIEW v_ranking_compradores AS
SELECT
    u.id                    AS usuario_id,
    u.mail,
    COUNT(en.id)            AS total_entradas_compradas,
    SUM(en.precio)          AS total_gastado
FROM usuario u
JOIN usuario_general ug ON ug.usuario_id = u.id
JOIN venta v            ON v.usuario_id = ug.usuario_id
JOIN entrada en         ON en.venta_id = v.id
GROUP BY u.id, u.mail
ORDER BY total_entradas_compradas DESC;

-- Vista: auditoria de funcionarios (sectores asignados vs validaciones realizadas)
CREATE VIEW v_auditoria_funcionarios AS
SELECT
    f.usuario_id                AS funcionario_id,
    u.mail                      AS funcionario_mail,
    f.numero_legajo,
    s.id                         AS sector_id,
    s.codigo                    AS sector,
    COUNT(val.entrada_id)        AS validaciones_realizadas
FROM funcionario_sector fs
JOIN funcionario f  ON f.usuario_id = fs.funcionario_id
JOIN usuario u       ON u.id = f.usuario_id
JOIN sector s         ON s.id = fs.sector_id
LEFT JOIN validacion_ternaria val
    ON val.funcionario_id = f.usuario_id
   AND val.entrada_id IN (SELECT id FROM entrada WHERE sector_id = s.id)
GROUP BY f.usuario_id, u.mail, f.numero_legajo, s.id, s.codigo;

-- Vista: precios vigentes por fase y sector (incluye estadio)
CREATE VIEW v_precios_vigentes AS
SELECT
    f.nombre        AS fase,
    f.orden,
    est.nombre      AS estadio,
    s.codigo        AS sector,
    s.capacidad_maxima,
    fs.precio
FROM fase_sector fs
JOIN fase f      ON f.id = fs.fase_id
JOIN sector s    ON s.id = fs.sector_id
JOIN estadio est ON est.id = s.estadio_id
ORDER BY f.orden, est.nombre, s.codigo;
