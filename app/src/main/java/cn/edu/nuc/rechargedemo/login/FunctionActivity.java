package cn.edu.nuc.rechargedemo.login;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.edu.nuc.rechargedemo.MainActivity;
import cn.edu.nuc.rechargedemo.R;
import cn.edu.nuc.rechargedemo.register.RegisterActivity;


/**
 * Created by Administrator on 2018/6/25.
 */

public class FunctionActivity extends Activity {

    private Button fun_recharge =null;
    private Button fun_demand=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function);

        fun_recharge=(Button)findViewById(R.id.fun_recharge);
        fun_demand=(Button)findViewById(R.id.fun_demand);

        fun_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(FunctionActivity.this, RechargeActivity.class);//显式Intent在MainActivity活动基础上打开LoginActivity
                startActivity(intent);//通过startActivity方法来执行Intent

            }
        });

        fun_demand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FunctionActivity.this, DemandActivity.class);
                startActivity(intent);

            }
        });
    }
}
