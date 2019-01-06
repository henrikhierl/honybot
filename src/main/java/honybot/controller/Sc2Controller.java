package honybot.controller;

import honybot.bot.settings.controller.SC2SettingsController;
import honybot.post.StarcraftPostHandler;
import org.json.simple.JSONObject;

import static spark.Spark.get;
import static spark.Spark.post;

public class Sc2Controller {

    public static void loadRoutes () {
        post("/sc2", (req, res) -> {
            System.out.println("sc2 trying to authenticate");
            res.type("application/json");
            String token = req.queryParams("token");
            String username = SC2SettingsController.getUsernameByAuth(token);
            JSONObject response = new JSONObject();
            if(username.equalsIgnoreCase("")){
                response.put("success", false);
            }else{
                response.put("success", true);
                response.put("message", "authorized as " + username);
            }
            return response;
        });

        post("/sc2/bet", (req, res) -> {
            res.type("application/json");
            StarcraftPostHandler.handleBet(req);
            return "{}";
        });

        post("/sc2/game", (req, res) -> {
            res.type("application/json");
            StarcraftPostHandler.handleGame(req);
            return "{}";
        });

        get("/sc2/session", (req, res) -> {
            res.type("application/json");
            return StarcraftPostHandler.getSession(req);
        });

        post("/sc2/session", (req, res) -> {
            res.type("application/json");
            return StarcraftPostHandler.increaseSession(req);
        });

        post("/sc2/status", (req, res) -> {
            res.type("application/json");
            StarcraftPostHandler.handleStatusUpdate(req);
            return "{}";
        });
    }

}
