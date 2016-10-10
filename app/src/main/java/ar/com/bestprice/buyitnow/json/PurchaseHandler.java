package ar.com.bestprice.buyitnow.json;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import ar.com.bestprice.buyitnow.Category;
import ar.com.bestprice.buyitnow.Month;
import ar.com.bestprice.buyitnow.PurchasesGroup;
import ar.com.bestprice.buyitnow.dto.Item;
import ar.com.bestprice.buyitnow.dto.Purchase;
import ar.com.bestprice.buyitnow.dto.PurchasesByMonth;
import ar.com.bestprice.buyitnow.dto.PurchasesByMonthContainer;

/**
 * Created by ivan on 09/10/16.
 */
public class PurchaseHandler {


    public PurchasesByMonthContainer getPurchasesFromJson(String json){

        Gson gson = new Gson();
        PurchasesByMonthContainer p = gson.fromJson(json, PurchasesByMonthContainer.class);
        return p;
    }

    @NonNull
    public Map<Month, PurchasesByMonth> sortPurchasesByMonth(List<PurchasesByMonth> purchasesByMonth) {
        Map<Month, PurchasesByMonth> sortedPurchases = new HashMap<>();

        for (PurchasesByMonth purchases : purchasesByMonth) {

            sortedPurchases.put(Month.valueOf(purchases.getMonth().toUpperCase()), purchases);

        }
        return sortedPurchases;
    }


    public Map<Integer, PurchasesGroup> getSortedPurchasesGroups(List<PurchasesByMonth> purchasesByMonth) {

        Map<Month, PurchasesByMonth> sortedPurchases = sortPurchasesByMonth(purchasesByMonth);

        Map<Integer, PurchasesGroup> groups = new HashMap<>();

        int j = 0;
        for (Month month : Month.values()){

            if (sortedPurchases.get(month) != null){

                PurchasesGroup purchasesGroup = new PurchasesGroup(month);

                for (Purchase purchase : sortedPurchases.get(month).getPurchases()){

                    purchasesGroup.addPurchase(purchase);
                }
                groups.put(j, purchasesGroup);
                j++;
            }
        }

        return groups;

    }

    public List<PurchasesByMonth> searchStringInPurchases(String pattern, List<PurchasesByMonth> purchasesByMonths){

        List<PurchasesByMonth> byMonths = new ArrayList<>();

        Pattern p = Pattern.compile(Pattern.quote(pattern), Pattern.CASE_INSENSITIVE);

        for(PurchasesByMonth pByMonth : purchasesByMonths){

            PurchasesByMonth pByMonthWhereItemWasFound = null;

            for (Purchase purchase : pByMonth.getPurchases()){


                Purchase pWhereItemWasFound = null;

                for(Item item: purchase.getItems()){

                    if(p.matcher(item.getDescription()).find()){

                        if (pWhereItemWasFound == null){
                            pWhereItemWasFound = new Purchase();
                            pWhereItemWasFound.setTime(purchase.getTime());
                            pWhereItemWasFound.setId(purchase.getId());
                            pWhereItemWasFound.setShop(purchase.getShop());
                        }

                        pWhereItemWasFound.addItem(item);
                    }
                }

                if(pWhereItemWasFound != null){
                    if(pByMonthWhereItemWasFound == null) {
                        pByMonthWhereItemWasFound = new PurchasesByMonth();
                        pByMonthWhereItemWasFound.setMonth(pByMonth.getMonth());
                    }
                    pByMonthWhereItemWasFound.addPurchase(pWhereItemWasFound);
                }
            }

            if(pByMonthWhereItemWasFound != null) {
                byMonths.add(pByMonthWhereItemWasFound);
            }
        }

        return byMonths;
    }

    public List<PurchasesByMonth> getPurchasesByCategory(List<Category> categories, List<PurchasesByMonth> purchasesByMonths){

        List<PurchasesByMonth> byMonths = new ArrayList<>();


        for(PurchasesByMonth pByMonth : purchasesByMonths){

            PurchasesByMonth pByMonthWhereItemWasFound = null;

            for (Purchase purchase : pByMonth.getPurchases()){


                Purchase pWhereItemWasFound = null;

                for(Item item: purchase.getItems()){

                    if(categories.contains(item.getCategory())){

                        if (pWhereItemWasFound == null){
                            pWhereItemWasFound = new Purchase();
                            pWhereItemWasFound.setId(purchase.getId());
                            pWhereItemWasFound.setTime(purchase.getTime());
                            pWhereItemWasFound.setShop(purchase.getShop());
                        }

                        pWhereItemWasFound.addItem(item);
                    }
                }

                if(pWhereItemWasFound != null){
                    if(pByMonthWhereItemWasFound == null) {
                        pByMonthWhereItemWasFound = new PurchasesByMonth();
                        pByMonthWhereItemWasFound.setMonth(pByMonth.getMonth());
                    }
                    pByMonthWhereItemWasFound.addPurchase(pWhereItemWasFound);
                }
            }

            if(pByMonthWhereItemWasFound != null) {
                byMonths.add(pByMonthWhereItemWasFound);
            }
        }

        return byMonths;
    }


}
