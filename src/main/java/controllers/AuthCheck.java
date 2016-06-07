package controllers;

import com.google.inject.Inject;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Ninja;
import ninja.Result;
import ninja.utils.NinjaTestBrowser;

/**
 * Created by vgoryachev on 17.05.2016.
 * Package: controllers.
 */
public class AuthCheck implements Filter {
    /**
     * If a username is saved we assume the session is valid
     */
    public static final String USERNAME = "username";

    private final Ninja ninja;

    @Inject
    public AuthCheck(Ninja ninja) {
        this.ninja = ninja;
    }

    @Override
    public Result filter(FilterChain chain, Context context) {

        // if we got no cookies we break:
        //public NinjaTestServer ninjaTestServer;
        NinjaTestBrowser ninjaTestBrowser;
        ninjaTestBrowser = new NinjaTestBrowser();
        String response = ninjaTestBrowser.makeJsonRequest("http://192.168.157.73"
                + "/api/account/current");

        System.out.println("****** NINJA: SPRING RESPONSE: " + response);

        if (context.getSession() == null
                || context.getSession().get(USERNAME) == null) {

            Result result = ninja.getForbiddenResult(context);
            return result;

        } else {

            System.out.println("****** NINJA: CONTEXT TOKEN: " + context.getSession().getAuthenticityToken());
            System.out.println("****** NINJA: CONTEXT USER: " + context.getSession().get(USERNAME));
            return chain.next(context);
        }

    }
}
