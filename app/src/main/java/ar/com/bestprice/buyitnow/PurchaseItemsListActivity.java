package ar.com.bestprice.buyitnow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

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

        //Toolbar toolbar = (Toolbar) findViewById(R.id.new_purchase_toolbar);

        //setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


    }


    private void renderList(PurchasesByMonthContainer purchasesContainer) {

        if(this.listView == null) {
            this.listView = (ListView) findViewById(R.id.listView_list_items);
        }

        this.listView.setAdapter(new ItemsListAdapter(this.getApplicationContext(), getPurchase()));


    }



}
