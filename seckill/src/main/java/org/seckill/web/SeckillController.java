package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by sunyinhui on 5/16/17.
 */
@Controller  // 放入到spring容器中
@RequestMapping("/seckill") // url:/模块/资源/{id}/细分
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    // 列表页
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<Seckill> seckillList  = seckillService.getSeckillList();
        model.addAttribute("list", seckillList);
        // list.jsp + model = ModelAndView
        return "list";
    }

    // 详情页
    @RequestMapping(value = "/{seckillId}/detail", method=RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {

        if (seckillId == null) {
            return "redirect:/seckill/list";
        }

        Seckill seckill = seckillService.getById(seckillId);

        if (seckill == null) {
            return "forward:/seckill/list";
        }

        model.addAttribute("seckill", seckill);
        return "detail";
    }

    // ajax接口,输出为json
    @RequestMapping(value = "/{seckillId}/exposer", method=RequestMethod.POST,
                    produces = {"application/json;charset=utf-8"} // 告诉浏览器context-type返回的是json数据
                    )
    @ResponseBody // 把返回类型封装成json
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (SeckillException e) {
            logger.info("exposer = {}", e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }

        return result;
    }

    // 执行秒杀ajax接口

    @RequestMapping(value = "/{seckillId}/{md5}/execution",method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId, @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone",required = false) Long userPhone) {

        if (userPhone == null ) {
            return new SeckillResult<SeckillExecution>(false , "未注册");
        }

        SeckillResult<SeckillExecution> result;
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
            result = new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch(RepeatKillException repeat) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            result =  new SeckillResult<SeckillExecution>(true, seckillExecution);
        } catch (SeckillCloseException close){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            result = new SeckillResult<SeckillExecution>(true, seckillExecution);
        }catch (SeckillException e) {
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            result = new SeckillResult<SeckillExecution>(true, seckillExecution);
        }
        return result;
    }


    // 获取系统时间
    @RequestMapping(value="/time/now", method=RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        return new SeckillResult<Long>(true, now.getTime());
    }


}
