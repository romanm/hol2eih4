var app = angular.module('MedProcedureOperationApp', []);
app.controller('MedProcedureOperationCtrl', function myCtrlF($scope, $http) {
	console.log('MedProcedureOperationCtrl');
	$scope.error = [];
	var errorHandle = function(response){
		$scope.error.push(response);
		console.log($scope.error);
	}
	var siblingLevel = 0;
	$http.get("/v/siblingProcedure/"+siblingLevel).success(function(response) {
		$scope.medProcedureDb = response;
	}).error(errorHandle);
	$scope.operationDb = [];
	$http.get("/v/operation/group").success(function(response) {
		$scope.operationDb = response;
	});
	$scope.procedureOperation = [];
	$scope.mapProcedureOperation = {p:{},o:{}};
	var setMapProcedureOperation = function(procedureOperation){
		angular.forEach($scope.procedureOperation, function(value, key) {
			if(this.p[value.PROCEDURE_CODE] == null){
				this.p[value.PROCEDURE_CODE] = key;
			}
			if(this.o[value.OPERATION_CODE] == null){
				this.o[value.OPERATION_CODE] = key;
			}
		}, $scope.mapProcedureOperation);
	}
	$http.get("/v/procedureOperation").success(function(response) {
		$scope.procedureOperation = response;
		setMapProcedureOperation();
	}).error(errorHandle);

	$scope.$watch("myCtrl.seekText", function handleChange( newValue, oldValue ) {
		if(newValue != null){
			$scope.procedureViewType = 'seek';
			var seekText = newValue.replace(".","-");
			$http.get("/v/seekProcedure/"+seekText).success(function(response) {
				$scope.seekProcedure = response;
			});
			$http.get("/v/operation/seek/"+newValue).success(function(response) {
				$scope.seekOperation = response;
			});
		}
	});

	$scope.medProcedure  = medProcedure;
	$scope.viewTab = "linking";//linked
	
	$scope.filterCode = '';
	$scope.changeFilterCode = function (){
		if($scope.filterCode == ''){
			$scope.filterCode = '7';
		}else{
			$scope.filterCode = '';
		}
	}

	$scope.procedureViewType = "navigation";
	$scope.gotoNavigation = function (){
		console.log($scope.procedureViewType);
		console.log("----gotoNavigation---------------");
		$scope.procedureViewType = "navigation";
		console.log($scope.procedureViewType);
	}
	$scope.seekUpdate = function (){
		console.log($scope.procedureViewType);
		console.log("-----------seekUpdate--------");
		$scope.procedureViewType = "seek";
		console.log($scope.procedureViewType);
	}
	var openProcedureTreeTo = function (code, level, procedureList){
		angular.forEach(procedureList, function(procedure) {
			if(procedure.PROCEDURE_CODE == code.substring(0,level)){
				$scope.openChild(procedure);
				if(procedure.procedure == null){
					$http.get("/v/siblingProcedure/"+procedure.PROCEDURE_ID).success(function(response) {
						procedure.procedure = response;
						if(procedure.procedure.length != 0){
							if(code.length > level){
								openProcedureTreeTo(code, level+1, procedure.procedure);
							}
						}
					}).error(errorHandle);
				}
			}
		});
	}
	$scope.gotoNavigationGroup = function (procedure){
		console.log("----gotoNavigationGroup---------------");
		openProcedureTreeTo(procedure.PROCEDURE_CODE, 1, $scope.medProcedureDb);
		console.log($scope.medProcedureDb);
		$scope.procedureViewType = "navigation";
	}
	$scope.openChildProcedureDb = function (procedure){
		$scope.openChild(procedure);
		if(procedure.procedure == null){
			var parentId = procedure.PROCEDURE_ID;
			$http.get("/v/siblingProcedure/"+parentId).success(function(response) {
				procedure.procedure = response;
				if(response.length == 0){
					checkToSaveProcedure(procedure);
				}
			}).error(errorHandle);
		}else{
			checkToSaveProcedure(procedure);
		}
	}
	
	$scope.isShowSql = false;

	$scope.isOperationGroup = function (operation){
		return !isNaN(code.substring(0,1));
	}
	$scope.isProcedureGroup = function (code){
		return !isNaN(code.substring(0,1));
	}
	// prepare to save and save
	$scope.procedureToSave = {};
	$scope.operationToSave = {};
	$scope.isToSaveOperation = function (operation){
		return operation.OPERATION_ID != null && $scope.operationToSave.OPERATION_ID == operation.OPERATION_ID;
	}
	$scope.isToSaveProcedure = function (procedure){
		return $scope.procedureToSave.PROCEDURE_ID == procedure.PROCEDURE_ID;
	}
	$scope.saveProceduteToOperation = function (){
		var insertProcedureToOperation = {
				"PROCEDURE_CODE":$scope.procedureToSave.PROCEDURE_CODE
				,"OPERATION_CODE":$scope.operationToSave.OPERATION_CODE};
		$http.post("/v/saveProceduteToOperation", insertProcedureToOperation).success(function(response) {
			console.log(response);
		});
	}
	$scope.toSaveOperation = function (operation){
		$scope.operationToSave = operation;
	}
	$scope.toSaveProcedure = function (procedure){
		$scope.procedureToSave = procedure;
	}
	// prepare to save and save END

	$scope.showSqlInsert = function (v, parentV){
		var procedureParentId = 0;
		if(parentV != null){
			procedureParentId = parentV.id;
		}
		var insert = 
		"insert into procedure (procedure_id, procedure_parent_id, procedure_code, procedure_name) values (" +
		v.id +
		", " +
		procedureParentId +
		", '" +
		v.codeId +
		"', '" +
		replaceAll(v.name,"'","’") +
		"');";
		return insert;
	}
	$scope.showSql = function (){
		$scope.isShowSql = !$scope.isShowSql;
	}

	$scope.isOpenAll = false;
	
	$scope.openAll = function (){
		$scope.isOpenAll = !$scope.isOpenAll;
	}
	
	$scope.isOpen = function (procedure){
		if($scope.isOpenAll) return true;
		return procedure.open;
	}
	$scope.openOperationDb = function (operation){
		$scope.openChild(operation);
		if(operation.operation == null){
			$http.get("/v/operation/operation/"+operation.OPERATION_SUBGROUP_ID).success(function(response) {
				operation.operation = response;
			});
		}
	}
	$scope.openOperationSubGroupDb = function (operation){
		$scope.openChild(operation);
		if(operation.operation == null){
			$http.get("/v/operation/subgroup/"+operation.OPERATION_GROUP_ID).success(function(response) {
				operation.operation = response;
			});
		}
	}
	checkToSaveProcedure = function (procedure){
		if(procedure.PROCEDURE_CODE.split(".").length == 2){
			$scope.toSaveProcedure(procedure)
		}
	}
	$scope.openChild = function (procedure){
		procedure.open = !procedure.open;
	}
});

var replaceAll = function (str, f, r){
	if(str.indexOf(f) >= 0){
		return replaceAll(str.replace(f,r),f,r);
	}
	return str;
}


//trim in all values of object
var trimValuesOfObject = function(obj){
	for(var k in obj)
		obj[k] = obj[k].trim();
	return obj;
};

var push = function (vParent, v){
	if(!("procedure" in vParent))
		vParent.procedure = [];
//	vParent.procedure.push(cleanProcedureAttribute(v));
	vParent.procedure.push(v);
}

var cleanProcedureAttribute = function (v){
	return {code:v.codeId,name:v.name,id:v.id}
}

var setCodeId = function (v){
	v.codeId = v.code;
	if(v.code2 > 0){
		v.codeId += "."+(v.code2<10?"0":"")+v.code2;
	}
}

