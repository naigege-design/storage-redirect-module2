# 存储重定向模块

这是一个LSPosed模块，用于将王者荣耀的反作弊文件重定向到虚拟位置，使这些文件失效。

## 功能特性

- 🎯 **精确重定向**：专门针对 `mrpcs-android-l.gr_925.data` 和 `mrpcs-android-1.gr_925.data` 文件
- 🔒 **安全隔离**：将文件重定向到应用私有目录，不影响系统
- 📱 **用户界面**：提供简洁的配置和状态监控界面
- 🧹 **缓存管理**：支持清除虚拟缓存文件

## 安装要求

1. **Root权限**：设备必须已获得Root权限
2. **LSPosed框架**：需要安装LSPosed或EdXposed框架
3. **Android版本**：支持Android 7.0 (API 24) 及以上版本

## 安装步骤

### 1. 准备环境
```bash
# 确保设备已Root并安装LSPosed
# 可以通过Magisk安装LSPosed模块
```

### 2. 编译模块
```bash
# 使用Android Studio打开项目
# 或使用命令行编译
./gradlew assembleRelease
```

### 3. 安装和激活
1. 安装生成的APK文件
2. 在LSPosed管理器中激活模块
3. 重启设备或重启王者荣耀应用

## 使用说明

### 配置模块
1. 打开"存储重定向"应用
2. 启用重定向功能
3. 查看状态信息确认模块正常工作

### 监控状态
- **模块状态**：显示重定向是否启用
- **虚拟目录**：显示重定向文件的存储位置
- **文件列表**：显示已重定向的文件及其大小

### 清除缓存
点击"清除虚拟缓存"按钮可以删除所有重定向的文件。

## 工作原理

模块通过Hook以下Java API来实现文件重定向：

1. **File构造函数**：拦截文件路径创建
2. **FileOutputStream**：拦截文件写入操作
3. **RandomAccessFile**：拦截随机文件访问

当检测到目标文件路径时，自动将其重定向到：
```
/data/data/com.example.storageredirect/virtual_storage/
```

## 技术细节

### Hook点
- `File(String path)`
- `File(String parent, String child)`
- `FileOutputStream(String name)`
- `FileOutputStream(File file)`
- `RandomAccessFile(String name, String mode)`
- `RandomAccessFile(File file, String mode)`

### 目标文件
- `mrpcs-android-l.gr_925.data`
- `mrpcs-android-1.gr_925.data`

### 重定向逻辑
```java
private String redirectPath(String originalPath) {
    if (originalPath == null) return originalPath;
    
    for (String targetFile : TARGET_FILES) {
        if (originalPath.contains(targetFile)) {
            String redirectedPath = VIRTUAL_DIR + targetFile;
            return redirectedPath;
        }
    }
    return originalPath;
}
```

## 注意事项

⚠️ **重要警告**：
1. 此模块可能影响游戏的反作弊检测
2. 使用前请了解相关风险和法律后果
3. 建议仅用于学习和研究目的

⚠️ **兼容性**：
1. 仅支持王者荣耀官方版本
2. 游戏更新可能影响模块效果
3. 不同设备可能有不同表现

## 故障排除

### 模块不生效
1. 确认LSPosed框架正常工作
2. 检查模块是否正确激活
3. 重启设备或目标应用

### 游戏无法启动
1. 暂时禁用模块
2. 检查日志输出
3. 清除虚拟缓存后重试

### 查看日志
```bash
# 使用adb查看日志
adb logcat | grep "StorageRedirect"
```

## 开发信息

- **开发语言**：Java
- **最低API**：24 (Android 7.0)
- **目标API**：34 (Android 14)
- **框架**：LSPosed/EdXposed

## 许可证

本项目仅供学习和研究使用，请遵守相关法律法规。
