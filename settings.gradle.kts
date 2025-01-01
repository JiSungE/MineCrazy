// 프로젝트에서 사용하는 플러그인을 다운로드할 저장소를 정의
// 플러그인 관리 설정은 settings.gradle.kts만 가능
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven {
            url = uri("https://maven.neoforged.net/releases")
        }
    }
}

plugins {
    // JDK 버전을 자동으로 관리하는 플러그인
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

// Gradle 프로젝트 이름 설정 (아마 없어도 됌)
rootProject.name = "MineCrazy"
