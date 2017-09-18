package logicaPresentazione;

import java.sql.SQLException;
import java.util.LinkedList;

import logicaBusiness.entit‡.DatoRilevazione;
import logicaBusiness.entit‡.DatoSintesiDashboard;
import logicaBusiness.entit‡.Utente;


/**
 * La classe si occupa di organizzare la visualizzazione dei dati delle dashboard degli utenti
 * 
 * @author Antonio Garofalo, Matteo Forina
 *
 */
public class VistaDashboard {
	private static final String MISURA_BINARIA = "on/off";
	private static final String ON = "ON";
	private static final String OFF = "OFF";
	private static final String PERCENT = "%";
	private static final String DOT = ".";
	/**
	 * Il metodo organizza i clienti da mostrare in una drop-down list
	 * @param unaListaClienti La lista di clienti da visualizzare
	 * @param unUtenteSelezionato L'email dell'utente attualmente selezionato per la ricerca
	 * @return Restituisce una stringa formattata in una drop-down list 
	 * contenente la lista dei clienti ottenuti
	 */
	public String mostraClienti(LinkedList<Utente> unaListaClienti, String unUtenteSelezionato) {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);
		
		risultato.append("<strong>Seleziona il cliente desiderato:</strong><br><br>(Attualmente selezionato: <strong>");
				if (unUtenteSelezionato != null && !unUtenteSelezionato.equals("")) {
					risultato.append(unUtenteSelezionato); 
				} else {
					risultato.append("Nessuno");
				}
		risultato.append("</strong>)&emsp;|&emsp;"
				+ "<select name=\"listaclienti\">");
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
	 * Il metodo organizza i filtri per i dati della dashboard inerenti all'utente target della richiesta
	 * 
	 * @param unaMail email identificativa dell'utente target della ricerca
	 * @param unaListaNomiAmbienti una lista con i nomi degli ambienti associati all'utente selezionato
	 * @param unaListaIntervalliTempo lista degli intervalli di tempo selezionabili per filtrare i dati
	 * @return Restituisce una stringa formattata in una drop-down list contenenti i filtri ottenuti 
	 * @throws SQLException Eccezione in caso di dati mancanti o non corretti
	 */
	public String mostraFiltriDatiDashboard(LinkedList<String> unaListaNomiAmbienti, int[] unaListaIntervalliTempo) throws SQLException {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);
	
		risultato.append("<strong>Seleziona i filtri desiderati:</strong> <br><br>"
				+ "<select name=\"filtroambiente\">"
				+ "<option selected=\"selected\" value=\"\"> Tutti gli Ambienti </option>");
		if(unaListaNomiAmbienti != null) {
			for (String nomeAmbiente : unaListaNomiAmbienti) {
				risultato.append("<option value=\"" + nomeAmbiente +"\">" + nomeAmbiente + "</option>");
			}	
		}
		
		risultato.append("</select>" + "&nbsp;&nbsp;&nbsp;"
				+ "<select name=\"intervallotempo\">");
		if(unaListaIntervalliTempo != null) {
			for (int intervallotempo : unaListaIntervalliTempo) {
				risultato.append("<option value=\"" + intervallotempo +"\">" + "Ultimi " + intervallotempo + " giorni" + "</option>");
			}
		}
		risultato.append("</select>" + "&nbsp;&nbsp;&nbsp;"
				+ "<button type=\"submit\" name=\"mostradati\">Mostra dati</button><br>"
				+ "<hr>");
		
		return risultato.toString();
	}
	
	/**
	 * Il metodo organizza i dati della dashboard del cliente target della richiesta prima che questi vengano visualizzati
	 * 
	 * @param unaLista lista di oggetti DatoDashboard contenente l'insieme delle entit‡ da organizzare
	 * @return Restituisce una stringa formattata in una tabella contenente i dati della dashboard
	 */
	public String visualizzaDatiSintesiDashboard(LinkedList<DatoSintesiDashboard> unaLista) {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);
		risultato.append("<table>");
		risultato.append("<caption><strong>DATI SINTESI</strong></caption>" 
				  +  "<tr class=\"header\"><td>Nome Ambiente</td><td>Tipo Sensore</td><td>Media Valori</td><td>Unit‡ di Misura</td></tr>");
		if (unaLista != null) {
			for (DatoSintesiDashboard datoSintesi : unaLista) {
				
				double valore = datoSintesi.getMediaValori();
				String unit‡DiMisura = datoSintesi.getUnitaDiMisura();
				risultato.append("<tr><td>" + datoSintesi.getNomeAmbiente() + "</td>"
						+ "<td>" + datoSintesi.getTipoSensore() + "</td>"
						+ "<td>" + formattaValoreSintesi(valore, unit‡DiMisura) + "</td>"
						+ "<td>" + unit‡DiMisura + "</td></tr>"); 
			}
		}
		risultato.append("</table>");
		
		return risultato.toString();	
	}
	
	
	/**
	 * Il metodo organizza i dati critici della dashboard del cliente target della richiesta primca che questi vengano visualizzati
	 * @param unaLista lista dei dati delle rilevazioni da formattare per la visualizzazione
	 * @return Stringa formattata in una tabella
	 */
	public String visualizzaDatiCriticiDashboard(LinkedList<DatoRilevazione> unaLista) {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);
		
		risultato.append("<br><br><table id=\"criticaltable\">"
				+ "<caption><strong>DATI CRITICI</strong></caption>"
				+"<tr class=\"header\"><td>Nome Ambiente</td><td>Tipo Sensore</td><td>Codice Sensore</td>"
				+ "<td>Valore Rilevazione</td><td>Data Rilevazione</td><td>Messaggio</td></tr>");
		if (unaLista != null) {
			for (DatoRilevazione datoRilevazione : unaLista) {
				risultato.append("<tr><td>" + datoRilevazione.getNomeAmbiente() + "</td>"
						+ "<td>" + datoRilevazione.getTipoSensore() + "</td>" 
						+ "<td>" + datoRilevazione.getCodiceSensore()+ "</td>"
						+ "<td>" + formattaValoreCritico(datoRilevazione) + "</td>"
						+ "<td>" + datoRilevazione.getData().toString().substring(0, 16) + "</td>"
						+ "<td>" + datoRilevazione.getMessaggio()+ "</td></tr>");
			}
		}
		risultato.append("</table>");
		
		return risultato.toString();	
	}
	
	/**
	 * Metodo interno per gestire la stampa del valore di sintesi di una rilevazione
	 * @param unValore valore da formattare
	 * @param unaUnit‡DiMisura unit‡ di misura da usare per la formattazione
	 * @return valore formattato
	 */
	private String formattaValoreSintesi(double unValore, String unaUnit‡DiMisura){
		String valoreStampato = "";
		int cifreDopoVirgola = 3;
		if (unaUnit‡DiMisura.equals(MISURA_BINARIA)) {
			unValore = (unValore) * 100;
			valoreStampato = Double.toString(unValore);
			int posizVirgola = valoreStampato.indexOf(DOT);
			if (valoreStampato.length() > (posizVirgola + cifreDopoVirgola)) {
				valoreStampato = valoreStampato.substring(0, posizVirgola + cifreDopoVirgola);
			}
			valoreStampato = valoreStampato + PERCENT;
			unaUnit‡DiMisura = ON;
			
		} else {
			valoreStampato = Double.toString(unValore);
			int posizVirgola = valoreStampato.indexOf(DOT);
			if (valoreStampato.length() > (posizVirgola + cifreDopoVirgola)) {
				valoreStampato = valoreStampato.substring(0, posizVirgola + cifreDopoVirgola);
			}
		}
		return valoreStampato;
	}
	
	/**
	 * Metodo interno per gestire la stampa del valore di una rilevazione
	 * @param unDatoRilevazione Il dato di cui gestire il valore
	 * @return Il valore formattato
	 */
	private String formattaValoreCritico(DatoRilevazione unDatoRilevazione) {
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
