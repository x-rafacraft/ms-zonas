# 🛡️ Microservicio de Zonas Seguras

Microservicio REST reactivo para la gestión de zonas seguras, desarrollado con Spring Boot WebFlux y SQL Server.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Endpoints](#endpoints)
- [Instalación y Ejecución](#instalación-y-ejecución)
- [Configuración](#configuración)
- [Testing](#testing)

---

## ✨ Características

- ✅ **CRUD Completo**: Crear, listar, consultar, actualizar y eliminar zonas
- ✅ **Filtros Avanzados**: Búsqueda por provincia, distrito y nivel de seguridad
- ✅ **Paginación**: Listados paginados con límite configurable
- ✅ **Resumen Estadístico**: Endpoint de resumen con totales por nivel de seguridad
- ✅ **Validaciones**: Validación exhaustiva en capa de negocio
- ✅ **Prevención de Duplicados**: Validación de nombre y coordenadas
- ✅ **Manejo de Errores**: Respuestas de error consistentes y descriptivas
- ✅ **Código Manual de Zona**: Asignación manual de IDs (no auto-increment)
- ✅ **Programación Reactiva**: Alto rendimiento con Project Reactor

---

## 🛠️ Tecnologías

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 21 | Lenguaje base |
| Spring Boot | 3.x | Framework principal |
| Spring WebFlux | 3.x | API REST reactiva |
| Project Reactor | 3.x | Programación reactiva |
| JDBC + SQL Server | - | Persistencia de datos |
| MapStruct | 1.5.x | Mapeo de objetos |
| Lombok | 1.18.x | Reducción de boilerplate |
| JUnit 5 | 5.x | Testing unitario |
| Mockito | 5.x | Mocking en tests |
| Gradle | 9.2.1 | Gestión de dependencias |
| GitHub Actions | - | CI/CD |

---

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Ports & Adapters)

```
src/main/java/pe/com/practicar/
│
├── expose/                    # Capa de Exposición (API REST)
│   ├── controller/            # Controllers y API definitions
│   └── schema/                # Request/Response DTOs
│
├── delegate/                  # Capa de Delegación
│   ├── builder/               # Mappers (MapStruct)
│   └── impl/                  # Implementaciones de delegates
│
├── business/                  # Capa de Negocio
│   ├── dto/                   # DTOs internos
│   ├── exception/             # Excepciones de negocio
│   ├── validator/             # Validadores
│   └── impl/                  # Servicios de negocio
│
├── repository/                # Capa de Persistencia
│   ├── model/                 # Modelos de BD
│   └── impl/                  # Implementación JDBC
│
├── mapper/                    # Mappers entre Repository y Business
└── util/                      # Utilidades y constantes
```

---

## 🚀 Endpoints

### Base URL
```
http://localhost:9091
```

### 1. **Listar Zonas** (Paginado)
```http
GET /zones?paginaActual=1&tamanioPagina=10
```

**Query Parameters** (opcionales):
- `paginaActual`: Número de página (1-1000, default: 1)
- `tamanioPagina`: Registros por página (1-1000, default: 10)
- `provincia`: Filtrar por provincia (ej: "Lima")
- `distrito`: Filtrar por distrito (ej: "Miraflores")
- `nivelSeguridad`: Filtrar por nivel de seguridad (1-10)

**Respuesta** `200 OK`:
```json
{
  "zone": [
    {
      "codzona": 2,
      "nombre": "Comisaría de San Isidro",
      "distrito": "San Isidro",
      "provincia": "Lima",
      "region": "Lima",
      "pais": "Perú",
      "latitud": -12.0979,
      "longitud": -77.0376,
      "nivelSeguridad": 5,
      "descripcion": "Comisaría principal del distrito",
      "activo": true,
      "usuarioCreacion": "admin",
      "fechaCreacion": "2025-01-17T12:34:26.066"
    }
  ],
  "paginaActual": 1,
  "tamanioPagina": 10,
  "existeSiguientePagina": false
}
```

---

### 2. **Obtener Zona por ID**
```http
GET /zones/{codigoZona}
```

**Parámetros**:
- `codigoZona`: ID numérico de la zona

**Respuesta** `200 OK`:
```json
{
  "codzona": 2,
  "nombre": "Comisaría de San Isidro",
  "distrito": "San Isidro",
  "provincia": "Lima",
  ...
}
```

**Error** `404 NOT FOUND`:
```json
{
  "error": {
    "tipo": "ZONE_NOT_FOUND",
    "codigo": "001",
    "mensaje": "Zona con código 999 no encontrada"
  }
}
```

---

### 3. **Crear Zona**
```http
POST /zones
Content-Type: application/json
```

**Body**:
```json
{
  "datos": {
    "codzona": 1234,
    "nombre": "Zona Central Lima",
    "distrito": "Miraflores",
    "provincia": "Lima",
    "region": "Lima",
    "pais": "Perú",
    "latitud": -12.1191,
    "longitud": -77.0292,
    "nivelSeguridad": 4,
    "descripcion": "Zona comercial segura",
    "usuarioCreacion": "admin"
  }
}
```

**Validaciones**:
- `codzona`: Obligatorio, >= 1, único
- `nombre`: Obligatorio, max 200 caracteres, sin caracteres especiales, único
- `latitud`: Obligatorio, entre -90 y 90
- `longitud`: Obligatorio, entre -180 y 180
- `nivelSeguridad`: Obligatorio, entre 1 y 10
- Coordenadas únicas (no puede haber dos zonas en el mismo punto)

**Respuesta** `201 CREATED`: Retorna la zona creada

**Error** `409 CONFLICT`:
```json
{
  "error": {
    "tipo": "ZONE_ALREADY_EXISTS",
    "codigo": "002",
    "mensaje": "Ya existe una zona con el código: 1234"
  }
}
```

---

### 4. **Actualizar Zona (Parcial)**
```http
PATCH /zones/{codigoZona}
Content-Type: application/json
```

**Body** (todos los campos opcionales):
```json
{
  "datos": {
    "nombre": "Nuevo Nombre",
    "latitud": -12.0464,
    "longitud": -77.0428,
    "nivelSeguridad": 5,
    "descripcion": "Descripción actualizada",
    "activo": true,
    "usuarioActualizacion": "admin"
  }
}
```

**Respuesta** `200 OK`: Zona actualizada

---

### 5. **Reemplazar Zona (Completo)**
```http
PUT /zones/{codigoZona}
Content-Type: application/json
```

**Body**: Igual a POST (todos los campos obligatorios excepto codzona)

**Respuesta** `200 OK`: Zona reemplazada

---

### 6. **Eliminar Zona**
```http
DELETE /zones/{codigoZona}
```

**Respuesta** `204 NO CONTENT`: Zona eliminada

**Error** `404 NOT FOUND`: Si la zona no existe

---

### 7. **Resumen de Zonas** ✨ NUEVO
```http
GET /zones/summary
```

**Respuesta** `200 OK`:
```json
{
  "resumenPorNivel": [
    {
      "nivelSeguridad": 1,
      "cantidad": 2
    },
    {
      "nivelSeguridad": 4,
      "cantidad": 11
    },
    {
      "nivelSeguridad": 5,
      "cantidad": 1
    }
  ],
  "totalZonas": 14
}
```

---

## 🔧 Instalación y Ejecución

### Prerrequisitos
- ☕ **Java 21+** (JDK instalado y configurado en PATH)
- 🗄️ **SQL Server** (2019 o superior)
- 📦 **Gradle 9.2.1+** (opcional, el proyecto incluye wrapper)
- 🛠️ **SQL Server Management Studio (SSMS)** o Azure Data Studio (recomendado)

---

### 📥 Paso 1: Clonar el Repositorio
```bash
git clone https://github.com/tu-usuario/ms-zonas-seguras.git
cd ms-zonas-seguras
```

---

### 🗄️ Paso 2: Configurar Base de Datos en SQL Server

#### 2.1. Crear la Base de Datos

Abre **SQL Server Management Studio (SSMS)** o **Azure Data Studio** y ejecuta:

```sql
-- Crear la base de datos
CREATE DATABASE ZONAS_SEGURAS_DB;
GO
```

#### 2.2. Crear el Schema

```sql
-- Usar la base de datos
USE ZONAS_SEGURAS_DB;
GO

-- Crear el schema PRUEBA00
CREATE SCHEMA PRUEBA00;
GO
```

#### 2.3. Crear la Tabla de Zonas

```sql
-- Crear tabla en el schema PRUEBA00
CREATE TABLE PRUEBA00.zonas (
    codzona INT PRIMARY KEY,
    nombre NVARCHAR(200) NOT NULL,
    distrito NVARCHAR(100),
    provincia NVARCHAR(100),
    region NVARCHAR(100),
    pais NVARCHAR(100),
    latitud DECIMAL(10, 7),
    longitud DECIMAL(10, 7),
    nivelSeguridad INT,
    descripcion NVARCHAR(500),
    activo BIT DEFAULT 1,
    usuarioCreacion NVARCHAR(100),
    fechaCreacion DATETIME DEFAULT GETDATE(),
    usuarioActualizacion NVARCHAR(100),
    fechaActualizacion DATETIME
);
GO
```

#### 2.4. Crear Usuario de Aplicación (Recomendado)

**Opción A: Usuario con autenticación SQL Server**
```sql
-- Crear login a nivel de servidor
USE master;
GO
CREATE LOGIN appuser25 WITH PASSWORD = 'App123Password!';
GO

-- Crear usuario en la base de datos
USE ZONAS_SEGURAS_DB;
GO
CREATE USER appuser25 FOR LOGIN appuser25;
GO

-- Otorgar permisos necesarios
ALTER ROLE db_datareader ADD MEMBER appuser25;
ALTER ROLE db_datawriter ADD MEMBER appuser25;
GO

-- Permisos específicos en el schema
GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::PRUEBA00 TO appuser25;
GO
```

**Opción B: Usuario con Windows Authentication**
```sql
USE ZONAS_SEGURAS_DB;
GO
CREATE USER [DOMAIN\username] FROM LOGIN [DOMAIN\username];
ALTER ROLE db_datareader ADD MEMBER [DOMAIN\username];
ALTER ROLE db_datawriter ADD MEMBER [DOMAIN\username];
GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::PRUEBA00 TO [DOMAIN\username];
GO
```

#### 2.5. Insertar Datos de Prueba (Opcional)

```sql
INSERT INTO PRUEBA00.zonas (codzona, nombre, distrito, provincia, region, pais, latitud, longitud, nivelSeguridad, descripcion, usuarioCreacion)
VALUES 
    (1, 'Comisaría de Miraflores', 'Miraflores', 'Lima', 'Lima', 'Perú', -12.1191, -77.0292, 5, 'Comisaría principal del distrito', 'admin'),
    (2, 'Comisaría de San Isidro', 'San Isidro', 'Lima', 'Lima', 'Perú', -12.0979, -77.0376, 5, 'Comisaría principal del distrito', 'admin'),
    (3, 'Serenazgo Surco', 'Santiago de Surco', 'Lima', 'Lima', 'Perú', -12.1463, -76.9979, 4, 'Base de serenazgo', 'admin');
GO
```

#### 2.6. Verificar la Instalación

```sql
-- Verificar que la tabla existe
SELECT * FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'PRUEBA00' AND TABLE_NAME = 'zonas';

-- Verificar datos insertados
SELECT * FROM PRUEBA00.zonas;

-- Verificar permisos del usuario
SELECT 
    USER_NAME() AS CurrentUser,
    HAS_PERMS_BY_NAME('PRUEBA00.zonas', 'OBJECT', 'SELECT') AS HasSelect,
    HAS_PERMS_BY_NAME('PRUEBA00.zonas', 'OBJECT', 'INSERT') AS HasInsert,
    HAS_PERMS_BY_NAME('PRUEBA00.zonas', 'OBJECT', 'UPDATE') AS HasUpdate,
    HAS_PERMS_BY_NAME('PRUEBA00.zonas', 'OBJECT', 'DELETE') AS HasDelete;
```

---

### ⚙️ Paso 3: Configurar application.yml

Edita el archivo `src/main/resources/application.yml` con tus credenciales:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://localhost:1433;databaseName=ZONAS_SEGURAS_DB;encrypt=false;trustServerCertificate=true
    username: appuser25
    password: App123Password!
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.SQLServerDialect
        default_schema: PRUEBA00

server:
  port: 9091
```

**Parámetros importantes:**
- `url`: Cambia `localhost` si tu SQL Server está en otro servidor
- `1433`: Puerto por defecto de SQL Server
- `databaseName`: Nombre de tu base de datos
- `encrypt=false;trustServerCertificate=true`: Para desarrollo local
- `username` / `password`: Credenciales del usuario creado
- `default_schema`: Schema donde están las tablas (PRUEBA00)

**Para producción:**
```yaml
spring:
  datasource:
    url: jdbc:sqlserver://${DB_HOST:localhost}:${DB_PORT:1433};databaseName=${DB_NAME:ZONAS_SEGURAS_DB};encrypt=true;trustServerCertificate=false
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

---

### 🚀 Paso 4: Compilar y Ejecutar

#### Opción A: Con Gradle Wrapper (Recomendado)

**En Windows:**
```bash
# Compilar el proyecto
.\gradlew clean build

# Ejecutar la aplicación
.\gradlew bootRun
```

**En Linux/Mac:**
```bash
# Compilar el proyecto
./gradlew clean build

# Ejecutar la aplicación
./gradlew bootRun
```

#### Opción B: Ejecutar el JAR directamente

```bash
# Compilar
.\gradlew clean build

# Ejecutar el JAR generado
java -jar build\libs\ms-zonas-seguras-0.0.1-SNAPSHOT.jar
```

#### Opción C: Con variables de entorno

```bash
# Windows
set DB_HOST=localhost
set DB_PORT=1433
set DB_NAME=ZONAS_SEGURAS_DB
set DB_USER=appuser25
set DB_PASSWORD=App123Password!
.\gradlew bootRun

# Linux/Mac
export DB_HOST=localhost
export DB_PORT=1433
export DB_NAME=ZONAS_SEGURAS_DB
export DB_USER=appuser25
export DB_PASSWORD=App123Password!
./gradlew bootRun
```

---

### ✅ Paso 5: Verificar que funciona

Una vez iniciada la aplicación, deberías ver en la consola:
```
INFO  o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 9091
INFO  p.c.p.LaunchApplication                   : Started LaunchApplication in X.XXX seconds
```

**Prueba el endpoint:**

```bash
# Con curl
curl http://localhost:9091/zones

# Con PowerShell
Invoke-WebRequest -Uri http://localhost:9091/zones | Select-Object -Expand Content
```

**O abre en tu navegador:**
```
http://localhost:9091/zones
```

---

###  Estructura de Archivos de Configuración

```
ms-zonas-seguras/
├── src/main/resources/
│   ├── application.yml          # Configuración principal
│   ├── application-dev.yml      # Configuración desarrollo (opcional)
│   ├── application-prod.yml     # Configuración producción (opcional)
│   └── banner.txt               # Banner de inicio
└── build.gradle                 # Dependencias del proyecto
```

La aplicación estará disponible en: **`http://localhost:9091`** 🚀

---

## ⚙️ Configuración

### Variables de Entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Puerto del servidor | 9091 |
| `DB_HOST` | Host de SQL Server | localhost |
| `DB_PORT` | Puerto de SQL Server | 1433 |
| `DB_NAME` | Nombre de la BD | ZonasSeguras |
| `DB_SCHEMA` | Schema a usar | PRUEBA00 |
| `DB_USER` | Usuario de BD | - |
| `DB_PASSWORD` | Contraseña de BD | - |

---

## 🧪 Testing

### Ejecutar tests
```bash
# Todos los tests
./gradlew test

# Con reporte de cobertura
./gradlew test jacocoTestReport

# Ver reporte
open build/reports/tests/test/index.html
```

### Estructura de Tests
```
src/test/java/pe/com/practicar/
├── repository/impl/           # Tests de Repository (JDBC)
├── business/impl/             # Tests de Service (lógica de negocio)
├── delegate/impl/             # Tests de Delegate (mapeo)
└── mapper/                    # Tests de Mappers
```

---

## � Notas de Desarrollo

### Semana 1
- ✅ Setup inicial del proyecto
- ✅ Conexión a SQL Server
- ✅ Endpoint GET /zones con paginación

### Semana 2
- ✅ CRUD completo (GET por ID, POST, PATCH, PUT, DELETE)
- ✅ Filtros en GET (provincia, distrito, nivelSeguridad)
- ✅ Modelo de errores consistente
- ✅ Validaciones exhaustivas
- ✅ Prevención de duplicados
- ✅ Asignación manual de códigos de zona

### Semana 3
- ✅ Endpoint GET /zones/summary
- ✅ Refactorización de código duplicado
- ✅ Mejora de nombrado y legibilidad
- ✅ Tests completos para todos los endpoints
- ✅ Documentación completa
