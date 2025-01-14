import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // Kotlin 언어를 사용하여 JVM에서 실행되는 프로그램을 개발하기 위해 필요한 플러그인
    kotlin("jvm") version "2.0.21"
    
    // Kotlin 직렬화 플러그인
    // JSON 및 기타 형식으로 데이터를 변환하거나 저장할 때 필요
    kotlin("plugin.serialization") version "2.0.21"

    // 의존성 관리 및 JAR 파일 빌드 기능을 제공 (자바 코드와의 호환성을 위해서 추가)
    `java-library`

    // IntelliJ IDEA 개발 환경에서 프로젝트 설정을 자동으로 관리
    idea

    // Maven 저장소에 라이브러리를 업로드하거나 배포할 수 있도록 도움
    // 로컬 또는 원격 Maven 저장소에 프로젝트를 게시할 때 사용
    `maven-publish`

    // Minecraft 모드 개발을 지원하는 NeoForge Gradle 플러그인을 추가
    // 개발 도구 설정, 데이터 생성 및 빌드 작업을 간편하게 처리할 수 있도록 도움
    id("net.neoforged.gradle.userdev") version "7.0.142"
}

version = project.property("mod_version") as String
group = project.property("mod_group_id") as String

repositories {
    // 시스템에 설치된 로컬 Maven 저장소를 참조 (일반적으로 %HOME/.m2/repository 경로에 존재하는 저장소를 참조함)
    mavenLocal()

    maven {
        // 이 저장소의 별칭을 "Kotlin for Forge"로 지정
        name = "Kotlin for Forge"

        // 지정된 URL에 있는 Maven 저장소를 참조
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
}

base {
    // 빌드 결과물(JAR 파일)의 이름을 설정 -> JAR로 빌드 시 ${mod_id}.jar로 빌드됨
    archivesName.set(project.property("mod_id") as String)
}

// 프로젝트가 컴파일 및 실행될 Java 버전을 설정
java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

runs {
    // configureEash 블록 내의 설정은 **모든 실행 환경(client, server 등)**에 적용
    configureEach {
        // Forge 로깅 시스템에서 REGISTRIES 마커를 활성화
        // 모드의 등록 작업과 관련된 로그를 출력
        systemProperty("forge.logging.markers", "REGISTRIES")

        // 콘솔 로그 레벨을 **debug(디버그)**로 설정하여 세부 정보를 출력
        systemProperty("forge.logging.console.level", "debug")

        // 프로젝트의 main 소스 세트를 모드의 소스 코드로 지정
        modSource(project.sourceSets.main.get())
    }

    // create("client"): 클라이언트 환경을 정의
    create("client") {
        // 모드 ID를 기준으로 테스트 네임스페이스를 활성화
        // 네임스페이스는 게임 내에서 테스트에 사용할 수 있는 구역 또는 범위를 정의
        systemProperty("forge.enabledGameTestNamespaces", project.property("mod_id") as String)

        programArguments.addAll("--username", "JISUNG")
    }

    // create("server"): 서버 환경을 정의
    create("server") {
        // 클라이언트와 동일하게 테스트 네임스페이스를 활성화
        systemProperty("forge.enabledGameTestNamespaces", project.property("mod_id") as String)

        // 서버 실행 시 GUI를 비활성화
        // 콘솔 기반으로만 실행되도록 설정하여 리소스를 절약
        programArgument("--nogui")
    }

    // create("gameTestServer"): 게임 테스트 전용 서버 환경을 정의
    create("gameTestServer") {
        // 테스트 네임스페이스 활성화는 클라이언트 및 일반 서버와 동일
        // 게임 내 테스트 시뮬레이션을 서버 환경에서 실행할 수 있도록 지원
        systemProperty("forge.enabledGameTestNamespaces", project.property("mod_id") as String)
    }

    // create("data"): 데이터 생성 전용 설정을 정의
    create("data") {
        // arguments 설정 ->
        // --mod: 모드 ID 지정
        // --all: 모든 데이터 생성
        // --output: 생성된 데이터의 출력 경로 설정(여기서는 src/generated/resources/)
        // --existing: 기존 데이터 참조 경로 설정(여기서는 src/main/resources/)
        programArguments.addAll(
            "--mod", project.property("mod_id") as String,
            "--all",
            "--output", file("src/generated/resources/").absolutePath,
            "--existing", file("src/main/resources/").absolutePath
        )
    }
}

// 프로젝트의 **메인 소스 세트(main)**를 참조
// 빌드 시 생성되는 리소스 경로를 설정
sourceSets["main"].resources.srcDir("src/generated/resources")

dependencies {
    // Kotlin 으로 NeoForge 기반 모드를 개발할 수 있도록 도와주는 핵심 라이브러리
    implementation("thedarkcolour:kotlinforforge-neoforge:5.6.0")

    // Kotlin 기본 함수 및 클래스(List, Map, String 등)를 제공
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")

    // 런타임에서 객체의 메타정보(클래스 이름, 메서드 리스트) 조회 또는 동적 호출이 필요할 때 사용
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.21")

    // NeoForge의 기본 프레임워크 및 API 기능을 제공
    // 모드 개발을 위한 블록, 아이템, 이벤트, 렌더링 등의 기능을 지원
    implementation("net.neoforged:neoforge:${project.property("neo_version") as String}")
}

// ProcessResources 태스크를 가져와 모든 인스턴스에 설정을 적용
tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = mapOf(
        "minecraft_version" to (project.property("minecraft_version") as String),
        "minecraft_version_range" to (project.property("minecraft_version_range") as String),
        "neo_version" to (project.property("neo_version") as String),
        "neo_version_range" to (project.property("neo_version_range") as String),
        "loader_version_range" to (project.property("loader_version_range") as String),
        "mod_id" to (project.property("mod_id") as String),
        "mod_name" to (project.property("mod_name") as String),
        "mod_license" to (project.property("mod_license") as String),
        "mod_version" to (project.property("mod_version") as String),
        "mod_authors" to (project.property("mod_authors") as String),
        "mod_description" to (project.property("mod_description") as String)
    )

    // replaceProperties 값을 입력 속성으로 정의하여 속성값이 변경되면 태스크가 다시 실행되도록 설정
    inputs.properties(replaceProperties)

    // neoforge.mods.toml 파일내에 ${__}을 실제 데이터로 치환하도록 설정
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties)
    }
}

publishing {
    // 프로젝트의 빌드 결과물(JAR, 메타데이터)을 Maven 형식으로 패키징
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    // 패키징된 결과물을 프로젝트 디렉터리 내 repo 폴더에 저장
    // 로컬 저장소에 업로드된 결과물을 다른 프로젝트에서 의존성으로 추가하여 사용가능
    repositories {
        maven {
            url = uri("file://${project.projectDir}/repo")
        }
    }
}

// 자바 컴파일 설정
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    sourceCompatibility = "21"
    targetCompatibility = "21"
}

// 코틀린 컴파일 설정
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        // 더 엄격한 Null Safe를 사용
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JvmTarget.JVM_21
    }

}

idea {
    module {
        // 의존성 라이브러리 자동 다운로드
        isDownloadSources = true
        // 라이브러리의 API 문서 (Javadoc)을 자동 다운로드
        isDownloadJavadoc = true
    }
}
