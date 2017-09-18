<%--
  --   Description: Questa pagina contiene l'interfaccia di gestione dei clienti per gli Amministratori
  --   Developer :  Antonio Garofalo, Matteo Forina
  --%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Gestione Clienti</title>
        <link rel="stylesheet" type="text/css" href="css/main.css">
        <link rel="stylesheet" type="text/css" href="css/home.css">
    </head>
    <style>
    	button.danger{
    		background: #ffffff;
   			color: #d53232;
    		border-color: #d53232;
		}

		button.danger:hover{
			background : #d53232;
  			color : white;
  			border-color: #d53232;
		}
		
		hr {
			border-top: 1px solid #4662ab;
			border-bottom: none;
		}
				
		div.error {
			color: #d53232;
			font-size: 12px;			
		} 
    	
    </style>
   
    <body>
        <div>
            <ul class="navbar card">
                <li><a class="navbar" href="login">Logout</a></li>
                <li><a class="navbar" href="homeAmministratore.jsp">${utenteloggato}</a></li>
                <li><a href="homeAmministratore.jsp"><img src="res/logowatcher.png" alt="logo" id="logo"></a></li>
            </ul>
        </div>
        <div>
            <ul class="sidebar card">
                <li><a class="sidebar" href="dashboard">Dashboard</a></li>
                <li><a class="sidebar" href="datiRilevazioni">Dati Rilevazioni</a></li>
                <li><a class="sidebar" href="datiInstallazione">Dati Installazione</a></li>
                <li><a class="sidebar active" href="gestioneClienti">Gestione Clienti&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
            </ul>
        </div>
         <div class="card">
         	<form id="formInserisciUtente" action="gestioneClienti" method="post">
         		<%-- Stampa un form per l'inserimento di un nuovo cliente--%>
         		<strong>Inserisci nuovo cliente:</strong>
         		<br><br>
        		<input type="hidden" name="idform" value="inseriscicliente"  />
        		<input type="text" name="emailutente" placeholder="Email" value="" required>
        		<input type="password" name="passwordutente" placeholder="Password" value="" required>&nbsp;&nbsp;
        		<button type="submit" name="">Inserisci Cliente</button><br>
        		<hr>
        	</form>
        	<br>
         	<form id="formEliminaUtente" action="gestioneClienti" method="post">
        		<input type="hidden" name="idform" value="eliminacliente"  />
        		<%-- Stampa un form per eliminazione un cliente--%>
        		${formeliminaclienti}
        		<button class="danger" type="submit" name="elimina" onClick="return window.confirm('Sei sicuro di voler eliminare questo cliente?')">Elimina Cliente</button><br>
        		<hr>
        	</form>
        	<br>
        	<div class="error">
        		<%-- Stampa un messaggio di errore se questo è presente --%>
        		${messaggioerrore}
        	</div>
         </div>
    </body>
</html>
