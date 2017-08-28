package com.aigestudio.wheelpicker.demo;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aigestudio.wheelpicker.model.Province;

import java.util.List;

/**
 * CREATE BY ALUN
 * EMAIL: alunfeixue2011@gmail.com
 * DATE : 2017/08/28 11:35
 */

public class LoadDataActivity extends Activity {

    private List<Province> provinces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loaddata);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                provinces = DataManager.loadData(LoadDataActivity.this, new DataManager.Callback() {
                    @Override
                    public void onCall(boolean result) {
                        if (result){
                            showMsg("导入完成");
                        } else {
                            showMsg("导入失败");
                        }
                    }
                });
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (provinces == null) {
                    showMsg("请先导入");
                    return;
                }

                DataManager.writeObjectToFile(provinces);
            }
        });

        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoadDataActivity.this, AreaActivity.class));
            }
        });
    }

    private void showMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
