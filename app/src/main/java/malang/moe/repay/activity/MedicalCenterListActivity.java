package malang.moe.repay.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import malang.moe.repay.R;
import malang.moe.repay.adapter.MedialCenterAdapter;
import malang.moe.repay.data.MedicalCenterData;
import malang.moe.repay.data.MedicalCenter_Response;
import malang.moe.repay.data.MedicalRow;
import malang.moe.repay.utils.NetworkService;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MedicalCenterListActivity extends AppCompatActivity {

    ListView listview;
    ArrayList<MedicalCenterData> arrayList;
    MedialCenterAdapter adapter;
    NetworkService service;
    Retrofit retrofit;
    List<MedicalRow> medicalRows;
    Call<MedicalCenter_Response> medicalCenter_Response;
    MaterialDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_center_list);
        setDefault();
        setRestAdapter();
        setActionbar(getSupportActionBar());
        parseData();
    }

    private void setDefault() {
        listview = (ListView) findViewById(R.id.medicalCenter_Listview);
        loading = new MaterialDialog.Builder(MedicalCenterListActivity.this)
                .title("데이터를 로드합니다")
                .content("잠시만 기다려주세요")
                .progress(true, 0).cancelable(false)
                .show();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView title = (TextView) view.findViewById(R.id.medical_listview_title);
                TextView address = (TextView) view.findViewById(R.id.medical_listview_address);
                TextView webpage = (TextView) view.findViewById(R.id.medical_listview_homepage);
                final TextView num = (TextView) view.findViewById(R.id.medical_listview_tel_num);
                View inflateView = getLayoutInflater().inflate(R.layout.dialog_medicalcenter, null);
                TextView dialogAddress = (TextView) inflateView.findViewById(R.id.dialog_medicalcenter_address);
                final TextView webAddress = (TextView) inflateView.findViewById(R.id.dialog_medicalcenter_web_address);
                TextView dialogNumber = (TextView) inflateView.findViewById(R.id.dialog_medicalcenter_tel_num);
                dialogAddress.setText(address.getText().toString().trim());
                webAddress.setText(webpage.getText().toString().trim());
                dialogNumber.setText(num.getText().toString().trim());
                Button call = (Button) inflateView.findViewById(R.id.dialog_medicalcenter_call_button);
                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActivityCompat.checkSelfPermission(MedicalCenterListActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num.getText().toString().trim())));
                    }
                });
                Button web = (Button) inflateView.findViewById(R.id.dialog_medicalcenter_web_intent);
                web.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webAddress.getText().toString().trim())));
                    }
                });
                new MaterialDialog.Builder(MedicalCenterListActivity.this)
                        .title(title.getText().toString().trim())
                        .customView(inflateView, true)
                        .show();
            }
        });

    }

    private void setRestAdapter() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://data.gwd.go.kr/apiservice/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(NetworkService.class);
        medicalCenter_Response = service.getMedicalList(1, 1000);
    }

    private void parseData() {
        medicalCenter_Response.enqueue(new Callback<MedicalCenter_Response>() {
            @Override
            public void onResponse(Response<MedicalCenter_Response> response, Retrofit retrofit) {
                if (response.code() == 200) {
                    medicalRows = response.body().medicalCenter.row;
                    setListView(medicalRows);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(MedicalCenterListActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActionbar(ActionBar actionbar) {
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("의료시설");
    }

    public void setListView(List<MedicalRow> list) {
        arrayList = new ArrayList<>();
        for (MedicalRow medicalRow : list) {
            arrayList.add(new MedicalCenterData(medicalRow.MED_NM, medicalRow.MED_ADDRESS, medicalRow.GOV_TYPE, medicalRow.TEL_NUM, medicalRow.HOMEPAGE));
        }
        adapter = new MedialCenterAdapter(MedicalCenterListActivity.this, arrayList);
        listview.setAdapter(adapter);
        loading.dismiss();
    }
}
