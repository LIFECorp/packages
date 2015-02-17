package com.mediatek.dialer.ext;

import android.content.Intent;

public interface IDialpadFragment {

    public void doCallOptionHandle(Intent intent);
    public void handleDialButtonClickWithEmptyDigits();
}
