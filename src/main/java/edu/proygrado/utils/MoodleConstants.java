package edu.proygrado.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoodleConstants {
	public static final String course_enroll_default_roleid = "1";
	public static final String privateFilesRootDir = "/";
	public static final List<String> rolesValidosDocente = Arrays.asList("teacher","editingteacher","manager");
	public static final List<String> rolesValidosAlumno = Arrays.asList("student");
	
	public static final List<String> rolesValidos = Stream.concat(rolesValidosDocente.stream(), rolesValidosAlumno.stream()).collect(Collectors.toList());
	
	
}
