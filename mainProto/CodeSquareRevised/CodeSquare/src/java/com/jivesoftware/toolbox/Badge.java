package com.jivesoftware.toolbox;

/**
 * Badge class used to store the badge info needed for activity stream post.
 * The badgesList being passed into the makeJsonPost() function should be a list of these objects.
 * Definitely import this class.
 * 
 * @author eric.ren
 *
 */

public class Badge {

    private String title = "";
    private String desc = "";
    private String imgUrl = "http://apphosting.jivesoftware.com/apps/dev/codesquareapp/images/";
    
    public Badge(String title, String desc, String imgName) {
	this.title = title;
	this.desc = desc;
	this.imgUrl = imgUrl + imgName;
    }
    
    public String getTitle() {
	return title;
    }
    
    public String getDesc() {
	return desc;
    }

    public String getImgLink() {
	return imgUrl;
    }


}