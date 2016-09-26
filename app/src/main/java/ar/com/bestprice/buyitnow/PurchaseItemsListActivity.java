package ar.com.bestprice.buyitnow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import ar.com.bestprice.buyitnow.dto.Item;

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

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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

        this.listView.setAdapter(new ItemsListAdapter(this.getApplicationContext(), new ArrayList<Item>()));
    }
}
