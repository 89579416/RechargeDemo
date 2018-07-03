package cn.edu.nuc.rechargedemo.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.nuc.rechargedemo.R;
import cn.edu.nuc.rechargedemo.utils.SharedPreferencesUtils;

/**
 * 显示余额
 * Created by Administrator on 2018/6/25.
 */
public class DemandActivity extends Activity{
    TextView demandTv;
    private SharedPreferences loginPreference = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demand);
        demandTv = (TextView) findViewById(R.id.demand_tv);
        loginPreference = getSharedPreferences("login", Context.MODE_PRIVATE);
        //获取登陆名和密码
        String userName = (String) SharedPreferencesUtils.getValue(DemandActivity.this,"name","");
        String password =  (String) SharedPreferencesUtils.getValue(DemandActivity.this,"psd","");
        Log.i("AAAA","userName="+userName);
        //如果登陆名不为空
        if(!userName.isEmpty()){
            //查询余额
            demandForGet(userName,password);
        }
    }

    /**
     * 查询余额 方法：发送get网络请求
     */
    private void demandForGet(final String studentnumber,final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象  RechargeServlet
                final String path = "http://192.168.1.75:8080/MyChongZhiXITong/RechargeServlet?studentnumber=" + studentnumber+ "&password=" + password+ "&pay=0" + "&type=demand";
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.connect();
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        StringBuffer sbf = new StringBuffer();
                        InputStream is = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        String strRead = null;
                        while ((strRead = reader.readLine()) != null) {
                            sbf.append(strRead);
                            sbf.append("\r\n");
                        }
                        reader.close();
                        Message msg = Message.obtain();
                        msg.what = 1;
                        msg.obj = sbf.toString();
                        handler.sendMessage(msg);
                    } else {
                        Message msg = Message.obtain();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }
        }).start();//这个start()方法不要忘记了

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.i("AAAA","查询余额得到的信息："+msg.obj.toString());//{"status":0,"balances":200,"studentnumber":1001}
                    try {
                        //将返回的信息转成json对象后再解析出来
                        JSONObject json = new JSONObject(msg.obj.toString());
                        //解析出返回的当前余额
                        String balances = json.getString("balances");
                        if(balances.isEmpty()){
                            showMessage("查询失败");
                        }else {
                            demandTv.setText(""+balances);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Intent intent = new Intent(RechargeActivity.this, FunctionActivity.class);
//                    startActivity(intent);
                    break;
                case 2:
                    showMessage("请求失败");
                    break;
                case 3:
                    showMessage("请求异常");
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 弹出toast提示
     * @param str
     */
    private void showMessage(String str){
        Toast.makeText(DemandActivity.this,str,Toast.LENGTH_SHORT).show();
    }
}
