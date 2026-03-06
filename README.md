# MeituanCollector - 美团医美数据采集器

**版本：** v0.1.0  
**创建时间：** 2026-03-05  
**开发：** 李二 🦞 + 李三 💻  
**状态：** 开发中

---

## 📱 项目简介

基于 Android AccessibilityService 的医美数据采集工具，用于自动采集美团平台上的医美机构项目数据。

**核心功能：**
- ✅ 自动识别美团页面元素
- ✅ 采集医院名称、项目名称、价格、销量
- ✅ 自动滚动加载更多
- ✅ 本地数据存储和导出
- ⏳ API 任务同步（规划中）

---

## 🏗️ 技术架构

| 模块 | 技术选型 |
|------|---------|
| 开发语言 | Kotlin 1.9+ |
| 最低版本 | Android 8.0 (API 26) |
| 目标版本 | Android 14 (API 34) |
| 无障碍服务 | AccessibilityService |
| 本地数据库 | Room (SQLite) |
| 网络请求 | Retrofit + OkHttp |
| 异步处理 | Kotlin Coroutines |
| 依赖注入 | Hilt |

---

## 📁 项目结构

```
MeituanCollector/
├── app/
│   ├── src/main/
│   │   ├── java/com/collector/
│   │   │   ├── MedicalBeautyCollectorService.kt  # 无障碍服务核心
│   │   │   ├── MainActivity.kt                   # 主界面
│   │   │   ├── CollectorApplication.kt           # Application
│   │   │   ├── data/
│   │   │   │   ├── local/                        # 本地数据
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── HospitalDao.kt
│   │   │   │   │   │   └── ProjectDao.kt
│   │   │   │   │   └── entity/
│   │   │   │   │       ├── HospitalEntity.kt
│   │   │   │   │       └── ProjectEntity.kt
│   │   │   │   └── remote/                       # 远程 API
│   │   │   │       ├── ApiService.kt
│   │   │   │       └── model/
│   │   │   ├── domain/
│   │   │   │   ├── model/                        # 业务模型
│   │   │   │   │   ├── Hospital.kt
│   │   │   │   │   └── Project.kt
│   │   │   │   └── collector/                    # 采集逻辑
│   │   │   │       ├── ElementParser.kt          # 元素解析
│   │   │   │       ├── PageNavigator.kt          # 页面导航
│   │   │   │       └── DataExtractor.kt          # 数据提取
│   │   │   └── util/
│   │   │       ├── PermissionHelper.kt           # 权限帮助
│   │   │       └── Logger.kt                     # 日志工具
│   │   ├── res/
│   │   │   ├── xml/
│   │   │   │   └── accessibility_service_config.xml
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml
│   │   │   └── values/
│   │   │       ├── strings.xml
│   │   │       └── themes.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🚀 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17+
- Android SDK 26+

### 编译步骤

1. **克隆项目**
   ```bash
   git clone <repo_url>
   cd MeituanCollector
   ```

2. **打开项目**
   - 用 Android Studio 打开项目目录

3. **同步依赖**
   - 等待 Gradle 自动同步完成

4. **编译 APK**
   ```bash
   ./gradlew assembleDebug
   ```

5. **安装到手机**
   - 连接手机（开启 USB 调试）
   - 点击 Run 或执行：
   ```bash
   ./gradlew installDebug
   ```

---

## 📱 使用指南

### 首次使用

1. **打开 App**
   - 启动 MeituanCollector

2. **开启无障碍权限**
   - 点击"开启无障碍服务"按钮
   - 跳转到系统设置
   - 找到"下载的应用" → "MeituanCollector"
   - 开启开关

3. **返回 App**
   - 权限开启后返回主界面
   - 显示"服务运行中"

### 开始采集

1. **打开美团 App**
   - 手动打开美团
   - 搜索目标医院（如"郑州美莱"）
   - 进入医院页面

2. **启动采集**
   - 切换回 MeituanCollector
   - 点击"开始采集"按钮
   - 脚本自动执行

3. **查看结果**
   - 采集完成后显示统计信息
   - 可在"数据"页面查看已采集内容
   - 支持导出 CSV

---

## 🔧 配置说明

### 采集参数

在 `CollectorConfig.kt` 中修改：

```kotlin
object CollectorConfig {
    const val MAX_SCROLL_COUNT = 50          // 最多滚动次数
    const val SCROLL_DELAY_MS = 2000L        // 滚动后等待时间
    const val NO_NEW_STOP_COUNT = 3          // 无新增停止次数
    const val RANDOM_DELAY_ENABLED = true    // 启用随机延迟
    const val MIN_DELAY_MS = 1500L           // 最小延迟
    const val MAX_DELAY_MS = 3000L           // 最大延迟
}
```

### 医院列表

在 `HospitalRepository.kt` 中配置：

```kotlin
val defaultHospitals = listOf(
    "郑州美莱",
    "郑州天后",
    "郑州辰星",
    "郑州一科"
)
```

---

## 📊 数据导出

### CSV 格式

```csv
医院名称，项目名称，价格，销量，采集时间
郑州美莱，"玻尿酸填充",1280,523,"2026-03-05 14:30:00"
郑州美莱，"光子嫩肤",599,1204,"2026-03-05 14:30:00"
```

### 导出位置

```
/Android/data/com.collector/files/export/
├── hospitals_20260305_143000.csv
└── all_projects_20260305_143000.csv
```

---

## ⚠️ 注意事项

1. **无障碍权限**
   - 必须手动开启，无法程序自动开启
   - 关闭 App 后权限依然有效

2. **美团版本**
   - 测试版本：美团 v12+
   - 过低版本可能 UI 结构不同

3. **系统版本**
   - 最低 Android 8.0
   - 推荐 Android 10+

4. **反检测**
   - 启用随机延迟降低检测风险
   - 建议不要过于频繁采集

---

## 🐛 常见问题

**Q: 无障碍服务无法开启？**
A: 检查系统设置 → 无障碍 → 下载的应用 → MeituanCollector

**Q: 采集不到数据？**
A: 确保在美团医院页面，且页面内容已加载完成

**Q: 导出文件在哪？**
A: `/Android/data/com.collector/files/export/` 目录

**Q: 如何查看日志？**
A: App 内"日志"页面，或使用 `adb logcat | grep Collector`

---

## 📝 开发计划

| 版本 | 功能 | 状态 |
|------|------|------|
| v0.1.0 | 基础框架 + 无障碍服务 | 开发中 |
| v0.2.0 | 美团元素识别 + 采集 | 规划中 |
| v0.3.0 | 本地存储 + 导出 | 规划中 |
| v0.4.0 | API 同步 + 定时任务 | 规划中 |

---

## 📄 许可证

本项目仅供学习研究使用，不得用于商业目的。

---

**最后更新：** 2026-03-05
