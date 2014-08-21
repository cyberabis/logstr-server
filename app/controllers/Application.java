package controllers;

import java.net.UnknownHostException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoURI;

import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

  @BodyParser.Of(BodyParser.Json.class)
  public static Result post() {
    JsonNode req = request().body().asJson();
    if (req == null) {
      return badRequest("Expecting Json data");
    } else {
      ArrayNode logs = (ArrayNode) req;
      MongoURI mongoURI = new MongoURI(System.getenv("MONGOHQ_URL"));
      DB db = null;
      try {
        db = mongoURI.connectDB();
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
      db.authenticate(mongoURI.getUsername(), mongoURI.getPassword());
      DBCollection coll = db.getCollection("logstr");

      for (int i = 0; i < logs.size(); i++) {
        String log = logs.get(i).asText();
        //Going to save to mongo
        coll.insert(new BasicDBObject("log", log));
      }
      return ok();
    }
  }


}
