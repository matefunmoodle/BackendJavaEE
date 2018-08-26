/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.proygrado.servicios.ghci;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import edu.proygrado.ejb.CommandsBean;

/**
 *
 * @author gonzalo
 */
@ServerEndpoint("/endpoint/{cedula}/{token}")
@Stateful
public class WebSocketEndpoint {

	@EJB
	private CommandsBean commandsBean;

	@OnMessage
	public String onMessage(String message, Session session) {
		System.out.println(message);
		commandsBean.ejecutarComandos(message, session);
		JsonObject ackJson = Json.createObjectBuilder().add("tipo", "ack").build();
		return ackJson.toString();
	}

	@OnOpen
	public void onOpen(@PathParam("cedula") String cedula, @PathParam("token") String token,  Session session) {
		System.out.println("Nueva conexion cedula:"+cedula+" sessionHashCode:" + session.hashCode());
		try {
			commandsBean.restartProcess(cedula, token, session);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnClose
	public void onClose(@PathParam("cedula") String cedula, @PathParam("token") String token, Session session, CloseReason closeReason) {
		System.out.println("Cerrando la conexión del web socket");
		commandsBean.eliminarRecursos(cedula, token);
	}
	
	
	@OnError
	public void onError(Throwable t) {
		System.err.println("Error en conexion");
		System.out.println(t.getMessage());
	}
}
