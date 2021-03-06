package malang.moe.repay.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import malang.moe.repay.R;

/**
 * Created by kotohana5706 on 2015. 11. 21.
 * Copyright by Sunrin Internet High School EDCAN
 * All rights reversed.
 */
public class SettingsActivity extends AppCompatActivity {

    public class SettingsData {
        public int icon;
        public String title;
        public String description;

        public SettingsData(int icon, String title, String description) {
            this.icon = icon;
            this.title = title;
            this.description = description;
        }
    }

    String number, id, username;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ListView listview;
    View header, getParentNumberView;
    ArrayList<SettingsData> array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setDefault();
        setActionbar(getSupportActionBar());
    }

    private void setDefault() {
        sharedPref = getSharedPreferences("Repay", 0);
        number = sharedPref.getString("parent_number", "");
        editor = sharedPref.edit();
        id = sharedPref.getString("id", "");
        username = sharedPref.getString("name", "");
        listview = (ListView) findViewById(R.id.settings_listview);
        header = getLayoutInflater().inflate(R.layout.listview_settings_header, null);
        getParentNumberView = getLayoutInflater().inflate(R.layout.view_settings_getparentnumber, null);
        TextView headerText = (TextView) header.findViewById(R.id.settings_listview_header_title);
        headerText.setText("기본 설정");
        array = new ArrayList<>();
        array.add(new SettingsData(1, username + "(" + id + ")으로 로그인 중", "로그아웃하려면 누르세요!"));
        array.add(new SettingsData(1, "부모 전화번호 설정", ""));
        SettingsAdapter adapter = new SettingsAdapter(SettingsActivity.this, array);
        listview.addHeaderView(header);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        new MaterialDialog.Builder(SettingsActivity.this)
                                .title("로그아웃하시겠습니까?")
                                .positiveText("확인")
                                .negativeText("취소")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        editor.clear();
                                        editor.commit();
                                        startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                                        MainActivity.activity.finish();
                                        finish();
                                    }
                                }).show();
                        break;
                    case 2:
                        number = sharedPref.getString("parent_number", "");
                        final EditText ed = (EditText) getParentNumberView.findViewById(R.id.parent_number);
                        if (!number.trim().equals("")) ed.setText(number);
                        new MaterialDialog.Builder(SettingsActivity.this)
                                .title("부모 전화번호 설정")
                                .customView(getParentNumberView, true)
                                .positiveText("설정")
                                .negativeText("취소")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        String asdf = ed.getText().toString().trim();
                                        if (asdf.length() < 9){
                                            Toast.makeText(SettingsActivity.this, "제대로 입력해주세요!", Toast.LENGTH_SHORT).show();
                                            ed.setText("");
                                        }
                                        else {
                                            editor.putString("parent_number", asdf);
                                            editor.commit();
                                            Toast.makeText(SettingsActivity.this, asdf + "번으로 설정되었습니다!", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(SettingsActivity.this, "설정되었습니다!", Toast.LENGTH_SHORT).show();
                                            EditText ed = (EditText) getParentNumberView.findViewById(R.id.parent_number);
                                            editor.putString("parent_number", asdf);
                                            editor.commit();
                                            Toast.makeText(SettingsActivity.this, asdf + "번으로 설정되었습니다!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .show();
                        break;
                }
            }
        });
    }

    private void setActionbar(ActionBar ab) {
        ab.setTitle("설정");
        ab.setElevation(0);
        ab.setDefaultDisplayHomeAsUpEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }


    public class SettingsAdapter extends ArrayAdapter<SettingsData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public SettingsAdapter(Context context, ArrayList<SettingsData> object) {
            // 상위 클래스의 초기화 과정
            // context, 0, 자료구조
            super(context, 0, object);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // 보여지는 스타일을 자신이 만든 xml로 보이기 위한 구문
        @Override
        public View getView(int position, View v, ViewGroup parent) {
            View view = null;
            // 현재 리스트의 하나의 항목에 보일 컨트롤 얻기
            if (v == null) {
                // XML 레이아웃을 직접 읽어서 리스트뷰에 넣음
                view = mInflater.inflate(R.layout.listview_settings_content, null);
            } else {
                view = v;
            }
            // 자료를 받는다.
            final SettingsData data = this.getItem(position);
            if (data != null) {
                //화면 출력
                TextView title = (TextView) view.findViewById(R.id.settings_listview_title);
                TextView description = (TextView) view.findViewById(R.id.settings_listview_content);
                title.setText(data.title);
                if (data.description.trim().equals("")) description.setVisibility(View.GONE);
                else description.setText(data.description);
            }
            return view;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
