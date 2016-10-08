package ar.com.bestprice.buyitnow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import ar.com.bestprice.buyitnow.dto.Item;
import ar.com.bestprice.buyitnow.dto.Purchase;
import ar.com.bestprice.buyitnow.dto.Purchases;
import ar.com.bestprice.buyitnow.dto.PurchasesByMonthContainer;

/**
 * Created by ivan on 18/09/16.
 */
public class PurchaseItemsListActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_purchase_items);

        String purchaseId = getIntent().getStringExtra(Constants.PURCHASE_ID);

        renderList(purchaseId);
    }


    private void renderList(String purchaseId) {


        String jsonString = null;

        try {
            PurchasesService service = new PurchasesService();
            jsonString = service.getPurchase(purchaseId);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(this.listView == null) {
            this.listView = (ListView) findViewById(R.id.listView_list_items);
        }

        Gson gson = new Gson();
        Purchase ps = gson.fromJson(jsonString, Purchase.class);

        ArrayList<Item> items = new ArrayList();
        for (Item item : ps.getItems() ){
            items.add(item);
        }

        CheckedTextView purchaseDescription = (CheckedTextView)findViewById(R.id.group_item_description);
        purchaseDescription.setText(ps.getShop());


        TextView totalAmount = (TextView)findViewById(R.id.total_amount);

        totalAmount.setText(String.format("$%.2f", ps.getTotalPrice()));


        this.listView.setAdapter(new ItemsListAdapter(this.getApplicationContext(), items, this));
    }
}
