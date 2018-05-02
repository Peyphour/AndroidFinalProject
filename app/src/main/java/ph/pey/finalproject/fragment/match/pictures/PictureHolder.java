package ph.pey.finalproject.fragment.match.pictures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PictureHolder {

    public static final List<Picture> ITEMS = new ArrayList<Picture>();

    public static final Map<String, Picture> ITEM_MAP = new HashMap<String, Picture>();

    public static void addItem(Picture item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void clear() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    public static class Picture {
        public final String id;
        public final String path;

        public Picture(String id, String content) {
            this.id = id;
            this.path = content;
        }

        @Override
        public String toString() {
            return path;
        }
    }
}
