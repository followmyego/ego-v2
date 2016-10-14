//
// Copyright 2016 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.9
//
package com.amazonaws.mobile.push;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.services.sns.model.UnsubscribeRequest;
import com.amazonaws.mobile.util.ThreadUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The Push Manager registers the GCM device token in Amazon SNS. The result of this
 * registration process is an Amazon SNS Endpoint ARN, which can be used to send
 * push notifications directly to a specific device. The Push Manager also manages
 * Amazon SNS topic subscriptions, allowing the app to subscribe to Amazon SNS topics,
 * which let you target groups of devices with push notifications.
 */
public class PushManager implements GCMTokenHelper.GCMTokenUpdateObserver {

    public interface PushStateListener {
        void onPushStateChange(PushManager pushManager, boolean isEnabled);
    }

    private static final String LOG_TAG = PushManager.class.getSimpleName();

    // Name of the shared preferences
    private static final String SHARED_PREFS_FILE_NAME = PushManager.class.getName();
    // Keys in shared preferences
    private static final String SHARED_PREFS_PUSH_ENABLED = "pushEnabled";
    private static final String SHARED_PREFS_KEY_ENDPOINT_ARN = "endpointArn";
    private static final String SHARED_PREFS_PREVIOUS_PLATFORM_APPLICATION = "previousPlatformApp";
    // Constants for SNS
    private static final String SNS_PROTOCOL_APPLICATION = "application";
    private static final String SNS_ENDPOINT_ATTRIBUTE_ENABLED = "Enabled";

    private static PushStateListener pushStateListener;

    private final AmazonSNS sns;

    private final SharedPreferences sharedPreferences;

    private final GCMTokenHelper gcmTokenHelper;

    private final String platformApplicationArn;
    private String endpointArn;
    private boolean shouldEnablePush;
    private boolean pushEnabled;
    private Boolean previousPushState = null;
    private final String defaultTopicArn;
    /** Map of topic ARN to SnsTopic. */
    private Map<String, SnsTopic> topics;

    /**
     * Constructor.
     * @param context application context
     * @param gcmTokenHelper the manager of the gcm token.
     * @param provider credentials provider
     * @param platformApplicationArn Amazon SNS platform application identifier
     * @param clientConfiguration client configuration for Amazon SNS client
     * @param defaultTopicArn the default topic ARN.
     * @param topicArns additional topic ARNs to register not including the default topic arn.
     * @param region the AWS region for Push feature
     */
    public PushManager(final Context context,
                       final GCMTokenHelper gcmTokenHelper,
                       final AWSCredentialsProvider provider,
                       final String platformApplicationArn,
                       final ClientConfiguration clientConfiguration,
                       final String defaultTopicArn,
                       final String[] topicArns,
                       final Regions region) {

        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_FILE_NAME,
            Context.MODE_PRIVATE);

        this.gcmTokenHelper = gcmTokenHelper;
        this.platformApplicationArn = platformApplicationArn;
        this.defaultTopicArn = defaultTopicArn;
        sns = new AmazonSNSClient(provider, clientConfiguration);
        sns.setRegion(Region.getRegion(region));

        topics = new TreeMap<String, SnsTopic>();
        setTopics(topicArns);

        // Avoid the situation where a previous download/build of the sample app has
        // been run in a re-used emulator and the platform application arn changed.
        final String previousPlatformApp =
            sharedPreferences.getString(SHARED_PREFS_PREVIOUS_PLATFORM_APPLICATION, "");

        if (!previousPlatformApp.equalsIgnoreCase(platformApplicationArn)) {
            Log.d(LOG_TAG, "SNS platform application ARN changed or not set. Triggering SNS endpoint refresh.");
            endpointArn = "";
            // clear shared preferences.
            sharedPreferences.edit().clear().apply();
            pushEnabled = false;
            shouldEnablePush = true;
        } else {
            endpointArn = sharedPreferences.getString(SHARED_PREFS_KEY_ENDPOINT_ARN, "");
            pushEnabled = sharedPreferences.getBoolean(SHARED_PREFS_PUSH_ENABLED, false);
            shouldEnablePush = pushEnabled;
        }
        gcmTokenHelper.addTokenUpdateObserver(this);
    }

    @Override
    public void onGCMTokenUpdate(final String gcmToken, final boolean didTokenChange) {
        if (didTokenChange || !isRegistered()) {
            try {
                Log.d(LOG_TAG, "GCM Token changed or SNS endpoint not registered.");
                try {
                    createPlatformArn();
                } catch (final AmazonClientException ex) {
                    Log.e(LOG_TAG, "Error creating platform endpoint ARN: " + ex.getMessage(), ex);
                    pushEnabled = false;
                    throw ex;
                }

                try {
                    Log.d(LOG_TAG, "Updating push enabled state to " + shouldEnablePush);
                    setSNSEndpointEnabled(shouldEnablePush);
                } catch (final AmazonClientException ex) {
                    Log.e(LOG_TAG, "Failed to set push enabled state : " + ex, ex);
                    throw ex;
                }

                try {
                    Log.d(LOG_TAG, "Resubscribing to subscribed topics.");
                    resubscribeToTopics();
                } catch (final AmazonClientException ex) {
                    Log.e(LOG_TAG, "Failed resubscribing to topics : " + ex, ex);
                    throw ex;
                }
            } catch (final AmazonClientException ex) {
                // Clear the endpoint ARN, regardless of what failed, this will force the app
                // to try again the next time the app is started or registerDevice() is called.
                endpointArn = "";
                Log.e(LOG_TAG, "Push Notifications - FAILED : " + ex, ex);
                return;
            } finally {
                sharedPreferences.edit()
                    .putString(SHARED_PREFS_PREVIOUS_PLATFORM_APPLICATION, platformApplicationArn)
                    .putString(SHARED_PREFS_KEY_ENDPOINT_ARN, endpointArn)
                    // Setting push enabled to whether push should be enabled, so a failure
                    // will not disable push in shared preferences, and the app will retry
                    // when restarted.
                    .putBoolean(SHARED_PREFS_PUSH_ENABLED, shouldEnablePush)
                    .apply();
                informStateListener();
            }
        }
        Log.d(LOG_TAG, "Push Notifications - OK ");
    }

    @Override
    public void onGCMTokenUpdateFailed(final Exception ex) {
        Log.e(LOG_TAG, "Push Notifications - FAILED : GCM registration failed : " + ex, ex);
        pushEnabled = false;
        informStateListener();
    }

    /**
     * Registers this application on this device to receive push notifications from Google cloud messaging.
     * Also registers the resulting device token with Amazon SNS. This creates an Amazon SNS platform
     * endpoint, which can be used to send push notifications directly to this device. Additionally, this
     * also subscribes to the default topic if it has not been explicitly disabled previously by a call to
     * {@link #unsubscribeFromTopic(SnsTopic)}.
     */
    public void registerDevice() {
        // Updates the GCM token, which triggers {@link #onGCMTokenUpdate(String,boolean)} to create the platform
        // arn set push enabled, and re-subscribe to any previously subscribed topics.
        gcmTokenHelper.updateGCMToken();
    }

    private void createPlatformArn() {
        final CreatePlatformEndpointRequest request = new CreatePlatformEndpointRequest();
        request.setPlatformApplicationArn(platformApplicationArn);
        request.setToken(gcmTokenHelper.getGCMToken());
        final CreatePlatformEndpointResult result = sns.createPlatformEndpoint(request);
        endpointArn = result.getEndpointArn();
        Log.d(LOG_TAG, "endpoint arn: " + endpointArn);
    }

    /**
     * Associates Amazon SNS topic ARNs to this push manager.
     *
     * @param topicArns a list of topic ARNs
     */
    private void setTopics(final String[] topicArns) {
        topics.clear();
        topics.put(defaultTopicArn, new SnsTopic(defaultTopicArn, sharedPreferences.getString(defaultTopicArn, "")));
        for (String topicArn : topicArns) {
            topics.put(topicArn, new SnsTopic(topicArn, sharedPreferences.getString(topicArn, "")));
        }
    }

    private void resubscribeToTopics() {
        // Subscribe to all topics that are enabled in shared preferences.
        final SnsTopic defaultTopic = getDefaultTopic();
        for (final SnsTopic topic : topics.values()) {
            final String topicSharedPrefValue =
                sharedPreferences.getString(topic.getTopicArn(), null);
            if (topicSharedPrefValue == null) {
                // The shared preference didn't exist, for the default topic we should auto-subscribe.
                if (topic == defaultTopic) {
                    subscribeToTopic(topic);
                    Log.d(LOG_TAG, "Push Notifications - Registered for default topic: " + topic.getDisplayName());
                }
            } else if (!topicSharedPrefValue.equals("")){
                // the shared preference existed and the topic was enabled so we should subscribe.
                subscribeToTopic(topic);
                Log.d(LOG_TAG, "Push Notifications - Registered for topic: " + topic.getDisplayName());
            }
        }
    }

    /**
     * Subscribes to a given Amazon SNS topic.
     *
     * @param topic topic to subscribe to
     */
    public void subscribeToTopic(final SnsTopic topic) {
        final SubscribeRequest request = new SubscribeRequest();
        request.setEndpoint(endpointArn);
        request.setTopicArn(topic.getTopicArn());
        request.setProtocol(SNS_PROTOCOL_APPLICATION);
        final SubscribeResult result = sns.subscribe(request);

        // update topic and save subscription in shared preferences
        final String subscriptionArn = result.getSubscriptionArn();
        topic.setSubscriptionArn(subscriptionArn);
        sharedPreferences.edit().putString(topic.getTopicArn(), subscriptionArn).apply();
    }

    /**
     * Unsubscribes from a given Amazon SNS topic.
     *
     * @param topic topic to unsubscribe from
     */
    public void unsubscribeFromTopic(final SnsTopic topic) {
        // Rely on the status stored locally even though it's likely that the device is
        // subscribed to a topic, but the subscription arn is lost, say due to clearing app data.
        if (!topic.isSubscribed()) {
            return;
        }

        final UnsubscribeRequest request = new UnsubscribeRequest();
        request.setSubscriptionArn(topic.getSubscriptionArn());
        sns.unsubscribe(request);

        // update topic and save subscription in shared preferences
        topic.setSubscriptionArn("");
        sharedPreferences.edit().putString(topic.getTopicArn(), "").apply();
    }

    private void setSNSEndpointEnabled(final boolean enabled) {
        Map<String, String> attr = new HashMap<String, String>();
        attr.put(SNS_ENDPOINT_ATTRIBUTE_ENABLED, String.valueOf(enabled));
        SetEndpointAttributesRequest request = new SetEndpointAttributesRequest();
        request.setEndpointArn(endpointArn);
        request.setAttributes(attr);
        sns.setEndpointAttributes(request);
        Log.d(LOG_TAG, String.format("Set push %s for endpoint arn: %s",
            enabled ? "enabled" : "disabled", endpointArn));
        this.pushEnabled = enabled;
    }
    /**
     * Changes push notification Amazon SNS endpoint status.
     *
     * @param enabled whether to enable the push notification endpoint
     */
    public void setPushEnabled(final boolean enabled) {
        shouldEnablePush = enabled;
        setSNSEndpointEnabled(enabled);
        informStateListener();
        sharedPreferences.edit()
            .putBoolean(SHARED_PREFS_PUSH_ENABLED, enabled)
            .putString(SHARED_PREFS_PREVIOUS_PLATFORM_APPLICATION, platformApplicationArn)
            .apply();
    }

    /**
     * Gets whether the device has been registered. If not registered,
     * registerDevice() should be invoked.
     *
     * @return true if registered, false otherwise
     */
    public boolean isRegistered() {
        return endpointArn != null && !endpointArn.isEmpty();
    }

    /**
     * Gets the device's Amazon SNS endpoint ARN. This endpoint identifier can be
     * used to send push notifications directly to this device, from the Amazon SNS
     * console, or from another mobile app, if the app has permissions in IAM to
     * publish messages to Amazon SNS.
     */
    public String getEndpointArn() {
        return endpointArn;
    }

    /**
     * Gets a Map of topics that this push manager has, including the default topic.
     *
     * @return a Map of topic ARNs to SnsTopic objects.
     */
    public Map<String, SnsTopic> getTopics() {
        return Collections.unmodifiableMap(topics);
    }

    /**
     * Get the default topic.
     * @return the default SNS topic.
     */
    public SnsTopic getDefaultTopic() {
        return topics.get(defaultTopicArn);
    }

    /**
     * Sets a listener to be informed when push notifications are enabled or disabled.
     * @param listener listener
     */
    public static void setPushStateListener(final PushStateListener listener) {
        PushManager.pushStateListener = listener;
    }

    private void informStateListener() {
        if (previousPushState == null || pushEnabled != previousPushState) {
            previousPushState = pushEnabled;
            if (pushStateListener == null) {
                return;
            }
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "PushStateListener: State changed to : " +
                        (pushEnabled ? "PUSH ENABLED" : "PUSH DISABLED"));

                    try {
                        pushStateListener.onPushStateChange(PushManager.this, pushEnabled);
                        Log.d(LOG_TAG, "PushStateListener:onPushStateChange ok");
                    } catch (final Exception e) {
                        Log.e(LOG_TAG, "PushStateListener:onPushStateChange Failed : " + e.getMessage(), e);
                    }
                }
            });
        }
    }

    /**
     * Gets whether the device is enabled for push notification.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isPushEnabled() {
        return pushEnabled;
    }
}
