package ar.com.bestprice.buyitnow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ar.com.bestprice.buyitnow.dto.Item;
import ar.com.bestprice.buyitnow.dto.Purchase;

public class PurchasesGroup {

    private Month month;

    private Map<String, Purchase> time_purchases = new HashMap<>();
    //private final List<Item> children = new ArrayList<>();
    private final List<Purchase> purchases = new ArrayList<>();


    public PurchasesGroup(Month month) {
        this.month = month;
    }

    /*public void addItem(Item item){
        children.add(item);
        purchasesTotalPrice += item.getPrice();
    }*/

    public List<Purchase> getChildren() {
        return purchases;
    }

    public String getString() {
        return month.toString();
    }

    public Month getMonth() {
        return month;
    }


    public Double getPurchasesTotalPrice() {
        Double purchasesTotalPrice = 0D;
        for(Purchase p: purchases){
            purchasesTotalPrice += p.getTotalPrice();
        }
        return purchasesTotalPrice;
    }


    public void addPurchase(Purchase purchase){
        time_purchases.put(purchase.getTime(), purchase);
        purchases.add(purchase);
    }

    public Purchase getPurchase(String time){
        return time_purchases.get(time);
    }

    public void removePurchaseAt(int childPosition) {

        /*Purchase item = children.remove(childPosition);
        Purchase purchase = time_purchases.get(item.getTime());
        purchase.removeItem(item);*/

        purchases.remove(childPosition);

        //purchasesTotalPrice -= item.getPrice();
    }

   /* public Item getItemAt(int childPosition) {
        return children.get(childPosition);
    }*/

    public Purchase getPurchaseAt(int childPosition) {
        return purchases.get(childPosition);
    }
}
