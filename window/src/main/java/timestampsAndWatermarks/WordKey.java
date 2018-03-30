package timestampsAndWatermarks;

/**
 * Created by jiaqi.ajq on 2017/6/16.
 */
public class WordKey {
    private String key;
    private String back;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    @Override
    public boolean equals(Object obj) {
        return ((WordKey) obj).getKey().equals(key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
