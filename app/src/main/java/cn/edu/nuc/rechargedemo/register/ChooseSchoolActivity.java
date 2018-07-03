package cn.edu.nuc.rechargedemo.register;

import android.app.ActionBar;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;


/**
 * 选择学校
 * Created by Administrator on 2018/6/25.
 */

public class ChooseSchoolActivity extends ExpandableListActivity {

    private String []provinces=new String[]{"山西省","陕西省"};
    private String [][]colleges=new String[][]{{"山西大学","太原理工大学","中北大学"},{"西安交通大学","西北工业大学","西安电子科技大学"}};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExpandableListAdapter adapter= new BaseExpandableListAdapter() {
            @Override
            //获取到省的数目；
            public int getGroupCount() {
                return provinces.length;
            }

            @Override
            //获取到某个省的城市数量；
            public int getChildrenCount(int i) {
                return colleges[i].length;
            }

            @Override
            //获取到指定的声
            public Object getGroup(int i) {
                return provinces[i];
            }

            @Override
            //获取到制定的城市
            public Object getChild(int i, int i1) {
                return colleges[i][i1];
            }

            @Override
            //获取到省的索引值
            public long getGroupId(int i) {
                return i;
            }

            @Override
            //获取到城市的索引值
            public long getChildId(int i, int i1) {
                return i1;
            }

            @Override
            //检测控件合法性的方法
            public boolean hasStableIds() {
                return true;
            }
            private  TextView getTextView(){
                AbsListView.LayoutParams layoutParams=new AbsListView.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,80);
                TextView textView = new TextView(ChooseSchoolActivity.this);
                textView.setLayoutParams(layoutParams);
                textView.setTextSize(24);
                textView.setTextColor(Color.BLUE);
                textView.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
                textView.setPadding(10,10,10,10);
                return textView;
            }

            @Override
            //该方法决定了组的显示形式
            public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
                TextView textView = getTextView();
                textView.setText(getGroup(i).toString());
                return textView;
            }

            @Override
            //该方法决定了Child的显示形式
            public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
                TextView textView = getTextView();
                textView.setText(getChild(i,i1).toString());

                return textView;
            }

            @Override
            //设置是否允许Child被选中;
            public boolean isChildSelectable(int i, int i1) {
                return true;
            }
        };
        setListAdapter(adapter);//设置该Activity的显示列表的适配器
        //为该可扩展列表绑定事件监听器
        getExpandableListView().setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Intent intent=getIntent();//获取到MainActivity传过来的那个intent对象
                Bundle data=new Bundle();//创建一个用于存放数据的data数据包对象
                data.putString("college",colleges[i][i1]);
                intent.putExtras(data);//将数据包data封装到intent中
                ChooseSchoolActivity.this.setResult(0,intent);
                ChooseSchoolActivity.this.finish();
                return false;
            }
        });
    }


}
