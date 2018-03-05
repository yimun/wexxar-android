package me.yimu.wexxar.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import me.yimu.wexxar.Constants;
import me.yimu.wexxar.route.RouteManager;

/**
 * Created by linwei on 2018/3/4.
 */

public class FacadeActivity extends AppCompatActivity {

    static final String TAG = FacadeActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatch(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        dispatch(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void dispatch(Intent intent) {
        if (intent == null) {
            return;
        }
        Uri uri = intent.getData();
        if (null == uri) {
            finish();
            return;
        }
        String uriStr = uri.toString().trim();
        if (TextUtils.equals(Uri.parse(uriStr)
                .getScheme(), Constants.SCHEME)) {
            if (RouteManager.getInstance().handleByNative(uriStr)) {
                Log.i(TAG, "handled : " + uriStr);
                WexxarActivity.startActivity(this, uriStr);
                finish();
            }
        }
        finish();

    }

}
