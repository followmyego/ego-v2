package com.egobeta.ego;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.signin.SignInManager;
import com.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobile.user.IdentityProvider;

import com.amazonaws.mobile.user.signin.FacebookSignInProvider;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.egobeta.R;

import java.util.List;

public class SignInActivity extends Activity {
    private final static String LOG_TAG = SignInActivity.class.getSimpleName();
    private SignInManager signInManager;

    /** Permission Request Code (Must be < 256). */
    private static final int GET_ACCOUNTS_PERMISSION_REQUEST_CODE = 93;

    /** The Google OnClick listener, since we must override it to get permissions on Marshmallow and above. */
    private View.OnClickListener googleOnClickListener;

    /**
     * SignInResultsHandler handles the final result from sign in. Making it static is a best
     * practice since it may outlive the SplashActivity's life span.
     */
    private class SignInResultsHandler implements IdentityManager.SignInResultsHandler {
        /**
         * Receives the successful sign-in result and starts the main activity.
         * @param provider the identity provider used for sign-in.
         */
        @Override
        public void onSuccess(final IdentityProvider provider) {
            Log.d(LOG_TAG, String.format("User sign-in with %s succeeded",
                provider.getDisplayName()));

            // The sign-in manager is no longer needed once signed in.
            SignInManager.dispose();

//            Toast.makeText(SignInActivity.this, String.format("Sign-in with %s succeeded.",
//                provider.getDisplayName()), Toast.LENGTH_LONG).show();

            // Load user name and image.
            AWSMobileClient.defaultMobileClient()
                .getIdentityManager().loadUserInfoAndImage(provider, new Runnable() {
                @Override
                public void run() {
//                    Log.d(LOG_TAG, "Launching Main Activity...");
//                    startActivity(new Intent(SignInActivity.this, MainActivity.class)
//                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                    // finish should always be called on the main thread.
//                    finish();
                    syncUserSettings();
                }
            });
        }

        /**
         * Recieves the sign-in result indicating the user canceled and shows a toast.
         * @param provider the identity provider with which the user attempted sign-in.
         */
        @Override
        public void onCancel(final IdentityProvider provider) {
            Log.d(LOG_TAG, String.format("User sign-in with %s canceled.",
                provider.getDisplayName()));

            Toast.makeText(SignInActivity.this, String.format("Sign-in with %s canceled.",
                provider.getDisplayName()), Toast.LENGTH_LONG).show();
        }

        /**
         * Receives the sign-in result that an error occurred signing in and shows a toast.
         * @param provider the identity provider with which the user attempted sign-in.
         * @param ex the exception that occurred.
         */
        @Override
        public void onError(final IdentityProvider provider, final Exception ex) {
            Log.e(LOG_TAG, String.format("User Sign-in failed for %s : %s",
                provider.getDisplayName(), ex.getMessage()), ex);

            final AlertDialog.Builder errorDialogBuilder = new AlertDialog.Builder(SignInActivity.this);
            errorDialogBuilder.setTitle("Sign-In Error");
            errorDialogBuilder.setMessage(
                String.format("Sign-in with %s failed.\n%s", provider.getDisplayName(), ex.getMessage()));
            errorDialogBuilder.setNeutralButton("Ok", null);
            errorDialogBuilder.show();
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        signInManager = SignInManager.getInstance(this);

        signInManager.setResultsHandler(this, new SignInResultsHandler());

        // Initialize sign-in buttons.
        signInManager.initializeSignInButton(FacebookSignInProvider.class,
            this.findViewById(R.id.fb_login_button));

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        signInManager.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // pause/resume Mobile Analytics collection
        AWSMobileClient.defaultMobileClient().handleOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // pause/resume Mobile Analytics collection
        AWSMobileClient.defaultMobileClient().handleOnPause();
    }


    /**Created from the AWS demo app**/
    /** Sync user's preferences only if user is signed in **/
    private void syncUserSettings() {
        // sync only if user is signed in
        if (AWSMobileClient.defaultMobileClient().getIdentityManager().isUserSignedIn()) {
            UserPermissions userPermissions = UserPermissions.getInstance(getApplicationContext());
            userPermissions.getDataset().synchronize(new DefaultSyncCallback() {
                @Override
                public void onSuccess(final Dataset dataset, final List<Record> updatedRecords) {
                    super.onSuccess(dataset, updatedRecords);
                    Log.d(LOG_TAG, "successfully synced user settings");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadUserSettings();
                        }
                    });
                    //If firstTimeUser = 0
                    // go to OnBoardingActivity
                    //else
                    // stay on current activity and
                }
            });
        }
    }

    private void loadUserSettings() {
        final UserPermissions userPermissions = UserPermissions.getInstance(this);
        final Dataset dataset = userPermissions.getDataset();
        final ProgressDialog dialog = ProgressDialog.show(this,
                getString(R.string.settings_fragment_dialog_title),
                getString(R.string.settings_fragment_dialog_message));
        Log.d(LOG_TAG, "Loading user settings from remote");
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(final Dataset dataset, final List<Record> updatedRecords) {
                super.onSuccess(dataset, updatedRecords);
                userPermissions.loadFromDataset();
                int isNewUser = userPermissions.getNewUser();

                if(isNewUser == 0){
                    updateUI(dialog, 0);
                } else {
                    updateUI(dialog, 1);
                }
            }

            @Override
            public void onFailure(final DataStorageException dse) {
                Log.w(LOG_TAG, "Failed to load user settings from remote, using default.", dse);
                updateUI(dialog, 3);
            }

            @Override
            public boolean onDatasetsMerged(final Dataset dataset,
                                            final List<String> datasetNames) {
                // Handle dataset merge. One can selectively copy records from merged datasets
                // if needed. Here, simply discard merged datasets
                for (String name : datasetNames) {
                    Log.d(LOG_TAG, "found merged datasets: " + name);
                    AWSMobileClient.defaultMobileClient().getSyncManager().openOrCreateDataset(name).delete();
                }
                return true;
            }
        });
    }

    private void updateUI(final ProgressDialog dialog, final int isFirstTimeUSer) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }

                Log.d(LOG_TAG, "Launching Main Activity...");
//                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//                intent.putExtra("new_user", isFirstTimeUSer);
                startActivity(new Intent(SignInActivity.this, MainActivity.class)
                        .putExtra("new_user", isFirstTimeUSer)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                // finish should always be called on the main thread.
                finish();

//                if (isFirstTimeUSer == 1) {
//                    Log.d(LOG_TAG, "Launching Main Activity...");
//                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
////                    startActivity(new Intent(SignInActivity.this, BlankActivity.class));
//                    // finish should always be called on the main thread.
//                    finish();
//                } else if (isFirstTimeUSer == 0) {
//                    Log.d(LOG_TAG, "Launching OnBoarding Process...");
//                    startActivity(new Intent(SignInActivity.this, Main_OnBoarding.class)
//                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//                    // finish should always be called on the main thread.
//                    finish();
//                } else {
//                    Toast.makeText(SignInActivity.this, "Failure updating", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//                }

            }
        });
    }

}
