package com.ba.grain;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import util.FoodType;
import util.Grade;
import util.NewVersionApk;
import util.Site;
import util.User;
import util.XMLParser;

public class G {

	public static Socket sSocket;

	public static final NewVersionApk sNewVersionApk = new NewVersionApk();
	public static final List<Site> sSites = new ArrayList<Site>();

	public static final User sUser = new User();

	public static void initDatasFromNode(Node node, String usr, String pwd) {
		sSites.clear();
		Node n = node.getFirstChild();
		System.out.println("n"+n);
		while (n != null) {
			if (n.getNodeName().equals("Site")) {
				Site st = new Site();
				st.address = XMLParser.getAttrVal(n, "address", "");
				st.name = XMLParser.getAttrVal(n, "name", "");
				st.type = XMLParser.getAttrVal(n, "type", "");
				st.id = XMLParser.getAttrVal(n, "id", "0");
				st.area = XMLParser.getAttrVal(n, "area", "");
				st.linkPhone = XMLParser.getAttrVal(n, "linkPhone", "");
				st.fax = XMLParser.getAttrVal(n, "fax", "");
				//System.out.println(st.address+":"+st.name+":"+st.type+":"+st.id);
				Node fd = n.getFirstChild();
				while (fd != null) {
					if (fd.getNodeName().equals("FoodType")) {
						FoodType food = new FoodType();
						food.name = XMLParser.getAttrVal(fd, "name", "");
						food.grade_id = XMLParser.getAttrVal(fd, "grade_id", "0");
						food.id = XMLParser.getAttrVal(fd, "id", "0");
						//System.out.println(food.name+":"+food.grade_id+":"+food.id);
						Node gd = fd.getFirstChild();
						while (gd != null) {
							if (gd.getNodeName().equals("Grade")) {
								Grade grade = new Grade();
								grade.name = XMLParser.getAttrVal(gd, "name", "");
								grade.id = XMLParser.getAttrVal(gd, "id", "0");
								food.grades.add(grade);
							}
							gd = gd.getNextSibling();
						}

						st.foods.add(food);
					}
					fd = fd.getNextSibling();
				}
				G.sSites.add(st);
			} else if (n.getNodeName().equals("User")) {
				G.sUser.cnName = XMLParser.getAttrVal(n, "cnName", "");
				G.sUser.email = XMLParser.getAttrVal(n, "email", "");
				G.sUser.handPhone = XMLParser.getAttrVal(n, "handPhone", "");
				G.sUser.telephone = XMLParser.getAttrVal(n, "telephone", "");
				G.sUser.id =XMLParser.getAttrVal(n, "id", "0");
				G.sUser.department_id = XMLParser.getAttrVal(n, "department_id","0");
				G.sUser.loginName = usr;
				G.sUser.password = pwd;
			}
			n = n.getNextSibling();
		}
	}
}
