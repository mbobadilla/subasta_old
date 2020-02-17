(function() {
    'use strict';

    var app = angular
        .module('subastadosApp', [
            'ngStorage',
            'tmh.dynamicLocale',
            'pascalprecht.translate',
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngCacheBuster',
            'ngFileUpload',
            'ui.bootstrap',
            'ui.bootstrap.datetimepicker',
            'ui.router',
            'infinite-scroll',
            // jhipster-needle-angularjs-add-module JHipster will add new module here
            'angular-loading-bar',
            'ngMaterial',
            'timer',
            'ui.carousel',
            'teljs',
            'countrySelect',
            'checklist-model',
            'moment-picker'
        ]);
		
		//app.constant("ServerURL", "http://ventaonline.solaguayre.com.ar/");
		//app.constant("ServerURL", "http://35.172.118.115/");
		//app.constant("ServerURL", "http://192.168.0.3:8080/");
		app.constant("ServerURL", "http://127.0.0.1:8080/");

    
    
        app.run(run);

    run.$inject = ['stateHandler', 'translationHandler', 'MobileControlVersionService', 'Auth'];

    function run(stateHandler, translationHandler, MobileControlVersionService, Auth) {
        stateHandler.initialize();
        translationHandler.initialize();
        
        addMobileResumeListener(MobileControlVersionService, Auth);
    }

    function addMobileResumeListener(MobileControlVersionService, Auth){

    	document.addEventListener("resume", function(){
    		MobileControlVersionService.isValidVersion(function(){
		    	location.reload();
    		}, function(){
				 Auth.logout();
    		});

        }, false);
    }
    
})();

