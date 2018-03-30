package com.other.utils.httprequest;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.client.fluent.Request;
import org.junit.Test;

/**
 * Created by jiaqi.ajq on 2017/8/4.
 */
public class DruidSearchRequestTest {

    @Test
    public void searchRequest() {
       /* String url
                = "http://eternal.proxy.taobao.org/commonOpenApi.do?function=showBucketTraceMetric&parameters=%s";


        String str = "cluster#re_direct;timeout#1200000;exper#re_favor;start_time#%s;end_time#%s;show_precision#60;"
                + "show_type#show_frame;metric_list#%s;dimension_list#%s;bucket_list#all";

        String param = String.format(str, "2017-07-31 00:00", "", "item_pv,ipv", "scene_id=14551");*/


        String url
                = "http://matrix-druid.alibaba-inc.com/commonOpenApi.do?function=showBucketTraceMetric&parameters=%s";
        String str = "cluster#matrix_sensor;timeout#1200000;exper#sensor_default;start_time#%s;end_time#%s;show_precision#60;"
                + "show_type#show_frame;metric_list#%s;dimension_list#%s;bucket_list#all";
        String param = String.format(str, "2017-07-31 00:00", "", "pv,click", "scene_id=14551");

        try {
            String apiRequestStr = String.format(url, URLEncoder.encode(param, "utf-8"));
            System.out.println(apiRequestStr);
            String resultStr = Request.Get(apiRequestStr)
                    .connectTimeout(2000)
                    .socketTimeout(5000)
                    .execute().returnContent().asString();
            System.out.println(resultStr);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
