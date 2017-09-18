<%--
  --   Description: Questa pagina contiene l'interfaccia del login
  --   Developer :  Antonio Garofalo, Matteo Forina, Ivan Lamparelli
  --%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="ISO-8859-1">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Watcher</title>
        <link rel="stylesheet" type="text/css" href="css/main.css">
        <link rel="stylesheet" type="text/css" href="css/login.css">
    </head>
    <style>
    	div.error {
			color: #d53232;
			text-align: center;
			font-size: 12px;			
		} 
    </style>
    <body>
        <div class="login">
            <img src="res/logowatcher.png" alt="logo">
            <br>
            <br>
            <%-- Form per l'autenticazione dell'utente nel sistema --%>
            <form method="post" action="login">
                <br>
                <input name="email" type="text" placeholder="Email" required/>
                <br>
                <br>
                <input name="password" type="password" placeholder="Password" required/>
                <br>
                <br>
                <div class="error">
                	<%-- Stampa un messaggio di errore, se presente, e successivamente rimuove la variabile globale corrispondente dal sistema --%>
                	${errore}
            	</div>
                <br>
                <button id="loginBtn" name="login_button" type="submit">Login</button>
                <br>
            </form>
        </div>
    </body>
</html>