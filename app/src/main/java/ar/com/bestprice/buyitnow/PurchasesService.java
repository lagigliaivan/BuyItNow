package ar.com.bestprice.buyitnow;

import com.facebook.HttpMethod;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ar.com.bestprice.buyitnow.dto.Purchase;
import ar.com.bestprice.buyitnow.dto.Purchases;

/**
 * Created by elagiglia on 22/7/16.
 */
public class PurchasesService {

    final ExecutorService service = Executors.newFixedThreadPool(1);

    public int savePurchases(ArrayList<Purchase> ps){

        final Purchases purchases = new Purchases();
        purchases.setPurchases(ps);

        Future<Integer>  task = service.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {

                HttpURLConnection urlConnection = getURLConnection("/purchases", "POST");

                Gson gson = new Gson();
                String it = gson.toJson(purchases);

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(it);
                writer.flush();
                writer.close();
                os.close();

                return urlConnection.getResponseCode();

        }});

        Integer status = 0;
        try {
            status = task.get();
        } catch (final InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        } finally {
            service.shutdownNow();
        }

        return status;
    }

    public int deletePurchase(Purchase ps){

        final Purchase p = ps;

        Future<Integer>  task = service.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {

                HttpURLConnection urlConnection = getURLConnection("/purchases/" + p.getId(), "DELETE");
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.flush();
                writer.close();
                os.close();

                return urlConnection.getResponseCode();

            }});

        Integer status = 0;
        try {
            status = task.get();
        } catch (final InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        } finally {
            service.shutdownNow();
        }

        return status;
    }


    public String getPurchases() {

        Future<String>  task = service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception{

                HttpURLConnection urlConnection = getURLConnection("/purchases?groupBy=month", "GET");

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return "";
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return "";
                }

                return buffer.toString();
        }});

        String response = "";
        try {
            response =  task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            service.shutdown();
        }

        return response;
    }

    public String getPurchase(final String purchaseId) {

        Future<String>  task = service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception{

                HttpURLConnection urlConnection = getURLConnection("/purchases/" + purchaseId, "GET");

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return "";
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return "";
                }

                return buffer.toString();
            }});

        String response = "";
        try {
            response =  task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            service.shutdown();
        }

        return response;
    }


    private HttpURLConnection getURLConnection(String resource, String httpMethod) throws IOException {

        URL url = new URL(Context.getContext().getServiceURL() + "/users/" + Context.getContext().getSha1() + resource);
        HttpURLConnection   urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(httpMethod);
        urlConnection.setRequestProperty("Authorization", Context.getContext().getUserSignInToken());
        urlConnection.setRequestProperty("TokenType ", Context.getContext().getUserSignInType().name());

        //If http method is GET, then output does have not to be set.
        if("GET".compareTo(httpMethod) != 0) {
            urlConnection.setDoOutput(true);
        }

        urlConnection.setDoInput(true);
        urlConnection.connect();

        return urlConnection;
    }

}
