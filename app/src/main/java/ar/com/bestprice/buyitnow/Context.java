package ar.com.bestprice.buyitnow;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Created by ivan on 19/04/16.
 */
public class Context {

    private static Context context = new Context();
    private String userEmail = "mayname@gmail.com.ar";
    private String pass = "password";
    private String userSignInToken = "";

    private String serviceURL = "http://ec2-52-42-147-180.us-west-2.compute.amazonaws.com:8080/catalog";

    private Context(){}

    public static Context getContext(){
        return context;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public String getUserEmail(){
        return userEmail;
    }

    public String getPass(){
        return pass;
    }

    public void setServiceURL(String URL) {
        this.serviceURL = URL;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }


    public String getSha1()
    {

        String userLogin[] = getUserEmail().split("@");
        String user = userLogin[0];
        String mail = userLogin[1];

        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            String credentials = user + ":" + getPass() + "@" + mail ;
            crypt.update(credentials.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }
    public static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public String getUserSignInToken() {
        return userSignInToken;
    }

    public void setUserSignInToken(String userSignInToken) {
        this.userSignInToken = userSignInToken;
    }
}
