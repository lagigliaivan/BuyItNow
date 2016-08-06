package ar.com.bestprice.buyitnow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import ar.com.bestprice.buyitnow.dto.PurchasesByMonthContainer;

/**
 * Created by ivan on 08/04/16.
 */
public class AddNewPurchaseActivity extends AppCompatActivity{

    ListView listView = null;
    ArrayAdapter<String> adapter = null;
    List<Item> items = new ArrayList<>();
    private  PopupWindow pw = null;

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

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, itemsAsString);

            listView.setAdapter(adapter);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.new_purchase_toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(AddNewPurchaseActivity.this.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.activity_add_purchase_item,(ViewGroup) findViewById(R.id.add_purchase_popup));
        pw = getPopUp(layout);


        if(toolbar != null) {
            toolbar.post(new Runnable() {
                public void run() {
                    try {
                        pw.showAtLocation(layout, Gravity.CENTER, 0, 30);
                        showKeyboard();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    private void addItem(Item item){

        listView = (ListView) findViewById(R.id.listview_show_items_in_a_purchase);
        items.add(item);

        List itemsAsString = new ArrayList();

        for (Item i: items ){
            itemsAsString.add(i.toString());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, itemsAsString);

        listView.setAdapter(adapter);
        pw.dismiss();

    }

    @NonNull
    private PopupWindow getPopUp(final View layout) {

        Spinner spinner = (Spinner) layout.findViewById(R.id.spinner);
        ArrayList arraySpinner = new ArrayList();

        for (Category c : Category.values()) {
            arraySpinner.add(c);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arraySpinner);
        spinner.setAdapter(adapter);

        Button addItemButton = (Button) layout.findViewById(R.id.add_item_button);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText description = (EditText)layout.findViewById(R.id.item_description);
                EditText price = (EditText)layout.findViewById(R.id.item_price);

                Spinner spinner = (Spinner)layout.findViewById(R.id.spinner);

                TextView textView = (TextView)spinner.getSelectedView();
                String category = textView.getText().toString();

                MessageDigest crypt = null;

                try {
                    crypt = MessageDigest.getInstance("SHA-1");
                    crypt.reset();
                    crypt.update(description.getText().toString().getBytes("UTF-8"));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String itemId = Context.byteToHex(crypt.digest());

                Item item = new Item();
                item.setId(itemId);
                item.setDescription(description.getText().toString());
                item.setPrice(Float.valueOf(price.getText().toString()));
                item.setCategory(category);

                addItem(item);



            }
        });

        return new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
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

            ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, itemsAsString);

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

        switch (menuItem.getItemId()){

            case R.id.add_item:

                try {

                    LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.getApplicationContext().LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.activity_add_purchase_item,(ViewGroup) findViewById(R.id.add_purchase_popup));
                    pw = getPopUp(layout);
                    pw.showAtLocation(layout, Gravity.CENTER, 0,30);

                    showKeyboard();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.save_purchase:

                final StringBuilder shop = new StringBuilder();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Ingrese lugar de compra");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        shop.append(input.getText().toString());
                        //2016-05-05T18:54:03.5102707-03:00
                        SimpleDateFormat datetime = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZZZZZ", Locale.US);
                        datetime.setTimeZone(TimeZone.getTimeZone("UTC"));

                        Date date = new Date(System.currentTimeMillis());

                        Purchase purchase = new Purchase();

                        if(shop.toString().isEmpty()){
                            shop.append("No especificado");
                        }
                        purchase.setShop(shop.toString());
                        purchase.setItems(items);
                        purchase.setTime(datetime.format(date));


                        ArrayList<Purchase> ps = new ArrayList<>();
                        ps.add(purchase);

                        PurchasesService purchasesService = new PurchasesService();
                        purchasesService.savePurchases(ps);

                        finish();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


                break;
        }

        return super.onOptionsItemSelected(menuItem);
    }
}