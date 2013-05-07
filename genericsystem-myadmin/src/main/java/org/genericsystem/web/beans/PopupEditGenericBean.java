//package org.genericsystem.web.beans;
//
//import java.io.Serializable;
//
//import javax.faces.bean.ViewScoped;
//import javax.inject.Named;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Named
//@ViewScoped
//public class PopupEditGenericBean implements Serializable {
//
//	private static final long serialVersionUID = -99280653395494512L;
//
//	protected static Logger log = LoggerFactory.getLogger(PopupEditGenericBean.class);
//
//	private String title;
//
//	private String input;
//
//	private String action;
//
//	public void modifyValueWindow(String key) {
//		log.info("modifyValueWindow " + key);
//		title = "#{msg.edit} #{msg.the} #{msg.value}";
//		input = "#{fileSystemBean.newValue}";
//		action = "#{fileSystemBean.modifyValue}";
//	}
//
//	public String getTitle() {
//		return title;
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//
//	public String getInput() {
//		return input;
//	}
//
//	public void setInput(String input) {
//		this.input = input;
//	}
//
//	public String getAction() {
//		return action;
//	}
//
//	public void setAction(String action) {
//		this.action = action;
//	}
//
// }
