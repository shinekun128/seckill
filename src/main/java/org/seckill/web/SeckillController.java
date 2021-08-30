package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.seckill.service.impl.SeckillServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckill")//url:/模块/资源/{id}/细分    /seckill/list
public class SeckillController {

    private Logger logger = LoggerFactory.getLogger(SeckillController.class);

    @Resource
    private SeckillService seckillService;

    @RequestMapping(value="list",method = RequestMethod.GET)
    public String list(Model model){
        List<Seckill> seckillList = seckillService.getSeckillList();
        model.addAttribute("list",seckillList);
        return "list";
    }

    @RequestMapping(value="/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
        if(seckillId == null){
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if(seckill == null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }


    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result ;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);
        } catch (Exception exception) {
            logger.error(exception.getMessage(),exception);
            result = new SeckillResult<Exposer>(false,exception.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=utf-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone" , required = false) Long phone){

        if(phone == null){
            return new SeckillResult<SeckillExecution>(false,"用户未注册");
        }
        try {
            SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        } catch (RepeatKillException e){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL,null);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        } catch (SeckillCloseException e){
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.END,null);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        } catch (SeckillException e) {
            logger.error(e.getMessage(),e);
            SeckillExecution seckillExecution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR,null);
            return new SeckillResult<SeckillExecution>(true,seckillExecution);
        }
    }

    @RequestMapping(value = "/time/now" , method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date dateNow = new Date();
        return new SeckillResult<Long>(true,dateNow.getTime());
    }

}
