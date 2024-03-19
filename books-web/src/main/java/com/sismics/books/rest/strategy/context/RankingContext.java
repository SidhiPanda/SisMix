package com.sismics.books.rest.strategy.context;

import com.sismics.books.rest.strategy.strategies.RankingStrategy;

import org.codehaus.jettison.json.JSONObject;

import java.util.List;

public class RankingContext {
    private RankingStrategy rankingStrategy;

    public RankingContext () {
    }

    public void setRankingStrategy(RankingStrategy rankingStrategy) {
        this.rankingStrategy = rankingStrategy;
    }

    public RankingStrategy getRankingStrategy() {
        return rankingStrategy;
    }

    public List<JSONObject> getRankedBooks(String arg, String userId) throws Exception {
        return rankingStrategy.getRankedBooks(arg, userId);
    }
}
