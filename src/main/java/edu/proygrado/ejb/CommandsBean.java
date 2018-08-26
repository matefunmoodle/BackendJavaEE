package edu.proygrado.ejb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.servlet.ServletContext;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;

import edu.proygrado.dto.ArchivoDTO;
import edu.proygrado.dto.ConfiguracionDTO;
import edu.proygrado.modelo.Usuario;

@Stateful
public class CommandsBean {

	@Inject
	ArchivosEJB archivos;

	@Inject
	InvitadoEJB invitadoEJB;

	@Inject
	UsuarioEJB usuarioEJB;
	
	@Inject
	LoginEJB loginEJB;

	@Inject
	private ServletContext context;

	private ProcessBuilder builder;
	private Session callback;
	private String cedula;
	private Thread standardConsoleThread;
	private Thread errorConsoleThread;
	private Process proceso;
	private BufferedWriter p_stdin;
	private CountDownLatch latch;

	private String nombrePrompt = "";

	public CommandsBean() {
		builder = null;
	}

	@PreDestroy
	private void terminoBean() {
		System.out.println("Elimino el proceso y los hilos");
		this.proceso.destroy();
		this.standardConsoleThread.interrupt();
		this.errorConsoleThread.interrupt();
		System.out.println("Proceso e hilos terminados");
	}

	public void ejecutarComandos(String comandos, Session session) {
		System.out.println("Ejecuto " + this.hashCode());
		try {
			JsonReader jsonReader = Json.createReader(new StringReader(comandos));
			JsonObject comandoJson = jsonReader.readObject();
			String token = comandoJson.getString("token");
			jsonReader.close();
			if (!this.proceso.isAlive()) {
				restartProcess(this.cedula, token, session);
				System.err.println("Se reinicia el proceso.");
			}
			
			if(!loginEJB.validarSesion(token)){
				session.close(new CloseReason(CloseCodes.VIOLATED_POLICY,"Sin permisos"));
				System.out.print("Web socket finalizado");
				return;
			}
			
			if (comandoJson.containsKey("ping")) {
//				System.out.println(comandoJson.getString("ping"));
			} else if (comandoJson.containsKey("comando")) {
				loginEJB.extendSession(token);
				String comando = comandoJson.getString("comando");

				this.p_stdin.write(comando);
				this.p_stdin.newLine();
				this.p_stdin.flush();
			} else if (comandoJson.containsKey("load")) {
				loginEJB.extendSession(token);
				int fileId = comandoJson.getInt("load");

				JsonArray dependenciasJsonArray = comandoJson.getJsonArray("dependencias");
				Iterator<JsonValue> iter = dependenciasJsonArray.iterator();
				while (iter.hasNext()) {
					JsonValue val = iter.next();
					int fileId_ = Integer.valueOf(val.toString());
					try {
						ArchivoDTO archivo;
						Usuario usuario = invitadoEJB.getUsuario(token);
						if (usuario != null && usuario.getCedula().toLowerCase().equals("invitado")) {
							archivo = invitadoEJB.getArchivo(token, fileId_);
						} else {
							archivo = archivos.getArchivo(fileId_);
						}
						String contenido = archivo.getContenido();
						String fullPathMatefunTmp = context
								.getRealPath("/WEB-INF/classes/edu/proygrado/binarios/MateFunTmp")+"/";
						try {

							File file;
							if (usuario != null && usuario.getCedula().toLowerCase().equals("invitado")) {
								file = new File(fullPathMatefunTmp + this.cedula + "_" + token + "/"
										+ corregirNombreArchivo(archivo.getNombre()) + ".mf");
							} else {
								file = new File(fullPathMatefunTmp + this.cedula + "/"
										+ corregirNombreArchivo(archivo.getNombre()) + ".mf");
							}
							file.getParentFile().mkdirs();
							FileWriter writer = new FileWriter(file);
							writer.write(contenido);
							
							writer.flush();
							writer.close();
						} catch (IOException e) {
							System.out.println("Error al guardar archivo en disco.");
							System.out.println(e.getMessage()); 
						}
					} catch (Exception ex) {
						Logger.getLogger(CommandsBean.class.getName()).log(Level.SEVERE, null, ex);
					}

				}

				try {
					ArchivoDTO archivo;
					Usuario usuario = invitadoEJB.getUsuario(token);
					if (usuario != null && usuario.getCedula().toLowerCase().equals("invitado")) {
						archivo = invitadoEJB.getArchivo(token, fileId);
					} else {
						archivo = archivos.getArchivo(fileId);
					}
					this.p_stdin.write("!cargar " + corregirNombreArchivo(archivo.getNombre()));
					this.p_stdin.newLine();
					this.p_stdin.flush();

				} catch (Exception ex) {
					Logger.getLogger(CommandsBean.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else if (comandoJson.containsKey("restart")) {
				//La extension de la sesion se realiza en restartProcess.
				restartProcess(this.cedula, token, session);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String corregirNombreArchivo(String nombre) {
		nombre = nombre.toLowerCase();
		String nombreCorregido = "";
		for (String s : nombre.split("\\s")) {
			nombreCorregido += Character.toUpperCase(s.charAt(0)) + s.substring(1);
		}
		nombreCorregido = nombreCorregido.replaceAll("[^A-Za-z0-9_]", "");
		this.nombrePrompt = nombreCorregido;
		return nombreCorregido;
	}

	public void restartProcess(String cedula, String token, Session session) throws InterruptedException {
		try {
			if(!loginEJB.validarSesion(token)){
				session.close(new CloseReason(CloseCodes.VIOLATED_POLICY,"Sin permisos"));
				System.out.print("Web socket finalizado");
				return;
			}
			loginEJB.extendSession(token);
			this.callback = session;
			this.cedula = cedula;
			if (this.proceso != null && this.proceso.isAlive()) {
				this.proceso.destroy();
			}

			String fullPathMatefun = context.getRealPath("/WEB-INF/classes/edu/proygrado/binarios/MateFun");
			String fullPathMatefunTmp = context.getRealPath("/WEB-INF/classes/edu/proygrado/binarios/MateFunTmp")+"/";

			System.out.println(fullPathMatefun);
			System.out.println(fullPathMatefunTmp);
			String carpetaRuntimeUsuario = cedula;
			Usuario usuario = invitadoEJB.getUsuario(token);
			if (usuario != null && usuario.getCedula().toLowerCase().equals("invitado")) {
				carpetaRuntimeUsuario += "_"+token;
			}

			Process p = Runtime.getRuntime().exec("chmod +x " + fullPathMatefun);
			p.waitFor();
			ConfiguracionDTO config = usuarioEJB.getConfiguracion(this.cedula);

			if (config != null && config.isArgumentoI() && config.isArgumentoF()) {
				System.out.println("restart con parametros -i -f");
				this.builder = new ProcessBuilder(
						new String[] { fullPathMatefun, "-p", fullPathMatefunTmp + carpetaRuntimeUsuario + "/", "-i", "-f", "-w" });
			} else if (config != null && config.isArgumentoI()) {
				System.out.println("restart con parametro -i");
				this.builder = new ProcessBuilder(
						new String[] { fullPathMatefun, "-p", fullPathMatefunTmp + carpetaRuntimeUsuario + "/", "-i", "-w" });
			} else if (config != null && config.isArgumentoF()) {
				System.out.println("restart con parametro -f");
				this.builder = new ProcessBuilder(
						new String[] { fullPathMatefun, "-p", fullPathMatefunTmp + carpetaRuntimeUsuario + "/", "-f", "-w" });
			} else {
				System.out.println("restart sin parametros");
				this.builder = new ProcessBuilder(
						new String[] { fullPathMatefun, "-p", fullPathMatefunTmp + carpetaRuntimeUsuario + "/", "-w" });

			}

			this.latch = new CountDownLatch(2);
			this.proceso = this.builder.start();
			this.p_stdin = new BufferedWriter(new OutputStreamWriter(proceso.getOutputStream()));
			if (this.standardConsoleThread != null && this.standardConsoleThread.isAlive()) {
				this.standardConsoleThread.interrupt();
			}
			if (this.errorConsoleThread != null && this.errorConsoleThread.isAlive()) {
				this.errorConsoleThread.interrupt();
			}
			this.standardConsoleThread = outputStandardConsoleThread();
			this.errorConsoleThread = outputErrorConsoleThread();
			this.latch.await();
		} catch (IOException ex) {
			Logger.getLogger(CommandsBean.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void eliminarRecursos(String cedula, String token){
		String fullPathMatefunTmp = context
				.getRealPath("/WEB-INF/classes/edu/proygrado/binarios/MateFunTmp")+"/";
		File directory;
		if (cedula.toLowerCase().equals("invitado")) {
			directory = new File(fullPathMatefunTmp + this.cedula + "_" + token );
			invitadoEJB.eliminarRecursos(token);
		} else {
			directory = new File(fullPathMatefunTmp + this.cedula);
		}
		deleteDirectory(directory);
		if(this.proceso!=null && this.proceso.isAlive())
			this.proceso.destroy();
		if(this.standardConsoleThread != null && this.standardConsoleThread.isAlive())
			this.standardConsoleThread.interrupt();
		if(this.errorConsoleThread != null && this.errorConsoleThread.isAlive())
			this.errorConsoleThread.interrupt();
		loginEJB.deleteExpiredSessions();
	}

	private boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}
	
	public ProcessBuilder getProcessBuilder() {
		return this.builder;
	}

	private Thread outputStandardConsoleThread() {
		Thread inputThread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Starting input stream thread...");
				while (!Thread.currentThread().isInterrupted()) {
					try {
						Scanner s = new Scanner(proceso.getInputStream());
						latch.countDown();
						Pattern p = Pattern.compile("OUTFigura:(Figura:)*\\[\\]");
						ArrayList<String> animacion = new ArrayList<>();
						while (s.hasNextLine()) {
							String result = s.nextLine();
							//System.out.println(result);
							//System.out.println("&&&/////");
							if (nombrePrompt != "" && result.contains(nombrePrompt + ">")) {
								if (nombrePrompt.length() > 10) {
									nombrePrompt = nombrePrompt.substring(0, 7) + "...";
								}
								JsonObject respuestaJson = Json.createObjectBuilder().add("tipo", "prompt")
										.add("resultado", nombrePrompt).build();
								callback.getBasicRemote().sendText(respuestaJson.toString());
								nombrePrompt = "";
							}
							JsonObject respuestaJson = null;
							if (result.contains("CANVAS:")) {
								int index = result.indexOf("CANVAS:");
								result = result.substring(index + 7);
								animacion.add(result);
								respuestaJson = null;
							} else if (result.equals("OUTFigura")) {

								respuestaJson = Json.createObjectBuilder().add("tipo", "canvas")
										.add("resultado", animacion.get(0)).build();
								animacion.clear();

							} else if (p.matcher(result).matches()) {
								JsonArrayBuilder animJson = Json.createArrayBuilder();
								for (String canvas : animacion) {
									animJson.add(canvas);
								}
								respuestaJson = Json.createObjectBuilder().add("tipo", "animacion")
										.add("resultado", animJson).build();

								animacion.clear();
							} else if (result.contains("GRAPH:")) {
								int index = result.indexOf("GRAPH:");
								result = result.substring(index + 6);
								respuestaJson = Json.createObjectBuilder().add("tipo", "graph").add("resultado", result)
										.build();
							} else {
								respuestaJson = Json.createObjectBuilder().add("tipo", "salida")
										.add("resultado", result).build();
								animacion.clear();
								;
							}

							if (respuestaJson != null) {
								int reintentos = 0;
								boolean enviado = false;

								while (!enviado && reintentos < 10) {
									try {
										callback.getBasicRemote().sendText(respuestaJson.toString());
										enviado = true;

									} catch (Exception ex) {
										System.err.println(ex.getMessage());
										// Thread.sleep(500);
										//Se reeintenta por bug de WebSphere. No sucede con wildfly.
										reintentos++;
									}
								}
								System.out.println("Enviado " + respuestaJson.toString());
							}
						}
						s.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("Closing input stream thread...");
			}
		});
		inputThread.start();
		return inputThread;
	}

	private Thread outputErrorConsoleThread() {
		Thread inputThread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Starting error stream thread...");
				while (!Thread.currentThread().isInterrupted()) {
					try {
						Scanner error = new Scanner(proceso.getErrorStream());
						latch.countDown();
						while (error.hasNextLine()) {
							String result = error.nextLine();
							JsonObject respuestaJson = Json.createObjectBuilder().add("tipo", "error")
									.add("resultado", result).build();
							int reintentos = 0;
							boolean enviado = false;
							while (!enviado && reintentos < 10) {
								try {
									callback.getBasicRemote().sendText(respuestaJson.toString());
									enviado = true;

								} catch (Exception ex) {
									System.err.println(ex.getMessage());
									// Thread.sleep(500);
									reintentos++;
								}
							}
							System.out.println(result + " " + enviado + " " + reintentos);
						}
						error.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("Closing error stream thread...");
			}
		});
		inputThread.start();
		return inputThread;
	}

}
