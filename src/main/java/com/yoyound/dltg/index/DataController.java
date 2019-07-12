package com.yoyound.dltg.index;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DataController extends Controller {
	public void index() {
		List<Record> goods= Db.find("select id,name,artNo,logo,price,market_price ,remarks,end_date,spec1,spec2,(select count(o.id) from dl_order_tuan o where o.goods_id=t.id)as orders  from dl_goods_tuan t where state='1' and del_flag='0' and now() BETWEEN begin_date and end_date order by end_date asc ");
		for (Record r:goods){
			if(r.getStr("logo").indexOf("http")<0){
				r.set("logo","http://image.yoyound.com"+r.getStr("logo"));
			}
			Date end=r.getDate("end_date");
			r.set("time",getDatePoor(end,new Date()));
		}
		renderJson(goods);
	}
	public void getGoodById() {
		String id=getPara("id");
		 Record r= Db.findFirst("select id,name,artNo,logo,price,market_price ,imgs,remarks,end_date,details,spec1,spec2,(select count(o.id) from dl_order_tuan o where o.goods_id=t.id)as orders  from dl_goods_tuan t where state='1' and del_flag='0' and now() BETWEEN begin_date and end_date and id=?",id);

		if(r.getStr("logo").indexOf("http")<0){
			r.set("logo","http://image.yoyound.com"+r.getStr("logo"));
		}
        List<Record> imgs=new ArrayList<>();
		String []is=r.getStr("imgs").split("\\|");
		for (String s:is){
			Record img=new Record();
			if(StrKit.notBlank(s)){
				if(s.indexOf("http")<0){
					img.set("url","http://image.yoyound.com"+s);
				}
				else{
					img.set("url",s);
				}
				imgs.add(img);
			}

		}
		List<Record> skus=Db.find("select spec1,spec2,stock from dl_goods_sku_tuan where goods_id=? ",id);
		Date end=r.getDate("end_date");
        r.set("time",getDatePoor(end,new Date()));
		r.set("urls",imgs);
		r.set("skus",skus);
		r.set("furl",imgs.get(0).getStr("url"));
		r.set("eurl",imgs.get(imgs.size()-1).getStr("url"));
		r.set("urll",imgs.size()-1);
		renderJson(r);
	}
	public static String getDatePoor(Date endDate, Date nowDate) {

		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - nowDate.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		long hour = diff % nd / nh;
		// 计算差多少分钟
		long min = diff % nd % nh / nm;
		// 计算差多少秒//输出结果
		// long sec = diff % nd % nh % nm / ns;
		return day + "天" + hour + "小时" + min + "分钟";
	}
	public void saveOrder() {
		String goodsId=getPara("goodsId");
		String phone=getPara("phone");
		String consignee=getPara("consignee");
		String address=getPara("address");
		String spec1=getPara("spec1");
		String spec2=getPara("spec2");
		String number=getPara("number");
		String userName=getPara("userName");
		String mobile=getPara("mobile");
		String name=getPara("name");
		String artNo=getPara("artNo");
        boolean f=true;
        Record r=new Record();
        r.set("code",1);
        r.set("msg","拼团成功,等待客服联系确认。");
		Record good= Db.findFirst("select id,name,artNo,logo,price,market_price ,imgs,remarks,end_date,details,spec1,spec2,(select count(o.id) from dl_order_tuan o where o.goods_id=t.id)as orders  from dl_goods_tuan t where state='1' and del_flag='0' and now() BETWEEN begin_date and end_date and id=?",goodsId);
        if(null==good){
			f=false;
			r.set("msg","商品已过期或者已下架");
		}
		if(StrKit.isBlank(goodsId)){
        	f=false;
			r.set("msg","商品没有选择");
		}
		if(StrKit.isBlank(phone)){
			f=false;
			r.set("msg","收件人电话不能为空");
		}
		if(StrKit.isBlank(consignee)){
			f=false;
			r.set("msg","收件人不能为空");
		}
		if(StrKit.isBlank(address)){
			f=false;
			r.set("msg","收件地址不能为空");
		}
		if(StrKit.isBlank(spec1)){
			f=false;
			r.set("msg","规格1必须填写");
		}
		 if(StrKit.isBlank(number)){
			f=false;
			r.set("msg","购买数量不能为空");
		}
		if(StrKit.isBlank(userName)){
			f=false;
			r.set("msg","会员姓名不能为空");
		}
		if(StrKit.isBlank(mobile)){
			f=false;
			r.set("msg","会员电话不能为空");
		}
		String id = UUID.randomUUID().toString().replaceAll("-", "");
		try {
			Db.update("INSERT INTO  dl_order_tuan(`id`,`name`, `artNo`, `phone`, `consignee`, `address`,  `spec1`, `spec2`,   `goods_id`,number,create_date,userName,mobile) " +
							"VALUES (?,?,?,?,?,?,?,?,?,?,now(),?,?)",
					id, name, artNo, phone, consignee, address, spec1, spec2, goodsId, number, userName, mobile);

		}catch (Exception e){
			e.printStackTrace();
			r.set("msg","订单出现异常请联系客服");
		}
        if(!f){
			r.set("code",0);
		}
		renderJson(r);
	}
}



