package cn.gongyinan.huobiinfo.web;

import cn.gongyinan.huobiinfo.task.HuoBiTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
public class WebController {

    @Autowired
    private HuoBiTask huoBiTask;

    @RequestMapping("/")
    @ResponseBody
    public String h() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<h2>服务器时间：" + new Date() + "</h2>");
        stringBuilder.append("<a href='log' target='_blank'>运行日志</a>");
        stringBuilder.append(huoBiTask.getRecordHtml());

        return stringBuilder.toString();
    }


    @RequestMapping(value = "/json", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String detail(@RequestParam() String filename, HttpServletResponse response) {
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        return huoBiTask.getJson(filename);
    }

    @RequestMapping(value = "/log", produces = "text/plain")
    @ResponseBody
    public String log(HttpServletResponse response) {
        response.setHeader("Content-Type", "text/plain");
        return huoBiTask.getLog();
    }
}
