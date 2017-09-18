package logicaPresentazione;

import java.util.LinkedList;

import logicaBusiness.entità.Utente;

/**
 * La classe organizza la visualizzazione dei form per la gestione dei clienti
 * 
 * @author Antonio Garofalo, Matteo Forina
 *
 */
public class VistaClienti {

	
	/**
	 * Compone l'oggetto della mail inviata per avvisare un nuovo cliente della registrazione dell'account
	 * @return L'oggetto della mail
	 */
	public static String scriviOggettoMailRegistrazione() {
		String oggetto = "Credenziali Utente";
		
		return oggetto;
	}
	
	/**
	 * Compone il testo della mail inviata per avvisare un nuovo cliente della registrazione dell'account
	 * @param unaMail L'email registrata
	 * @param unaPassword La password registrata
	 * @return Il corpo della mail
	 */
	public static String scriviCorpoMailRegistrazione(String unaMail, String unaPassword) {
		String corpo = "Nuovo Utente registrato!"
				+ "<br>È possibile accedere al suo account utilizzando i seguenti dati:"
				+ "<br><br>- <strong>Email</strong>: " + unaMail
				+ "<br>- <strong>Password</strong>: " + unaPassword
				+ "<br><br>Le auguriamo buona giornata.";
		
		return corpo;
	}
	
	
	/**
	 * Il metodo visualizza la lista dei clienti del sistema
	 * 
	 * @return stringa formattata per la visualizzazione dei clienti
	 */
	public String formEliminaClienti(LinkedList<Utente> listaClienti) {
		String risultato = "";
		int sbsize = 128;
		StringBuilder stringBuilder = new StringBuilder(sbsize);
		stringBuilder.append(risultato);
				
		if (listaClienti == null) {
			listaClienti = new LinkedList<Utente>();
		}
		
		stringBuilder.append("<strong>Seleziona il cliente da eliminare:</strong>&emsp;&emsp;<br><br>"
				+	"<select name=\"clientedaeliminare\">");
		for (Utente nomecliente : listaClienti) {
			stringBuilder.append("<option value=\"" + nomecliente.getEmail() +"\">" + nomecliente.getEmail() + "</option>");
		}
		stringBuilder.append("</select>" + "&nbsp;&nbsp;&nbsp;");
		
		return stringBuilder.toString();
	}
}
