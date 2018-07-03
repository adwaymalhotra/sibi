package com.sibi.model;

/**
 * Created by adway on 08/12/17.
 */

public class YouTubeItem {

    public Snippet snippet;
    public Id id;

    public class Id {
        public String videoId;
    }

    public class Snippet {
        public String title;
        public Thumbnails thumbnails;

        public class Thumbnails {
            public High high;

            public class High {
                public String url;
            }
        }
    }
}
