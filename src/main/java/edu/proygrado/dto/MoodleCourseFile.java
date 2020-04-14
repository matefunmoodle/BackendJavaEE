package edu.proygrado.dto;

public class MoodleCourseFile {
	
	Integer courseid;
	Integer courseContentId;
	Integer moduleId;
	
	String filename;
	String fileurl;
	String mimetype;
	String author;
	
	Integer timecreated; 
	Integer timemodified;
	
	String content;
	
	public MoodleCourseFile() {
		super();
	}

	public MoodleCourseFile(Integer courseid, Integer courseContentId, Integer moduleId, String filename, String fileurl,
			String mimetype, String author, Integer timecreated, Integer timemodified, String content ) {
		super();
		this.courseid = courseid;
		this.courseContentId = courseContentId;
		this.moduleId = moduleId;
		this.filename = filename;
		this.fileurl = fileurl;
		this.mimetype = mimetype;
		this.author = author;
		this.timecreated = timecreated;
		this.timemodified = timemodified;
		this.content = content;
	}
	
	public Integer getCourseid() {
		return courseid;
	}

	public void setCourseid(Integer courseid) {
		this.courseid = courseid;
	}

	public Integer getCourseContentId() {
		return courseContentId;
	}

	public void setCourseContentId(Integer courseContentId) {
		this.courseContentId = courseContentId;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFileurl() {
		return fileurl;
	}

	public void setFileurl(String fileurl) {
		this.fileurl = fileurl;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Integer getTimecreated() {
		return timecreated;
	}

	public void setTimecreated(Integer timecreated) {
		this.timecreated = timecreated;
	}

	public Integer getTimemodified() {
		return timemodified;
	}

	public void setTimemodified(Integer timemodified) {
		this.timemodified = timemodified;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
