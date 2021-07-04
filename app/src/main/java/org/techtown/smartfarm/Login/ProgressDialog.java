package org.techtown.smartfarm.Login;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import org.techtown.smartfarm.R;

public class ProgressDialog extends Dialog {
    public ProgressDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);
    }
}
