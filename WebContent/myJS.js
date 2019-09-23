window.onload = customize;

function customize() {
	window.document.getElementById('div3').style.display = 'none';
	
	// initial customize
	if (sessionStorage.getItem("state") === null)
		sessionStorage.setItem("state", "access");
	
	disableButtonsExcept(sessionStorage.getItem("state"));
	//showAppFolder("{\"entries\":[{\".tag\":\"file\",\"name\":\"Prime_Numbers.txt\",\"id\":\"id:a4ayc_80_OEAAAAAAAAAXw\",\"client_modified\":\"2015-05-12T15:50:38Z\",\"server_modified\":\"2015-05-12T15:50:38Z\",\"rev\":\"a1c10ce0dd78\",\"size\":7212,\"path_lower\":\"\/homework\/math\/prime_numbers.txt\",\"path_display\":\"\/Homework\/math\/Prime_Numbers.txt\",\"sharing_info\":{\"read_only\":true,\"parent_shared_folder_id\":\"84528192421\",\"modified_by\":\"dbid:AAH4f99T0taONIb-OurWxbNQ6ywGRopQngc\"},\"is_downloadable\":true,\"property_groups\":[{\"template_id\":\"ptid:1a5n2i6d3OYEAAAAAAAAAYa\",\"fields\":[{\"name\":\"SecurityPolicy\",\"value\":\"Confidential\"}]}],\"has_explicit_shared_members\":false,\"content_hash\":\"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\"},{\".tag\":\"folder\",\"name\":\"math\",\"id\":\"id:a4ayc_80_OEAAAAAAAAAXz\",\"path_lower\":\"\/homework\/math\",\"path_display\":\"\/Homework\/math\",\"sharing_info\":{\"read_only\":false,\"parent_shared_folder_id\":\"84528192421\",\"traverse_only\":false,\"no_access\":false},\"property_groups\":[{\"template_id\":\"ptid:1a5n2i6d3OYEAAAAAAAAAYa\",\"fields\":[{\"name\":\"SecurityPolicy\",\"value\":\"Confidential\"}]}]}],\"cursor\":\"ZtkX9_EHj3x7PMkVuFIhwKYXEpwpLwyxp9vMKomUhllil9q7eWiAu\",\"has_more\":false}");
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
    else if (inputType === "showAppFolder") {
    	var checkBox = window.document.getElementById("cb1");
    	var include_non_downloadable_files = "true";
    	if (checkBox.checked == true) {
    		include_non_downloadable_files = "false";
    	}
    	
    	q_str = 'type=showAppFolder&access_token=' + sessionStorage.getItem("access_token")
    			+ '&path=' + window.document.getElementById('t5').value + "&include_non_downloadable_files=" + include_non_downloadable_files;   	
    	callback = 'doQueryShowAppFolder_back';
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
		//sessionStorage.setItem("state", "extraFeature");
		customize();
	}	
}

function doQueryShowAppFolder_back(result)
{	
	if (result.substring(0,5)=='error') {
		 window.document.getElementById('div3').style.display = 'block';
		 window.document.getElementById('div3').innerHTML="<p style='color:red;'><b>"+result.substring(6)+"</b></p>";
	}
	else {
		//TODO confirmation OK message about upload success
		showAppFolder(result);
		
		//TODO extraFeature
		//sessionStorage.setItem("state", "extraFeature");
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
	document.getElementById('b5').disabled = true;
	
	var buttonIDs = [];
	
	switch (state) {
	case "access":
		buttonIDs.push('b1');
		break;
	case "token":
		buttonIDs.push('b2');
		break;
	case "getAccInfo":
		buttonIDs.push('b3');
		break;
	case "uploadFile":
		buttonIDs.push('b4');
		buttonIDs.push('b5');
		break;
	default:
		//buttonID = 'b4';
		break;
	}
	
	for (var i = 0; i < buttonIDs.length; i++) {
		document.getElementById(buttonIDs[i]).disabled = false;
	}
	
	
}	

function showAppFolder(jsonInput) {
	console.log(jsonInput);
	var parsedJSON = JSON.parse(jsonInput);
	console.log(parsedJSON.entries);
	
	var pathsWOSlash = [];
	var pathsWSlash = [];
	
	for (var i = 0; i < parsedJSON.entries.length; i++) {
	    var fileEntry = parsedJSON.entries[i];    
	    pathsWOSlash.push(fileEntry.path_display.substring(1));
	    pathsWSlash.push(fileEntry.path_display);
	}
	
	var hierarchy = pathsWOSlash.reduce(function(hier,path){
	    var x = hier;
	    path.split('/').forEach(function(item){
	        if(!x[item]){
	            x[item] = {};
	        }
	        x = x[item];
	    });
	    x.path = path;
	    return hier;
	}, {});
	
	
	var makeul = function(hierarchy, classname) {
	    var dirs = Object.keys(hierarchy);
	    var ul = '<ul';
	    if(classname){
	        ul += ' class="' + classname + '"';
	    }
	    ul += '>\n';
	    
	    dirs.forEach(function(dir){
	    	if (dir == "path") {
	    		return;
	    	}	    	
	        var path = hierarchy[dir].path;
	        var keysLen = Object.keys(hierarchy[dir]).length;
	        
	        if(path && keysLen == 1){ // file
	            ul += '<li class="file" data-url="' + path + '">' + dir + '</li>\n';
	        }else{
        	    ul += '<li class="folder">' + dir + '\n';
            	ul += makeul(hierarchy[dir]);
	            ul += '</li>\n';	        		
	        }	        

	    });
	    ul += '</ul>\n';
	    return ul;
	};
	
	treeViewHtml = "<p><b>Tree view of DropBox application folders</b></p>";
	treeViewHtml += makeul(hierarchy, 'base-UL');
	treeViewHtml += "<p><b>List view of DropBox application folders</b></p>";
	treeViewHtml += "<p>";
	
	for (var i = 0; i < pathsWSlash.length; i++) {
	    var pathWSlash = pathsWSlash[i];    
	    treeViewHtml += pathWSlash + "<br>";
	}
	treeViewHtml += "</p>";
	
	window.document.getElementById('div9').innerHTML = treeViewHtml;
}