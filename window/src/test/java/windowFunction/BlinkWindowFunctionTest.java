package windowFunction;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jiaqi.ajq on 2017/6/16.
 */
public class BlinkWindowFunctionTest {

    @Test
    public void test() {
        try {
            FileInputStream inputStream = new FileInputStream("123.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}