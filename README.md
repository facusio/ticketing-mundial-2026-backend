# Ticketing Mundial 2026 — Backend

API REST desarrollada en Spring Boot (Java 21) + PostgreSQL para el sistema de ticketing del Mundial 2026.
Trabajo Obligatorio — Bases de Datos II, UCU 2026.

## Integrantes

- Facundo Banchero
- Gaston Puyares



## Requisitos previos

- Java 21+
- Maven 3.9+
- PostgreSQL 15+ con el schema `ticketing` ya creado (ejecutar el script SQL antes de levantar la app)

## Variables de entorno

| Variable | Default (dev) | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host de PostgreSQL |
| `DB_PORT` | `5432` | Puerto de PostgreSQL |
| `DB_NAME` | `ticketing_mundial` | Nombre de la base de datos |
| `DB_USER` | `postgres` | Usuario de PostgreSQL |
| `DB_PASSWORD` | `postgres` | Contraseña de PostgreSQL |
| `JWT_SECRET` | (dev only) | Secret HS256, mínimo 32 chars. **Obligatorio en prod** |
| `SPRING_PROFILES_ACTIVE` | `dev` | Perfil activo: `dev` o `prod` |
| `PORT` | `8080` | Puerto HTTP del servidor |

## Preparar la base de datos

1. Crear la base de datos:
   ```sql
   CREATE DATABASE ticketing_mundial;
   ```

2. Ejecutar el script DDL (crea el schema `ticketing` y todas las tablas, triggers y vistas):
   ```bash
   psql -U postgres -d ticketing_mundial -f src/main/resources/db/create.sql
   ```

   El script crea:
   - Schema: `ticketing`
   - Tablas: `usuario`, `admin_pais`, `funcionario`, `usuario_general`, `telefono`, `dispositivo`, `estadio`, `sector`, `fase`, `fase_sector`, `evento`, `venta`, `entrada`, `transferencia`, `token_qr`, `validacion_ternaria`, `funcionario_sector`
   - Triggers: control de límite de 5 entradas, límite de 3 transferencias, no superposición de eventos, un token QR activo por entrada, actualización de propietario al aceptar transferencia, marcado de entrada como CONSUMIDA al validar
   - Vistas: `v_ranking_eventos`, `v_ranking_compradores`, `v_auditoria_funcionarios`

## Levantar la aplicación

### Perfil desarrollo (con SQL logging)

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=ticketing_mundial
export DB_USER=postgres
export DB_PASSWORD=tu_password

mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Perfil producción

```bash
export DB_HOST=...
export DB_PASSWORD=...
export JWT_SECRET=un-secret-de-al-menos-32-caracteres-muy-seguro
export SPRING_PROFILES_ACTIVE=prod

mvn spring-boot:run
```

### Con jar empaquetado

```bash
mvn clean package -DskipTests
java -jar target/ticketing-mundial-2026-backend-1.0.0.jar
```

## Documentación Swagger

Una vez levantada la app:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

## Estructura de paquetes

```
com.ucu.ticketing
├── config/          SecurityConfig, CorsConfig, OpenApiConfig
├── controller/      AuthController, UsuarioController, AdminController, FuncionarioController
├── dto/
│   ├── request/     DTOs de entrada con validaciones @Valid
│   └── response/    DTOs de salida
├── entity/          Entidades JPA mapeadas al schema "ticketing"
├── exception/       Excepciones custom + GlobalExceptionHandler
├── repository/      Interfaces Spring Data JPA
├── security/        JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
└── service/         AuthService, UsuarioService, AdminService, FuncionarioService
```

## Endpoints principales

### Auth (público)
| Método | Path | Descripción |
|--------|------|-------------|
| POST | `/api/auth/register` | Registro de USUARIO_GENERAL |
| POST | `/api/auth/login` | Login → `{ token, rol, usuarioId }` |

### Usuario General (ROLE_USUARIO_GENERAL)
| Método | Path | Descripción |
|--------|------|-------------|
| GET | `/api/usuario/perfil` | Datos del usuario autenticado |
| GET | `/api/usuario/eventos` | Próximos eventos (`?pais=&estadioId=`) |
| GET | `/api/usuario/eventos/{id}/precios` | Precios por sector |
| POST | `/api/usuario/ventas` | Comprar entradas (máx. 5) |
| GET | `/api/usuario/ventas` | Historial de ventas |
| GET | `/api/usuario/entradas` | Entradas en posesión |
| GET | `/api/usuario/entradas/{id}/qr` | QR dinámico (30 seg) |
| POST | `/api/usuario/transferencias` | Iniciar transferencia |
| GET | `/api/usuario/transferencias/recibidas` | Transferencias pendientes recibidas |
| POST | `/api/usuario/transferencias/{id}/aceptar` | Aceptar transferencia |
| POST | `/api/usuario/transferencias/{id}/rechazar` | Rechazar transferencia |

### Admin País (ROLE_ADMIN_PAIS)
| Método | Path | Descripción |
|--------|------|-------------|
| POST | `/api/admin/estadios` | Crear estadio |
| GET | `/api/admin/estadios` | Listar estadios propios |
| POST | `/api/admin/estadios/{id}/sectores` | Crear sector |
| POST | `/api/admin/eventos` | Crear evento |
| GET | `/api/admin/eventos` | Listar eventos propios |
| POST | `/api/admin/fases` | Crear fase |
| POST | `/api/admin/fases/{id}/precios` | Definir precio sector-fase |
| GET | `/api/admin/reportes/ranking-eventos` | Vista v_ranking_eventos |
| GET | `/api/admin/reportes/ranking-compradores` | Vista v_ranking_compradores |
| GET | `/api/admin/reportes/auditoria-funcionarios` | Vista v_auditoria_funcionarios |

### Funcionario (ROLE_FUNCIONARIO)
| Método | Path | Descripción |
|--------|------|-------------|
| POST | `/api/funcionario/dispositivos/registrar` | Registrar dispositivo/navegador |
| GET | `/api/funcionario/sectores-asignados` | Sectores habilitados |
| POST | `/api/funcionario/validar` | Validar QR (`{ codigoQr, deviceUid }`) |
| GET | `/api/funcionario/validaciones` | Historial de validaciones |

## Notas de seguridad

- Passwords almacenadas con BCrypt (nunca en texto plano ni en logs)
- JWT firmado con HS256, expira en 24 horas
- Stateless: sin sesiones del lado del servidor
- Las reglas de negocio críticas se validan en el service (primera barrera) y también en triggers de PostgreSQL (segunda barrera)
