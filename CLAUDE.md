# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概况

PhpStorm 插件：PHP 关联数组键的深度类型推断与补全（`$user['']` 时分析变量的来源表达式推断键名），另含键跳转定义、phpdoc 键类型声明（`@param $x = ['key' => ...]`）、内置数组函数（array_map 等）参数推断、Ctrl+Alt+Q 查看变量结构。

本仓库是 klesun 原插件（2022 停更，Beer-Ware 许可）的社区 fork，**仅适配 PhpStorm 2026.2**（since-build=262, until-build=262.*）。插件 ID `com.tang.deep-assoc-completion`。纯 Java，无 Kotlin，无自动化测试。

## 构建与运行

```bash
./gradlew compileJava     # 编译（最快的错误检查方式）
./gradlew buildPlugin     # 产出 build/distributions/deep-assoc-completion-<version>.zip
./gradlew verifyPlugin    # Plugin Verifier 兼容性检查
./gradlew runIde          # 沙箱 IDE（默认 2026.2 正式版，需许可证）
./gradlew runIde -PeapIdePath=D:\ides\PhpStorm-263.3889.75   # EAP 沙箱（免许可，推荐调试用）
```

要点：
- **必须用 JDK 25 工具链编译**——2026.2 平台类文件是 bytecode 69。本机 JBR 25（PhpStorm 安装目录 `jbr/`）和 JDK 21 已注册在 `~/.gradle/gradle.properties` 的 `org.gradle.java.installations.paths`。
- `-PeapIdePath` 模式下用 `local()` IDE 依赖且不注入 until-build（否则 263 沙箱拒载 262.* 的插件）。
- 版本号/构建号范围在 `gradle.properties`，由 patchPluginXml 注入 plugin.xml，**不要在 plugin.xml 里手写 version/idea-version**。
- 网络环境：Gradle 发行版走腾讯镜像，Maven 依赖走阿里云镜像（已配置）。
- `instrumentCode` 不可关闭——`DeepSettingsForm.form`（GUI Designer）靠它织入 `$$$setupUI$$$`，关掉设置页就崩（2022 年出过一次事故）。
- 无测试框架；自测靠 Tools 菜单的 `Run deep-assoc Test` action，要求打开的 PHP 项目里存在名为 `UnitTest` / `ExactKeysUnitTest` / `UsageResolverUnitTest` 的类（含 `provide*` 数据供给函数）。

## 架构

类型推断核心链路：`entry/` 的 EP 入口 → `contexts/` 构造解析上下文 → `resolvers/` 按 PSI 表达式类型分发 → 产出 `structures/DeepType`（键集合 + 值类型）→ `completion_providers/` 转成 LookupElement。

- `entry/` — 不含实现，纯 plugin.xml 桥接层：3 个 completion contributor、`typeProvider4`（MixedTypePvdr 把数组访问标为 MIXED 防误报；AssocTypePvdr 后台解析真实类型）、reference contributor、goto-declaration、settings。
- `contexts/` — `SearchCtx`（根：总表达式上限、递归检测、缓存）→ `FuncCtx`（函数级：$this、实参、闭包变量）→ `ExprCtx`（表达式级：父链，支持惰性求值的深度限制）。**Context + Expression = 唯一解析结果**。
- `resolvers/` — 与 PHP PSI 表达式类近似一一对应的解析器（MethCallRes、VarRes、FuncCallRes、ClosRes 等）；`builtins/` 是内置函数硬编码类型；`var_res/` 处理变量来源（赋值、foreach、list 解构）。
- `structures/` — `DeepType`（键→类型的树）、`Key`/`KeyType`、`Mt`（多类型包装）；`psalm/` 是 psalm 类型注解的 AST 解析。
- `org.klesun.lang` — 自研的单子式惰性集合库（`It`/`Opt`/`L`/`MemIt`），全项目的数据流都写成它的链式调用（`.fap` flatMap、`.fop` filter+map、`.map`、`.flt`、`.thn`/`.els`、`.def`、`.unw`）。**改代码前先读 `src/org/klesun/lang/README.md` 和各类注释**，不要按 Java Stream 的习惯猜。

## 平台 API 注意事项（2026.2）

- `ReferencesSearch` 只能在显式进度指示器下跑——`VarNamePvdr.getGlobalsMagicVarUsages` 用 `ProgressManager.hasProgressIndicator()` 守卫，不要绕过。
- `MethCallRes`/`UsageBasedTypeResolver` 直接读 PHP 插件内部 stub 索引（`PhpExpectedReturnValuesIndex` 等），`ArrFuncRefCbtr` 用内部类 `PhpCallbackReferenceBase.PhpClassMemberCallbackReference`——这些是内部 API，升级目标平台时**优先检查**这几处。
- `MysqliRes` 用了标记为待移除的 `DasObject.getDbChildren`（数据库表列推断），下个平台版本可能要改写。
- 设置存储已从 workspace.xml 迁到默认项目存储（`DeepSettings` 的 `@State` 不再指定 storage）。
