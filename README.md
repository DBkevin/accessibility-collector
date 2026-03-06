# Accessibility Collector

Android 无障碍服务数据采集应用

## 📱 功能

- 基于 Android AccessibilityService
- 自动采集医美机构数据
- 支持数据导出 CSV

## 🔧 技术栈

- **Language:** Kotlin 1.9.20
- **Build:** Gradle 8.2
- **DI:** Hilt 2.48.1
- **Database:** Room

## 📦 编译

### 本地编译

```bash
./gradlew assembleDebug
```

APK 输出位置：`app/build/outputs/apk/debug/app-debug.apk`

### 云端编译（推荐）

1. 在 GitHub 创建仓库
2. 推送代码
3. GitHub Actions 自动编译
4. 在 Actions 页面下载 APK

## 🚀 GitHub Actions 使用

推送代码后自动编译，或手动触发：

1. 进入仓库 → Actions 标签
2. 选择 "Build Android APK" workflow
3. 点击 "Run workflow"
4. 编译完成后在 Artifacts 下载 APK

## 📄 License

MIT
