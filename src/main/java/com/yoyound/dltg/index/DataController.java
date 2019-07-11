package com.yoyound.dltg.index;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class DataController extends Controller {
	public void index() {
		List<Record> goods= Db.find("select id,name,artNo,logo,price,market_price ,remarks,(select count(o.id) from dl_order_tuan o where o.goods_id=t.id)as orders  from dl_goods_tuan t where state='1' and del_flag='0' and date(now()) BETWEEN begin_date and end_date ");
		for (Record r:goods){
			if(r.getStr("logo").indexOf("http")<0){
				r.set("logo","http://image.yoyound.com"+r.getStr("logo"));
			}
		}
		renderJson(goods);
	}
}



