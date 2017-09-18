package logicaPresentazione;

import java.util.LinkedList;

import logicaBusiness.entit‡.DatoRilevazione;
import logicaBusiness.entit‡.Utente;

/**
 * La classe si occupa di organizzare la visualizzazione
 *  dei dati delle rilevazioni degli utenti
 * @author Antonio Garofalo, Matteo Forina
 *
 */
public class VistaDatiRilevazioni {
	private static final String MISURA_BINARIA = "on/off";
	private static final String ON = "ON";
	private static final String OFF = "OFF";
	/**
	 * Il metodo organizza i clienti da mostrare in una drop-down list
	 * @param unaListaClienti La lista di clienti da visualizzare
	 * @param unUtenteSelezionato L'email dell'utente attualmente selezionato per la ricerca
	 * @return Restituisce una stringa formattata in una drop-down list 
	 * contenente la lista dei clienti ottenuti
	 */
	public String mostraFormSceltaCliente(LinkedList<Utente> unaListaClienti, String unUtenteSelezionato) {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);
		
		risultato.append("<strong>Seleziona il cliente desiderato:</strong><br><br>(Attualmente selezionato: <strong>");
				if (unUtenteSelezionato != null && !unUtenteSelezionato.equals("")) {
					risultato.append(unUtenteSelezionato); 
				} else {
					risultato.append("Nessuno");
				}
		risultato.append("</strong>)&emsp;|&emsp;"
				+ "<select name=\"utenteselezionato\">");
		if(unaListaClienti != null) {
			for (Utente nomecliente : unaListaClienti) {
				risultato.append("<option ");
					if (nomecliente.getEmail().equals(unUtenteSelezionato)) {
						risultato.append("selected=\"selected\" ");
					}
				risultato.append("value=\"" + nomecliente.getEmail() +"\">" + nomecliente.getEmail() + "</option>");
			}
		}
		
		risultato.append("</select>" + "&nbsp;&nbsp;&nbsp;");
		
		return risultato.toString();
	}

	
	/**
	 * Il metodo organizza i filtri per i dati di installazione 
	 * inerenti all'utente target della richiesta
	 * 
	 * @param unaMail email identificativa dell'utente target della ricerca
	 * @return Restituisce una stringa formattata in un due drop-down list 
	 * contenenti i filtri ottenuti 
	 */
	public String mostraFiltriDatiRilevazioni(String unaMail, LinkedList<String> unaListaAmbienti, LinkedList<String> unaListaTipiSensori) {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);
		
		risultato.append("<strong>Seleziona i filtri desiderati:</strong> <br><br>"
				+ "<select name=\"filtroambiente\">"
				+ "<option selected=\"selected\" value=\"\"> Tutti gli Ambienti</option>");
		if(unaListaAmbienti != null) {
			for (String nomeAmbiente : unaListaAmbienti) {
				risultato.append("<option value=\"" + nomeAmbiente +"\">" + nomeAmbiente + "</option>");
			}
		}
		
		risultato.append("</select>" + "&nbsp;&nbsp;&nbsp;"
				+ "<select name=\"filtrotiposensore\">"
				+ "<option selected=\"selected\" value=\"\"> Tutti i Tipi di Sensore</option>");
		if(unaListaTipiSensori != null) {
			for (String tipoSensore : unaListaTipiSensori) {
				risultato.append("<option value=\"" + tipoSensore +"\">" + tipoSensore + "</option>");
			}
		}
		
		risultato.append("</select>" + "&nbsp;&nbsp;&nbsp;"
				+ "<button type=\"submit\" name=\"mostradati\">Mostra dati</button><br>"
				+ "<hr>");	
		
		return risultato.toString();
	}	
	
	/**
	 * Il metodo visualizza i dati delle rilevazioni di un cliente
	 * @param unaLista LinkedList di DatoRilevazione da formattare per la visualizzazione
	 * @return stringa formattata per la visualizzazione
	 */
	public String visualizzaDatiRilevazioni(LinkedList<DatoRilevazione> unaLista) {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);
		
		risultato.append("<table>");
		risultato.append("<tr class=\"header\"><td>Nome Ambiente</td><td>Tipo Sensore</td><td>Codice Sensore</td>"
				+ "<td>Valore Rilevazione</td><td>Data Rilevazione</td><td>Messaggio</td></tr>");
		if(unaLista != null) {
			for (DatoRilevazione datoRilevazione : unaLista) {
				risultato.append("<tr><td>" + datoRilevazione.getNomeAmbiente() + "</td>"
					+ "<td>" + datoRilevazione.getTipoSensore() + "</td>" 
					+ "<td>" + datoRilevazione.getCodiceSensore()+ "</td>"
					+ "<td>" + formattaValore(datoRilevazione) + "</td>"
					+ "<td>" + datoRilevazione.getData().toString().substring(0, 16) + "</td>"
					+ "<td>" + datoRilevazione.getMessaggio()+ "</td></tr>");
			}
		}
		risultato.append("</table>");
		return risultato.toString();	
	}
	
	
	/**
	 * Metodo interno per gestire la stampa del valore di una rilevazione
	 * @param unDatoRilevazione Il dato di cui gestire il valore
	 * @return Il valore formattato
	 */
	private String formattaValore(DatoRilevazione unDatoRilevazione) {
		String valore = Integer.toString(unDatoRilevazione.getValore());
		if (unDatoRilevazione.ËErrore()) {
			valore = "E" + valore;
		} else if (unDatoRilevazione.getUnit‡DiMisura().equals(MISURA_BINARIA)) {
			if (valore.equals("1")) {
				valore = ON; 
			} else {
				valore = OFF;
			}
		} else {
			valore = valore + " " + unDatoRilevazione.getUnit‡DiMisura();
		}
		
		return valore;
	}
	
}