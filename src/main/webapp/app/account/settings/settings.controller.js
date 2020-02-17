(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('SettingsController', SettingsController);

    SettingsController.$inject = ['Principal', 'Auth', 'JhiLanguageService', '$translate', '$http', 'ServerURL'];

    function SettingsController (Principal, Auth, JhiLanguageService, $translate, $http, ServerURL) {
        var vm = this;

        vm.error = null;
        vm.save = save;
        vm.settingsAccount = null;
        vm.success = null;
        vm.resendEmail = resendEmail;
        
        var dateToUTCMillis = function(d) {
        	var date = new Date(d);
        	var time = date.getTime();
        	var offset = date.getTimezoneOffset();
            offset = offset * 60000;
            return new Date(time + offset);
        };
        
        function resendEmail () {
    		$http({
    		  method: 'GET',
    		  url: ServerURL + 'api/reSendValidationEmail'
    		}).then(function() {
    			//success
    			console.log("Email de validacion enviado");
    		});
    	};

        /**
         * Store the "settings account" in a separate variable, and not in the shared "account" variable.
         */
        var copyAccount = function (account) {
            return {
                activated: account.activated,
                email: account.email,
                firstName: account.firstName,
                langKey: account.langKey,
                lastName: account.lastName,
                login: account.login,
                dni: account.dni,
                birthday: account.birthday !== null ? dateToUTCMillis(account.birthday) : null,
                address: account.address,
                cellPhone: account.cellPhone,
                phoneNotifications: account.phoneNotifications,
                emailNotifications: account.emailNotifications,
                phoneValid: account.phoneValid,
                emailValid: account.emailValid
            };
        };

        Principal.identity().then(function(account) {
            vm.settingsAccount = copyAccount(account);
        });

        function save () {
            Auth.updateAccount(vm.settingsAccount).then(function() {
                vm.error = null;
                vm.success = 'OK';
                Principal.identity(true).then(function(account) {
                    vm.settingsAccount = copyAccount(account);
                });
                JhiLanguageService.getCurrent().then(function(current) {
                    if (vm.settingsAccount.langKey !== current) {
                        $translate.use(vm.settingsAccount.langKey);
                    }
                });
            }).catch(function() {
                vm.success = null;
                vm.error = 'ERROR';
            });
        }
    }
})();
