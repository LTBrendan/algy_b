// js/script.js
'use strict';

/**
 * Déclaration de l'application routeApp
 */
var routeApp = angular.module('routeApp', [
    // Dépendances du "module"
    'ngRoute'
]);

routeApp.config(['$locationProvider', function($locationProvider){
    $locationProvider.hashPrefix('');
}]);

routeApp.config(['$routeProvider',
    function($routeProvider) {

        // Système de routage
        $routeProvider
            .when('/home', {
                templateUrl: 'html/home.html'
            })
            .when('/manual', {
                templateUrl: 'html/manual.html'
            })
            .when('/about', {
                templateUrl: 'html/about.html'
            })
            .when('/arrays', {
                templateUrl: 'html/variables/arrays.html'
            })
            .when('/values', {
                templateUrl: 'html/variables/values.html'
            })
            .when('/length', {
                templateUrl: 'html/tableaux/length.html'
            })
            .when('/getset', {
                templateUrl: 'html/tableaux/getSet.html'
            })
            .when('/integer', {
                templateUrl: 'html/valeurs/integer.html'
            })
            .when('/char', {
                templateUrl: 'html/valeurs/char.html'
            })
            .when('/read', {
                templateUrl: 'html/valeurs/read.html'
            })
            .when('/write', {
                templateUrl: 'html/valeurs/write.html'
            })
            .when('/maths', {
                templateUrl: 'html/maths/maths.html'
            })
            .when('/if', {
                templateUrl: 'html/logique/if.html'
            })
            .when('/logical', {
                templateUrl: 'html/logique/logical.html'
            })
            .when('/new', {
                templateUrl: 'html/macro/new.html'
            })
            .when('/call', {
                templateUrl: 'html/macro/call.html'
            })
            .when('/cond', {
                templateUrl: 'html/macro/cond.html'
            })
            .otherwise({
                redirectTo: '/home'
            });
    }
]);