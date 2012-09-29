package mx.ferreyra.dogapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class ShowDogPhoto extends Activity {

    private final String PHOTO_ID = "photoId";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_dog_photo);

        // Check parameters for activity
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            // No parameters found
            // TODO Implement a treatment for this stage
        } else {
            // Parameters found
            Integer photoId = (Integer)extras.get(PHOTO_ID);

            if(photoId == null || photoId < 0) {
                // Not valid photo id
                // TODO Implement a treatment for this stage
            }

            // Invoke webservices using asynctask
            ShowDogPhotoTask task = new ShowDogPhotoTask(this);
            task.execute(new Integer[] {photoId});
        }
    }

    public void pojoToView() {

    }

    private class ShowDogPhotoTask extends AsyncTask<Integer, Integer, String> {

        private final Context context;
        private ProgressDialog dialog;

        public ShowDogPhotoTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.please_wait_signing_up));
            dialog.show();
        }

        @Override
        protected String doInBackground(Integer... params) {
            WsDogUtils ws = new WsDogUtils();
            String result = null;
            // TODO query webservice to find photo
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Stop and hide dialog
            dialog.dismiss();

            // Check result
            if(result == null) {
                // Something wrong happened
                // TODO finish this flow
            } else {
                // Load data into view
                pojoToView();
            }
        }
    }
}
