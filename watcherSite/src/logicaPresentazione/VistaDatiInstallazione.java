package logicaPresentazione;


import java.util.LinkedList;

import logicaBusiness.entit‡.DatoInstallazione;
import logicaBusiness.entit‡.Utente;

/**
 * La classe si occupa di organizzare i risultati delle elaborazioni inerenti ai dati di installazione
 * 
 * @author Antonio Garofalo, Matteo Forina
 *
 */
public class VistaDatiInstallazione {
	
	/**
	 * Il metodo organizza i clienti da mostrare in una drop-down list
	 * 
	 * @param unUtenteSelezionato utente eventualmente gi‡ selezionato dall'amministratore
	 * @param unaListaClienti lista dei clienti presenti nel sistema
	 * @return Restituisce una stringa formattata in una drop-down list contenente la lista dei clienti ottenuti
	 */
	public String mostraClienti(String unUtenteSelezionato, LinkedList<Utente> unaListaClienti) {
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
			risultato.append("</select>" + "&nbsp;&nbsp;&nbsp;");
		}
		
		return risultato.toString();
	}
	
	/**
	 * Il metodo organizza i filtri per i dati di installazione inerenti all'utente target della richiesta
	 * 
	 * @param unUtente email identificativa dell'utente target della ricerca
	 * @param unaListaNomiAmbienti lista dei nomi degli ambienti appartenenti all'utente specificato
	 * @param unaListaTipiSensori lista dei nomi dei sensori appartenenti all'utente specificato
	 * @return Restituisce una stringa formattata in un due drop-down list contenenti i filtri ottenuti 
	 */
	public String mostraFiltriDatiInstallazione(LinkedList<String> unaListaNomiAmbienti, LinkedList<String> unaListaTipiSensori) {
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
				+ "<select name=\"filtrotiposensore\">"
				+ "<option selected=\"selected\" value=\"\"> Tutti i Tipi di Sensore </option>");
		if (unaListaTipiSensori != null) {
			for (String tipoSensore : unaListaTipiSensori) {
				risultato.append("<option value=\"" + tipoSensore +"\">" + tipoSensore + "</option>");
			}
		}
		
		risultato.append("</select>" + "&nbsp;&nbsp;&nbsp;"
				+ "<button type=\"submit\" name=\"mostradati\">Mostra Dati</button><br>"
				+ "<hr>");
		
		return risultato.toString();
	}
	
	/**
	 * Il metodo organizza i dati di installazione del cliente target della richiesta prima che questi vengano visualizzati
	 * 
	 * @param LinkedList di DatoInstallazione contenente l'insieme delle entit‡ da organizzare
	 * @return Restituisce una stringa formattata in una tabella contenente i dati di instalazione
	 */
	public String visualizzaDatiInstallazione(LinkedList<DatoInstallazione> unaListaDatiInstallazione) {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);
		risultato.append("<table>");
		risultato.append("<tr class=\"header\"><td>Nome Ambiente</td><td>Tipo Ambiente</td><td>Tipo Sensore</td><td>Unit‡ di Misura</td>"
				+ "<td>Marca Sensore</td><td>Modello Sensore</td><td>Codice Sensore</td><td>Posizione</td></tr>");
		if (unaListaDatiInstallazione != null) {
			for (DatoInstallazione datoInstallazione : unaListaDatiInstallazione) {
				risultato.append("<tr><td>" + datoInstallazione.getNomeAmbiente() + "</td>"
						+ "<td>" + datoInstallazione.getTipoAmbiente() + "</td>"
						+ "<td>" + datoInstallazione.getTipoSensore() + "</td>"
						+ "<td>" + datoInstallazione.getUnit‡DiMisura() + "</td>"
						+ "<td>" + datoInstallazione.getMarcaSensore() + "</td>"
						+ "<td>" + datoInstallazione.getModelloSensore() + "</td>"
						+ "<td>" + datoInstallazione.getCodiceSensore() + "</td>"
						+ "<td>" + datoInstallazione.getPosizioneSensore() + "</td></tr>"); 
			}
		}
		risultato.append("</table>");
		return risultato.toString();	
	}
	
	/**
	 * Il metodo visualizza un form per l'inserimento di un nuovo dato di installazione
	 * 
	 * @param unaListaTipiSensori lista dei tipi di sensori presenti nel sistema associati all'utente selezionato
	 * @return stringa fomrattata per la visualizzazione
	 */
	public String formInserisciDatoInstallazione(LinkedList<String> unaListaTipiSensori) {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);

		risultato.append("<strong>Inserisci dato installazione:</strong> <br><br>"
					+ 	"<label> Dati Ambiente: </label>"
					+	"<input name=\"insnomeambiente\" type=\"text\" placeholder=\"Nome Ambiente\" required>&nbsp;&nbsp;"
					+	"<input name=\"instipoambiente\" type=\"text\" placeholder=\"Tipo Ambiente\" required>&nbsp;&nbsp;"
					+ 	"<br><br>"
					+ 	"<label> Dati Sensore: </label>"
					+	"<select name=\"instiposensore\">");
		if(unaListaTipiSensori != null) {
			for (String tipoSensore : unaListaTipiSensori) {
				risultato.append("<option value=\"" + tipoSensore+"\">" + tipoSensore+ "</option>");
			}
		}
		risultato.append("</select>" + "&nbsp;&nbsp;"
				+	"<input name=\"insmarcasensore\" type=\"text\" placeholder=\"Marca Sensore\" required>&nbsp;&nbsp;"
				+	"<input name=\"insmodellosensore\" type=\"text\" placeholder=\"Modello Sensore\" required>&nbsp;&nbsp;"
				+	"<input name=\"insidsensore\" type=\"text\" placeholder=\"ID Sensore\" required>&nbsp;&nbsp;"
				+	"<input name=\"insposizionesensore\" type=\"text\" placeholder=\"Posizione Sensore\" required>&nbsp;&nbsp;"
				+	"<br><br>"
				+	"<button id=\"formDatiInstallazioneBtn\" name=\"form_dati_installazione_button\" type=\"submit\">Inserisci</button>"
				+   "<hr>");
		
		return risultato.toString();
	}
	
	/**
	 * Il metodo visualizza un form per inserire un nuovo tipo di sensore, selezionando una configurazione della struttura dei dati che trasmette
	 * @param unaMail identificativa dell'utente target della modifica
	 * @return stringa formattata per la visualizzazione
	 */
	public String formInserisciNuovoTipoSensore(String[] delleStruttureRilevazioni) {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);
		
		risultato.append("<strong>Inserisci un nuovo tipo di sensore:</strong><br><br>"		
			+	"<input name=\"insnuovotiposensore\" type=\"text\" placeholder=\"Tipo Sensore\" required>&nbsp;&nbsp;"
			+	"<input name=\"insunitamisura\" type=\"text\" placeholder=\"Unit‡ di Misura\" required>&nbsp;&nbsp;"
			+	 "<select name=\"insstrutturarilevazione\">");
		if(delleStruttureRilevazioni != null) {
			for (String strutturaRilevazione : delleStruttureRilevazioni) {
				risultato.append("<option value=\"" + strutturaRilevazione+"\">" + strutturaRilevazione+ "</option>");
			}
		}
		risultato.append("</select>" + "&nbsp;&nbsp;"
				+ "<button id=\"formDatiInstallazioneBtn\" name=\"form_dati_installazione_button\" type=\"submit\">Inserisci</button>"
				+ "<hr>");
		
		return risultato.toString();
	}
	
	/**
	 * Il metodo visualizza un form per la modifica della posizione di un sensore all'interno di un dato di installazione
	 * @param unaListaDatiInstallazione lista dei dati di installazione associati all'utente selezionato dall'amministratore
	 * @return stringa fomrattata per la visualizzazione del form richiesto
	 */
	public String formModificaDatoInstallazione(LinkedList<DatoInstallazione> unaListaDatiInstallazione) {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);
		
		risultato.append("<strong>Seleziona il dato di installazione da modificare:</strong> <br><br>"
				+ 	"<select name=\"modificasensore\">");
		if(unaListaDatiInstallazione != null) {
			for (DatoInstallazione datoInstallazione : unaListaDatiInstallazione) {
				risultato.append("<option value=\"" + datoInstallazione.getNomeAmbiente() + "_" + datoInstallazione.getCodiceSensore() 
				+ "\">" + datoInstallazione.getNomeAmbiente() + " - " + datoInstallazione.getTipoSensore() + " - " + datoInstallazione.getCodiceSensore() + "</option>");
			}
		}
		
		risultato.append("</select>" + "&nbsp;&nbsp;"
				+ "<input name=\"modposizionesensore\" type=\"text\" placeholder=\"Nuova Posizione\" value=\"\" maxlength=\"4\" required></input>&nbsp;&nbsp;"
				+ "<button type=\"submit\" name=\"modifica_dato_installazione\">Modifica Dato Installazione</button><br>"
				+ "<hr>");
		
		return risultato.toString();
	}
		
	/**
	 * Il metodo visualizza un form per eliminare un ambiente associato all'utente dal sistema
	 * @param unaListaNomiAmbienti lista degli ambienti associati all'utente selezionato dall'amministratore
	 * @return stringa formattata per la visualizzazione del form richiesto
	 */
	public String formEliminaAmbiente(LinkedList<String> unaListaNomiAmbienti) {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);
		
		risultato.append("<strong>Seleziona l'ambiente da eliminare:</strong> <br><br>"
				+ "<select name=\"eliminaambiente\">");
		if(unaListaNomiAmbienti != null) {
			for (String nomeAmbiente : unaListaNomiAmbienti) {
				risultato.append("<option value=\"" + nomeAmbiente +"\">" + nomeAmbiente + "</option>");
			}
		}
		risultato.append("</select>" + "&nbsp;&nbsp;"
				+ "<button type=\"submit\" name=\"elimina_ambiente\" class=\"danger\"  onClick=\"return window.confirm('Sei sicuro di voler eliminare questo ambiente?')\">Elimina Ambiente</button><br>"
				+ "<hr>");
		
		return risultato.toString();
	}

	/**
	 * Il metodo visualizza un form per eliminare un dato di installazione
	 * 
	 * @param unaMail mail identificativa dell'utente target della richiesta
	 * @return stringa formattata per la visualizzazione
	 */
	public String formEliminaSensore(LinkedList<DatoInstallazione> unaListaDatiInstallazione) {
		int sbsize = 128;
		StringBuilder risultato = new StringBuilder(sbsize);
		
		risultato.append("<strong>Seleziona il sensore da eliminare:</strong> <br><br>"
				+	"<select name=\"eliminasensore\">");
		if(unaListaDatiInstallazione != null) {
			for(DatoInstallazione dato : unaListaDatiInstallazione) {
				risultato.append("<option value=\"" + dato.getCodiceSensore() +"\">" + dato.getNomeAmbiente()+ " - " + dato.getTipoSensore() + " - " + dato.getCodiceSensore() + "</option>");
			}
		}
		
		risultato.append("</select>" + "&nbsp;&nbsp;"
				+	"<button type=\"submit\" name=\"elimina_sensore\" class=\"danger\" onClick=\"return window.confirm('Sei sicuro di voler eliminare questo dato?')\">Elimina Sensore</button><br>");
			
		return risultato.toString();
	}
	
	
}