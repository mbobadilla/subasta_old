(function() {
    'use strict';

    angular
        .module('subastadosApp')
        .controller('RegisterController', RegisterController);


    RegisterController.$inject = ['$translate', '$timeout', 'Auth', 'LoginService', 'errorConstants', '$state', '$stateParams'];

    function RegisterController ($translate, $timeout, Auth, LoginService, errorConstants, $state, $stateParams) {
        var vm = this;

        vm.doNotMatch = null;
        vm.error = null;
        vm.errorUserExists = null;
        vm.login = LoginService.open;
        vm.registerFirstStep = registerFirstStep;
        vm.registerSecondStep = registerSecondStep;
        vm.registerAccount = $stateParams.firstStepAccount || {};
        vm.success = null;
        vm.showSecondStep = true;
        vm.secondStepFail = null;
        vm.phoneValidationFail = null;
        
        var today = new Date();
        vm.minAge = 18;
        vm.minAge = new Date(today.getFullYear() - vm.minAge, today.getMonth(), today.getDate());
        vm.registerAccount.birthday = vm.minAge;
        vm.registerAccount.birthday = vm.minAge;
        vm.registerAccount.emailNotifications = true;
        vm.registerAccount.phoneNotifications = true;
        
        $timeout(function (){angular.element('#login').focus();});
        
        function registerFirstStep () {
            if (vm.registerAccount.password !== vm.confirmPassword) {
                vm.doNotMatch = 'ERROR';
            } else {
                vm.registerAccount.langKey = $translate.use();
                vm.doNotMatch = null;
                vm.error = null;
                vm.errorUserExists = null;
                vm.errorEmailExists = null;

                Auth.createAccountFirstStep(vm.registerAccount).then(function () {
                    vm.registerAccount.password = null;
                    $state.go('register-second-step', {firstStepAccount: vm.registerAccount});
                }).catch(function (response) {
                    vm.success = null;
                    if (response.status === 400 && angular.fromJson(response.data).type === errorConstants.LOGIN_ALREADY_USED_TYPE) {
                        vm.errorUserExists = 'ERROR';
                    } else if (response.status === 400 && angular.fromJson(response.data).type === errorConstants.EMAIL_ALREADY_USED_TYPE) {
                        vm.errorEmailExists = 'ERROR';
                    } else {
                        vm.error = 'ERROR';
                    }
                });
            }
        }
        
        function registerSecondStep () {
        	vm.secondStepFail = null;
            vm.phoneValidationFail = null;
            
            Auth.createAccountSecondStep(vm.registerAccount).then(function () {
            	vm.showSecondStep = false;
                vm.success = 'OK';
            }).catch(function (response) {
            	vm.showSecondStep = false;
                vm.success = null;
                if (response.status === 400 && angular.fromJson(response.data).title === "Bad validation key") {
                    vm.phoneValidationFail = 'ERROR';
                } else {
                    vm.secondStepFail = 'ERROR';
                }
            });
        }
    }
})();
