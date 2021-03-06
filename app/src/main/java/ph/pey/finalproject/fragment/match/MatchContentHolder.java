/**
 * By Bertrand NANCY and Kevin NUNES
 * Copyright 2018
 */

package ph.pey.finalproject.fragment.match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.pey.finalproject.sql.MatchEntity;


public class MatchContentHolder {


    public static final List<MatchEntity> ITEMS = new ArrayList<>();

    public static final Map<Integer, MatchEntity> ITEM_MAP = new HashMap<>();

    public static void addItem(MatchEntity item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getUid(), item);
    }

    public static void clear() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }
}
