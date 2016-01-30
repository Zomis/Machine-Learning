package net.zomis.machlearn.images

class MyGroovyUtils {

    static String text(URL url) {
        if (url == null) {
            return null;
        }
        return url.text;
    }

}
