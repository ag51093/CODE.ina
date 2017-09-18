<%--
  --   Description: Questa pagina contiene l'interfaccia di gestione del Trasferimento Automatico per i clienti Standard
  --   Developer :  Antonio Garofalo, Matteo Forina
  --%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Trasferimento Automatico</title>
        <link rel="stylesheet" type="text/css" href="css/main.css">
    </head>
    <style>
    	input {
    		color: #4662ab;
    		border: 0px;
    		height: auto;
    		border-bottom: 1px solid;
    		text-align: center;
		}
		
		hr {
			border-top: 1px solid #4662ab;
			border-bottom: none;
		}
    </style>
    <body>
        <div>
            <ul class="navbar card">
                <li><a class="navbar" href="login">Logout</a></li>
                <li><a class="navbar" href="homeStandard.jsp">${utenteloggato}</a></li>
                <li><a href="homeStandard.jsp"><img src="res/logowatcher.png" alt="logo" id="logo"></a></li>
            </ul>
        </div>
        <div>
            <ul class="sidebar card">
                <li><a class="sidebar" href="dashboard">Dashboard</a></li>
                <li><a class="sidebar" href="datiRilevazioni">Dati Rilevazioni</a></li>
                <li><a class="sidebar" href="datiInstallazione">Dati Installazione</a></li>
                <li><a class="sidebar active" href="trasferimento">Trasferimento Automatico</a></li>
            </ul>
        </div>
         <div class="card">
			<form action="trasferimento" method="post">
				<input type="hidden" name="idform" value="cambiastatotrasfaut">
				<%-- Stampa un form per la modifica l'attivazione/disattivazione del Trasferimento Automatico --%>
				<strong>Modifica stato:</strong>
				<br>
				(Stato attuale: <strong>${statotrasferimento}</strong>)
				&nbsp;&nbsp;&nbsp;
				<button type="submit" name="conferma" onClick="return window.confirm('Sei sicuro di voler cambiare lo stato del Trasferimento Automatico?')">Cambia Stato</button><br>
			</form>
			<hr>
			<br>
			<form action="trasferimento" method="post">
				<input type="hidden" name="idform" value="personalizzatrasfaut"/>
				<%-- Stampa un form per la personalizzazione del Trasferimento Automatico--%>
				${formpersonalizzatrasferimento}
			</form>
			<br>
			<div class="error">
        		${erroreinserimento}
        	</div>
		</div>
    </body>
</html>