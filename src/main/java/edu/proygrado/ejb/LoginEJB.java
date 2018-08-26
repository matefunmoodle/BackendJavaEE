/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.ejb;

import java.io.FileReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;
import javax.transaction.UserTransaction;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;

import edu.proygrado.dto.MoodleCourseDTO;
import edu.proygrado.dto.MoodleCoursesInfoDTO;
import edu.proygrado.dto.MoodleRoleDTO;
import edu.proygrado.dto.MoodleTokensDTO;
import edu.proygrado.dto.MoodleUserInfoDTO;
import edu.proygrado.dto.UsuarioDTO;
import edu.proygrado.matefun.MatefunException;
import edu.proygrado.matefun.MatefunForbiddenException;
import edu.proygrado.modelo.Alumno;
import edu.proygrado.modelo.Archivo;
import edu.proygrado.modelo.Configuracion;
import edu.proygrado.modelo.Docente;
import edu.proygrado.modelo.EstadoArchivo;
import edu.proygrado.modelo.Grupo;
import edu.proygrado.modelo.GrupoPK;
import edu.proygrado.modelo.Liceo;
import edu.proygrado.modelo.LiceoPK;
import edu.proygrado.modelo.Sesion;
import edu.proygrado.modelo.Usuario;

/**
 *
 * @author gonzalo
 */
@Stateless
@TransactionManagement(value=TransactionManagementType.BEAN)
public class LoginEJB {
	
	@Inject
	private ServletContext context;
	
	public LoginEJB() {
		super();
	}
	@PersistenceContext(unitName = "matefunDS")
	private EntityManager em;

	@EJB
	private InvitadoEJB invitadoEJB;
	
	@Resource
	private UserTransaction userTransaction;

	public boolean validarSesion(String token){
		Sesion sesion = em.find(Sesion.class,token);
		Date now = new Date();
		if(sesion != null && sesion.getTimestamp().getTime() > now.getTime()-60*60*1000){
			return true;		
		}
		return false;		
	}
	
	private void updateSession(String token,Usuario usuario){
		Sesion sesion = em.find(Sesion.class,token);
		if(sesion == null){
			sesion = new Sesion();
			sesion.setToken(token);
			sesion.setUsuario(usuario);
			sesion.setTimestamp(new Date());
			try {
				userTransaction.begin();
				em.persist(sesion);
				userTransaction.commit();
			}catch(Exception e) {
				System.out.println("Error persistiendo sesion: "+e.getMessage());
			}
			
		}else{
			sesion.setTimestamp(new Date());
		}
	}
	
	public void extendSession(String token){
		Sesion sesion = em.find(Sesion.class,token);
		if(sesion != null){
			sesion.setTimestamp(new Date());
		}
	}
	
	public void deleteExpiredSessions(){
		Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    cal.add(Calendar.HOUR_OF_DAY, -1);	    
		Date horaExpiracion = cal.getTime();
		List<Sesion> sesiones = em.createQuery("select s from Sesion s where s.timestamp < :horaExpiracion", Sesion.class)
				.setParameter("horaExpiracion", horaExpiracion)
				.getResultList();
		for(Sesion sesion:sesiones){
			em.remove(sesion);
		}
	}
	
	public UsuarioDTO login(String cedula, String password) throws MatefunException {
		Usuario usuario;

		TypedQuery<Usuario> query = em.createQuery("select u from Usuario u where u.cedula=:cedula", Usuario.class)
				.setParameter("cedula", cedula);
		try {
			usuario = query.getSingleResult();
			em.detach(usuario);
		} catch (Exception ex) {
			usuario = null;
			System.out.println("Usuario local no existe");
		}
		if (usuario != null && usuario.getPassword().equals(generateHash(password))) {
			System.out.println("Retorno usuario local");
			System.out.println(this.toString());
			String tokenAuth = generateToken();
			updateSession(tokenAuth, usuario);
			invitadoEJB.setUsuario(tokenAuth, usuario);
			return new UsuarioDTO(tokenAuth, usuario);
		}

		//Si no se encuentra configuracion para Moodle se notifica que las credenciales son incorrectas.
		if(getMoodleAPIEndpoint() == null || getMoodleDefaultGroup() == null){
			throw new MatefunException("Usuario o contraseña incorrecto");
		}
		String token = null;
		token = getTokenMoodle(cedula, password);
		System.out.println("Obtengo token desde moodle");

		MoodleUserInfoDTO userInfo = null;
		userInfo = getUserInfoMoodle(token);
		System.out.println("Obtengo userinfo desde moodle");

		if (usuario != null) {
			System.out.println("Credenciales locales invalidas pero validas en moodle. Retorno usuario.");
			usuario.setPassword(generateHash(password));
			String tokenAuth = generateToken();
			updateSession(tokenAuth, usuario);
			invitadoEJB.setUsuario(tokenAuth, usuario);
			return new UsuarioDTO(tokenAuth, usuario);
		} else {
			System.out.println("Creo usuario basado en datos del moodle y lo retorno.");
			userInfo.getUserid();
			MoodleCoursesInfoDTO coursesInfo = null;

			coursesInfo = getCursesInfo(token, userInfo.getUserid(), 1l);
			Long courseId = null;
			for (MoodleCourseDTO course : coursesInfo.getEnrolledcourses()) {
				System.out.println(course.getShortname());
				String groupName = getMoodleDefaultGroup().toLowerCase();
				if (course.getShortname().toLowerCase().equals(groupName)) {
					courseId = course.getId();
					System.out.println(courseId);
				}
			}
			if (courseId != null) {
				coursesInfo = getCursesInfo(token, userInfo.getUserid(), courseId);
			} else {
				throw new MatefunForbiddenException("Usuario no asignado al curso \"" +getMoodleDefaultGroup() +"\" en Moodle");
			}

			String rol = null;
			for (MoodleRoleDTO role : coursesInfo.getRoles()) {
				if (role.getShortname().toLowerCase().equals("student")) {
					rol = "student";
				} else if (role.getShortname().toLowerCase().equals("teacher")
						|| role.getShortname().toLowerCase().equals("editingteacher")
						|| role.getShortname().toLowerCase().equals("manager")) {
					rol = "teacher";
				}
			}

			if (rol == null) {
				throw new MatefunForbiddenException("Rol incorrecto en curso \"" +getMoodleDefaultGroup()+"\"");
			}

			Usuario nuevoDesdeMoodle;
			Grupo grupo = null;
			try {
				GrupoPK grupoPK = getDefaultGroup();
				grupo = em.find(Grupo.class, grupoPK);
			} catch (Exception ex) {
				System.out.println("No se encuentra grupo por defecto, no se asigna");
			}
			if (rol.equals("student")) {
				nuevoDesdeMoodle = new Alumno(cedula, userInfo.getFirstname(), userInfo.getLastname(),
						generateHash(password));
				if (grupo != null) {
					grupo.addAlumno((Alumno) nuevoDesdeMoodle);
				}
			} else {
				nuevoDesdeMoodle = new Docente(cedula, userInfo.getFirstname(), userInfo.getLastname(),
						generateHash(password));
				if (grupo != null) {
					((Docente) nuevoDesdeMoodle).addGrupoAsignado(grupo);
				}
			}
			Archivo root = new Archivo();
			root.setCreador(nuevoDesdeMoodle);
			root.setDirectorio(true);
			root.setNombre("root");
			root.setFechaCreacion(new Date());
			root.setEditable(true);
			root.setEliminado(false);
			root.setEstado(EstadoArchivo.Edicion);
			if (rol.equals("student")) {
				((Alumno) nuevoDesdeMoodle).addArchivo(root);
			} else {
				((Docente) nuevoDesdeMoodle).addArchivo(root);
			}
			Configuracion conf = new Configuracion();
			conf.setArgumentoF(false);
			conf.setArgumentoI(false);
			conf.setFontSizeEditor(12);
			conf.setThemeEditor("dracula");
			nuevoDesdeMoodle.setConfiguracion(conf);
			try {
				userTransaction.begin();
				em.persist(conf);
				em.persist(root);
				em.persist(nuevoDesdeMoodle);
				userTransaction.commit();
			}catch(Exception e) {
				System.out.println("Error guardando configuracion de usuario");
			}
			String tokenAuth = generateToken();
			updateSession(tokenAuth, usuario);
			invitadoEJB.setUsuario(tokenAuth, nuevoDesdeMoodle);
			return new UsuarioDTO(tokenAuth, nuevoDesdeMoodle);
		}
	}
	
	private String getMoodleAPIEndpoint(){
		try{
			String pathMatefunProperties = context.getRealPath("/WEB-INF/matefun.properties");
			Properties props = new Properties();
			props.load(new FileReader(pathMatefunProperties));
			return props.getProperty("moodle_endpoint");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private String getMoodleDefaultGroup(){
		try{
			String pathMatefunProperties = context.getRealPath("/WEB-INF/matefun.properties");
			Properties props = new Properties();
			props.load(new FileReader(pathMatefunProperties));
			return props.getProperty("moodle_group");
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private GrupoPK getDefaultGroup(){
		try{
			String pathMatefunProperties = context.getRealPath("/WEB-INF/matefun.properties");
			Properties props = new Properties();
			props.load(new FileReader(pathMatefunProperties));
			String[] grupo = props.getProperty("default_group").split("#");
			LiceoPK lpk = new LiceoPK(Long.valueOf(grupo[0]));
			return new GrupoPK(Integer.valueOf(grupo[3]), Integer.valueOf(grupo[1]), grupo[2], lpk);
		}catch(Exception e){
			return null;
		}
	}
	
	private String getTokenMoodle(String username, String password) throws MatefunException {
		javax.ws.rs.client.ClientBuilder cb = ClientBuilder.newBuilder();

		javax.ws.rs.client.Client c = cb.build();

		String url =  getMoodleAPIEndpoint() + "/login/token.php";
		MoodleTokensDTO tokens = null;
		try {
			// https://tics.moodlecloud.com/login/token.php
			// ?username=USUARIO&password=PASSWORD&service=moodle_mobile_app
			tokens = c.target(url).queryParam("username", username).queryParam("password", password)
					.queryParam("service", "moodle_mobile_app").request().get(MoodleTokensDTO.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MatefunException("Usuario o contraseña incorrecto en Moodle");
		} finally {
			c.close();
		}

		if (tokens.getToken() != null) {
			return tokens.getToken();
		} else {
			throw new MatefunException("Usuario o contraseña incorrecto en Moodle");
		}

	}

	private MoodleUserInfoDTO getUserInfoMoodle(String token) throws MatefunException {
		javax.ws.rs.client.ClientBuilder cb = ClientBuilder.newBuilder();

		javax.ws.rs.client.Client c = cb.build();
		String url = getMoodleAPIEndpoint()+ "/webservice/rest/server.php";

		MoodleUserInfoDTO userInfo = null;
		try {
			// https://tics.moodlecloud.com/webservice/rest/server.php
			// ?wstoken=TOKEN&wsfunction=core_webservice_get_site_info&moodlewsrestformat=json
			userInfo = c.target(url).queryParam("wstoken", token)
					.queryParam("wsfunction", "core_webservice_get_site_info").queryParam("moodlewsrestformat", "json")
					.request().get(MoodleUserInfoDTO.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MatefunException("Error al obtener datos del usuario Moodle");
		} finally {
			c.close();
		}

		if (userInfo.getUserid() != null) {
			return userInfo;
		} else {
			throw new MatefunException("Error al obtener datos del usuario Moodle");
		}

	}

	private MoodleCoursesInfoDTO getCursesInfo(String token, Long userId, Long courseId) throws MatefunException {
		javax.ws.rs.client.ClientBuilder cb = ClientBuilder.newBuilder();
		javax.ws.rs.client.Client c = cb.build();
		String url = getMoodleAPIEndpoint()+ "/webservice/rest/server.php";

		List<MoodleCoursesInfoDTO> coursesInfo = null;
		try {
			// https://tics.moodlecloud.com/webservice/rest/server.php
			// ?wstoken=TOKEN&wsfunction=core_user_get_course_user_profiles&userlist[0][userid]=USER_ID&userlist[0][courseid]=COURSE_ID&moodlewsrestformat=json
			coursesInfo = c.target(url).queryParam("wstoken", token)
					.queryParam("wsfunction", "core_user_get_course_user_profiles")
					.queryParam("userlist[0][userid]", userId).queryParam("userlist[0][courseid]", courseId)
					.queryParam("moodlewsrestformat", "json").request()
					.get(new GenericType<List<MoodleCoursesInfoDTO>>() {
					});
		} catch (Exception e) {
			e.printStackTrace();
			throw new MatefunException("Error al obtener datos de los cursos Moodle");
		} finally {
			c.close();
		}
		
		if (coursesInfo != null && !coursesInfo.isEmpty() && coursesInfo.get(0).getId() != null) {
			return coursesInfo.get(0);
		}else{
			throw new MatefunException("Error al obtener datos de los cursos Moodle");
		}

	}

	private static String generateHash(String input) {
		StringBuilder hash = new StringBuilder();

		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] hashedBytes = sha.digest(input.getBytes());
			char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			for (int idx = 0; idx < hashedBytes.length; idx++) {
				byte b = hashedBytes[idx];
				hash.append(digits[(b & 0xf0) >> 4]);
				hash.append(digits[b & 0x0f]);
			}
		} catch (NoSuchAlgorithmException e) {
			// handle error here.
		}

		return hash.toString();
	}

	private String generateToken() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);

	}

	public String cargarDatosDePrueba() {
		Liceo varela = new Liceo("Colegio Varela");
		em.persist(varela);

		GrupoPK segundoAPK = new GrupoPK(2017, 2, "Año", varela.getLiceoPK());
		Grupo segundoAnio = new Grupo(segundoAPK);
		em.persist(segundoAnio);

		Archivo manual = new Archivo("Manual", new Date(), getContenidoArchivoManual(), EstadoArchivo.Edicion, false,
				false, null, null);
		em.persist(manual);
		segundoAnio.addArchivo(manual);

		Archivo discreto = new Archivo("Discreto", new Date(), getContenidoArchivoDiscreto(), EstadoArchivo.Edicion,
				false, false, null, null);
		em.persist(discreto);
		segundoAnio.addArchivo(discreto);

		Archivo continuo = new Archivo("Continuo", new Date(), getContenidoArchivoContinuo(), EstadoArchivo.Edicion,
				false, false, null, null);
		em.persist(continuo);
		segundoAnio.addArchivo(continuo);

		Archivo figuras = new Archivo("Figuras", new Date(), getContenidoArchivoFiguras(), EstadoArchivo.Edicion, false,
				false, null, null);
		em.persist(figuras);
		segundoAnio.addArchivo(figuras);

		// ALUMNO INVITADO
		Alumno invitado = new Alumno("invitado", "", "Invitado", generateHash("invitado"));
		em.persist(invitado);
		Configuracion configInvitado = new Configuracion();
		configInvitado.setArgumentoF(true);
		configInvitado.setArgumentoI(true);
		configInvitado.setFontSizeEditor(12);
		configInvitado.setThemeEditor("ambiance");
		em.persist(configInvitado);
		invitado.setConfiguracion(configInvitado);

		Archivo root_invitado = new Archivo("root", new Date(), "Carpeta raiz", EstadoArchivo.Edicion, true, true, null,
				invitado);
		invitado.addArchivo(root_invitado);
		em.persist(root_invitado);

		Archivo estacionar = new Archivo("Estacionar", new Date(), getContenidoArchivoEstacionar(),
				EstadoArchivo.Edicion, true, false, root_invitado, invitado);
		invitado.addArchivo(estacionar);
		em.persist(estacionar);

		Archivo mate = new Archivo("Mate", new Date(), getContenidoArchivoMate(), EstadoArchivo.Edicion, true, false,
				root_invitado, invitado);
		invitado.addArchivo(mate);
		em.persist(mate);

		segundoAnio.addAlumno(invitado);

		return "ok";
	}

	private String getContenidoArchivoManual() {
		return "{- EL LENGUAJE MateFun -}\n\n{-\n# Definición de Funciones\n\nPara definir una función se debe indicar su signatura y la ecuación que la define.  La signatura se compone del nombre de la función, el conjunto dominio y el codominio.  La ecuación se define dando el nombre de la función, los parámetros y el cuerpo de la función. Por ejemplo, la siguiente función retorna el cuadrado de un número real:\n-}\ncuad :: R -> R\ncuad(x) = x * x\n\n{-\nEl nombre de la función es cuad, el dominio es R, el codominio R, la variable independiente x y el cuerpo la expresión x * x. \nEl conjunto R de los números reales es uno de los conjuntos primitivos del lenguaje. Los otros conjuntos elementales primitivos son Fig (figuras) y Color.\n-}\n\n{-\nLas expresiones, que definen el cuerpo de una función, se pueden componer de literales, variables, aplicación de operadores infijos (por ejemplo, para R están definidos +, -, *, / y ^), aplicación de operadores prefijos (-) y aplicación de funciones. Por ejemplo, la siguiente función calcula el área de un círculo en función de su radio:\n-}\narea_circ :: R -> R\narea_circ(r) = 3.14 * cuad(r)\n\n{-\nNotar que al definir area_circ se utiliza la función cuad, definida anteriormente.\n-}\n\n{-\n# Funciones con múltiples variables\n\nSe pueden definir funciones con más de una variable independiente, para esto se utiliza el conjunto producto cartesiano (y su generalización n-tupla). Por ejemplo la siguiente función calcula el área del triángulo dadas su base y altura:\n-}\n\narea_tria :: R X R -> R\narea_tria(base,altura) = (base * altura) / 2\n\n{-\n# Funciones definidas por casos\n\nLa ecuación de la función se puede definir por casos, estableciendo la condición de cada caso y por último un caso por defecto (si no se cumple ninguna condición), de manera que la función sea total sobre su dominio. Por ejemplo, la siguiente función calcula el valor absoluto de un real:\n-}\nabs :: R -> R\nabs(x) =  x si x >= 0\n       o -x\n\n{-\nLas condiciones para los casos se definen como comparaciones (==,/=,>,>=,<,<=) entre expresiones. \n-}\nmax :: R X R -> R\nmax(x,y) = x si x >= y\n         o y\n\n{-\nTambién se puede definir una condición como una conjunción de condiciones entre paréntesis y separadas con comas. Por ejemplo:\n-}\nmax3 :: R X R X R -> R\nmax3(x,y,z) = x si (x >= y, x >=z)\n            o y si y >= z\n            o z\n{-\nEsta función se puede leer de la siguiente manera: La función max3 toma tres reales (x,y,z) y retorna x si x es mayor o igual que y y x es mayor o igual que z o (si no se cumple lo anterior) retorna y si y es mayor o igual que z o (si no se cumple ninguna de las anteriores) retorna z. Notar que las condiciones se evalúan de forma secuencial y que una condición sólo se evalúa si las anteriores no se cumplieron.\n-}\n\n{-\n# Definición de Conjuntos\nAdemás de utilizar los conjuntos primitivos, en un programa MateFun se pueden definir nuevos conjuntos. Hay dos formas de definir conjuntos: por Enumeración (o Extensión) y por Comprensión.\n-}\n\n{-\n# Por Enumeración\nPara definir un conjunto por Enumeración se deben listar todos sus componentes. Por ejemplo, si queremos definir el conjunto de los meses:\n-}\nconj Mes = { Enero, Febrero, Marzo\n           , Abril, Mayo, Junio\n           , Julio, Agosto, Setiembre\n           , Octubre, Noviembre, Diciembre }\n{-\nNotar que los nombres de conjuntos y sus elementos empiezan con mayúsculas, mientras que los nombres de funciones y variables empiezan con minúsculas. Esto es una regla del lenguaje.\n-}\n\n{-\nLa siguiente función retorna la cantidad de días que (generalmente) tiene un mes:\n-}\ndias :: Mes -> R\ndias(m) = 31 si m == Enero\n        o 28 si m == Febrero\n        o 31 si m == Marzo\n        o 30 si m == Abril\n        o 31 si m == Mayo\n        o 30 si m == Junio\n        o 31 si m == Julio\n        o 31 si m == Agosto\n        o 30 si m == Setiembre\n        o 31 si m == Octubre\n        o 30 si m == Noviembre\n        o 31\n\n\n{-\n# Por Comprensión\n\nPara definir un conjunto por Comprensión se debe especificar un conjunto base y la condición que deben cumplir los elementos de ese conjunto base para pertenecer al nuevo conjunto que estamos definiendo. Por lo tanto el nuevo conjunto será un subconjunto del conjunto base. Ahora podemos definir el conjunto de los Reales no nulos:\n-}\nconj Rno0 = { x en R | x /= 0 }\n\n{-\nTeniendo este conjunto podemos definir la función inverso como una función total:\n-}\ninverso :: Rno0 -> R\ninverso(x) = 1 / x\n\n{-\nCualquier conjunto puede ser base de un conjunto por comprensión, incluso otro conjunto definido por comprensión. Por ejemplo, si definimos al conjunto de los enteros (Z) como el de los reales que son iguales a su redondeo (red):\n-}\nconj Z = { x en R | red(x) == x }\n\n{-\nLuego podemos definir a los naturales (N) como los enteros mayores o iguales que cero:\n-}\nconj N = { x en Z | x >= 0 }\n\n\npred :: N -> N\npred (x) = x - 1\n\n\n{-\n# Tuplas\nComo hemos visto anteriormente, las tuplas se utilizan para definir funciones con más de una variable independiente. Pero también se pueden manipular tuplas tanto como elementos del dominio como del codominio de una función. Por ejemplo, podemos definir una función que toma un número de mes (del 1 al 12) y retorna un par con el mes y la cantidad de días que tiene: \n-}\nconj NMes = { x en N | (x > 0, x <= 12) }\ninfo_mes :: NMes -> Mes X R\ninfo_mes(m) = (Enero,     dias(Enero))     si m == 1\n            o (Febrero,   dias(Febrero))   si m == 2\n            o (Marzo,     dias(Marzo))     si m == 3\n            o (Abril,     dias(Abril))     si m == 4\n            o (Mayo,      dias(Mayo))      si m == 5\n            o (Junio,     dias(Junio))     si m == 6\n            o (Julio,     dias(Julio))     si m == 7\n            o (Agosto,    dias(Agosto))    si m == 8\n            o (Setiembre, dias(Setiembre)) si m == 9\n            o (Octubre,   dias(Octubre))   si m == 10\n            o (Noviembre, dias(Noviembre)) si m == 11\n            o (Diciembre, dias(Diciembre)) \n            \n\n{-\nPara obtener un componente de una tupla se utiliza el operador de proyección “!”. Por ejemplo la siguiente función toma un número de mes y retorna la cantidad de días que tiene proyectando el segundo componente de la tupla que retorna la función info_mes:\n-}\ndias_mes :: NMes -> R\ndias_mes (m) = info_mes(m) ! 2\n\n\n{- Par Ordenado -}\nconj POrd = { p en R X R | p!1 <= p!2 }\n\npord :: R X R -> POrd\npord (x,y) = (x,y)\n\n\n{-\n# Secuencias\n\nEn ocasiones es útil procesar varios elementos de un mismo conjunto en una función. Para esto se utilizan las secuencias. Dado un conjunto, por ejemplo R, una secuencia, en este caso R*, es una colección ordenada de elementos de ese conjunto en la que se admiten elementos repetidos. Por ejemplo, la secuencia R* se define inductivamente como:\n- la secuencia vacía [R] pertenece a R*\n- si r pertenece a R y s pertenece a R* entonces r:s pertenece a R*\n\nPara obtener los elementos de una secuencia se utilizan las funciones primero, que retorna el primer elemento de una secuencia, y resto, que retorna la secuencia sin su primer elemento. Utilizando secuencias podemos definir la función tienen, que dado un número de días, retorna todos los meses que tienen ese número de días (y la secuencia vacía si ningún mes los tiene):\n-}\ntienen :: R -> Mes*\ntienen(d) = Abril:Junio:\n            Setiembre:Noviembre:[Mes]       si d == 30\n          o Enero:Marzo:Mayo:\n            Julio:Agosto:Octubre:\n            Diciembre:[Mes]                 si d == 31\n          o Febrero:[Mes]                   si d == 28\n          o [Mes]\n\n\n{-\n# Recursión\n\nDada la naturaleza inductiva del conjunto secuencia, es natural que muchas funciones que operan sobre secuencias se definan usando recursión.\nPor ejemplo, para obtener la suma de una secuencia de reales, sumamos el primer real de la secuencia con el resultado de la suma del resto (considerando que la secuencia vacía suma 0):\n-}\nsuma :: R* -> R\nsuma(l) = 0 si esVacia(l)\n        o primero(l) + suma(resto(l))\n\n{-\nDe forma similar, si queremos obtener el largo de una secuencia:\n-}\nlargo :: R* -> R\nlargo(l) = 0 si esVacia(l)\n        o 1 + largo(resto(l))\n\n\n{-\nO el máximo de una secuencia no vacía de reales:\n-}\nconj RSeqNV = { l en R* | largo(l) /= 0 }\n\nmaximo :: RSeqNV -> R\nmaximo (l) = primero(l) si esVacia(resto(l))\n           o max(primero(l),maximo(resto(l)))\n\n{-\nPor supuesto que la recursión no se limita sólo a las secuencias, por ejemplo la función factorial se define como una recursión sobre los naturales:\n-}\nfactorial :: N -> N\nfactorial (n) = 1 si n == 0\n              o n * factorial(n-1)\n\ncontramano :: R -> Fig\ncontramano(x) = juntar(color(circ(x),Rojo),rect(x,2))\n\n\nrodar :: Fig -> Fig*\nrodar (f) = f:rotar(f,45):rotar(f,90):rotar(f,135):\n            rotar(f,180):rotar(f,225):rotar(f,270):\n            rotar(f,315):[Fig]\n";
	}

	private String getContenidoArchivoDiscreto() {
		return "{- conjunto de los enteros (reales que son iguales a su redondeo) -}\nconj Z = { x en R | x == red(x) }  \n\n{- conjunto de los natulares (enteros mayores o iguales que cero) -}\nconj N = { x en Z | x >= 0  }\n\n{- truncar un real a su parte entera -}\ntrunc :: R -> Z\ntrunc(x) = 0 si (x < 1, x > -1)\n         o trunc(x-1) + 1 si x >= 1\n         o trunc(x+1) - 1\n\n{- valor absoluto de un entero -}\nabs :: Z -> N\nabs(x) =  x  si x >= 0\n       o -x\n\n{- signo de un entero -}\nsigno :: Z -> Z\nsigno(x) =  1  si x >= 0\n         o -1\n\n{- predecesor de un entero -}\npred :: Z -> Z\npred (x) = x - 1\n\n{- sucesor de un entero -}\nsuc :: Z -> Z\nsuc (x) = x + 1\n\n\n{- division de dos enteros -}\ndiv :: Z X Z -> Z\ndiv (x,y) = signo(x) * signo(y) * divN(abs(x),abs(y))\n\n{- division de dos naturales -}\ndivN :: N X N -> N\ndivN (x,y) = 0 si x < y\n           o 1 + divN (x-y,y)\n\n{- resto de division de dos enteros -}\nmod :: Z X Z -> Z\nmod (x,y) = signo(x) * modN(abs(x),abs(y))\n\n{- resto de division de dos naturales -}\nmodN :: N X N -> N\nmodN (x,y) = x si x < y\n           o modN (x-y,y)\n\n\n{- factorial de un natural -}\nfact :: N -> N\nfact (x) = 1 si x == 0\n         o x * fact (x-1)\n";
	}

	private String getContenidoArchivoContinuo() {
		return "{- modulo de dos numeros dados -}\nmod :: R X R -> R\nmod(x,y) = x si x < y\n         o mod(x-y,y)\n\n{- maximo de dos numeros dados -}\nmax :: R X R -> R\nmax (x, y) = x si x >= y\n             o y\n\n{- minimo de dos numeros dados -}\nmin :: R X R -> R\nmin (x, y) = x si x <= y\n             o y\n\n{- valor absoluto -}\nabs :: R -> R\nabs (x) = x si x >= 0\n             o -x\n             \n{- parte entera -}\nent :: R -> R\nent (x) = 0 si x < 1\n 		o mod(x,10) + 10 * ent(x/10)\n\n{- raiz de una funcion de forma f(x) = a x + b -}\nraiz :: R X R -> R\nraiz (a,b) = - b / a\n\n{- funcion de ejemplo, se puede ver su grafica y comparar\n   con el resultado de raiz(2,6) -}\nf :: R -> R\nf(x) = 2 * x + 6\n\n{- funcion que representa las funciones lineales,\n   se puede probar por ejemplo con (2,6,-3)-}\nf_lineal :: R X R X R -> R\nf_lineal (a,b,x) = a * x + b\n";
	}

	private String getContenidoArchivoFiguras() {
		return "{- conjunto de los puntos cardinales -}\nconj Cardinal =  {Norte, Sur, Este, Oeste } \n\n\n{- toma un punto cardinal y retorna el punto cardinal\n   resultante de girar a la derecha 90 grados -}  \nderecha :: Cardinal -> Cardinal\nderecha (x) = Este  si x == Norte\n            o Sur   si x == Este\n            o Oeste si x == Sur\n            o Norte\n\n{- dados dos puntos cardinales retorna una figura\n   con el primero pintado de rojo si es igual al primero\n   y de negro en caso contrario -}  \npunto :: Cardinal X Cardinal -> Fig\npunto (p,d) = color(aFig(p),Rojo) si p == d\n            o color(aFig(p),Negro)  \n\n{- dibuja los cuatro puntos cardinales y marca con\n   rojo el que se pasa como parametro -}  \ncartelDir :: Cardinal -> Fig\ncartelDir (d) = juntar ( circ(10)\n                       , juntar (mover(punto(Norte,d),(0,8))\n                       , juntar (mover(punto(Sur,d),(0,-8))\n                       , juntar (mover(punto(Este,d),(8,0))\n                       , mover(punto(Oeste,d),(-8,0))))))";
	}

	private String getContenidoArchivoEstacionar() {
		return "\nconj Pasos = { Uno, Dos, Tres, Cuatro, Cinco, Seis, Siete, Ocho }\n\n{- El conjunto 'Nada' se usa como dominio de las funciones constantes -}\nconj Nada = { Nada }\n\n\nestacionar :: Nada -> Fig*\nestacionar (n) = paso(Uno):paso(Dos):paso(Tres):paso(Cuatro):\n                 paso(Cinco):paso(Seis):paso(Siete):paso(Ocho):[Fig]\n\npaso :: Pasos -> Fig\npaso (p) \n   = figpaso(p,((5,1),0,0))     si p == Uno\n   o figpaso(p,((5,1),0,45))    si p == Dos\n   o figpaso(p,((2,0),-45,45))  si p == Tres\n   o figpaso(p,((2,0),-45,0))   si p == Cuatro  \n   o figpaso(p,((0,0),-45,0))   si p == Cinco\n   o figpaso(p,((0,0),-45,-45)) si p == Seis  \n   o figpaso(p,((0,-2),0,-45))  si p == Siete  \n   o figpaso(p,((0,-2),0,0))\n\nfigpaso :: Pasos X ((R X R) X R X R) -> Fig \nfigpaso(p,d) = juntar(mover(aFig(p),(0,-5)),autos(d))\n\ndosautos :: Nada -> Fig X Fig\ndosautos(n) = (auto(2,2), auto(0,0))\n\n{-un auto es un rectangulo en determinada posicion -}\nauto :: R X R -> Fig\nauto (x,y) = mover(rect(4,2),(x,y))\n\n\n\n{- los dos autos estacionados -}    \nestacionados :: Nada -> Fig\nestacionados (x) = juntar(auto(5,-2),auto(-5,-2))\n\n{- una rueda tiene una posicion y una rotacion -}\nrueda :: (R X R) X R -> Fig\nrueda (pos,rot) = mover(rotar(color(rect(1,0.5),Rojo),rot),pos)\n\n{- mi auto tiene una posicion y sus ruedas una rotacion -}\nmi_auto :: (R X R) X R -> Fig\nmi_auto (pos,rot) = juntar(auto(pos!1,pos!2)\n                          ,juntar(rueda((pos!1 + 1.3, pos!2 + 1),rot)\n                                 ,rueda((pos!1 + 1.3, pos!2 - 1),rot)))\n\n{- todos los autos -}\nautos :: (R X R) X R X R -> Fig\nautos (pos,rot_auto,rot_ruedas) \n   = juntar(estacionados(Nada),rotar(mi_auto(pos,rot_ruedas),rot_auto))\n";
	}

	private String getContenidoArchivoMate() {
		return "conj RF = { r en R | r <= 10 }\ncontramano :: RF -> Fig\ncontramano(x) = juntar(color(circ(x),Rojo),rect(x-2,2))\n\nconj Nada = { Nada }\nmail :: Nada -> Fig\nmail(x) = juntar( poli((-9,-9):(-9,9):(0,0):(9,9):(9,-9):[R X R])\n                , linea((-9,9),(9,9)))\n                \nm :: Color -> Fig\nm(c) = color((poli((-2,-9):(-2,9):(-1,9):\n                  (0,1):(1,9):(2,9):\n                  (2,-9):(1,-9):(1,1):\n                  (0,-1):(-1,1):(-1,-9):[R X R])),c)\n\na :: Color -> Fig\na(c) = color(poli((-2,-9):(-1,9):(1,9):(2,-9):\n                  (1,-9):(0.5,0):\n                  (0.5,2):(0,7):(-0.5,2):(0.5,2):\n                  (0.5,0):(-0.5,0):(-1,-9):[R X R]),c)\n                  \nt :: Color -> Fig\nt(c) = color(poli((-0.5,-9):(-0.5,7):(-2,7):(-2,9):\n                  (2,9):(2,7):(0.5,7):(0.5,-9):[R X R]),c)\n\ne :: Color -> Fig\ne(c) = color(poli((-2,-9):(-2,9):(2,9):(2,7):(-1,7):(-1,2):\n                  (1,2):(1,0):(-1,0):(-1,-7):(2,-7):(2,-9):[R X R]),c)\n\nf :: Color -> Fig\nf(c) = color(poli((-2,-9):(-2,9):(2,9):\n                  (2,8):(-1,8):(-1,7):\n                  (2,7):(2,6):(-1,6):(-1,-9):[R X R]),c)\n                  \nmf :: Color -> Fig                  \nmf(c) = juntar(mover(m(c),(-2.5,0)),mover(f(c),(2.5,0)))\n\nmate :: Color -> Fig\nmate(c) = juntar(mover(m(c),(-8,  0)),\n          juntar(mover(a(c),(-4,  0)),\n          juntar(mover(t(c),(-0.8,0)), mover(e(c),(3.4,0)))))";
	}
}
