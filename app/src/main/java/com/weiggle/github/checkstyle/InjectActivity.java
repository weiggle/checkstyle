package com.weiggle.github.checkstyle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.weiggle.github.apt_api.BindView;
import com.weiggle.github.apt_api.ButterKnife;

public class InjectActivity extends AppCompatActivity {

    @BindView(R.id.text)
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inject);
        ButterKnife.inject(this);
        text.setText("wkvejbviwe ");
    }
}
