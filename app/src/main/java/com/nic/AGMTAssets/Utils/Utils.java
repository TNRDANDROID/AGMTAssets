package com.nic.AGMTAssets.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.nic.AGMTAssets.Application.NICApplication;
import com.nic.AGMTAssets.Constant.AppConstant;
import com.nic.AGMTAssets.R;
import com.nic.AGMTAssets.Session.PrefManager;
import com.nic.AGMTAssets.Support.MyCustomTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Utils {

    private static final String SHARED_PREFERENCE_UTILS = "Nimble";
    private static final int SECONDS_IN_A_MINUTE = 60;
    private static final int MINUTES_IN_AN_HOUR = 60;
    private static SharedPreferences sharedPreferences;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static String CIPHER_NAME = "AES/CBC/PKCS5PADDING";
    private static int CIPHER_KEY_LEN = 16; //128 bits
    private static PrefManager prefManager;

    private static void initializeSharedPreference() {
        sharedPreferences = NICApplication.getGlobalContext()
                .getSharedPreferences(SHARED_PREFERENCE_UTILS,
                        Context.MODE_PRIVATE);
    }

    public static boolean isOnline() {
        boolean status = false;
        ConnectivityManager cm = (ConnectivityManager) NICApplication.getGlobalContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                status = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = true;
            }
        } else {
            status = false;
        }

        return status;
    }



    public static void showAlert(Context context, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = inflater.inflate(R.layout.alert_dialog, null);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setView(dialogView, 0, 0, 0, 0);
            alertDialog.setCancelable(false);
            alertDialog.show();

            MyCustomTextView tv_message = (MyCustomTextView) dialogView.findViewById(R.id.tv_message);
            tv_message.setText(message);

            Button btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        } catch (Exception e) {
        }
    }










    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static String randomChar() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15; i++)
            sb.append(chars[rnd.nextInt(chars.length)]);
        Log.d("rand", sb.toString());
        return sb.toString();
    }

    public static String getSHA(String UserPassWord) {
        try {

            // Static getInstance method is called with hashing SHA
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method called
            // to calculate message digest of an input
            // and return array of byte
            byte[] messageDigest = md.digest(UserPassWord.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown"
                    + " for incorrect algorithm: " + e);

            return null;
        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String encrypt(String key, String iv, String data) {

        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
            SecretKeySpec secretKey = new SecretKeySpec(fixKey(key).getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            byte[] encryptedData = cipher.doFinal((data.getBytes()));

            String encryptedDataInBase64 = android.util.Base64.encodeToString(encryptedData, 0);
            String ivInBase64 = android.util.Base64.encodeToString(iv.getBytes("UTF-8"), 0);

            return encryptedDataInBase64 + ":" + ivInBase64;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }


    }

    private static String fixKey(String key) {

        if (key.length() < CIPHER_KEY_LEN) {
            int numPad = CIPHER_KEY_LEN - key.length();

            for (int i = 0; i < numPad; i++) {
                key += "0"; //0 pad to len 16 bytes
            }

            return key;

        }

        if (key.length() > CIPHER_KEY_LEN) {
            return key.substring(0, CIPHER_KEY_LEN); //truncate to 16 bytes
        }

        return key;
    }

    /**
     * Decrypt data using AES Cipher (CBC) with 128 bit key
     *
     * @param key  - key to use should be 16 bytes long (128 bits)
     * @param data - encrypted data with iv at the end separate by :
     * @return decrypted data string
     */


    public static String decrypt(String key, String data) {

        try {
            String[] parts = data.split(":");

            IvParameterSpec iv = new IvParameterSpec(android.util.Base64.decode(parts[1], 1));
            // System.out.println(fixKey(iv));
            SecretKeySpec secretKey = new SecretKeySpec(fixKey(key).getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] decodedEncryptedData = android.util.Base64.decode(parts[0], 1);

            byte[] original = cipher.doFinal(decodedEncryptedData);

            return new String(original);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public static JSONObject roadListJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_ROAD_LIST);
        dataSet.put(AppConstant.DISTRICT_CODE,prefManager.getDistrictCode());
        dataSet.put(AppConstant.BLOCK_CODE,prefManager.getBlockCode());
        Log.d("utils_roadlist", "" + dataSet);
        return dataSet;
    }
    public static JSONObject assetListJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_ASSET_LIST);
        dataSet.put(AppConstant.DISTRICT_CODE,prefManager.getDistrictCode());
        dataSet.put(AppConstant.BLOCK_CODE,prefManager.getBlockCode());
        Log.d("utils_assetlist", "" + dataSet);
        return dataSet;
    }
    public static JSONObject pmgsyVillageListJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_PMGSY_VILLAGE_LIST);
        dataSet.put(AppConstant.DISTRICT_CODE,prefManager.getDistrictCode());
        dataSet.put(AppConstant.BLOCK_CODE,prefManager.getBlockCode());
        Log.d("utils_pmgsyVillage", "" + dataSet);
        return dataSet;
    }
    public static JSONObject pmgsyHabitationListJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_PMGSY_HABITATION_LIST);
        dataSet.put(AppConstant.DISTRICT_CODE,prefManager.getDistrictCode());
        dataSet.put(AppConstant.BLOCK_CODE,prefManager.getBlockCode());
        Log.d("utils_pmgsyHabitation", "" + dataSet);
        return dataSet;
    }
    public static JSONObject pmgsyImagesListJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_HABITATION_IMAGE);
        Log.d("utils_pmgsyImages", "" + dataSet);
        return dataSet;
    }
    public static JSONObject bridgesListJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_BRIDGES_CULVERT);
        Log.d("utils_bridges", "" + dataSet);
        return dataSet;
    }
    //Version name
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;
        return version;
    }

    public static JSONObject HabitationListDistrictBlockVillageWiseJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_HABITATION_LIST_DISTRICT_BLOCK_WISE);
        dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
        dataSet.put(AppConstant.BLOCK_CODE, prefManager.getBlockCode());
        Log.d("HabListDistBlock", "" + dataSet);
        return dataSet;
    }
    public static JSONObject agmtListJsonParams(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_AGMT_FORM_LIST);
        dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
        dataSet.put(AppConstant.BLOCK_CODE, prefManager.getBlockCode());
        Log.d("KEY_AGMT_FORM_LIST", "" + dataSet);
        return dataSet;
    }
    public static JSONObject agmt_list_of_asset(Activity activity) throws JSONException {
        prefManager = new PrefManager(activity);
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_agmt_list_of_asset);
        dataSet.put(AppConstant.DISTRICT_CODE, prefManager.getDistrictCode());
        dataSet.put(AppConstant.BLOCK_CODE, prefManager.getBlockCode());
        Log.d("KEY_AGMT_FORM_LIST", "" + dataSet);
        return dataSet;
    }

}
