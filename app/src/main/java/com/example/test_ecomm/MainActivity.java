package com.example.test_ecomm;

import static com.example.test_ecomm.Utils.showMessage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.ecommpay.sdk.*;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //If you using onActivityResult() (DEPRECATED)
    //private static int PAY_ACTIVITY_REQUEST = 888;

    private static int PROJECT_ID = 123;
    private static String SECRET = "key";
    private static String RANDOM_PAYMENT_ID = "test_integration_ecommpay_" + getRandomNumber();

    //STEP 1: Create payment info object with product information
    private ECMPPaymentInfo paymentInfo = getPaymentInfoAllParams(); // getPaymentInfoAllParams

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //STEP 2: Signature should be generated on your server and delivered to your app
        String signature = SignatureGenerator.generateSignature(
                getParamsForSigning(),
                SECRET
        );

        //STEP 3: Sign payment info object
        setSignature(signature);

        //STEP 4: Create the intent of SDK
        Intent SDKIntent = ECMPActivity.buildIntent(this, paymentInfo);

        //STEP 5: Present Checkout UI (default theme is light)
        startActivityForResult.launch(SDKIntent);

    }

    //Only for testing
    private static String getRandomNumber(){
        int randomNumber = (new Random().nextInt(9999) + 1000);
        return Integer.toString(randomNumber);
    }

    // Handle SDK result
    private final ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                switch (result.getResultCode()) {
                    case ECMPActivity.RESULT_SUCCESS: {
                        showMessage(this, "Success");
                        break;
                    }
                    case ECMPActivity.RESULT_CANCELLED: {
                        showMessage(this, "Cancelled");
                        break;
                    }
                    case ECMPActivity.RESULT_DECLINE: {
                        showMessage(this, "Decline");
                        break;
                    }
                    case ECMPActivity.RESULT_FAILED: {
                        showMessage(this, "Failed");
                        break;
                    }
                }
                Intent data = result.getData();
                if (data != null && data.hasExtra(ECMPActivity.DATA_INTENT_EXTRA_ERROR)) {
                    String error = data.getStringExtra(ECMPActivity.DATA_INTENT_EXTRA_ERROR);
                }

                if (data != null && data.hasExtra(ECMPActivity.DATA_INTENT_EXTRA_TOKEN)) {
                    String token = data.getStringExtra(ECMPActivity.DATA_INTENT_EXTRA_TOKEN);
                }

                if (data != null && data.hasExtra(ECMPActivity.DATA_INTENT_SESSION_INTERRUPTED) &&
                        data.getBooleanExtra(ECMPActivity.DATA_INTENT_SESSION_INTERRUPTED, false)) {
                    //Do something to handle interrupted payment session
                }
            }
    );

    ECMPPaymentInfo getPaymentInfoAllParams() {
        return new ECMPPaymentInfo(
                PROJECT_ID, // project ID that is assigned to you
                RANDOM_PAYMENT_ID, // payment ID to identify payment in your system
                1000, // 1.00
                "USD",
                "T-shirt with dog print",
                "10", // unique ID assigned to your customer
                "UK"
        );
    }

    //Get params for signing payment (do it only after create paymentInfo object)
    private String getParamsForSigning()  {
        return paymentInfo.getParamsForSignature();
    }

    //Setters for payment info
    private void setSignature(String signature) {
        paymentInfo.setSignature(signature);
    }

}