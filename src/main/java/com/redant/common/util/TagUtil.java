package com.redant.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 记录两个标签之间所耗的时间
 * @author gris.wang
 *
 */
public class TagUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(TagUtil.class);

	private static class GHandle{
		public static Map<String,Long> tags = Collections.synchronizedMap(new HashMap<String,Long>());
	}
	
	/**
	 * 新增标签点
	 * @param tag 标签名称
	 */
	public static void addTag(String tag){
		if(tag==null || tag.trim().length()==0){
			throw new RuntimeException("标签名称不可以为空");
		}
		GHandle.tags.put(tag, System.currentTimeMillis());
	}
	
	/**
	 * 计算开始标签和结束标签之间的耗时
	 * @param startTag 开始标签名称
	 * @param endTag 结束标签名称，如果为空，以当前调用代码所在行设置默认标签名称并计算耗时
	 */
	public static void showCost(String startTag,String endTag){
		if(startTag==null || startTag.trim().length()==0){
			throw new RuntimeException("开始标签名称不可以为空");
		}
		if(endTag==null || endTag.trim().length()==0){
			String tempTag= "cur_"+System.currentTimeMillis();
			addTag(tempTag);
			endTag=tempTag;
		}else if(!GHandle.tags.containsKey(endTag)){
			addTag(endTag);
		}
		Long start= GHandle.tags.get(startTag);
		Long end= GHandle.tags.get(endTag);
		if(start==null){
			throw new RuntimeException("获取标签["+startTag+"]信息失败!");
		}
		if(end==null){
			throw new RuntimeException("获取标签["+endTag+"]信息失败!");
		}
		long cost = end-start;
		logger.info("from ["+startTag+"] to ["+endTag+"] cost ["+cost+"ms]");
	}





}
