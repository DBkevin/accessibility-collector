# 🚀 快速开始指南

**最后更新：** 2026-03-05  
**版本：** v0.1.0

---

## 📋 环境要求

### 开发环境

- **Android Studio:** Hedgehog (2023.1.1) 或更高版本
- **JDK:** 17+
- **Gradle:** 8.2+
- **最低 SDK:** Android 8.0 (API 26)
- **目标 SDK:** Android 14 (API 34)

### 测试设备

- **手机系统:** Android 8.0+
- **美团版本:** v12+ (建议最新版)
- **存储空间:** 至少 100MB 可用空间

---

## 🔧 编译步骤

### 1. 克隆项目

```bash
git clone <repo_url>
cd accessibility-collector
```

### 2. 用 Android Studio 打开

1. 启动 Android Studio
2. 点击 `Open` 或 `File → Open`
3. 选择项目目录 `accessibility-collector`
4. 等待 Gradle 同步完成

### 3. 同步依赖

首次打开会自动同步 Gradle 依赖，约需 2-5 分钟。

**如果遇到同步失败：**
```bash
# 清理 Gradle 缓存
./gradlew clean

# 重新同步
./gradlew build
```

### 4. 编译 APK

**Debug 版本：**
```bash
./gradlew assembleDebug
```

**Release 版本：**
```bash
./gradlew assembleRelease
```

编译产物位置：
```
app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/apk/release/app-release-unsigned.apk
```

### 5. 安装到手机

**方法 A：Android Studio 安装**
1. 连接手机（开启 USB 调试）
2. 点击 `Run` 按钮 (绿色三角形)
3. 选择设备

**方法 B：ADB 命令安装**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**方法 C：手动安装**
1. 复制 APK 到手机
2. 在手机上打开安装

---

## 📱 使用指南

### 首次使用

#### 1. 打开 App

启动 `美团医美采集器`

#### 2. 开启无障碍权限

1. 点击 `去开启` 按钮
2. 跳转到系统设置 → 无障碍
3. 找到 `下载的应用` → `美团医美采集服务`
4. 开启开关
5. 确认风险提示
6. 返回 App

#### 3. 验证服务状态

返回 App 后应显示：
```
✅ 无障碍服务已开启
```

### 开始采集

#### 方法 A：手动模式（推荐）

1. **打开美团 App**
   - 手动启动美团
   - 搜索目标医院（如"郑州美莱"）
   - 点击进入医院页面

2. **启动采集**
   - 切换回采集器 App
   - 点击 `开始采集` 按钮
   - 服务自动识别并开始采集

3. **等待完成**
   - 脚本自动滚动页面
   - 采集所有项目数据
   - 显示采集进度

4. **查看结果**
   - 采集完成后显示统计
   - 点击 `查看已采集数据` 浏览
   - 支持导出 CSV

#### 方法 B：自动搜索模式

1. **配置医院列表**
   - 编辑 `CollectorConfig.kt`
   - 修改 `DEFAULT_HOSPITALS` 列表

2. **启动自动采集**
   - 点击 `开始采集`
   - 脚本自动搜索每家医院
   - 依次采集并保存

---

## 📊 数据管理

### 查看数据

1. 打开 App
2. 点击 `查看已采集数据`
3. 按医院浏览项目列表

### 导出 CSV

1. 在数据页面点击 `导出`
2. 选择导出范围（单家医院 / 全部）
3. 等待导出完成
4. 文件位置：
   ```
   /Android/data/com.collector/files/export/
   ```

### 数据位置

**数据库：**
```
/data/data/com.collector/databases/meituan_collector_db
```

**导出文件：**
```
/Android/data/com.collector/files/export/
├── 郑州美莱_20260305_143000.csv
├── 郑州天后_20260305_144500.csv
└── all_hospitals_20260305_150000.csv
```

---

## 🐛 常见问题

### Q1: 无障碍服务无法开启？

**解决：**
1. 检查系统设置 → 无障碍 → 下载的应用
2. 找到 `美团医美采集服务`
3. 如果找不到，重启 App 再试
4. 还是不行，重启手机

### Q2: 采集不到数据？

**检查：**
- ✅ 是否在美团 App 中
- ✅ 是否在医院页面（不是首页）
- ✅ 页面内容是否已加载完成
- ✅ 网络是否正常

**解决：**
1. 手动打开美团
2. 进入医院页面
3. 等待 2 秒让内容加载
4. 返回采集器点击开始

### Q3: 导出文件找不到？

**查看方法：**
```bash
adb shell
cd /sdcard/Android/data/com.collector/files/export/
ls -la
```

或用文件管理器访问上述路径。

### Q4: 采集速度慢？

**调整配置：**
编辑 `CollectorConfig.kt`：
```kotlin
const val SCROLL_DELAY_MS = 1000L  // 减少等待时间
const val RANDOM_DELAY_ENABLED = false  // 关闭随机延迟
```

⚠️ 注意：过快可能被美团检测

### Q5: App 闪退？

**排查：**
```bash
# 查看日志
adb logcat | grep Collector

# 或查看完整日志
adb logcat -d > crash_log.txt
```

常见原因：
- 权限未开启
- 美团版本不兼容
- 手机内存不足

---

## 🔐 权限说明

| 权限 | 用途 | 必须 |
|------|------|------|
| 无障碍服务 | 读取界面元素、执行操作 | ✅ |
| 存储 | 保存采集数据 | ✅ |
| 网络 | API 同步（可选） | ⏳ |
| 前台服务 | 后台保活 | ✅ |
| 忽略电池优化 | 防止被杀后台 | 推荐 |

---

## 📝 开发笔记

### 项目结构

```
app/src/main/java/com/collector/
├── MainActivity.kt                  # 主界面
├── MedicalBeautyCollectorService.kt # 无障碍服务
├── CollectorApplication.kt          # Application
├── data/                            # 数据层
│   ├── local/                       # 本地数据库
│   └── remote/                      # 远程 API
├── domain/                          # 业务逻辑
├── collector/                       # 采集核心
│   ├── ElementParser.kt             # 元素解析
│   ├── PageNavigator.kt             # 页面导航
│   └── DataExtractor.kt             # 数据提取
└── util/                            # 工具类
    ├── CollectorConfig.kt           # 配置
    ├── Logger.kt                    # 日志
    ├── PermissionHelper.kt          # 权限
    └── CsvExporter.kt               # CSV 导出
```

### 关键技术点

1. **无障碍服务配置**
   - `AndroidManifest.xml` 注册服务
   - `accessibility_service_config.xml` 配置参数

2. **元素识别**
   - 遍历 `AccessibilityNodeInfo` 树
   - 通过文本特征识别价格、标题

3. **手势模拟**
   - `dispatchGesture()` 执行滑动
   - 需要 Android 7.0+

4. **数据存储**
   - Room 数据库
   - CSV 导出

---

## 📞 技术支持

遇到问题：
1. 查看日志 `adb logcat | grep Collector`
2. 检查无障碍权限是否开启
3. 确认美团版本兼容性
4. 联系开发者

---

**文档结束**
