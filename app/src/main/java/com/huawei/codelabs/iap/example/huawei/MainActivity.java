package com.huawei.codelabs.iap.example.huawei;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapClient;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseReq;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseResult;
import com.huawei.hms.iap.entity.InAppPurchaseData;
import com.huawei.hms.iap.entity.OrderStatusCode;
import com.huawei.hms.iap.entity.ProductInfo;
import com.huawei.hms.iap.entity.ProductInfoReq;
import com.huawei.hms.iap.entity.ProductInfoResult;
import com.huawei.hms.iap.entity.PurchaseIntentReq;
import com.huawei.hms.iap.entity.PurchaseIntentResult;
import com.huawei.hms.iap.entity.PurchaseResultInfo;
import com.huawei.hms.support.api.client.Status;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQ_CODE_BUY = 4002;
    private ListView listView;
    private String productId = "product1";
    private String item_name = "NAME";
    private String item_price = "PRICE";
    private String item_description = "DESCRIPTION";
    private String item_type = "TYPE";
    private List<HashMap<String, Object>> products = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_layout);
        loadProduct();
    }

    private void loadProduct() {
        listView = findViewById(R.id.itemlist);
        IapClient iapClient = Iap.getIapClient(MainActivity.this);
        Task<ProductInfoResult> task = iapClient.obtainProductInfo(createProductInfoReq());
        task.addOnSuccessListener(new OnSuccessListener<ProductInfoResult>() {
            @Override
            public void onSuccess(ProductInfoResult result) {
                if (result != null && !result.getProductInfoList().isEmpty()) {
                    showProduct(result.getProductInfoList());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    private ProductInfoReq createProductInfoReq() {
        ProductInfoReq req = new ProductInfoReq();

        //Есть три типа продуктов - Consumable, Non-Consumable, Subscription
        //Их запросы выглядят идентично за исключением поля PriceType

        req.setPriceType(IapClient.PriceType.IN_APP_CONSUMABLE);
        ArrayList<String> productIds = new ArrayList<>();
        productIds.add(productId);
        req.setProductIds(productIds);
        return req;
    }

    private void showProduct(List<ProductInfo> productInfoList) {
        for (ProductInfo productInfo : productInfoList) {
            HashMap<String, Object> item1 = new HashMap<String, Object>();
            item1.put(item_name, productInfo.getProductName());
            item1.put(item_price, productInfo.getPrice());
            item1.put(item_description, productInfo.getProductDesc());
            if (productInfo.getPriceType()==0) {
                item1.put(item_type, getString(R.string.consumable));
            }
            products.add(item1);

        }
        SimpleAdapter simAdapter = new SimpleAdapter(
                MainActivity.this, products, R.layout.item_layout,
                new String[]{item_name, item_price, item_type, item_description}, new int[]{
                R.id.itemHeader, R.id.priceTextView, R.id.itemType, R.id.itemText });
        listView.setAdapter(simAdapter);
        simAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                gotoPay(MainActivity.this, productId, IapClient.PriceType.IN_APP_CONSUMABLE);
            }
        });
    }

    private void gotoPay(final Activity activity, String productId, int type) {
        IapClient mClient = Iap.getIapClient(activity);
        Task<PurchaseIntentResult> task = mClient.createPurchaseIntent(createPurchaseIntentReq(type, productId));
        task.addOnSuccessListener(new OnSuccessListener<PurchaseIntentResult>() {
            @Override
            public void onSuccess(PurchaseIntentResult result) {
                if (result == null) {
                    return;
                }
                Status status = result.getStatus();
                if (status == null) {
                    return;
                }
                if (status.hasResolution()) {
                    try {
                        status.startResolutionForResult(activity, REQ_CODE_BUY);
                    } catch (IntentSender.SendIntentException exp) {
                    }
                } else {
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    private PurchaseIntentReq createPurchaseIntentReq(int type, String productId) {
        PurchaseIntentReq req = new PurchaseIntentReq();
        req.setProductId(productId);
        req.setPriceType(type);
        req.setDeveloperPayload("test");
        return req;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_BUY) {
            PurchaseResultInfo purchaseResultInfo = Iap.getIapClient(this).parsePurchaseResultInfoFromIntent(data);
            switch(purchaseResultInfo.getReturnCode()) {
                case OrderStatusCode.ORDER_STATE_SUCCESS:
                    boolean success = CipherUtil.doCheck(purchaseResultInfo.getInAppPurchaseData(), purchaseResultInfo.getInAppDataSignature(), Key.getPublicKey());
                    if (success) {
                        consumeOwnedPurchase(this, purchaseResultInfo.getInAppPurchaseData());
                    }
                    return;
                case OrderStatusCode.ORDER_STATE_CANCEL:
                    Toast.makeText(this, getString(R.string.user_cancel), Toast.LENGTH_SHORT).show();
                    return;
                case OrderStatusCode.ORDER_PRODUCT_OWNED:
                    Toast.makeText(this, getString(R.string.already_owned), Toast.LENGTH_SHORT).show();
                    return;
                default:
                    Toast.makeText(this, getString(R.string.fail), Toast.LENGTH_SHORT).show();
                    break;
            }
            return;
        }
    }

    private void consumeOwnedPurchase(final Context context, String inAppPurchaseData) {
        IapClient mClient = Iap.getIapClient(context);
        Task<ConsumeOwnedPurchaseResult> task = mClient.consumeOwnedPurchase(createConsumeOwnedPurchaseReq(inAppPurchaseData));
        task.addOnSuccessListener(new OnSuccessListener<ConsumeOwnedPurchaseResult>() {
            @Override
            public void onSuccess(ConsumeOwnedPurchaseResult result) {
                Toast.makeText(context, getString(R.string.success), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    private ConsumeOwnedPurchaseReq createConsumeOwnedPurchaseReq(String purchaseData) {
        ConsumeOwnedPurchaseReq req = new ConsumeOwnedPurchaseReq();
        try {
            InAppPurchaseData inAppPurchaseData = new InAppPurchaseData(purchaseData);
            req.setPurchaseToken(inAppPurchaseData.getPurchaseToken());
        } catch (JSONException e) {
        }
        return req;
    }

}

