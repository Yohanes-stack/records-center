package org.example.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 */
public class Result extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	
	public Result() {
		put("code", "0000");
		put("msg", "success");
	}
	
	public static Result error() {
		return error(1000, "未知异常，请联系管理员");
	}
	
	public static Result error(String msg) {
		return error(1000, msg);
	}
	
	public static Result unLoginError() {
		return error(1001, "用户信息丢失，请重新登陆！");
	}
	
	public static Result error(int code, String msg) {
		Result r = new Result();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static Result ok(String msg) {
		Result r = new Result();
		r.put("code", "0000");
		r.put("msg", msg);
		return r;
	}
	
	public static Result ok(Map<String, Object> map) {
		Result r = new Result();
		r.putAll(map);
		return r;
	}
	
	public static Result ok(Object object) {
		Result r = new Result();
		r.put(object);
		return r;
	}
	
	public static Result ok() {
		return new Result();
	}

	@Override
	public Result put(String key, Object value) {
		super.put(key, value);
		return this;
	}
	
	public Result put(Object value) {
		super.put("data", value);
		return this;
	}
}