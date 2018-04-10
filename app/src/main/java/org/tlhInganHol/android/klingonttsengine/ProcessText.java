/*
 * Copyright (C) 2018 De'vID jonpIn (David Yonge-Mallo)
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
package org.tlhInganHol.android.klingonttsengine;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import java.util.Locale;

public class ProcessText extends Activity {
    private static final String TAG = "ProcessText";

    private String mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CharSequence text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
        mText = text.toString();
        new TTSTask().execute();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    // The async TTS task.
    private class TTSTask extends AsyncTask<Void, Void, Void> implements TextToSpeech.OnInitListener {
      private static final String TAG = "TTSTask";

      private TextToSpeech mTts;

      @Override
      protected Void doInBackground(Void... params) {
        mTts = new TextToSpeech(getApplicationContext(), this, "org.tlhInganHol.android.klingonttsengine");
        return null;
      }

      // TTS:
      public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
          mTts.setLanguage(new Locale("tlh", "", ""));
          Log.d(TAG, "setLanguage called.");

          mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onDone(String id) {
              Log.d(TAG, "onDone: " + id);
              if (mTts != null) {
                mTts.stop();
                mTts.shutdown();
              }
            }
            @Override
            public void onStart(String id) {
              Log.d(TAG, "onStart: " + id);
            }
            @Override
            public void onError(String id) {
              Log.d(TAG, "onError: " + id);
            }
          });

          String id = this.hashCode() + "";
          Bundle bundle = new Bundle();
          bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
          mTts.speak(mText, TextToSpeech.QUEUE_FLUSH, bundle, id);

          Log.d(TAG, "speak called with: \"" + mText + "\"");
        }
      }

    }
}

