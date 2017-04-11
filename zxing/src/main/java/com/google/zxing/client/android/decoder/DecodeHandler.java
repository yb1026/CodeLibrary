/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.decoder;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.client.android.QRMessageIds;

final class DecodeHandler extends Handler {

    private Decoder decoder;

    private boolean running = true;

    DecodeHandler(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public void handleMessage(Message message) {
        if (!running) {
            return;
        }
        switch (message.what) {
            case QRMessageIds.decode:
                decoder.decodeQRCode((byte[]) message.obj, message.arg1, message.arg2);
                break;
            case QRMessageIds.quit:
                running = false;
                Looper.myLooper().quit();
                break;
        }
    }

}
