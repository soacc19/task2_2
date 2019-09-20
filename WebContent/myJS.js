window.onload = customize;

function customize() {
	window.document.getElementById('div3').style.display = 'none';
	
	// initial customize
	if (sessionStorage.getItem("state") === null)
		sessionStorage.setItem("state", "access");
	
	disableButtonsExcept(sessionStorage.getItem("state"));
}

function doQuery(inputType)
{
    window.document.getElementById('div3').style.display = 'none';
    var q_str = null;
    var callback = null;
    
    if (inputType === "access") {
    	q_str = 'type=access';
    	callback = 'doQueryAccess_back';
    }
    else if (inputType === "token") {
       	var code = parseUrl(window.location).search;
    	code = code.replace('?', '&');
    	sessionStorage.setItem("code", code);
    	
    	q_str = 'type=token' + sessionStorage.getItem("code");
    	callback = 'doQueryToken_back';
    }
    else if (inputType === "getAccInfo") {
    	q_str = 'type=getAccInfo&access_token=' + sessionStorage.getItem("access_token")
    			+ '&account_id=' + sessionStorage.getItem("account_id");   	
    	callback = 'doQueryGetAccInfo_back';
    }
    else if (inputType === "uploadFile") {    	
    	q_str = 'type=uploadFile&access_token=' + sessionStorage.getItem("access_token")
    			+ '&path=' + window.document.getElementById('t4').value;   	
    	callback = 'doQueryUploadFile_back';
    }
    else {
    	return;
    }
	
	doAjax('DBServlet', q_str, callback, 'post', 0);
}

function doQueryAccess_back(result)
{
	window.location = result;	
	sessionStorage.setItem("state", "token");
	// customize() is already invoked via redirectURI
}

function doQueryToken_back(result)
{
	var tokenJSON = JSON.parse(result);
	sessionStorage.setItem("access_token", tokenJSON.access_token);
	sessionStorage.setItem("account_id", tokenJSON.account_id);
	
	sessionStorage.setItem("state", "getAccInfo");
	customize();
}

function doQueryGetAccInfo_back(result)
{	
	var tokenJSON = JSON.parse(result);
	sessionStorage.setItem("given_name", tokenJSON.name.given_name);
	sessionStorage.setItem("surname", tokenJSON.name.surname);
	sessionStorage.setItem("email", tokenJSON.email);
	sessionStorage.setItem("profile_photo_url", tokenJSON.profile_photo_url);
	
	window.document.getElementById('td1').innerHTML = sessionStorage.getItem("given_name");
	window.document.getElementById('td2').innerHTML = sessionStorage.getItem("surname");
	window.document.getElementById('td3').innerHTML = sessionStorage.getItem("email");
	window.document.getElementById('img1').src = sessionStorage.getItem("profile_photo_url");
	
	sessionStorage.setItem("state", "uploadFile");
	customize();
}

function doQueryUploadFile_back(result)
{	
	if (result.substring(0,5)=='error') {
		 window.document.getElementById('div3').style.display = 'block';
		 window.document.getElementById('div3').innerHTML="<p style='color:red;'><b>"+result.substring(6)+"</b></p>";
	}
	else {
		//TODO confirmation OK message about upload success
		var tokenJSON = JSON.parse(result);
		
		//TODO extraFeature
		sessionStorage.setItem("state", "extraFeature");
		customize();
	}	
}


function parseUrl(url) {
    var a = document.createElement('a');
    a.href = url;
    return a;
}

function disableButtonsExcept(state) {
	document.getElementById('b1').disabled = true;
	document.getElementById('b2').disabled = true;
	document.getElementById('b3').disabled = true;
	document.getElementById('b4').disabled = true;
	
	var buttonID = null;
	
	switch (state) {
	case "access":
		buttonID = 'b1';
		break;
	case "token":
		buttonID = 'b2';
		break;
	case "getAccInfo":
		buttonID = 'b3';
		break;
	case "uploadFile":
		buttonID = 'b4';
		break;
	default:
		//buttonID = 'b4';
		break;
	}
	
	document.getElementById(buttonID).disabled = false;
}
