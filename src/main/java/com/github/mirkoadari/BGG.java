package com.github.mirkoadari;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class BGG {

  private final String URL = "https://boardgamegeek.com/xmlapi/";
  private final Map<String, List<BoardGame>> searchCache = new HashMap<String, List<BoardGame>>();
  private final Map<String, BoardGame> gameCache = new HashMap<String, BoardGame>();

  public BGG() {
  }

  public Node call(String s) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      // System.out.println("URL: " + URL + s);
      Document doc = db.parse(new URL(URL + s).openStream());
      return doc;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<BoardGame> searchCache(String prefix) {
    List<BoardGame> r = searchCache.get(prefix);
    // TODO smarter cache search from substrings
    if (r == null) {
      searchCache.put(prefix, r = search(prefix));
    }
    return r;
  }

  private List<BoardGame> search(String prefix) {
    List<BoardGame> result = new ArrayList<BoardGame>();
    Node n = call("search?boardgame=&search=" + prefix);
    n = n.getFirstChild();// boardgames
    for (int i = 0; i < n.getChildNodes().getLength(); i++) {
      Node child = n.getChildNodes().item(i);
      if ("boardgame".equals(child.getNodeName())) {
        result.add(new BoardGame(child));
      }
    }
    return result;
  }

  public BoardGame getGameCache(String game) {
    BoardGame r = gameCache.get(game);
    if (r == null) {
      gameCache.put(game, r = getGame(game));
    }
    return r;
  }

  private BoardGame getGame(String game) {
    Node n = call("boardgame/" + game);
    n = n.getFirstChild();// boardgames
    n = n.getFirstChild();// boardgames
    n = n.getNextSibling();
    return new BoardGame(n);
  }

  public static class BoardGame extends HashMap<String, String> {
    private static final long serialVersionUID = 1L;
    private final String id;

    public BoardGame(Node g) {
      if ("boardgame".equals(g.getNodeName())) {
        id = g.getAttributes().getNamedItem("objectid").getNodeValue();
        for (int j = 0; j < g.getChildNodes().getLength(); j++) {
          if (g.getChildNodes().item(j).getChildNodes().getLength() == 1) {
            put(g.getChildNodes().item(j).getNodeName(), g.getChildNodes().item(j).getTextContent());
          }
        }
      }
      else {
        id = "0";
      }
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return get("name");
    }

    public String getYearpublished() {
      return get("yearpublished");
    }

    public String getMinplayers() {
      return get("minplayers");
    }

    public String getMaxplayers() {
      return get("maxplayers");
    }

    public String getAge() {
      return get("age");
    }

    public String getThumbnail() {
      return get("thumbnail");
    }

    public String getImage() {
      return get("image");
    }

    public String toString() {
      return getName() + " (" + getYearpublished() + ")";
    }
  }
}