package cn.edu.nuc.rechargedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.edu.nuc.rechargedemo.login.LoginActivity;
import cn.edu.nuc.rechargedemo.register.RegisterActivity;

public class MainActivity extends AppCompatActivity {
    private Button login =null;
    private Button register=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login=(Button)findViewById(R.id.login);
        register=(Button)findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this, LoginActivity.class);//显式Intent在MainActivity活动基础上打开LoginActivity
                startActivity(intent);//通过startActivity方法来执行Intent

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });
    }
}
