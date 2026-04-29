# NexoLab

Sistema de gestión de análisis clínicos veterinarios. Conecta clínicas, veterinarios y laboratorios en un flujo unificado: el veterinario solicita estudios, el laboratorio los procesa y carga los resultados.

## Stack

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 17+ |
| UI | Swing + [FlatLaf 3.7.1](https://www.formdev.com/flatlaf/) |
| Base de datos | MySQL 8+ |
| Driver JDBC | mysql-connector-j 9.7.0 |
| Autenticación | SHA-256 (contraseñas hasheadas) |

## Roles

| Rol | Acciones |
|-----|----------|
| **Admin** | Registrar y listar clínicas, laboratorios y usuarios |
| **Veterinario** | Registrar dueños y pacientes, crear solicitudes de análisis, ver historial de órdenes |
| **Laboratorio** | Ver órdenes pendientes o todas, actualizar estado de orden, cargar resultado (PDF + observaciones) |

## Requisitos

- Java 17 o superior
- MySQL 8+
- Las JARs de dependencias están incluidas en `lib/`

## Configuración

### 1. Base de datos

```sql
mysql -u root -p < sql/schema.sql
```

El script crea la base de datos `nexolab`, todas las tablas y datos de prueba.

### 2. Contraseña de base de datos

La app lee la contraseña desde la variable de entorno `DB_PASSWORD` (o el flag `-Ddb.password`). El usuario de base de datos configurado es `root`.

```bash
# Linux / macOS
export DB_PASSWORD="tu_contraseña"

# Windows PowerShell
$env:DB_PASSWORD="tu_contraseña"
```

### 3. Compilar

```bash
javac -cp "lib/mysql-connector-j-9.7.0.jar;lib/flatlaf-3.7.1.jar" \
      -sourcepath src -d out \
      src/Main.java src/model/*.java src/dao/*.java src/service/*.java src/view/*.java
```

> En Linux/macOS reemplazá `;` por `:` en el classpath.

### 4. Ejecutar

```bash
java -cp "out;lib/mysql-connector-j-9.7.0.jar;lib/flatlaf-3.7.1.jar" Main
```

## Usuarios de prueba

| Username | Contraseña | Rol | Clínica / Lab |
|----------|-----------|-----|---------------|
| `admin` | `admin123` | Admin | — |
| `dra.garcia` | `vet123` | Veterinario | Huellitas (ID 1) |
| `dr.perez` | `vet123` | Veterinario | PetCare (ID 2) |
| `tec.biovet` | `lab123` | Laboratorio | BioVet (ID 1) |

## Estructura del proyecto

```
nexolab/
├── src/
│   ├── Main.java
│   ├── dao/          # Acceso a base de datos (JDBC)
│   ├── model/        # Entidades del dominio
│   ├── service/      # Lógica de negocio
│   └── view/         # Interfaz Swing (Login, PanelAdmin, PanelVeterinario, PanelLaboratorio)
├── lib/              # Dependencias JAR
├── sql/
│   └── schema.sql    # DDL + datos semilla
└── out/              # Clases compiladas (generado)
```

## Flujo de una orden

```
Veterinario crea solicitud
        ↓
  Estado: PENDIENTE
        ↓
  Laboratorio recibe → RECIBIDA
        ↓
  En análisis       → EN_PROCESO
        ↓
  Finaliza          → FINALIZADA
        ↓
  Laboratorio carga resultado (observaciones + PDF opcional)
```

Estados terminales: `FINALIZADA`, `CANCELADA`.
