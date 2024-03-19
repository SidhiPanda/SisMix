package com.sismics.books.rest.command;

import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import com.sismics.books.rest.strategy.context.RankingContext;
import com.sismics.books.rest.strategy.strategies.AverageRankingStrategy;
import com.sismics.books.rest.strategy.strategies.NumberRankingStrategy;

public class RankingLibraryCommand implements LibraryCommand {
    
    @Override
    public List<JSONObject> getLibraryBooks(String type, String arg, String userId) throws Exception {
        RankingContext rankingContext = new RankingContext();
        switch (type) {
            case "average":
                rankingContext.setRankingStrategy(new AverageRankingStrategy());
                break;
            case "number":
                rankingContext.setRankingStrategy(new NumberRankingStrategy());
                break;
            default:
                rankingContext.setRankingStrategy(new AverageRankingStrategy());
                break;
        }
        return rankingContext.getRankedBooks(arg, userId);
    }
}
