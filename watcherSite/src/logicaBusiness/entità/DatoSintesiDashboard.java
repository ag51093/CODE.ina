package logicaBusiness.entità;

/**
 * Classe per raccogliere le informazioni su un dato di sintesi della dashboard
 * @author Antonio Garofalo
 *
 */
public class DatoSintesiDashboard {
	private String nomeAmbiente;
	private String tipoSensore;
	private double mediaValori;
	private String unitaDiMisura;
	
	public DatoSintesiDashboard(String nomeAmbiente, String tipoSensore, double unaMediaValori, String unaUnitaDiMisura) {
		super();
		this.nomeAmbiente = nomeAmbiente;
		this.tipoSensore = tipoSensore;
		this.mediaValori = unaMediaValori;
		this.unitaDiMisura = unaUnitaDiMisura;
	}
	
	public String getNomeAmbiente() {
		return nomeAmbiente;
	}

	public String getTipoSensore() {
		return tipoSensore;
	}

	public double getMediaValori() {
		return mediaValori;
	}

	public String getUnitaDiMisura() {
		return unitaDiMisura;
	}

}
