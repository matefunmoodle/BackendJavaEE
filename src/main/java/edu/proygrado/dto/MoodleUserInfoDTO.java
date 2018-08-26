package edu.proygrado.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MoodleUserInfoDTO {
	private String sitename;
	private String username;
	private String firstname;
	private String lastname;
	private String fullname;
	private String lang;
	private Long userid;	
	private String siteurl;
	private String userpictureurl;
	private List<Object> functions;
	private Long downloadfiles;
	private Long uploadfiles;
	private String release;
	private String version;
	private String mobilecssurl;
	private List<Object> advancedfeatures;
	private Boolean usercanmanageownfiles;
	private Long userquota;
	private Long usermaxuploadfilesize;
	private Long userhomepage;
	private Long siteid;
	
	public String getSitename() {
		return sitename;
	}
	public void setSitename(String sitename) {
		this.sitename = sitename;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public Long getUserid() {
		return userid;
	}
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	public String getSiteurl() {
		return siteurl;
	}
	public void setSiteurl(String siteurl) {
		this.siteurl = siteurl;
	}
	public String getUserpictureurl() {
		return userpictureurl;
	}
	public void setUserpictureurl(String userpictureurl) {
		this.userpictureurl = userpictureurl;
	}
	public List<Object> getFunctions() {
		return functions;
	}
	public void setFunctions(List<Object> functions) {
		this.functions = functions;
	}
	public Long getDownloadfiles() {
		return downloadfiles;
	}
	public void setDownloadfiles(Long downloadfiles) {
		this.downloadfiles = downloadfiles;
	}
	public Long getUploadfiles() {
		return uploadfiles;
	}
	public void setUploadfiles(Long uploadfiles) {
		this.uploadfiles = uploadfiles;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMobilecssurl() {
		return mobilecssurl;
	}
	public void setMobilecssurl(String mobilecssurl) {
		this.mobilecssurl = mobilecssurl;
	}
	public List<Object> getAdvancedfeatures() {
		return advancedfeatures;
	}
	public void setAdvancedfeatures(List<Object> advancedfeatures) {
		this.advancedfeatures = advancedfeatures;
	}
	public Boolean getUsercanmanageownfiles() {
		return usercanmanageownfiles;
	}
	public void setUsercanmanageownfiles(Boolean usercanmanageownfiles) {
		this.usercanmanageownfiles = usercanmanageownfiles;
	}
	public Long getUserquota() {
		return userquota;
	}
	public void setUserquota(Long userquota) {
		this.userquota = userquota;
	}
	public Long getUsermaxuploadfilesize() {
		return usermaxuploadfilesize;
	}
	public void setUsermaxuploadfilesize(Long usermaxuploadfilesize) {
		this.usermaxuploadfilesize = usermaxuploadfilesize;
	}
	public Long getUserhomepage() {
		return userhomepage;
	}
	public void setUserhomepage(Long userhomepage) {
		this.userhomepage = userhomepage;
	}
	public Long getSiteid() {
		return siteid;
	}
	public void setSiteid(Long siteid) {
		this.siteid = siteid;
	}	
}
