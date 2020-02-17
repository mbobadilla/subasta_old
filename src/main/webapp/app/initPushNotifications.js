	var deviceParameters = {
			registrationId: undefined,
			platform: "web",
			version : "version",
			model: "model"
	}

    document.addEventListener('deviceready', function(){

	  	   deviceParameters.platform = device.platform;
		   deviceParameters.version = device.version;
		   deviceParameters.model = device.model;
		  
	        try {
	    
	            var push = PushNotification.init({
	                "android": {
	                    "senderID": "368696538597",
	                    "icon": "logo",
	                    "forceShow": "true"
	                },
	                "ios": {
	                    "alert": "true",
	                    "badge": "true",
	                    "sound": "true"
	                },
	                "windows": {}
	              });
	            
	               push.on('registration', function(data) {
	            	  deviceParameters.registrationId = data.registrationId;
	               });
	              
	               push.on('notification', function(data) {
	                	//alert(data.additionalData.notificationType);
	               });
	    
	               push.on('error', function(e) {
	                   console.log(e.message);
	               });
	               
	           } catch (e) {
	                console.log(e);
	            }
	        
	    }, false);
	

	
	