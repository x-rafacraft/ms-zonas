# GitHub Actions Workflows

Este directorio contiene los workflows de CI/CD para el microservicio de Zonas Seguras.

## 📋 Workflows Configurados

### `ci.yml` - Integración Continua

**Se ejecuta en:**
- ✅ Push a `main` o `develop`
- ✅ Pull Requests a `main` o `develop`

**Pasos:**

1. **Build**
   - Checkout del código
   - Configuración de JDK 21
   - Compilación con Gradle
   - Ejecución de tests
   - Generación de reportes
   - Subida de artefactos (JAR)

2. **Code Quality** (opcional)
   - Análisis de calidad de código
   - Checkstyle

## 🚀 Cómo Usar

### 1. Subir el Workflow a GitHub

```bash
git add .github/workflows/ci.yml
git commit -m "feat: agregar workflow de CI con GitHub Actions"
git push origin main
```

### 2. Verificar Ejecución

1. Ve a tu repositorio en GitHub
2. Click en la pestaña **Actions**
3. Verás el workflow ejecutándose automáticamente

### 3. Ver Resultados

Los workflows mostrarán:
- ✅ Estado de compilación
- ✅ Resultados de tests
- ✅ Artefactos generados (JAR)
- ✅ Reportes de calidad

## 🔧 Configuración Adicional

### Variables de Entorno (Secrets)

Si necesitas conectar a base de datos u otros servicios en CI:

```yaml
env:
  DB_URL: ${{ secrets.DB_URL }}
  DB_USER: ${{ secrets.DB_USER }}
  DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
```

Agrégalas en: **Settings** → **Secrets and variables** → **Actions**

### Badges en README

Agrega badges al README principal:

```markdown
[![CI](https://github.com/TU_USUARIO/ms-zonas-seguras/workflows/CI%20-%20Microservicio%20Zonas%20Seguras/badge.svg)](https://github.com/TU_USUARIO/ms-zonas-seguras/actions)
```

## 📊 Estructura del Workflow

```
CI Workflow
├── Build Job
│   ├── Checkout código
│   ├── Setup Java 21
│   ├── Compilar (./gradlew build)
│   ├── Ejecutar tests (./gradlew test)
│   ├── Reportes de tests
│   └── Subir JAR
└── Code Quality Job
    ├── Checkout código
    ├── Setup Java 21
    └── Checkstyle
```

## 🐛 Troubleshooting

### Error: "Permission denied" en gradlew
**Solución:** El workflow incluye `chmod +x ./gradlew`

### Tests fallan en CI pero pasan localmente
**Posibles causas:**
- Base de datos no disponible en CI
- Variables de entorno faltantes
- Diferencias de timezone

**Solución:** Usa `continue-on-error: true` temporalmente o configura una BD de prueba

### Artefactos no se suben
**Verificar:**
- El build fue exitoso (`if: success()`)
- La ruta del JAR es correcta
- El workflow tiene permisos

## 📝 Mejoras Futuras

- [ ] Agregar cobertura de código (JaCoCo)
- [ ] Integración con SonarQube
- [ ] Deploy automático a entornos de desarrollo
- [ ] Notificaciones a Slack/Discord
- [ ] Docker build y push a registry
- [ ] Tests de integración con base de datos
