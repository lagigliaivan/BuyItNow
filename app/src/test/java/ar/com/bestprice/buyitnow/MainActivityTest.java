package ar.com.bestprice.buyitnow;

import android.widget.TextView;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.bestprice.buyitnow.dto.Purchase;
import ar.com.bestprice.buyitnow.dto.PurchasesByMonthContainer;

/**
 * Created by ivan on 09/10/16.
 */
public class MainActivityTest {




    @Test
    public void test(){
        MainActivity activity = new MainActivity();
    }



    @Test
    public void test2(){

        String date = "2016-12-01T12:20:01Z";


        PurchaseItemsListActivity activity = new PurchaseItemsListActivity();
        String purchaseDate = null;

        try {
            purchaseDate = activity.formatPurchaseDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
            Assert.fail();
        }


        Assert.assertEquals(purchaseDate, "01/12/2016 (12:20)");
        System.out.print(purchaseDate);
    }
}
