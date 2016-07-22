package ar.com.bestprice.buyitnow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ar.com.bestprice.buyitnow.dto.Item;
import ar.com.bestprice.buyitnow.dto.Purchase;
import ar.com.bestprice.buyitnow.dto.Purchases;

/**
 * Created by ivan on 08/04/16.
 */
public class AddNewPurchaseActivity extends AppCompatActivity{

    ListView listView = null;
    ArrayAdapter<String> adapter = null;
    List<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_purchase_tool_bar);

        Item item = (Item)getIntent().getSerializableExtra(Constants.ITEM);

        if(item != null) {

            listView = (ListView) findViewById(R.id.listview_show_items_in_a_purchase);
            items.add(item);

            List itemsAsString = new ArrayList();

            for (Item i: items ){
                itemsAsString.add(i.toString());
            }

            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, itemsAsString);

            listView.setAdapter(adapter);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.new_purchase_toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


            if (resultCode == CommonStatusCodes.SUCCESS && data != null) {

                    Item item = (Item) data.getSerializableExtra(Constants.ITEM);
                    items.add(item);

                    List itemsAsString = new ArrayList();

                    for (Item i : items) {
                        itemsAsString.add(i.toString());
                    }

                    ArrayAdapter adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, itemsAsString);

                    listView.setAdapter(adapter);
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_purchase_activity_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        // Handling action bar item clicks

        switch (menuItem.getItemId()){

            case R.id.add_item:

                Intent intent = new Intent(this.getApplicationContext(), AddItemActivity.class);

                //Adding an extra param for AddItemActivity to know if AddNewPurchase activity has to be started or
                //the captured item has just to be returned.
                intent.putExtra(Constants.CALLING_ACTIVITY, Constants.NEW_PURCHASE);
                startActivityForResult(intent, Constants.NEW_PURCHASE);

                break;

            case R.id.save_purchase:

                final ExecutorService service = Executors.newFixedThreadPool(1);
                final Future<Integer> task;

                //2016-05-05T18:54:03.5102707-03:00
                SimpleDateFormat datetime = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZZZZZ", Locale.US);
                datetime.setTimeZone(TimeZone.getTimeZone("UTC"));

                Date date = new Date(System.currentTimeMillis());

                Purchases purchases = new Purchases();
                Purchase purchase = new Purchase();
                purchase.setItems(items);
                purchase.setTime(datetime.format(date));


                ArrayList<Purchase> ps = new ArrayList<>();
                ps.add(purchase);

                purchases.setPurchases(ps);

                String serviceURL = Context.getContext().getServiceURL();
                task = service.submit(new POSTServiceClient(serviceURL + "/purchases", purchases));

                try {
                    Integer status = task.get();
                } catch (final InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                } finally {
                    service.shutdownNow();
                }

                finish();
                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }
}
