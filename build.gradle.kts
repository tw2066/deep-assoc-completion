plugins {
    java
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "com.tang"
version = providers.gradleProperty("pluginVersion").get()

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

java {
    toolchain {
        // PhpStorm 2026.2 runs on JBR 25 and ships class files at bytecode 69,
        // so the compiler must be JDK 25 (PhpStorm's own bundled jbr works)
        languageVersion = JavaLanguageVersion.of(25)
    }
}

sourceSets {
    main {
        java.srcDir("src")
        resources.srcDir("resources")
    }
}

tasks.processResources {
    from("src") {
        include("**/*.png", "**/*.js")
    }
}

dependencies {
    // platform no longer ships commons-lang 2.x on the plugin classpath
    implementation("commons-lang:commons-lang:2.6")

    intellijPlatform {
        // EAP 沙箱调试无许可限制：./gradlew runIde -PeapIdePath=D:\ides\PhpStorm-263.3889.75
        // 打包/验证仍用 phpstorm(platformVersion) 正式版
        if (project.hasProperty("eapIdePath")) {
            local(project.property("eapIdePath").toString())
        } else {
            phpstorm(providers.gradleProperty("platformVersion").get())
        }
        bundledPlugin("com.jetbrains.php")
        bundledPlugin("com.intellij.database")
        bundledPlugin("com.jetbrains.twig")
        bundledModule("com.intellij.modules.json")
        pluginVerifier()
        jetbrainsRuntime()
    }
}

intellijPlatform {
    pluginConfiguration {
        version = providers.gradleProperty("pluginVersion")
        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            // EAP 沙箱是更新的构建号（263），不带 until-build 才能加载
            if (!project.hasProperty("eapIdePath")) {
                untilBuild = providers.gradleProperty("pluginUntilBuild")
            }
        }
    }
}
