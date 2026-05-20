Param(
    [string[]]$Targets = @("1.21.1", "1.21.4")
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$propsPath = Join-Path $root "gradle.properties"
$distDir = Join-Path $root "dist"

$baseLines = Get-Content $propsPath

function Set-PropLine {
    param(
        [string[]]$Lines,
        [string]$Key,
        [string]$Value
    )
    for ($i = 0; $i -lt $Lines.Count; $i++) {
        if ($Lines[$i] -like "$Key=*") {
            $Lines[$i] = "$Key=$Value"
            return $Lines
        }
    }
    return $Lines + "$Key=$Value"
}

if (!(Test-Path $distDir)) {
    New-Item -ItemType Directory -Path $distDir | Out-Null
}

foreach ($target in $Targets) {
    $versionPropsPath = Join-Path $root "versions\neoforge-$target.properties"
    if (!(Test-Path $versionPropsPath)) {
        throw "Missing version properties: $versionPropsPath"
    }

    Write-Host "Building NeoForge $target..."
    $lines = @($baseLines)
    foreach ($line in Get-Content $versionPropsPath) {
        if ([string]::IsNullOrWhiteSpace($line)) { continue }
        $parts = $line.Split("=", 2)
        $lines = Set-PropLine -Lines $lines -Key $parts[0] -Value $parts[1]
    }
    Set-Content -Path $propsPath -Value $lines

    & "$root\gradlew.bat" clean build --no-daemon

    $jar = Get-ChildItem (Join-Path $root "build\libs") -Filter "cleaveore-*.jar" | Select-Object -First 1
    if ($null -eq $jar) {
        throw "No jar found for target $target"
    }
    Copy-Item $jar.FullName (Join-Path $distDir $jar.Name) -Force
}

# restore default to 1.21.1
$defaultProps = Join-Path $root "versions\neoforge-1.21.1.properties"
$lines = @($baseLines)
foreach ($line in Get-Content $defaultProps) {
    if ([string]::IsNullOrWhiteSpace($line)) { continue }
    $parts = $line.Split("=", 2)
    $lines = Set-PropLine -Lines $lines -Key $parts[0] -Value $parts[1]
}
Set-Content -Path $propsPath -Value $lines

Write-Host "Done. Jars copied to dist/."
