package cn.edu.nuc.rechargedemo.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.edu.nuc.rechargedemo.R;
import cn.edu.nuc.rechargedemo.login.LoginActivity;


/**
 * 注册成功显示信息
 * Created by Administrator on 2018/6/25.
 */
public class ResultActivity extends Activity {
    private TextView resultnumber = null;
    private TextView resultpsd = null;
    private TextView resultschool = null;
    private Button login_btn;

    private String student_number;//学号
    private String password;//密码
    private String school;//学校

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.succeed_regist);

        resultnumber=(TextView)findViewById(R.id.resultnumber);
        resultpsd=(TextView)findViewById(R.id.resultpsd);
        resultschool=(TextView)findViewById(R.id.resultschool);
        login_btn = (Button) findViewById(R.id.go_login_btn);

        //从上一个页面取值
        student_number = getIntent().getStringExtra("student_number");
        password = getIntent().getStringExtra("password");
        school = getIntent().getStringExtra("school");

        resultnumber.setText("你的学号为："+student_number);
        resultpsd.setText("你的密码为："+password);
        resultschool.setText("你的学校为："+school);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
