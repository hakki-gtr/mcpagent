Param([string]$Bump="patch")
$ROOT = (Resolve-Path "$PSScriptRoot\..").Path
$POM = "$ROOT\product\pom.xml"
[xml]$xml = Get-Content $POM
$curr = $xml.project.version
$parts = $curr -split '[-\.]'
$maj=[int]$parts[0]; $min=[int]$parts[1]; $pat=[int]$parts[2]
switch ($Bump) { "major" {$maj++;$min=0;$pat=0}; "minor" {$min++;$pat=0}; default {$pat++} }
$tag = "v{0}.{1}.{2}" -f $maj,$min,$pat
$new = "{0}.{1}.{2}-SNAPSHOT" -f $maj,$min,$pat
mvn -q -f "$ROOT\product\pom.xml" versions:set -DnewVersion="$new" -DgenerateBackupPoms=false | Out-Null
git add $POM
git commit -m "chore(release): bump version to $new" | Out-Null
git tag -a $tag -m "Release $tag"
& "$ROOT\scripts\docker\build-base.ps1" $tag
& "$ROOT\scripts\docker\build-product.ps1" $tag
& "$ROOT\scripts\docker\publish.ps1" $tag
