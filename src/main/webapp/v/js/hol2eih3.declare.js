var hol2eih3App = angular.module('hol2eih3App', ['textAngular', 'ui.bootstrap']);
//var hol2eih3App = angular.module('hol2eih3App', ['textAngular','ngCookies','ui.bootstrap']);
hol2eih3App.factory();
var parameters = {};
if(window.location.search){
//	$.each(window.location.search.split("?")[1].split("&"), function(index, value){
	angular.forEach(window.location.search.split("?")[1].split("&"), function(value, index){
		var par = value.split("=");
		parameters[par[0]] = par[1];
	});
}