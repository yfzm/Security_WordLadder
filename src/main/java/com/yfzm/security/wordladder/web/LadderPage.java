package com.yfzm.security.wordladder.web;

import ch.qos.logback.classic.BasicConfigurator;
import com.yfzm.security.wordladder.beans.LadderBean;
import com.yfzm.security.wordladder.utils.WordLadder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class LadderPage {

    private static Logger LOGGER = LogManager.getLogger(LadderPage.class);

    @RequestMapping("/wordladder")
    @ResponseBody
    public LadderBean func(String begin, String end) {
//        BasicConfigurator.configure();

        LOGGER.info("Enter root page");
        LadderBean lb = new LadderBean();

        if (begin == null || end == null) {
            lb.setStatus(false);
            lb.setBegin("");
            lb.setEnd("");
            lb.setLadder(null);
            lb.setLength(0);
            return lb;
        }

        WordLadder wl = new WordLadder();
        wl.createLadder(begin, end);

        LOGGER.info("params: begin:" + begin + ", end:" + end);

        lb.setStatus(true);
        lb.setBegin(begin);
        lb.setEnd(end);
        lb.setLength(wl.getLadderStep());

        int length = wl.getLadderStep();
        if (length > 0) {
            lb.setLadder(new ArrayList<>(wl.getLadderStack()));
            LOGGER.info("Result: length:"+length + ", ladder:"+lb.getLadder().toString());

        } else {
            lb.setLadder(null);
            LOGGER.info("Result: No ladder found.");
        }

        return lb;
    }
}
