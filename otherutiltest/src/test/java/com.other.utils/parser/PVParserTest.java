package com.other.utils.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.taobao.loganalyzer.input.p4ppv.parser.P4PPVLog;
import com.taobao.loganalyzer.input.p4ppv.parser.P4PPVLog.ADSection;
import com.taobao.loganalyzer.input.p4ppv.parser.P4PPVLogParser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by jiaqi.ajq on 2017/8/4.
 */
public class PVParserTest {

    private static final String fileName = "k2-pv-test-string";
    private static List<String> inputStringList = new LinkedList<String>();

    @BeforeClass
    public static void initInputString() {
        String path = "src//test//resources//" + fileName;
        FileReader fr;
        BufferedReader br;
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String str = br.readLine();
            while(str != null) {
                inputStringList.add(str);
                str = br.readLine();
            }
            br.close();
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseTest() {
        for(String data : inputStringList) {
            P4PPVLog pvLog = P4PPVLogParser.parse(data);
            Long timestamp = Long.parseLong(pvLog.getTimestamp());
            System.out.println("timestamp : " + timestamp);
            String sessionId = pvLog.getSessionID();
            System.out.println("sessionid : " + sessionId);
            String nickUnicode = pvLog.getWangWangID();
            System.out.println("nick : " + nickUnicode);
            //String nick = UnicodeUtil.unicodeToString(nickUnicode);
            List adsectionList = pvLog.getAdList();
            for(Object object : adsectionList) {
                ADSection adSection = (ADSection) object;
                System.out.println("service id :" + adSection.getServiceID());
            }
            System.out.println();
        }
    }
}
