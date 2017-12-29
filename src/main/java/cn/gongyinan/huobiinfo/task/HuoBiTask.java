package cn.gongyinan.huobiinfo.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@Component
public class HuoBiTask {

    private int lastId = 0;
    private int lastPrice = 0;

    @Scheduled(cron = "0/5 * *  * * ? ")
    private void fetch() {
        String url = "https://api-otc.huobi.pro/v1/otc/trade/list/public?coinId=1&tradeType=1&currentPage=1&payWay=&country=&currPage=1";
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            String json = okHttpClient.newCall(new Request.Builder().url(url).build()).execute().body().string();
            JSONObject jsonObject = JSONObject.parseObject(json);
            if (jsonObject.getInteger("code") == 200) {
                FileUtils.write(new File("log.txt"), new Date() + "抓取成功\r\n", true);
                String logFile = System.currentTimeMillis() + ".json";
                FileUtils.write(new File("json/" + logFile), json, true);
                JSONArray data = jsonObject.getJSONArray("data");
                int diff = data.getJSONObject(1).getInteger("fixedPrice") - data.getJSONObject(0).getInteger("fixedPrice");
                if (diff > 1000 && (data.getJSONObject(1).getInteger("id") != lastId || data.getJSONObject(0).getInteger("fixedPrice") != lastPrice)) {
                    FileUtils.write(new File("record.html"), "<p><span>" + new Date() + "</span><span>前两笔差值：</span> <span class='diff'>" + diff + "</span> 价格:" + data.getJSONObject(0).getInteger("fixedPrice") + "  <a href='json?filename=" + logFile + "' target='_blank'>详细Json记录</a> <a href='https://otc.huobi.pro/#/tradeInfo?id=" + data.getJSONObject(0).getInteger("id") + "'>交易地址</a></p>\r\n", true);
                }
                lastPrice = data.getJSONObject(0).getInteger("fixedPrice");
                lastId = data.getJSONObject(1).getInteger("id");
            } else {
                FileUtils.write(new File("log.txt"), new Date() + "抓取失败 code:" + jsonObject.getInteger("code") + "\r\n");
            }
        } catch (Exception e) {
            System.out.println(new Date().toString());
            e.printStackTrace();
            try {
                FileUtils.write(new File("log.txt"), new Date() + "抓取失败");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    public String getRecordHtml() {
        try {
            return FileUtils.readFileToString(new File("record.html"));
        } catch (IOException e) {
            return "读取失败";
        }
    }

    public String getLog() {
        try {
            return FileUtils.readFileToString(new File("log.txt"));
        } catch (IOException e) {
            return "读取失败";
        }
    }


    public String getJson(String fileName) {
        try {
            return FileUtils.readFileToString(new File("json/" + fileName));
        } catch (IOException e) {
            return "读取失败";
        }
    }

}
