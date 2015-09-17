var hol2eih3App = angular.module('hol2eih3App', ['textAngular','ui.bootstrap']);

var parameters = {};
if(window.location.search){
//	$.each(window.location.search.split("?")[1].split("&"), function(index, value){
	angular.forEach(window.location.search.split("?")[1].split("&"), function(value, index){
		var par = value.split("=");
		parameters[par[0]] = par[1];
	});
}
console.log(parameters);

var initDocCookie = function($scope){
	console.log(document.cookie);
	$scope.docCooke = {};
	var docCooke = document.cookie.split(";");
	console.log(docCooke);
	for(var i=0; i<docCooke.length; i++) {
		var c = docCooke[i].split("=");
		$scope.docCooke[c[0].trim()] = c[1].trim();
	}
	console.log($scope.docCooke);
	$scope.inputType = $scope.docCooke.inputType;
}
