package com.example.storageredirect;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Switch;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    
    private Switch enableSwitch;
    private TextView statusText;
    private Button clearCacheButton;
    private SharedPreferences prefs;
    
    private static final String PREFS_NAME = "storage_redirect_prefs";
    private static final String KEY_ENABLED = "redirect_enabled";
    private static final String VIRTUAL_DIR = "/data/data/com.example.storageredirect/virtual_storage/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        loadSettings();
        updateStatus();
    }
    
    private void initViews() {
        enableSwitch = findViewById(R.id.enableSwitch);
        statusText = findViewById(R.id.statusText);
        clearCacheButton = findViewById(R.id.clearCacheButton);
        
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        enableSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveEnabled(isChecked);
            updateStatus();
            Toast.makeText(this, isChecked ? "重定向已启用" : "重定向已禁用", Toast.LENGTH_SHORT).show();
        });
        
        clearCacheButton.setOnClickListener(v -> clearVirtualCache());
    }
    
    private void loadSettings() {
        boolean enabled = prefs.getBoolean(KEY_ENABLED, true);
        enableSwitch.setChecked(enabled);
    }
    
    private void saveEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply();
    }
    
    private void updateStatus() {
        boolean enabled = enableSwitch.isChecked();
        File virtualDir = new File(VIRTUAL_DIR);
        
        StringBuilder status = new StringBuilder();
        status.append("模块状态: ").append(enabled ? "已启用" : "已禁用").append("\n");
        status.append("虚拟目录: ").append(VIRTUAL_DIR).append("\n");
        status.append("目录存在: ").append(virtualDir.exists() ? "是" : "否").append("\n");
        
        if (virtualDir.exists()) {
            File[] files = virtualDir.listFiles();
            status.append("重定向文件数: ").append(files != null ? files.length : 0).append("\n");
            
            if (files != null && files.length > 0) {
                status.append("文件列表:\n");
                for (File file : files) {
                    status.append("- ").append(file.getName()).append(" (").append(file.length()).append(" bytes)\n");
                }
            }
        }
        
        statusText.setText(status.toString());
    }
    
    private void clearVirtualCache() {
        File virtualDir = new File(VIRTUAL_DIR);
        if (virtualDir.exists()) {
            File[] files = virtualDir.listFiles();
            if (files != null) {
                int deletedCount = 0;
                for (File file : files) {
                    if (file.delete()) {
                        deletedCount++;
                    }
                }
                Toast.makeText(this, "已删除 " + deletedCount + " 个文件", Toast.LENGTH_SHORT).show();
                updateStatus();
            }
        } else {
            Toast.makeText(this, "虚拟目录不存在", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }
}
