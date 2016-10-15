package ar.com.bestprice.buyitnow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ar.com.bestprice.buyitnow.dto.PurchasesByMonthContainer;
import ar.com.bestprice.buyitnow.json.PurchaseHandler;


public class MainActivity extends AppCompatActivity {


    private ExpandableListView listView = null;

    //Contains the purchases returned by the server
    private PurchasesByMonthContainer purchasesContainer = null;

    private final Map<Category, Boolean> lastSelectedItems = new LinkedHashMap<>();
    private final PurchaseHandler purchaseHandler = new PurchaseHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tool_bar);
        renderView();

    }

    private void renderView() {

        renderPurchasesList();

        setSupportActionBar((Toolbar) findViewById(R.id.main_tool_bar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private MyExpandableListAdapter getListViewAdapter(PurchasesByMonthContainer purchasesContainer) {

        Map<Integer, PurchasesGroup> groups = purchaseHandler.getSortedPurchasesGroups(purchasesContainer.getPurchasesByMonth());
        return new MyExpandableListAdapter(this, groups);
    }

    private String getPurchases() {

        String jsonString = null;

        try {
            PurchasesService service = new PurchasesService();
            jsonString = service.getPurchases();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return jsonString;
    }

    private ExpandableListView getListView() {

        if(this.listView == null) {
            this.listView = (ExpandableListView) findViewById(R.id.listView_show_purchases);
        }

        return listView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){

            case R.id.add_item:

                Intent intent = new Intent(this.getApplicationContext(), AddNewPurchaseActivity.class);
                startActivity(intent);

                break;

            case R.id.refresh_purchases:

                renderPurchasesList();
                break;

            case R.id.search_purchases:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Ingrese descripcion a buscar");

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String m_Text = input.getText().toString();

                        List purchasesByMonth = purchaseHandler.searchStringInPurchases(m_Text, purchasesContainer.getPurchasesByMonth());

                        PurchasesByMonthContainer container = new PurchasesByMonthContainer();
                        container.setPurchasesByMonth(purchasesByMonth);

                        renderList(container);
                        InputMethodManager imm = (InputMethodManager)getSystemService(android.content.Context.INPUT_METHOD_SERVICE);

                        imm.hideSoftInputFromWindow( getListView().getWindowToken(), 0);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                InputMethodManager imm = (InputMethodManager)getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                builder.show();

                break;

            case R.id.filter_purchases:



                final List<CharSequence> items = new ArrayList<>();
                final CharSequence[] array = new CharSequence[Category.values().length];

                for(Category cat : Category.values()){
                    items.add(cat.getName());

                }

                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Seleccione las categorias a filtrar");


                if(lastSelectedItems.size() == 0){
                    for (Category cat : Category.values()){
                        lastSelectedItems.put(cat, false);
                    }
                }

                boolean[] checked = new boolean[lastSelectedItems.size()];

                int i = 0;
                for(Boolean c : lastSelectedItems.values()){

                    checked[i] = c;
                    i++;
                }

                final List<Integer> selectedItems = new ArrayList();
                final List<Integer> deSelectedItems = new ArrayList();

                b.setMultiChoiceItems(items.toArray(array), checked ,
                new DialogInterface.OnMultiChoiceClickListener() {


                            // indexSelected contains the index of item (of which checkbox checked)
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                if (isChecked) {
                                    selectedItems.add(indexSelected);
                                } else {
                                    deSelectedItems.add(indexSelected);
                                }
                            }
                        })
                        // Set the action buttons
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                for(Integer i : selectedItems){
                                    lastSelectedItems.put(Category.values()[i], true);
                                }
                                for(Integer i : deSelectedItems){
                                    lastSelectedItems.put(Category.values()[i], false);
                                }

                                List<Category> categories = new ArrayList();

                                for(Category cat : lastSelectedItems.keySet()){
                                    if (lastSelectedItems.get(cat) == true) {
                                        categories.add(cat);
                                    }
                                }

                                if(categories.size() != 0){

                                    List purchasesByMonth = purchaseHandler.getPurchasesByCategory(categories, purchasesContainer.getPurchasesByMonth());

                                    PurchasesByMonthContainer container = new PurchasesByMonthContainer();
                                    container.setPurchasesByMonth(purchasesByMonth);

                                    renderList(container);
                                }else {
                                    renderList(purchasesContainer);
                                }
                                selectedItems.clear();
                                deSelectedItems.clear();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                selectedItems.clear();
                                deSelectedItems.clear();
                            }
                        });

                AlertDialog dialog = b.create();//AlertDialog dialog; create like this outside onClick
                dialog.show();
                break;


            case R.id.logout:

                if (getParent() == null) {
                    setResult(1001);
                } else {
                    getParent().setResult(1001);
                }

                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void renderPurchasesList() {

        String jsonString = getPurchases();


//        String jsonString =  "{\"purchasesByMonth\":[\n" +
//                "\t\t{ \"month\" : \"January\",\n" +
//                "\t\t  \"purchases\":[\n" +
//                "\t\t\t\t{\n" +
//                "\t\t  \t\t\"time\":\"2016-04-12T00:06:22.364Z\",\n" +
//                "\t\t  \t\t\"items\":[\n" +
//                "\t\t\t   \t\t{\n" +
//                "\t\t\t     \t\t\"id\":\"1233123\",\n" +
//                "\t\t\t     \t\t\"description\":\"no se\",\n" +
//                "\t\t\t     \t\t\"price\":12.0,\n" +
//                "\t\t\t     \t\t\"category\":\"SALUD\"\n" +
//                "\t\t\t   \t\t},\n" +
//                "\t\t\t\t\t{\n" +
//                "\t\t\t     \t\t\"id\":\"1234343123\",\n" +
//                "\t\t\t     \t\t\"description\":\"very long long long description of a product which has really a long long long description. Not sure what will happen\",\n" +
//                "\t\t\t     \t\t\"price\":12.0,\n" +
//                "\t\t\t     \t\t\"category\":\"SALUD\"\n" +
//                "\t\t\t   \t\t}\n" +
//                "\n" +
//                "\t\t\t  \t\t]\t\n" +
//                "\t\t\t\t}\n" +
//                "\t  \t\t ]\n" +
//                "\t\t},\n" +
//                "\t\t{ \"month\" : \"March\",\n" +
//                "\t\t  \"purchases\":[\n" +
//                "\t\t\t\t{\n" +
//                "\t\t  \t\t\"time\":\"2016-04-12T00:06:22.364Z\",\n" +
//                "\t\t  \t\t\"items\":[\n" +
//                "\t\t\t   \t\t{\n" +
//                "\t\t\t     \t\t\"id\":\"1233123\",\n" +
//                "\t\t\t     \t\t\"description\":\"no se\",\n" +
//                "\t\t\t     \t\t\"price\":32.0,\n" +
//                "\t\t\t     \t\t\"category\":\"SALUD\"\n" +
//                "\t\t\t   \t\t},\n" +
//                "\t\t\t   \t\t{\n" +
//                "                        \"id\":\"1233123\",\n" +
//                "                        \"description\":\"no se\",\n" +
//                "                        \"price\":32.0,\n" +
//                "                        \"category\":\"MERCADERIA\"\n" +
//                "                    },\n" +
//                "\t\t\t\t\t{\n" +
//                "\t\t\t     \t\t\"id\":\"1234343123\",\n" +
//                "\t\t\t     \t\t\"description\":\"very long long long description of a product which has really a long long long description. Not sure what will happen\",\n" +
//                "\t\t\t     \t\t\"price\":22.0,\n" +
//                "\t\t\t     \t\t\"category\":\"SALUD\"\n" +
//                "\t\t\t   \t\t}\n" +
//                "\n" +
//                "\t\t\t  \t\t]\t\n" +
//                "\t\t\t\t}\n" +
//                "\t  \t\t ]\n" +
//                "\t\t},{ \"month\" : \"April\",\n" +
//                "          \t\t  \"purchases\":[\n" +
//                "          \t\t\t\t{\n" +
//                "          \t\t  \t\t\"time\":\"2016-04-12T00:06:22.364Z\",\n" +
//                "          \t\t  \t\t\"items\":[\n" +
//                "          \t\t\t   \t\t{\n" +
//                "          \t\t\t     \t\t\"id\":\"1233123\",\n" +
//                "          \t\t\t     \t\t\"description\":\"no se\",\n" +
//                "          \t\t\t     \t\t\"price\":2.0,\n" +
//                "          \t\t\t     \t\t\"category\":\"DIVERSION\"\n" +
//                "          \t\t\t   \t\t},\n" +
//                "          \t\t\t   \t\t{\n" +
//                "                                  \"id\":\"1233123\",\n" +
//                "                                  \"description\":\"no se\",\n" +
//                "                                  \"price\":3.0,\n" +
//                "                                  \"category\":\"MERCADERIA\"\n" +
//                "                              },\n" +
//                "          \t\t\t\t\t{\n" +
//                "          \t\t\t     \t\t\"id\":\"1234343123\",\n" +
//                "          \t\t\t     \t\t\"description\":\"very long long long description of a product which has really a long long long description. Not sure what will happen\",\n" +
//                "          \t\t\t     \t\t\"price\":22.0,\n" +
//                "          \t\t\t     \t\t\"category\":\"SERVICIOS\"\n" +
//                "          \t\t\t   \t\t}\n" +
//                "\n" +
//                "          \t\t\t  \t\t]\n" +
//                "          \t\t\t\t}\n" +
//                "          \t  \t\t ]\n" +
//                "          \t\t}\n" +
//                "\n" +
//                "]}";

        if (jsonString != null && !jsonString.isEmpty()){
            purchasesContainer = purchaseHandler.getPurchasesFromJson(jsonString);
            renderList(purchasesContainer);
        }
    }

    private void renderList(PurchasesByMonthContainer purchasesContainer) {

        ExpandableListView listView = getListView();
        MyExpandableListAdapter adapter = getListViewAdapter(purchasesContainer);
        adapter.setParent(listView);
        listView.setAdapter(adapter);

        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        Map<Integer, PurchasesGroup> purchases = purchaseHandler.getSortedPurchasesGroups(purchasesContainer.getPurchasesByMonth());

        float accumPurchases = 0;

        for (PurchasesGroup group : purchases.values()) {
            accumPurchases += group.getPurchasesTotalPrice();
        }

        float purchasesAverage = 0;
        if (purchases.size()!= 0) {
            purchasesAverage = accumPurchases / purchases.size();
        }
        TextView average = (TextView) findViewById(R.id.average);
        TextView accumulated = (TextView) findViewById(R.id.accumulated);

        average.setText(String.format("Promedio mensual: $%.2f", purchasesAverage));
        accumulated.setText(String.format("Acumulado: $%.2f", accumPurchases));

        if(purchases.values().size() > 0 ) {
            listView.expandGroup(purchases.values().size() - 1); //Expand the last month
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_toolbar_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderPurchasesList();
    }
}
