//var initController = function($scope, $http, $filter, $cookies){
var initController = function($scope, $http, $filter){
	console.log("initController");
	$scope.param = parameters;
	$scope.inputType = "text";
	$scope.isRoleRuh = false;
//	initDocCookie($scope);
	
	var isRole = function(role){
		console.log($scope.moveTodayPatients);
		angular.forEach($scope.moveTodayPatients.principal.authorities, function(authoritie, key) {
			if(authoritie.authority.indexOf(role) >= 0){
				$scope.isRoleRuh = true;
			}
		});
	}

	initDateVariables = function(){
		var today = new Date($scope.moveTodayPatients.today);
//		$cookies.put('year', today.getFullYear());
		$scope.excelFileName = "pyx-" +today.getFullYear() + "-v.2.xls";
	}
	
	$scope.readMoveTodayPatients = function(){
		var url = "/readMoveTodayPatients"
		if(parameters.date){
			var url = "/readMove-"+parameters.date+"-Patients"
		}
		console.log(url);
		$http({ method : 'GET', url : url
		}).success(function(data, status, headers, config) {
			$scope.moveTodayPatients = data;
			console.log($scope.moveTodayPatients);
			initDateVariables();
			isRole("ruh");
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
		if(parameters.date){
			/*зчитуання руху з електронки 2 */
			var url3 = "/v/hol1F007Spital-"+parameters.date;
			console.log(url3);
			$http({ method : 'GET', url : url3
			}).success(function(data, status, headers, config) {
				$scope.hol1F007Spital = data;
				console.log($scope.hol1F007Spital);
			}).error(function(data, status, headers, config) {
				$scope.error = data;
			});
			
			/*зчитуання руху з електронки 1 */
			var url2 = "/v/departmentPatientsMove-"+parameters.date;
			console.log(url2);
			$http({ method : 'GET', url : url2
			}).success(function(data, status, headers, config) {
				$scope.allDepartmentPatientsMove = data;
				console.log($scope.departmentPatientsMove);
			}).error(function(data, status, headers, config) {
				$scope.error = data;
			});
		}
	}

	$scope.formatDateyyyyMMdd = function(d){
		return $filter('date')( d, "yyyy-MM-dd")
	}

	$scope.isMyDate = function(d){
		return parameters.date == $scope.formatDateyyyyMMdd(d);
	}

	$scope.summ = function(fieldName){
		var summ = 0;
		if($scope.moveTodayPatients)
			angular.forEach($scope.moveTodayPatients.moveTodayPatientsList, function(department, key) {
				summ += department[fieldName]/1;
			});
		return summ;
	}

	$scope.summMinusDR = function(fieldName){
		var summ = 0;
		if($scope.moveTodayPatients)
			angular.forEach($scope.moveTodayPatients.moveTodayPatientsList, function(department, key) {
				if(department.DEPARTMENT_ID != 5
				&& department.DEPARTMENT_ID != 22
				){
					summ += department[fieldName];
				}
			});
		return summ;
	}

	$scope.today = new Date();
	$scope.last7day = [$scope.today];
	for (var i = 0; i < 7; i++) {
		$scope.last7day.push(new Date($scope.last7day[i].getTime() - (24*60*60*1000)));
	}
	$scope.last7day.reverse();
	

	if(parameters.date){
		$scope.paramDate = parameters.date;
	}else{
		$scope.paramDate = $filter('date')(new Date(), "yyyy-MM-dd");
	}
	$scope.paramDateDate = new Date(Date.parse($scope.paramDate));
	$scope.addDay = function(date, day){
		var dateOffset = (24*60*60*1000) * day; //5 days
		var myDate = new Date();
		myDate.setTime(date.getTime() + dateOffset);
		return myDate;
	}
}

var initCtrl = function($scope, $http){
	$scope.viewTab = viewTab;
	$scope.error = [];
	$scope.param = parameters;
	console.log($scope.param);
	if(!$scope.param.ix){
		if(window['eixId'] != undefined){
			$scope.param.ix = eixId; 
		}
	}
	$scope.model = {};

	$scope.errorHandle = function(response){
		$scope.error.push(response);
		console.log($scope.error);
	}

	$http({ method : 'GET', url : "/v/show-tables-columns"
	}).success(function(model, status, headers, config) {
		$scope.showTablesColumns = model;
		if($scope.showTablesColumns.principal != null){
			$scope.showTablesColumns.notNulls = {};
			angular.forEach($scope.showTablesColumns.tables, function(tableColumns, keyTableName) {
				$scope.showTablesColumns.notNulls[keyTableName] = {};
				angular.forEach(tableColumns, function(column) {
					if(column.Null=="NO"){
						$scope.showTablesColumns.notNulls[keyTableName][column.Field] = column;
					}
				});
			});
		}
	}).error(function(model, status, headers, config) {
		$scope.error.push(model);
	});
	
	$scope.model = {};

	$http({ method : 'GET', url : "/v/readAuthorityUser"
	}).success(function(model, status, headers, config) {
		$scope.model.authority = model;
		if($scope.model.authority.departmentId){
			$http({ method : 'GET', url : "/v/department-patient/"+$scope.model.authority.departmentId
			}).success(function(model, status, headers, config) {
				$scope.model.departmentPatient = model;
			}).error(function(model, status, headers, config) {
				$scope.error.push(model);
			});
		}
	}).error(function(model, status, headers, config) {
		$scope.error.push(model);
	});

	$scope.$watch("model.authority", function handleChange( newValue, oldValue ) {
		if(newValue != null){
			$http({ method : 'GET', url : "/v/operation/start-lists"
			}).success(function(model, status, headers, config) {
				$scope.model.operationEditLists = model;
				console.log($scope.model.operationEditLists);
				if($scope.model.operationEditLists.principal){
					for (var i = 0; i < $scope.model.operationEditLists.departmentOperation.length; i++) {
						if( $scope.model.operationEditLists.departmentOperation[i].department_id == $scope.model.authority.departmentId ){
							$scope.model.authority.department = $scope.model.operationEditLists.departmentOperation[i];
						}
						
					}
					for (var i = 0; i < $scope.model.operationEditLists.departmentSurgery.length; i++) {
						if( $scope.model.operationEditLists.departmentSurgery[i].personal_id == $scope.model.authority.personalId ){
							$scope.model.authority.personal = $scope.model.operationEditLists.departmentSurgery[i];
						}
					}
				}

			}).error(function(model, status, headers, config) {
				$scope.error.push(model);
			});
		}
	});

	if($scope.param.ix){
		console.log("/v/ix/"+$scope.param.ix+window.location.search);
		$http({ method : 'GET', url : "/v/ix/"+$scope.param.ix+window.location.search
		}).success(function(model, status, headers, config) {
			$scope.ix = model;
			console.log($scope.ix);
			if(angular.isDefined($scope.initOperation)){
				angular.forEach($scope.ix.operationHistoryList, function(oh, key) {
					$scope.initOperation(oh);
				});
			}
		}).error(function(model, status, headers, config) {
			$scope.error.push(model);
		});
	}
}
// ------------IxCtrl
hol2eih3App.controller('IxCtrl', function ($scope, $http, $filter, $sce, $interval) {
	console.log("IxCtrl");
	initCtrl($scope, $http);
	//$interval(frameCtrl, 3000);
	
});
// ------------IxCtrl END
// ------------OperationCodeCtrl
hol2eih3App.controller('OperationCodeCtrl', ['$scope', '$http', '$filter', '$sce', '$interval'
		, function ($scope, $http, $filter, $sce, $interval) {
	console.log("OperationCodeCtrl");
	$scope.fieldsOperationNotNull = ["procedure_moz_id","icd_id","anestesia_id","operation_result_id"];
	$scope.fieldsOperation = {
		beginOp: "Дата/почато - закінчено/тривалість"
		,surgery: "Хірург"
		,operation : "Операція"
		,icd : "Діагноз при операції"
		,department : "Відділення"
		,anesthetist: "Анестезіолог"
		,anestesia: "Анестезія"
		,result: "Результат"
		,complication: "Ускладнення"
	};

	initCtrl($scope, $http);

	$scope.deleteOperationHistory = function(operationHistory){
		$http.post('/deleteOperationHistory-'+operationHistory.operation_history_id).
		then(function(response) {
			console.log("---insertOperationHistory------success--------");
			window.location.replace('/operation-code.html?ix='+$scope.param.ix);
		}, function(response) {
			console.log("---insertOperationHistory-------erros-------");
		});
	}
	$scope.removeOperationHistory = function(operationHistory){
		console.log(operationHistory);
		console.log($scope.operationHistoryIdToDelete+"=="+operationHistory.operation_history_id);
		if(operationHistory.operation_history_id == null){
			$scope.operationHistoryIdToDelete = 0;
		}else
		if($scope.operationHistoryIdToDelete == operationHistory.operation_history_id){
			$scope.operationHistoryIdToDelete = null;
		}else{
			$scope.operationHistoryIdToDelete = operationHistory.operation_history_id;
		}
		console.log($scope.operationHistoryIdToDelete);
	}
	$scope.openToEditPrevious = function(){
		var list = Object.keys($scope.fieldsOperation);
		var currentPosition = list.indexOf($scope.openedToEdit);
		if(currentPosition == 0){
			currentPosition = list.length;
		}
		var previous = list[currentPosition - 1];
		console.log("openToEditPrevious"+$scope.openedToEdit+"/"+list.length+"/"+currentPosition);
		$scope.openToEdit(previous);
	}
	
	$scope.openToEditNext = function(){
		var list = Object.keys($scope.fieldsOperation);
		var currentPosition = list.indexOf($scope.openedToEdit);
		var next = list[currentPosition + 1];
		console.log("openToEditNext "+$scope.openedToEdit+"/"+list.length+"/"+currentPosition);
		$scope.openToEdit(next);
	}

	$scope.selectedToSave = function(fieldName, operationHistory){
		if($scope.operationHistoryToEdit){
			if($scope.operationHistoryToEdit.operation_history_id == operationHistory.operation_history_id){
				if($scope.openedToEdit){
					if($scope.openedToEdit == fieldName){
						return true;
					}
				}
			}
		}
		return false;
	}

	$scope.openToEdit = function(fieldName){
		$scope.openedToEdit = fieldName;
		if($scope.openedToEdit == 'operation'){
			$scope.openChildProcedureDb($scope.procedureOperation);
		}else
		if($scope.openedToEdit == 'icd'){
			$scope.initIcd();
			if($scope.operationCodeCtrl.seekIcd == null){
				if($scope.operationCodeCtrl.seekOperation != null){
					$scope.operationCodeCtrl.seekIcd =
						$scope.operationCodeCtrl.seekOperation;
				}
			}
		}
	}
	//------procedure------Operation-----------
	$scope.changeAnesthetist = function(newValue){
		console.log(newValue);
		$scope.operationHistoryToEdit.anesthetist_id = newValue.personal_id;
		if(newValue.anesthetist_id)
			$scope.operationHistoryToEdit.anesthetist_id = newValue.anesthetist_id;
		$scope.operationHistoryToEdit.anesthetist_name = newValue.personal_username;
		console.log($scope.operationHistoryToEdit);
		updateOperationHistory(null, "anesthetist_id", "int");
	}
	$scope.changeAnesthesia = function(newValue){
		$scope.operationHistoryToEdit.anestesia_id = newValue.anestesia_id;
		$scope.operationHistoryToEdit.anestesia_name = newValue.anestesia_name;
		updateOperationHistory(null, "anestesia_id", "int");
	}
	$scope.changeComplication = function(newValue){
		console.log("-----------");
		console.log(newValue);
		$scope.openedToEdit = "complication";
		$scope.operationHistoryToEdit.operation_complication_id = newValue.operation_complication_id;
//		$scope.operationHistoryToEdit.operation_complication_id = newValue.operation_complication_result_id;
		$scope.operationHistoryToEdit.operation_complication_name = newValue.operation_complication_name;
		console.log($scope.operationHistoryToEdit);
		updateOperationHistory(null, "operation_complication_id", "int");
	}
	$scope.changeResult = function(newValue){
		$scope.operationHistoryToEdit.operation_result_id = newValue.operation_result_id;
		$scope.operationHistoryToEdit.operation_result_name = newValue.operation_result_name;
		updateOperationHistory(null, "operation_result_id", "int");
	}
	$scope.changeDepartment = function(newValue){
		$scope.operationHistoryToEdit.department_id = newValue.department_id;
		$scope.operationHistoryToEdit.department_name = newValue.department_name;
		updateOperationHistory(null, "department_id", "int");
	}
	$scope.changeSurgery = function(newValue){
		if($scope.operationHistoryToEdit){
			$scope.operationHistoryToEdit.personal_id = newValue.personal_id;
			$scope.operationHistoryToEdit.surgery_name = newValue.personal_username;
			updateOperationHistory(null, "personal_id", "int");
		}
	}
	//-------------OperationSaveTimer-------------------------
	$scope.OperationSaveTimer = null;
	$scope.StartOperationSaveTimer = function () {
		$scope.OperationSaveTimerMessage = {};
		$scope.OperationSaveTimerMessage.strObjects = {
			oldValues: {operation_additional:null, icd_additional:null, operation_history_complication:null}
			,lengthChange: {operation_additional:0, icd_additional:0, operation_history_complication:0}
		};
		$scope.OperationSaveTimerMessage.message = "Введення нової операції розпочато. ";
		if($scope.operationHistoryToEdit.operation_history_id){
			$scope.OperationSaveTimerMessage.message = " Внесення змін до операції. ";

		}
		var stopwatch = new Date();
		var fixFirstSave = null;
		stopwatch.setHours(stopwatch.getHours() - stopwatch.getTimezoneOffset()/60);

		$scope.OperationSaveTimer = $interval(function () {
			$scope.OperationSaveTimerMessage.message = "" ;
			angular.forEach($scope.OperationSaveTimerMessage.strObjects.lengthChange , function(value, key) {
				if(value > 0){
					console.log("save "+ key);
					updateOperationHistoryStrfield({field:key, value:$scope.OperationSaveTimerMessage.strObjects.oldValues[key]});
					$scope.OperationSaveTimerMessage.strObjects.lengthChange[key] = 0;
				}
			});
			$scope.myFieldsOperationNotNull = [];
			for (var i = 0; i < $scope.fieldsOperationNotNull.length; i++){
				if($scope.operationHistoryToEdit[$scope.fieldsOperationNotNull[i]] == null){
					$scope.myFieldsOperationNotNull.push($scope.fieldsOperationNotNull[i]);
				}
			}
			if($scope.myFieldsOperationNotNull.length > 0){
				$scope.OperationSaveTimerMessage.message += "Залишилось заповнити " + $scope.myFieldsOperationNotNull.length + " невідмінних поля. ";
			}else if(!$scope.operationHistoryToEdit.operation_history_id){
				if(!fixFirstSave)
					fixFirstSave = $filter('date')(new Date() - stopwatch, 'HH:mm:ss')
					$scope.OperationSaveTimerMessage.message += " Пішов запис після (" + fixFirstSave + ").";
				if(!$scope.operationHistoryToEdit.sendetToSave){
					$scope.operationHistoryToEdit.sendetToSave = true;
					$http.post('/insertOperationHistory', $scope.operationHistoryToEdit).
					then(function(response) {
						console.log("---insertOperationHistory------success--------");
						$scope.operationHistoryToEdit = response.data;
						$scope.operationHistoryToEdit.operationHistoryStart = new Date($scope.operationHistoryToEdit.operation_history_start);
						console.log($scope.operationHistoryToEdit);
					}, function(response) {
						console.log("---insertOperationHistory-------erros-------");
					});
				}else{
					$scope.OperationSaveTimerMessage.message += " Очікування."  ;
				}
			}else{
				if(fixFirstSave != null)
					$scope.OperationSaveTimerMessage.message += " Операція записана (" + fixFirstSave + ").";
				else
					$scope.OperationSaveTimerMessage.message += " Внесення змін до операції. ";
			}
			var time = $filter('date')(new Date() - stopwatch, 'HH:mm:ss');
			$scope.OperationSaveTimerMessage.message += " Витрачено часу. " + time;
		}, 2700);
	};

	$scope.StopOperationSaveTimer = function () {
		$scope.OperationSaveTimerMessage.message = "Timer stopped.";
		if (angular.isDefined($scope.OperationSaveTimer)) {
			$interval.cancel($scope.OperationSaveTimer);
		}
	};
	//-------------OperationSaveTimer-------------------------END
	//
	
	$scope.isProcedureGroup = function (code){
		return !isNaN(code.substring(0,1));
	}
	$scope.procedureOperation = {procedure_moz_id:3552};// group 7 -- хірургічні операції
	$scope.openChild = function (procedure){
		procedure.open = !procedure.open;
	}
	checkToSaveProcedure = function (procedure){
		if((procedure.PROCEDURE_CODE && procedure.PROCEDURE_CODE.split(".").length == 2)
				|| (procedure.procedure_moz_code && procedure.procedure_moz_code.split(".").length == 2)){
			$scope.toSaveProcedure(procedure)
		}
	}
	$scope.openChildProcedureDb = function (procedure){
		$scope.openChild(procedure);
		if(procedure.procedure == null){
			var siblingLevel = procedure.procedure_moz_id;
			$http.get("/v/siblingProcedureOperation/"+siblingLevel).success(function(response) {
				procedure.procedure = response;
				if(response.length == 0){
					$scope.toSaveProcedure(procedure);
					//checkToSaveProcedure(procedure);
				}
			}).error($scope.errorHandle);
		}else{
			checkToSaveProcedure(procedure);
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
			if(procedure.procedure_moz_code == code.substring(0,level)){
				$scope.openChild(procedure);
				if(procedure.procedure == null){
					$http.get("/v/siblingProcedureOperation/"+procedure.procedure_moz_id).success(function(response) {
						procedure.procedure = response;
						if(procedure.procedure.length != 0){
							if(code.length > level){
								openProcedureTreeTo(code, level+1, procedure.procedure);
							}
						}
					})
				}
			}
		});
	}
	$scope.gotoNavigationGroup = function (procedure){
		console.log("----gotoNavigationGroup---------------");
		console.log($scope.procedureOperation.procedure);
		openProcedureTreeTo(procedure.PROCEDURE_CODE, 2, $scope.procedureOperation.procedure);
		console.log($scope.procedureOperation.procedure);
		$scope.procedureViewType = "navigation";
	}
	
	//------procedure------Operation-----------END
	$scope.toSaveIcd = function (icd){
		console.log(icd);
		$scope.icdToSave = icd;
		if(icd.icd_id){
			$scope.operationHistoryToEdit.icd_id = icd.icd_id;
			$scope.operationHistoryToEdit.icd_start = icd.icd_start;
			$scope.operationHistoryToEdit.icd_end = icd.icd_end;
		}else if(icd.icd10uatree_id){
			$scope.operationHistoryToEdit.icd_id = icd.icd10uatree_id;
		}
		$scope.operationHistoryToEdit.icd_code = icd.icd_code;
		$scope.operationHistoryToEdit.icd_name = icd.icd_name;
		updateOperationHistory("icd", "icd_id");
	}
	$scope.toSaveProcedure = function (newValue){
		if(newValue.procedure_moz_id){
			$scope.operationHistoryToEdit.procedure_moz_id = newValue.procedure_moz_id;
			$scope.operationHistoryToEdit.procedure_moz_name = newValue.procedure_moz_name;
			$scope.operationHistoryToEdit.procedure_moz_code = newValue.procedure_moz_code;
		}else if(newValue.PROCEDURE_ID){
			$scope.operationHistoryToEdit.procedure_moz_id = newValue.PROCEDURE_ID;
			$scope.operationHistoryToEdit.procedure_moz_name = newValue.PROCEDURE_NAME;
			$scope.operationHistoryToEdit.procedure_moz_code = newValue.PROCEDURE_CODE;
		}
		updateOperationHistory("procedure", "procedure_moz_id");
	}

	//--------update---operation_history--db----------------------
	var updateOperationHistoryStrfield = function(updateParameters){
		if($scope.operationHistoryToEdit && $scope.operationHistoryToEdit.operation_history_id){
			var putUrl = "/operation-history-strfield-where-"
			+ $scope.operationHistoryToEdit.operation_history_id;
			$http.put(putUrl, updateParameters).then(function(response) {
				console.log(putUrl + "--------success--------");
				console.log(response);
			}, function(response) {
				console.log(putUrl + "--------erros-------");
				console.log(response);
			});
		}
	}
	var updateOperationHistory = function(groupName, fieldName, fieldType){
		if($scope.operationHistoryToEdit && $scope.operationHistoryToEdit.operation_history_id){
			var putUrl = "/operation-history";
//			if(fieldType == "int"){
			if(fieldType != null){
				putUrl += "-" + fieldType + "-set-"+fieldName + "-"
				+ $scope.operationHistoryToEdit[fieldName] +
				"-where-" + $scope.operationHistoryToEdit.operation_history_id;
			}else{
				putUrl += "-set-"+ groupName + "-" 
				+ $scope.operationHistoryToEdit[fieldName] +
				"-where-" + $scope.operationHistoryToEdit.operation_history_id;
			}
			console.log(putUrl);
			$http.put(putUrl).then(function(response) {
				console.log(putUrl + "--------success--------");
				console.log(response);
			}, function(response) {
				console.log(putUrl + "--------erros-------");
				console.log(response);
			});
		}
	}

	//--------update---operation_history--db----------------------END
	$scope.setDurationHHMM = function(oh, min){
		var hour = (min - min%60)/60;
		var hourStr = (hour<10?"0":"")+hour;
		var minRest = min - hour*60;
		var minStr = (minRest<10?"0":"")+minRest;
		oh.durationHHMM = hourStr+":"+minStr; 
		oh.operationDurationMin = min;
	}
	$scope.initOperation = function(operation){
		$scope.setDurationHHMM(operation, operation.operation_history_duration/60);
		operation.operationHistoryStart = new Date(operation.operation_history_start);
	}

	var setEndOperation = function(min){
		$scope.setDurationHHMM($scope.operationHistoryToEdit, min);
		$scope.operationHistoryToEdit.operation_history_end = 
			$scope.operationHistoryToEdit.operation_history_start + min*60*1000;
		$scope.operationHistoryToEdit.operation_history_duration = min*60;
	}
	$scope.$watch("operationHistoryToEdit.operationHistoryStart", function handleChange( newValue, oldValue ) {
		if(newValue != null && oldValue != null){
			if($scope.operationHistoryToEdit != null && $scope.operationHistoryToEdit.operation_history_start != null){
				$scope.operationHistoryToEdit.operation_history_start = $scope.operationHistoryToEdit.operationHistoryStart.getTime();
				setEndOperation($scope.operationHistoryToEdit.operationDurationMin);
				updateOperationHistory("start", "operation_history_start");
			}
		}
	});
	$scope.$watch("operationHistoryToEdit.operationDurationMin", function handleChange( newValue, oldValue ) {
		if(newValue != null){
			if(oldValue != null){
				setEndOperation(newValue);
				updateOperationHistory("duration", "operation_history_duration");
			}
		}
	});
	$scope.$watch("operationHistoryToEdit.icd_additional", function handleChange( newValue, oldValue ) {
		if(newValue != null&& oldValue != null){
			changeTextField("icd_additional", newValue)
		}
	});
	$scope.$watch("operationHistoryToEdit.operation_additional", function handleChange( newValue, oldValue ) {
		if(newValue != null&& oldValue != null){
			changeTextField("operation_additional", newValue)
		}
	});
	$scope.$watch("operationHistoryToEdit.operation_history_complication", function handleChange( newValue, oldValue ) {
		if(newValue != null&& oldValue != null){
				changeTextField("operation_history_complication", newValue)
		}
	});
	var changeTextField = function(fieldName, newValue){
		console.log(newValue);
		console.log($scope.OperationSaveTimerMessage.strObjects.oldValues[fieldName]);
		$scope.OperationSaveTimerMessage.strObjects.lengthChange[fieldName] 
			+=  Math.abs( newValue.length - $scope.OperationSaveTimerMessage.strObjects.lengthChange[fieldName]) ;
		$scope.OperationSaveTimerMessage.strObjects.oldValues[fieldName] = newValue;
		console.log($scope.OperationSaveTimerMessage.strObjects);
	}

	$scope.addOperation = function(){
		console.log("----------------");
		$scope.openedToEdit = "beginOp";
		//перевірка на наявність відкритої і не збереженої операції
		for (var i = 0; i < $scope.ix.operationHistoryList.length; i++)
			if(!$scope.ix.operationHistoryList[i].operation_history_id)
				return;
		console.log("----------------");
		$scope.operationHistoryIdToDelete = 0;
		$scope.operationHistoryToEdit = {};
		$scope.ix.operationHistoryList.unshift($scope.operationHistoryToEdit);
		//створити запис операції з інформації про колонки таблиці operation_history
		angular.forEach($scope.showTablesColumns.tables.operation_history, function(oh, key) {
			$scope.operationHistoryToEdit[oh.Field] = null;
		});
		//додати дату операції як поточна дата
		$scope.operationHistoryToEdit.history_id = $scope.param.ix;
		$scope.operationHistoryToEdit.operationHistoryStart = new Date();
		$scope.operationHistoryToEdit.operation_history_start = $scope.operationHistoryToEdit.operationHistoryStart.getTime();
		$scope.operationHistoryToEdit.operation_history_duration = 60; //1m=60s
		setEndOperation($scope.operationHistoryToEdit.operation_history_duration/60);
		console.log($scope.operationHistoryToEdit.operation_history_end);
		$scope.operationHistoryToEdit.personal_id = $scope.model.authority.personal.personal_id;
		$scope.operationHistoryToEdit.surgery_name = $scope.model.authority.personal.personal_username;
		$scope.operationHistoryToEdit.department_id = $scope.model.authority.department.department_id;
		$scope.operationHistoryToEdit.department_name = $scope.model.authority.department.department_name;
		$scope.StartOperationSaveTimer();
	}
	
	$scope.$watch("operationCodeCtrl.seekTextIcd", function handleChange( newValue, oldValue ) {
		if(newValue != null && newValue.length > 0){
			$scope.icdViewType = "seek";
			var seekTextIcd = newValue.replace(".","-");
			var getUrl = "/v/icd/seek/"+seekTextIcd;
			console.log(getUrl);
			$http.get(getUrl).success(function(response) {
				$scope.seekIcd = response;
				console.log($scope.seekIcd);
			});
			/*
			var getUrl = "/v/seekIcd/"+seekTextIcd;
			console.log(getUrl);
			$http.get(getUrl).success(function(response) {
				$scope.seekIcd = response;
				console.log($scope.seekIcd);
			});
			 * */
		}
	});

	$scope.$watch("operationCodeCtrl.seekOperation", function handleChange( newValue, oldValue ) {
		if(newValue != null && newValue.length > 0){
			$scope.procedureViewType = 'seek';
			var seekText = newValue.replace(".","-");
			var getUrl = "/v/seekProcedureOperation/"+seekText;
			console.log(getUrl);
			$http.get(getUrl).success(function(response) {
				$scope.seekProcedure = response;
				console.log($scope.seekProcedure);
			});
		}
	})

	$scope.useOperation = function(operationHistory){
		$scope.operationHistoryToEdit = operationHistory;
		console.log("useOperation");
		if($scope.OperationSaveTimer == null){
			$scope.StartOperationSaveTimer();
		}
	}

	var initDurationOp = function(operation){
		if(!operation.durationOpMin){
			operation.durationOpMin =
				(operation.operation_history_end - operation.operation_history_start)/1000/60;
		}
	}
	$scope.loginToThisPage = function(){
		return "/throughlogin/gotopage/h/operation-code.html"+window.location.search;
	};

declareIcdModule($scope, $http);

}]);

var declareIcdModule = function($scope, $http){
	console.log("----declareIcdModule-------------");
	$scope.icdRoot = {};

	$scope.icd10uatreeWithParentName = function (operationHistoryToEdit){
		var icdId = operationHistoryToEdit.icd_id;
		$http.put('/icd10uatree_with_parent_name/'+icdId).then(function(response) {
			$http.get("/v/ix-operation/"+operationHistoryToEdit.history_id).success(function(response) {
				angular.forEach(response, function(oh, key) {
					if(operationHistoryToEdit.operation_history_id = oh.operation_history_id){
						operationHistoryToEdit.icd10uatree_with_parent_name = oh.icd10uatree_with_parent_name;
						operationHistoryToEdit.icd_name = oh.icd_name;
					}
				});
			});
		}, function(response) {
			console.log("---addGroupName-------erros-------");
			console.log(response);
		});
		/*
		 * */
	}
	$scope.codeWithPoint = function (icdCode){
		if(icdCode == null)
			return false;
		return icdCode.indexOf(".") > 0;
	}
	
	$scope.icdViewType = 'navigation'
	$scope.gotoNavigationIcd = function (){
		console.log("----gotoNavigation---------------");
		$scope.icdViewType = "navigation";
	}
	$scope.seekUpdateIcd = function (){
		console.log("-----------seekUpdate--------");
		$scope.icdViewType = "seek";
	}
	
	
	$scope.gotoIcdNavigationGroup = function (o){
		console.log("----gotoIcdNavigationGroup---------------");
		$scope.icdViewType = 'navigation'
		console.log(o);
		$http.get("/v/pathToRoot/"+o.icd_id).success(function(response) {
			console.table(response);
			openIcdTreeTo(response, 0, $scope.icdRoot.childs)
		});
	}

	var openIcdTreeTo = function (pathToIcdNode, level, list){
		if(level < pathToIcdNode.length){
			var seekIcdElement = pathToIcdNode[level];
			angular.forEach(list, function(checkIcd) {
				if(checkIcd.icd10uatree_id == seekIcdElement.icd10uatree_id){
					checkIcd.open = true;
//					checkIcd.open = !checkIcd.open;
					if(checkIcd.childs == null){
						var parentId = checkIcd.icd10uatree_id;
						console.log(parentId);
						$http.get("/v/siblingIcd/"+parentId).success(function(response) {
							checkIcd.childs= response;
							openIcdTreeTo(pathToIcdNode, level + 1, checkIcd.childs)
						});
					}else{
						openIcdTreeTo(pathToIcdNode, level + 1, checkIcd.childs)
					}
				}
			});
		}
	}

	$scope.isOpenIcd = function (o){
		return o.open;
	}

	$scope.openChildIcd = function (o){
		o.open = !o.open;
		console.log(o);
		openChildIcdDb(o);
	}

	var openChildIcdDb = function (o){
		if(o.childs == null){
			var parentId = o.icd10uatree_id;
			$http.get("/v/siblingIcd/"+parentId).success(function(response) {
				o.childs= response;
				if(response.length == 0){
					$scope.toSaveIcd(o);
				}
			});
		}
	}

	$scope.initIcd = function(){
		console.log("----declareIcdModule-----initIcd--------");
		if($scope.icdViewType == null){
			$scope.icdViewType = "navigation";
		}
		if(!$scope.icdRoot.childs){
			console.log("----declareIcdModule---initIcd----2------");
			$http.get("/v/rootSiblingIcd").success(function(response) {$scope.icdRoot.childs = response;});
		}
	}
}

// ------------OperationCodeCtrl END
hol2eih3App.controller('HomeCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	console.log("HomeCtrl");
	$http({ method : 'GET', url : "/v/readHome"+window.location.search
	}).success(function(model, status, headers, config) {
		$scope.model = model;
		console.log($scope.model);
	}).error(function(model, status, headers, config) {
		$scope.error = model;
	});
}]);
//  1  Запис надходжень/виписки хворих за сьогодні – saveMovePatients.html.
//hol2eih3App.controller('SaveCopeTodayPatientsCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//, function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
//initController($scope, $http, $filter, $cookies);
hol2eih3App.controller('SaveCopeTodayPatientsCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	initController($scope, $http, $filter);
	//  1.1  Зчитування надходження/виписки хворих на сьогодні – readTodayMovePatients
	$scope.readMoveTodayPatients();
	
	// 1.2   Запис надходження/виписки хворих на сьогодні – saveMoveTodayPatients
	$scope.saveMoveTodayPatients = function(){
		var url = "/save-"+$scope.paramDate+"-Patients";
		var today = new Date();
		console.log(today.getFullYear()+"-"+today.getMonth()+"-"+today.getDate());
		if(!$scope.paramDate){
			url = "/save-"+today.getFullYear()+"-"+today.getMonth()+"-"+today.getDate()+"-Patients";
		}
		console.log(url);
		console.log($scope.moveTodayPatients);
		$http({ method : 'POST', data : $scope.moveTodayPatients, url : url
		}).success(function(data, status, headers, config){
			console.log(data);
			$scope.moveTodayPatients = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
	$scope.calc = function(departmentHol){
		console.log(departmentHol);
		var sum = 0;
		if(departmentHol.MOVEDEPARTMENTPATIENT_PATIENT1DAY)
			sum = departmentHol.MOVEDEPARTMENTPATIENT_PATIENT1DAY/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_IN)
			sum += departmentHol.MOVEDEPARTMENTPATIENT_IN/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_INDEPARTMENT)
			sum += departmentHol.MOVEDEPARTMENTPATIENT_INDEPARTMENT/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_OUTDEPARTMENT)
			sum -= departmentHol.MOVEDEPARTMENTPATIENT_OUTDEPARTMENT/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_OUT)
			sum -= departmentHol.MOVEDEPARTMENTPATIENT_OUT/1;
		if(departmentHol.MOVEDEPARTMENTPATIENT_DEAD)
			sum -= departmentHol.MOVEDEPARTMENTPATIENT_DEAD/1;
		departmentHol.MOVEDEPARTMENTPATIENT_PATIENT2DAY = sum;
	}

}]);
var fileUploadApp = angular.module('fileUploadApp', ['ngFileUpload']);
fileUploadApp.controller('fileUploadCtrl', ['$scope', 'Upload', '$timeout', '$http', function ($scope, Upload, $timeout, $http) {
	console.log("UploadExcelCtrl");

	$scope.setFile = function(e){
		if(e.picFile){
			console.log("---------------");
			console.log(e.picFile.name);
			console.log("---------------");
		}
	}

	$scope.uploadPic = function(file) {
		console.log("---------------");
		console.log(file);
		console.log(file.name);
		console.log("---------------");
		file.upload = Upload.upload({
//			url: 'https://angular-file-upload-cors-srv.appspot.com/upload',
			url: '/upload',
//			url: '/upload2',
			data: {file: file, fileName: file.name},
		});

		file.upload.then(function (response) {
			console.log("file.upload.then 1 param");
			console.log(response);
			$timeout(function () {
				file.result = response.data;
			});
		}, function (response) {
			console.log("file.upload.then 2 param");
			console.log(response);
			if (response.status > 0)
				$scope.errorMsg = response.status + ': ' + response.data;
		}, function (evt) {
			console.log("file.upload.then 3 param");
			console.log(evt);
			console.log("------------------------");
//			Math.min is to fix IE which reports 200% sometimes
			file.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
		});
	}

}]);

//hol2eih3App.controller('DepartmentMonthMovementH2Ctrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                         , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentMonthMovementH2Ctrl', ['$scope', '$http', '$filter', '$sce'
		, function ( $scope, $http, $filter, $sce) {
	console.log("DepartmentMonthMovementH2Ctrl");
	var url = "/r/readBedDayH2-2-2";
	$http({ method : 'GET', url : url
	}).success(function(data, status, headers, config) {
		$scope.bedDay = data;
		console.log($scope.bedDay);
//			initDateVariables();
	}).error(function(data, status, headers, config) {
		$scope.error = data;
	});
}]);

var initQuartal = function($scope, $http){
	$scope.eqMonth = function(){
		if(!$scope.paramYear){
			$scope.paramYear = $scope.historyYears[0].year;
		}
		console.log(" - "+$scope.minMonth+" - "+$scope.maxMonth+" - "+$scope.paramYear);
		var url = "?m1="+$scope.minMonth+"&m2="+$scope.maxMonth+"&y="+$scope.paramYear+"&department_id=" + $scope.param.department_id+"&type="+$scope.eqMonthType;
		window.location.href = url;
	}
eqMonth = function(){
	var url1 = $scope.url1;
	var url = url1 + "-" + $scope.paramYear + "-" + $scope.minMonth + "-" + $scope.maxMonth + "-" + $scope.param.department_id;
	console.log(url);
	console.log($scope.showExpandIcd10);
//	alert(url)
	$http({ method : 'GET', url : url
	}).success(function(data, status, headers, config) {
		$scope.bedDay = data;
		$scope.dbDuration = data.duration;
		console.log($scope.bedDay);
		if($scope.param.viddilennja){
			$scope.bedDayOfMonthMySql 
				= $scope.bedDay.bedDayOfMonthMySql[$scope.param.viddilennja]
			console.log($scope.bedDayOfMonthMySql);
			mmArray();
		}
//		initDateVariables();
	}).error(function(data, status, headers, config) {
		$scope.error = data;
	});
}
}

var initReport = function($scope, $http){
	$scope.init = {searchText: ''};
	$scope.param = parameters;
	$scope.eqMonthType = "month";
	if(parameters.type){
		$scope.eqMonthType = parameters.type;
	}
	var today = new Date();
	var mm = today.getMonth();
	$scope.yyyy = today.getFullYear();
	if(mm==0) mm=1;
	$scope.minMonth = mm;
	$scope.maxMonth = mm;
	if(parameters.m1){
		$scope.minMonth = parameters.m1;
		if($scope.eqMonthType == "month"){
			$scope.maxMonth = parameters.m1;
		}else if(parameters.m2){
			$scope.maxMonth = parameters.m2;
		}
	}

	if(parameters.y){
		$scope.paramYear = parameters.y;
	}

	$http({ method : 'GET', url : '/r/readHistoryYears'
	}).success(function(data, status, headers, config) {
		$scope.historyYears = data;
		console.log($scope.historyYears);
		if(!$scope.paramYear){
			$scope.paramYear = $scope.historyYears[0].year;
			console.log($scope.paramYear);
		}
	}).error(function(data, status, headers, config) {
		$scope.error = data;
	});

	$scope.setParamYear = function(year){
		$scope.paramYear = year;
	}
	if(!$scope.paramYear)
		$scope.paramYear = new Date().getFullYear();
	console.log($scope.paramYear);
	
	$scope.error = [];
	
	mmArray = function(){
		if($scope.param.department_id > 0){
			$scope.mmArray =[];
			for (var i = $scope.minMonth*1; i <= $scope.maxMonth; i++) {
				$scope.mmArray.push(i);
			}
			console.log($scope.mmArray);
			if($scope.bedDay){
				$scope.mmNewArray =[];
				if($scope.bedDay.bedDayDepartmentMySql){
					if($scope.mmArray.length > $scope.bedDay.bedDayDepartmentMySql.length){
						console.log($scope.bedDay.bedDayDepartmentMySql.length);
						var mmNew = $scope.mmArray.length - $scope.bedDay.bedDayDepartmentMySql.length;
						for (var i = 1; i <= mmNew; i++) {
							$scope.mmNewArray.push(i);
						}
						console.log($scope.mmNewArray);
					}
				}
			}
		}
	}
	
	$scope.isWaleMonth = function(month){
		return month >= $scope.minMonth && month <= $scope.maxMonth;
	}

	$scope.setMonthType = function(monthType){
		$scope.eqMonthType = monthType;
	}

	$scope.setMonth = function(month){
		console.log($scope.eqMonthType);
		if($scope.eqMonthType == "month"){
			$scope.maxMonth = month;
			$scope.minMonth = month;
		}else{
			if($scope.minMonth == null){
				$scope.minMonth = month;
			}
			if($scope.maxMonth == null){
				$scope.maxMonth = month;
			}
			if(month < $scope.minMonth){
				$scope.minMonth = month;
			}else if(month > $scope.maxMonth){
				$scope.maxMonth = month;
			}else if((month - $scope.minMonth) < ($scope.maxMonth - month)){
				$scope.minMonth = month;
			}else if((month - $scope.minMonth) > ($scope.maxMonth - month)){
				$scope.maxMonth = month;
			}else{
				$scope.maxMonth = month;
				$scope.minMonth = month;
			}
		}
		console.log(month+" - "+$scope.minMonth+" - "+$scope.maxMonth);
		mmArray();
	}

	function urlParam(){
		var url = "?department_id="+parameters.department_id + "&m1="+$scope.minMonth+"&m2="+$scope.maxMonth
		+ "&type="+$scope.eqMonthType;
		if(!(parameters.department_id > 0))
			url = "?m1="+$scope.minMonth+"&m2="+$scope.maxMonth+"&type="+$scope.eqMonthType;
		if($scope.paramYear){
			url = url + '&y=' + $scope.paramYear;
		}
		return url;
	}

	$scope.urlNrr = function(nrr){
		var url = urlParam()+ '&nrr=' + nrr;
		return url;
	}

	$scope.eqMonth = function(){
		var url = urlParam();
		window.location.href = url;
	}

	$scope.months = {
		'nominative': '_січень_лютий_березень_квітень_травень_червень_липень_серпень_вересень_жовтень_листопад_грудень'.split('_'),
		'accusative': '_січня_лютого_березня_квітня_травня_червня_липня_серпня_вересня_жовтня_листопада_грудня'.split('_')
	};
}

//hol2eih3App.controller('K1Icd10Ctrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                       , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('K1Icd10Ctrl', [ '$scope', '$http', '$filter', '$sce'
	, function ( $scope, $http, $filter, $sce) {
	console.log("K1Icd10Ctrl");
	initReport($scope, $http);
	$scope.icd10Head = [
		{"title":"","name":"МКХ-10","key":"icd_code"}
		,{"title":"","name":"Діагноз","key":"icd_name"}
		,{"title":"","name":"Кількість","key":"cnt"}
	];

	eqMonth = function(){
				var url1 = "/r/readIcd10K2-";
//				var url1 = "/r/readIcd10K1-";
		var url = url1 + $scope.minMonth + "-" + $scope.maxMonth;
		console.log(url);
		$http({ method : 'GET', url : url
		}).success(function(data, status, headers, config) {
			$scope.k1 = data;
			console.log($scope.k1);
		}).error(function(data, status, headers, config) {
			$scope.error.push(data);
			console.log($scope.error);
		});
	}

	eqMonth();
	
}]);

//hol2eih3App.controller('DepartmentMotionCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentMotionCtrl', [ '$scope', '$http', '$filter', '$sce'
		, function ( $scope, $http, $filter, $sce) {
	console.log("DepartmentMotionCtrl");
	$scope.url1 = "/r/readDepartmentMotion";
	initReport($scope, $http);
	initQuartal($scope, $http);

	eqMonth();

}]);

hol2eih3App.controller('DbAdressCtrl', ['$scope', '$http', '$filter', '$sce'
	, function ($scope, $http, $filter, $sce) {
	console.log("DbAdressCtrl");

	$scope.openLocality = function(c, r, d, country, panel){
		selectPunct(panel, country, d, r, c);
		//alert('Hello World! ' + c.locality_name)
	}
	
	function adressIds(c, d, r, l){
		var adress = [c.country_id];
		if(l)
			adress = [c.country_id, d.district_id,r.region_id, l.locality_id];
		else
		if(r)
			adress = [c.country_id, d.district_id,r.region_id];
		else
		if(d)
			adress = [c.country_id, d.district_id];
		console.log(adress);
		return adress;
	}

	$scope.adress1 = [];
	$scope.adress2 = [];

	function selectPunct(panel, c, d, r, l){
		var adress = adressIds(c, d, r, l);
		if(panel == 1){
			$scope.panel1 = [c,d,r,l];
			$scope.adress1 = adress;
		}
		if(panel == 2){
			$scope.panel2 = [c,d,r,l]
			$scope.adress2 = adress;
		}
		//adress = [c.country_id, d.district_id,r.region_id, l.locality_id]
		$http({ method : 'POST', data : adress, url : '/v/cntPatientsOfAdress'
		}).success(function(data, status, headers, config){
			$scope.cntPatient = data;
			console.log(data);
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}

	$scope.replacePatientsAdress = function(){
		var adress12 = {adress1:$scope.adress1, adress2:$scope.adress2};
		$http({ method : 'POST', data : adress12, url : '/v/replacePatientsAdress'
		}).success(function(data, status, headers, config){
			console.log(data);
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
function openPanel(c, panel){
	if(panel == 1)
		c.open = !c.open;
	if(panel == 2)
		c.open2 = !c.open2;
}
	$scope.openRegion = function(c, d, country, panel){
		selectPunct(panel, country, d, c);
		openPanel(c, panel);
		console.log(c);
		if(!c.region){
			$http({ method : 'GET', url : '/v/localityOfRegion-' + c.region_id
			}).success(function(data, status, headers, config) {
				c.locality = data;
				console.log(c.locality);
			}).error(function(data, status, headers, config) {
				$scope.error = data;
			});
		}
	}
	$scope.openDistrict = function(c, country, panel){
		selectPunct(panel, country, c);
		openPanel(c, panel);
		console.log(c);
		if(!c.region){
			$http({ method : 'GET', url : '/v/regionOfDistrict-' + c.district_id
			}).success(function(data, status, headers, config) {
				c.region = data;
				console.log(c.region);
			}).error(function(data, status, headers, config) {
				$scope.error = data;
			});
		}
	}

	$scope.openCountry = function(c, panel){
		selectPunct(panel, c);
		openPanel(c, panel);
		console.log(c);
		if(!c.district){
			$http({ method : 'GET', url : '/v/districtOfCountry-' + c.country_id
			}).success(function(data, status, headers, config) {
				c.district = data;
				console.log(c.district);
			}).error(function(data, status, headers, config) {
				$scope.error = data;
			});
		}
	}

	$http({ method : 'GET', url : '/v/countries'
		}).success(function(data, status, headers, config) {
			$scope.countries = data;
			console.log($scope.countries);
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});

}]);

//hol2eih3App.controller('DepartmentAdressCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentAdressCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	console.log("DepartmentAdressCtrl");
	$scope.url1 = "/r/readDepartmentAdress";
	initReport($scope, $http);
	initQuartal($scope, $http);
	$scope.bedDayHead = [
 {"title":"","name":"Райони","key":""}
 ,{"title":"Поступило з направленням","name":"з напр.","key":"with"}
 ,{"title":"Поступило без направлення","name":"без напр.","key":"without"}
 ,{"title":"Переведений з іншого відділення","name":"пер. з","key":"from_dep"}
 ,{"title":"Переведений в інше відділення","name":"пер. в","key":"to_dep"}
 ,{"title":"Виписаний","name":"вип.","key":"remove"}
 ,{"title":"Померлі","name":"пом.","key":"dead"}
 ];

eqMonth();

}]);

//hol2eih3App.controller('DepartmentIcd10Ctrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                               , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentIcd10Ctrl', [ '$scope', '$http', '$filter', '$sce'
		, function ($scope, $http, $filter, $sce) {
	console.log("DepartmentIcd10Ctrl");
	initReport($scope, $http);
	console.log($scope.param);
	console.log($scope.param.showExpandIcd10==1);
	$scope.url1 = $scope.param.showExpandIcd10==1?"/r/readDepartmentIcd10":"/r/readDepartmentIcd10Group";
	initQuartal($scope, $http);

$scope.bedDayHead = [
	{"title":"","name":"МКХ10","key":""}
	,{"title":"","name":"Місто","key":"misto"}
	,{"title":"","name":"Село","key":"selo"}
	,{"title":"Поступило з направленням","name":"з напр.","key":"referral_has"}
	,{"title":"Поступило без направлення","name":"без напр.","key":"referral_self"}
	,{"title":"Переведений з іншого відділення","name":"пер. з","key":"referral_hol"}
	,{"title":"Переведений в інше відділення","name":"пер. в","key":"remove_hol"}
	,{"title":"Виписаний","name":"вип.","key":"remove_true"}
	,{"title":"Померлі","name":"пом.","key":"remove_dead"}
];

$scope.clickShowAllIcd10 = function(){
	console.log("showAllIcd10 "+$scope.showAllIcd10+"; showExpandIcd10 "+$scope.showExpandIcd10);
}

$scope.showIcd10 = function(bedDayOfMonthMySql){
	if($scope.showAllIcd10)
		return true;
	var showIcd10 = bedDayOfMonthMySql.misto_cnt + bedDayOfMonthMySql.selo_cnt > 0;
	return showIcd10;
}

eqMonth();

}]);

//F20t3500
hol2eih3App.controller('F20t3500Ctrl', [ '$scope', '$http', '$filter', '$sce'
	, function ( $scope, $http, $filter, $sce) {
	
	console.log("F20t3500Ctrl");
	initReport($scope, $http);

	var urlM1M2Year = '-' + $scope.minMonth + '-' + $scope.maxMonth + '-' + $scope.paramYear;

	var urlF20t3500 = '/r/F20t3500' + urlM1M2Year;
	if(parameters.nrr){
		var urlF20t3500 = '/r/F20t3500NrrPatienten' + urlM1M2Year + '-' + parameters.nrr;
	}

	$http({ method : 'GET', url : urlF20t3500
	}).success(function(data, status, headers, config) {
		$scope.f20t3500 = data;
		console.log($scope.f20t3500);
		$scope.dbDuration = data.duration;
		$scope.f20t3500.nrrIndexes = {};
		$scope.f20t3500.list.forEach(function(f20t3500, i){
			$scope.f20t3500.nrrIndexes['groupById_' + f20t3500.groupById] = i;
		});
		console.log($scope.f20t3500.nrrIndexes);
	}).error(function(data, status, headers, config) {
		$scope.error = data;
	});

	$scope.f20t3500Head = [
		{'key':'', 'nrr':'Номер рядка', 'name':'Наіменування операцій'
			, 'all':'Кількість операцій, проведених у стаціонарі, усього'
			, 'child17':'у тому числі дітям віком 0-17 років включно'
			, 'dead':'Померло оперованих у стаціонарі, усього'
			, 'village':'Кількість операцій, проведених сільським жителям, (із гр.1)'
		}
		,{'key':'N', 'nrr':'1.0', 'name':'Усього операцій', 'groupById':1}
		,{'key':'N','nrr':'2.0 ','name':'Операції на нервовій системі', 'groupById':1700}
		,{'key':'N','nrr':'2.1 ','name':'з них:  на головному мозку', 'groupById':1764}
		,{'key':'N','nrr':'2.2 ','name':'на периферичній нервовій системі', 'groupById':1765}
		,{'key':'N','nrr':'3.0 ','name':'Операції на ендокринній системі', 'groupById':100}
		,{'key':'N','nrr':'3.1 ','name':'з них:  на щитоподібній залозі', 'groupById':102}
		,{'key':'N','nrr':'3.2 ','name':'паращитоподібних залозах', 'groupById':103}
		,{'key':'N','nrr':'3.3 ','name':'наднирниках', 'groupById':104}
		,{'key':'N','nrr':'4.0 ','name':'Операції на органах зору', 'groupById':200}
		,{'key':'N','nrr':'4.1 ','name':'з них з приводу:  глаукоми', 'groupById':206}
		,{'key':'N','nrr':'4.2 ','name':'енукліації', 'groupById':207}
		,{'key':'N','nrr':'4.3 ','name':'катаракти', 'groupById':208}
		,{'key':'N','nrr':'5.0 ','name':'Операції на органах вуха, горла, носа', 'groupById':300}
		,{'key':'N','nrr':'5.1 ','name':'з них:  на вусі', 'groupById':310}
		,{'key':'N','nrr':'5.2 ','name':'на мигдаликах та аденоїдах', 'groupById':311}
		,{'key':'N','nrr':'6.0 ','name':'Операції на органах дихання', 'groupById':400}
		,{'key':'N','nrr':'6.1 ','name':'з них:  пульмоектомія', 'groupById':413}
		,{'key':'N','nrr':'6.2 ','name':'резекція частини легені', 'groupById':414}
		,{'key':'N','nrr':'6.3 ','name':'резекція сегмента легені', 'groupById':415}
		,{'key':'N','nrr':'7.0 ','name':'Операції на серці', 'groupById':500}
		,{'key':'N','nrr':'7.1 ','name':'з них на відкритому серці', 'groupById':517}
		,{'key':'N','nrr':'7.2 ','name':'аортокоронарне шунтування', 'groupById':518}
		,{'key':'N','nrr':'7.3 ','name':'імплантація кардіостимулятора', 'groupById':519}
		,{'key':'N','nrr':'8.0 ','name':'Операції на судинах', 'groupById':600}
		,{'key':'N','nrr':'8.1 ','name':'з них: на артеріях', 'groupById':621}
		,{'key':'N','nrr':'8.2 ','name':'у тому числі на брахіоцефальних судинах', 'groupById':622}
		,{'key':'N','nrr':'8.3 ','name':'на венах'}
		,{'key':'N','nrr':'8.4 ','name':'у тому числі венектомії при варикозному розширенні', 'groupById':624}
		,{'key':'N','nrr':'8.5 ','name':'операції при післятромбофлеботичному синдромі', 'groupById':625}
		,{'key':'N','nrr':'9.0 ','name':'Операції на органах травлення та черевної порожнини', 'groupById':700}
		,{'key':'N','nrr':'9.1 ','name':'з них: на стравоході', 'groupById':734}
		,{'key':'N','nrr':'9.2 ','name':'на шлунку з приводу виразкової хвороби', 'groupById':735}
		,{'key':'N','nrr':'9.3 ','name':'апендектомії при хронічному апендициті', 'groupById':736}
		,{'key':'N','nrr':'9.4 ','name':'холецистектомії при хронічних холециститах'}
		,{'key':'N','nrr':'9.5 ','name':'у тому числі при жовчокам´яній хворобі', 'groupById':727}
		,{'key':'N','nrr':'9.6 ','name':'операції на жовчних протоках', 'groupById':728}
		,{'key':'N','nrr':'9.7 ','name':'операції на підшлунковій залозі'}
		,{'key':'N','nrr':'9.8 ','name':'у тому числі при хронічному панкреатиті', 'groupById':730}
		,{'key':'N','nrr':'9.9 ','name':'операції з приводу незащемленої грижі', 'groupById':731}
		,{'key':'N','nrr':'9.10 ','name':'лапаротомії діагностичні', 'groupById':732}
		,{'key':'N','nrr':'10.0 ','name':'Операції при непухлинних захворюваннях прямої кишки', 'groupById':800}
		,{'key':'N','nrr':'11.0 ','name':'Операції на нирках і сечоводах', 'groupById':900}
		,{'key':'N','nrr':'11.1 ','name':'з них нефроектомії', 'groupById':939}
		,{'key':'N','nrr':'12.0 ','name':'Операції на передміхуровій залозі', 'groupById':1000}
		,{'key':'N','nrr':'13.0 ','name':'Операції на жіночих статевих органах', 'groupById':1100}
		,{'key':'N','nrr':'13.1 ','name':'з них: вишкрібання матки (крім штучного переривання вагітності)', 'groupById':1142}
		,{'key':'N','nrr':'13.2 ','name':'стерилізація жінок', 'groupById':1144}
		,{'key':'N','nrr':'14.0 ','name':'Акушерські операції', 'groupById':1200}
		,{'key':'N','nrr':'14.1 ','name':'з них: накладання щипців', 'groupById':1246}
		,{'key':'N','nrr':'14.2 ','name':'вакуум-екстракції', 'groupById':1247}
		,{'key':'N','nrr':'14.3 ','name':'кесарів розтин (крім малих піхвових)', 'groupById':1248}
		,{'key':'N','nrr':'14.4 ','name':'плодоруйнівні', 'groupById':1249}
		,{'key':'N','nrr':'14.5 ','name':'штучне переривання вагітності', 'groupById':1250}
		,{'key':'N','nrr':'14.6 ','name':'інші вишкрібання матки, які пов´язані з вагітністю (крім штучного переривання  вагітності)', 'groupById':1251}
		,{'key':'N','nrr':'14.7 ','name':'вакуум-аспірації', 'groupById':1252}
		,{'key':'N','nrr':'14.8 ','name':'вакуум-екскохлеації', 'groupById':1253}
		,{'key':'N','nrr':'15.0 ','name':'Операції на кістково-м´язовій системі', 'groupById':1300}
		,{'key':'N','nrr':'15.1 ','name':'з них на кістках і суглобах', 'groupById':1355}
		,{'key':'N','nrr':'15.2 ','name':'у тому числі ампутації кінцівок', 'groupById':1356}
		,{'key':'N','nrr':'15.3 ','name':'з них при судинних захворюваннях', 'groupById':1357}
		,{'key':'N','nrr':'15.4 ','name':'у тому числі при цукровому діабеті', 'groupById':1358}
		,{'key':'N','nrr':'16.0 ','name':'Операції на молочній залозі', 'groupById':1400}
		,{'key':'N','nrr':'16.1 ','name':'з них при злоякісних пухлинах', 'groupById':1460}
		,{'key':'N','nrr':'17.0 ','name':'Операції на шкірі та підшкірній клітковині', 'groupById':1500}
		,{'key':'N','nrr':'18.0 ','name':'Інші операції', 'groupById':1600}
		,{'key':'N','nrr':'18.1 ','name':'з них з приводу хвороб ротової порожнини, залоз та щелен', 'groupById':1663}
	];
}]);

//F20t3600
hol2eih3App.controller('F20t3600Ctrl', [ '$scope', '$http', '$filter', '$sce'
		, function ( $scope, $http, $filter, $sce) {
	console.log("F20t3600Ctrl");
	initReport($scope, $http);
	
	var urlM1M2Year = '-' + $scope.minMonth + '-' + $scope.maxMonth + '-' + $scope.paramYear;
	if(parameters.nrr){
		var urlF20t3600NrrPatienten = '/r/F20t3600NrrPatienten' + urlM1M2Year + '-' + parameters.nrr;
		console.log('-------------------------' + urlF20t3600NrrPatienten);
		$http({ method : 'GET', url : urlF20t3600NrrPatienten
		}).success(function(data, status, headers, config) {
			$scope.f20t3600NrrPatienten = data;
			console.log($scope.f20t3600NrrPatienten);
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}

	var urlF20t3600 = '/r/F20t3600' + urlM1M2Year;
	$http({ method : 'GET', url : urlF20t3600
	}).success(function(data, status, headers, config) {
		$scope.f20t3600 = data;
		console.log($scope.f20t3600);
		$scope.dbDuration = data.duration;
		$scope.f20t3600.nrrIndexes = {};
		$scope.f20t3600.list.forEach(function(f20t3600, i){
			$scope.f20t3600.nrrIndexes['row_' + f20t3600.nrr] = i;
		});
		console.log($scope.f20t3600.nrrIndexes);
	}).error(function(data, status, headers, config) {
		$scope.error = data;
	});
	
	$scope.f20t3600Head = [
		{'key':''
			,'name':'Наіменування операцій'
			,'termin':'Термін доставки у стаціонар від початку захворювання'
			,'nrr':'Номер рядка'
			,'op':'оперовано'
			,'dead':'з них померло'
			,'all':'усього'
			,'after24':'у тому числі пізніше 24 години'
			,'old_0_17':'0-17 р.¹'
			,'old_0_17_full':'¹у тому числі дітей віком 0-17 років включно'
		}
		,{'key':'N','name':'Гостра непрохідність'}
		,{'key':'N','name':'Гострий апендицит'}
		,{'key':'N','name':'Проривна виразка шлунка та 12-палої кишки'}
		,{'key':'N','name':'Шлунково кишкова кровотеча'}
		,{'key':'N','name':'Защемлена грижа'}
		,{'key':'N','name':'Гострий холецистит'}
		,{'key':'N','name':'Гострий панкреатит'}
		,{'key':'N','name':'Позаматкова вагітність'}
		,{'key':'N','name':'Травма внутрішніх органів грудної та черевної порожнини й таза'}
	];
}]);

//F20t3220
hol2eih3App.controller('F20t3220Ctrl', [ '$scope', '$http', '$filter', '$sce'
		, function ( $scope, $http, $filter, $sce) {
	console.log("F20t3220Ctrl");

	var pmml1 = { "sepalLength": 6.4, "sepalWidth": 3.2, "petalLength":4.5, "petalWidth":1.5 };
	console.log(pmml1);
	
	$http.get('http://localhost:8081/r/readHistoryYears').success(function (response) {
		console.log(response);
	});
	
		$http({ method : 'POST', data : pmml1, url : "http://localhost:9004"
		}).success(function(data, status, headers, config){
			console.log(data);
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});

	initReport($scope, $http);
	
	$scope.nrrData = function(h){
		if($scope.f20t3220)
			return $scope.f20t3220.list[$scope.f20t3220.nrrIndexes[h.nrr]];
	}


	var urlM1M2Year = '-' + $scope.minMonth + '-' + $scope.maxMonth + '-' + $scope.paramYear;
	var url = '/r/F20t3220' + urlM1M2Year;
	console.log(url);

	if(parameters.nrr){
		var urlM1M2YearNrr = urlM1M2Year + '-' + parameters.nrr.replace('.','_');
		$http({ method : 'GET', url : '/r/F20t3220NrrPatienten' + urlM1M2YearNrr
		}).success(function(data, status, headers, config) {
			$scope.f20t3220NrrPatienten = data;
			console.log($scope.f20t3220NrrPatienten);
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}

	$http({ method : 'GET', url : url
	}).success(function(data, status, headers, config) {
		$scope.f20t3220 = data;
		$scope.dbDuration = data.duration;
		initF20t3220();
		console.log($scope.f20t3220);
		$http({ method : 'GET', url : '/r/2_F20t3220' + urlM1M2Year
		}).success(function(data, status, headers, config) {
			console.log($scope.f20t3220.list.length);
			console.log(Object.keys($scope.f20t3220.nrrIndexes).length);
			console.log(data);
			data.list.forEach(function(f20t3220){
				var i = $scope.f20t3220.list.length;
				console.log(i+'/'+f20t3220.nrr);
				$scope.f20t3220.list.push(f20t3220);
				$scope.f20t3220.nrrIndexes[f20t3220.nrr] = i;
			});
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}).error(function(data, status, headers, config) {
		$scope.error = data;
	});

	function initF20t3220(){
		$scope.f20t3220.nrrIndexes = {};
		$scope.f20t3220.list.forEach(function(f20t3220, i){
			$scope.f20t3220.nrrIndexes[f20t3220.nrr] = i;
		});
		console.log($scope.f20t3220.nrrIndexes);
	}

	$scope.f20t3220Head = [
		{'key':'','nrr':'Номер рядка','icd10':'Шифр відповідно до МКХ-10'
			,'A':'А.Дорослі віком 18 років і старші'
			,'B':'Б.Діти віком 0-17 років включно'
			,'removed':'Виписано хворих'
			,'up_to_1_year':'до 1 р.¹'
			,'up_to_1_year_full':'¹у тому числі віком до 1 року'
			,'bed_days':'проведено виписаними ліжкоднів'
			,'dead':'померло'
			,'name':'Наіменування класів та окремих хвороб'}
		,{'key':'N','nrr':'1.0','icd10':'A00-T98','name':'<b>Усього</b>'}
		,{'key':'N','nrr':'2.0','icd10':'A00-B99','name':'у тому числі:<br/><b>Деякі інфекційні та паразитарні хвороби</b>'}
		,{'key':'N','nrr':'2.1','icd10':'A00-A09','name':'з них:<br/>  кишкові інфекції'}
		,{'key':'N','nrr':'2.2','icd10':'A15.0-9, A16.0-9, A19.0-част. I, A19.8-част. I','name':'туберкульоз легенів та позалегеневий туберкульоз органів дихання'}
		,{'key':'N','nrr':'2.3','icd10':'A39','name':'менінгококова інфекція'}
		,{'key':'N','nrr':'2.4','icd10':'A40-A41','name':'септицемії'}
		,{'key':'N','nrr':'2.5','icd10':'B15-B19','name':'вірусний гепатит'}
		,{'key':'N','nrr':'2.6','icd10':'B18.0.1','name':'у тому числі: <br/> хронічний вірусний гепатит B'}
		,{'key':'N','nrr':'2.7','icd10':'B18.2','name':'хронічний вірусний гепатит C'}
		,{'key':'N','nrr':'3.0','icd10':'C00-D48','name':'<b>Новоутворення</b>'}
		,{'key':'N','nrr':'3.1','icd10':'C00-C97','name':'з них злоякісні новоутворення'}
		,{'key':'N','nrr':'3.2','icd10':'C81-C96','name':'у тому числі злоякісні новоутворення лімфоїдної, кровотворної та споріднених з ними тканин'}
		,{'key':'N','nrr':'4.0','icd10':'D50-D89','name':'<b>Хвороби крові, кровотворних органів і окремі порушення із залученням імунного механізму</b>'}
		,{'key':'N','nrr':'4.1','icd10':'D50-D64','name':'з них:<b/> анемії'}
		,{'key':'N','nrr':'4.2','icd10':'D65-D69','name':'порушення згортання крові, пурпура, інші геморагічні стани'}
		,{'key':'N','nrr':'5.0','icd10':'E00-E90','name':'<b>Хвороби ендокринної системи, розладу харчування, порушення обміну речовин</b>'}
		,{'key':'N','nrr':'5.1','icd10':'E01.0, E04.0','name':'з них:<br/> дифузний зоб II - III ступенів'}
		,{'key':'N','nrr':'5.2','icd10':'E01.8, E03','name':'набутий гіпотиреоз та інші форми гіпотиреозу'}
		,{'key':'N','nrr':'5.3','icd10':'E10-E14','name':'цукровий діабет'}
		,{'key':'N','nrr':'6.0','icd10':'F00-F99','name':'<b>Розлади психіки та поведінки</b>'}
		,{'key':'N','nrr':'7.0','icd10':'G00-G99','name':'<b>Хвороби нервової системи</b>'}
		,{'key':'N','nrr':'7.1','icd10':'G00,G03, G04, G06, G08, G09','name':'з них:<br/> запальні хвороби центральної нервової системи'}
		,{'key':'N','nrr':'7.2','icd10':'G45','name':'транзиторні церебральні ішемічні напади та споріднені синдроми'}
		,{'key':'N','nrr':'7.3','icd10':'G80-G83','name':'церебральний параліч та інші паралітичні синдроми'}
		,{'key':'N','nrr':'8.0','icd10':'H00-H59','name':'<b>Хвороби ока та придаткового апарата</b>'}
		,{'key':'N','nrr':'9.0','icd10':'H60-H95','name':'<b>Хвороби вуха та соскоподібного відростка</b>'}
		,{'key':'N','nrr':'9.1','icd10':'H65.0.1, H66.0','name':'з них гострий отит середнього вуха'}
		,{'key':'N','nrr':'10.0','icd10':'I00-I99','name':'<b>Хвороби системи кровообігу</b>'}
		,{'key':'N','nrr':'10.1','icd10':'I00-I02','name':'з них:<br/> гостра ревматична гарячка'}
		,{'key':'N','nrr':'10.2','icd10':'I05-I09','name':'хронічні ревматичні хвороби серця'}
		,{'key':'N','nrr':'10.3','icd10':'I10-I13','name':'гіпертонічна хвороба (без згадування про ішемічну хворобу серця та судинні ураження мозку)'}
		,{'key':'N','nrr':'10.4','icd10':'I20-I25','name':'ішемічна хвороба серця'}
		,{'key':'N','nrr':'10.5','icd10':'I20','name':'у тому числі:<br/> стенокардія'}
		,{'key':'N','nrr':'10.6','icd10':'I21-I22','name':'гострий інфаркт міокарда'}
		,{'key':'N','nrr':'10.7','icd10':'I24','name':'інші форми гострої ішемічної хвороби серця'}
		,{'key':'N','nrr':'10.8','icd10':'I60-I69','name':'цереброваскулярні хвороби'}
		,{'key':'N','nrr':'10.9','icd10':'I60-I62','name':'у тому числі:<br/> внутрішньочерепний крововилив'}
		,{'key':'N','nrr':'10.10','icd10':'I63','name':'інфаркт головного мозку'}
		,{'key':'N','nrr':'10.11','icd10':'I64','name':'інсульт, неуточнений як крововилив або інфаркт мозку'}
		,{'key':'N','nrr':'11.0','icd10':'J00-J99','name':'<b>Хвороби органів дихання</b>'}
		,{'key':'N','nrr':'11.1','icd10':'J12-J16, J18','name':'з них:<br/> пневмонії'}
		,{'key':'N','nrr':'11.2','icd10':'J44.0,1,9','name':'інші обструктивні хвороби легень'}
		,{'key':'N','nrr':'11.3','icd10':'J44.8.2','name':'бронхіт хронічний обструктивний'}
		,{'key':'N','nrr':'11.4','icd10':'J45-J46','name':'бронхіальна астма'}
		,{'key':'N','nrr':'11.5','icd10':'J47','name':'бронхоектатична хвороба'}
		,{'key':'N','nrr':'12.0','icd10':'K00-K93','name':'<b>Хвороби органів травлення</b>'}
		,{'key':'N','nrr':'12.1','icd10':'K04-K14','name':'з них:<br/> хвороби ротової порожнини, залоз та щелеп'}
		,{'key':'N','nrr':'12.2','icd10':'K21','name':'гостро-езофаренгиальний рефлюкс'}
		,{'key':'N','nrr':'12.3','icd10':'K25-K27','name':'виразка шлунка та 12-палої кишки'}
		,{'key':'N','nrr':'12.4','icd10':'K25.1, 2, 5, 6; K26.1, 2, 5, 6','name':'у тому числі проривна виразка шлунка та 12-палої кишки'}
		,{'key':'N','nrr':'12.5','icd10':'K29','name':'гастрит та дуоденіт'}
		,{'key':'N','nrr':'12.6','icd10':'K29.0, K29.1','name':'у тому числі гострий геморагічний та інші гострі гастрити'}
		,{'key':'N','nrr':'12.7','icd10':'K30','name':'диспепсія'}
		,{'key':'N','nrr':'12.8','icd10':'K35','name':'гострий апендицит'}
		,{'key':'N','nrr':'12.9','icd10':'K40-K46','name':'грижа'}
		,{'key':'N','nrr':'12.10','icd10':'K40.0,1,3,4, K41.0,1,3,4, K42.0,1,3,4 - K46.0,1,3,4','name':'у тому числі защемлена грижа (з непрохідністю, гангреною)'}
		,{'key':'N','nrr':'12.11','icd10':'K50','name':'хвороба Крона'}
		,{'key':'N','nrr':'12.12','icd10':'K51','name':'неспецифічний виразковий коліт'}
		,{'key':'N','nrr':'12.13','icd10':'K58','name':'синдром подразнення кишечника'}
		,{'key':'N','nrr':'12.14','icd10':'K70.3, K71.7, K74.3-6','name':'цироз печінки'}
		,{'key':'N','nrr':'12.15','icd10':'K80, K81, K82.2, K83.0','name':'жовчокамʼяна хвороба, холецистит, холангіт'}
		,{'key':'N','nrr':'12.16','icd10':'K80.0, K81.0, K82.2','name':'у тому числі гострий холецистит'}
		,{'key':'N','nrr':'12.17','icd10':'K85-K86','name':'хвороби підшлункової залози'}
		,{'key':'N','nrr':'12.18','icd10':'K85','name':'у тому числі: гострий панкреатит'}
		,{'key':'N','nrr':'12.19','icd10':'K90.0','name':'целіакія'}
		,{'key':'N','nrr':'13.0','icd10':'L00-L99','name':'<b>Хвороби шкіри та підшкірної клітковини</b>'}
		,{'key':'N','nrr':'14.0','icd10':'M00-M99','name':'<b>Хвороби кістково-мʼязової системи та сполучної тканини</b>'}
		,{'key':'N','nrr':'14.1','icd10':'M05-M06, M08, M10-M13','name':'з них: ревматоїдний артрит та інші запальні артропатії'}
		,{'key':'N','nrr':'14.2','icd10':'M05-M06','name':'у тому числі ревматоїдний артрит'}
		,{'key':'N','nrr':'14.3','icd10':'M86','name':'остеомієліт'}
		,{'key':'N','nrr':'14.4','icd10':'M40-M43 M46-M48 M53-M54','name':'інші дорсопатії, спондилопатії'}
		,{'key':'N','nrr':'15.0','icd10':'N00-N99','name':'Хвороби сечової системи'}
		,{'key':'N00','nrr':'15.1','icd10':'N00','name':'з них: гострий гломерулонефрит'}
		,{'key':'N03','nrr':'15.2','icd10':'N03','name':'хнорічний гломерулонефрит'}
		,{'key':'N10','nrr':'15.3','icd10':'N10-N12','name':'інфекції нирок'}
		,{'key':'N','nrr':'15.4','icd10':'N11','name':'у тому числі хронічний пієлонефрит'}
		,{'key':'N','nrr':'15.5','icd10':'N20','name':'камені нирок і сечоводу'}
		,{'key':'N','nrr':'15.6','icd10':'N40-N42','name':'хвороби передміхурової залози'}
		,{'key':'N','nrr':'16.0','icd10':'O00-O99','name':'<b>Вагітність, пологи та післяпологовий період</b>'}
		,{'key':'N','nrr':'17.0','icd10':'P00-P96','name':'<b>Окремі стани, що виникають у перинатальному періоді</b>'}
		,{'key':'N','nrr':'18.0','icd10':'Q00-Q99','name':'<b>Уроджені аномалії (вади розвитку), деформації і хромосомні порушення</b>'}
		,{'key':'N','nrr':'19.0','icd10':'R00-R99','name':'<b>Симптоми, ознаки та відхилення від норми, що виявлені при лабораторних та клінічних дослідженнях, не класифіковані в інших рубриках</b>'}
		,{'key':'N','nrr':'20.0','icd10':'S00-T98','name':'<b>Травми, отруєння та деякі інші наслідки дії зовнішніх причин</b>'}
		,{'key':'N','nrr':'20.1','icd10':'S02, S12, S22, S32, T02, T08','name':'з них:<br/> переломи кісток черепа, хребта, кісток тулуба, переломи в декількох ділянках тіла'}
		,{'key':'N','nrr':'20.2','icd10':'S06','name':'внутрішньочерепні травми'}
		,{'key':'N','nrr':'20.3','icd10':'S26, S27, S36, S37','name':'травми інших внутрішніх органів, грудної, черевної порожнини і таза'}
		,{'key':'N','nrr':'20.4','icd10':'T20-T32','name':'термічні та хімічні опіки'}
		,{'key':'N','nrr':'20.5','icd10':'T36-T50','name':'отруєння ліками та біологічними речовинами'}
		];
	$scope.f20t3220HeadNrrIndexes = {};
	$scope.f20t3220Head.forEach(function(h,i){
		$scope.f20t3220HeadNrrIndexes[h.nrr] = i;
	});
	console.log($scope.f20t3220HeadNrrIndexes);
}]);

//GeneralReport
hol2eih3App.controller('GeneralReportCtrl', [ '$scope', '$http', '$filter', '$sce'
	, function ( $scope, $http, $filter, $sce) {
		console.log("GeneralReportCtrl");
		initReport($scope, $http);
		$scope.bedDayHead = [
			{'key':'','name':'Показник'}
			,{'key':'cnt_out','name':'Вибуло хворих з стаціонару','per_unit':'cnt_out'}
			,{'key':'bed_day','name':'Проведено вибулими ліжкоднів','per_unit':'bed_day'}
			,{'key':'gender_m','name':'З них чоловіків','per_unit':'cnt_out'}
			,{'key':'gender_f','name':'жінок','per_unit':'cnt_out'}
			,{'key':'adult','name':'з них: дорослих','per_unit':'cnt_out'}
			,{'key':'child','name':'дітей','per_unit':'cnt_out'}
			,{'key':'dead','name':'Померло хворих','per_unit':'cnt_out'}
			,{'key':'dead_adult','name':'з них: дорослих','per_unit':'cnt_out'}
			,{'key':'dead_child','name':'дітей','per_unit':'cnt_out'}
			,{'key':'h_planovo','name':'Госпіталізовано планово','per_unit':'cnt_out'}
			,{'key':'h_terminovo','name':'Госпіталізовано терміново','per_unit':'cnt_out'}
			,{'key':'h_terminovo6','name':'терміни: до 6 годин','per_unit':'cnt_out'}
			,{'key':'h_terminovo7_24','name':'від 7 до 24 годин','per_unit':'cnt_out'}
			,{'key':'h_terminovo24','name':'пізніше 24-х годин','per_unit':'cnt_out'}
			,{'key':'powtorno','name':'Госпіталізовано повторно','per_unit':'cnt_out'}
			,{'key':'move_other_hostital','name':'Переведено в інші стаціонари','per_unit':'cnt_out'}
			,{'key':'healthy','name':'Особи, які виявилися здоровими','per_unit':'cnt_out'}
			
			,{'key':'iiww_participant','name':'Вибуло учасників ВВВ','per_unit':'cnt_out'}
			,{'key':'iiww_participant_dead','name':'з них померло','per_unit':'cnt_out'}
			,{'key':'iiww_participant_bed_day','name':'проведено вибулими ліжкоднів','per_unit':'bed_day'}
			,{'key':'iiww_invalid','name':'Вибуло інвалідів ВВВ','per_unit':'cnt_out'}
			,{'key':'iiww_invalid_dead','name':'з них померло','per_unit':'cnt_out'}
			,{'key':'iiww_invalid_bed_day','name':'проведено вибулими ліжкоднів','per_unit':'bed_day'}
			,{'key':'chernobyl','name':'Вибуло чорнобильців','per_unit':'cnt_out'}
			,{'key':'chernobyl_dead','name':'з них померло','per_unit':'cnt_out'}
			,{'key':'chernobyl_bed_day','name':'проведено вибулими ліжкоднів','per_unit':'bed_day'}
			
			,{'key':'condition_passable','name':'З числа виписаних поступили в задовільному стані','per_unit':'cnt_out'}
			,{'key':'condition_moderate','name':'середньої важкості','per_unit':'cnt_out'}
			,{'key':'condition_serious','name':'важкому','per_unit':'cnt_out'}
			,{'key':'condition_extremely','name':'вкрай важкому','per_unit':'cnt_out'}
			,{'key':'condition_terminal','name':'термінальному','per_unit':'cnt_out'}
			,{'key':'recovered','name':'З числа виписаних виписано з одужанням','per_unit':'cnt_out'}
			,{'key':'recovered_adult','name':'з них: дорослих','per_unit':'cnt_out'}
			,{'key':'recovered_child','name':'дітей','per_unit':'cnt_out'}
			,{'key':'improved','name':'З числа виписаних виписано з покращенням','per_unit':'cnt_out'}
			,{'key':'improved_adult','name':'з них: дорослих','per_unit':'cnt_out'}
			,{'key':'improved_child','name':'дітей','per_unit':'cnt_out'}
			,{'key':'unchanged','name':'З числа виписаних виписано без змін','per_unit':'cnt_out'}
			,{'key':'unchanged_adult','name':'з них: дорослих','per_unit':'cnt_out'}
			,{'key':'unchanged_child','name':'дітей','per_unit':'cnt_out'}
			,{'key':'deterioration','name':'З числа виписаних виписано з погрішенням','per_unit':'cnt_out'}
			,{'key':'deterioration_adult','name':'з них: дорослих','per_unit':'cnt_out'}
			,{'key':'deterioration_child','name':'дітей','per_unit':'cnt_out'}
		];

		$scope.calcPart = function (h){
			if($scope.generalReport){
				var firstYear = $scope.generalReport.splitYears[$scope.years[0]];
				var myField = firstYear[h.key];
				if(myField){
					if(h.per_unit){
						var myFieldUnit = firstYear[h.per_unit];
						var calcPart = Math.round(myField/myFieldUnit*1000)/10;
						return calcPart + '%';
					}
				}
			}
			return '-';
		};

		$scope.calcProcent = function (year, key){
			var calcProcent = Math.round(($scope.generalReport.splitYears[$scope.years[0]][key]/
					$scope.generalReport.splitYears[year][key] - 1) * 1000)/10;
			return calcProcent;
		}

		var url = '/r/report-general-'+$scope.minMonth+'-'+$scope.maxMonth;
		console.log(url);
		$http({ method : 'GET', url : url
		}).success(function(data, status, headers, config) {
			$scope.generalReport = data;
			console.log($scope.generalReport);
			initGeneralReport();
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});

		/*
		$http.get(url).success(function(response) {
		}).error($scope.errorHandle);
		 * */
		function initGeneralReport(){
			$scope.years = [];
			$scope.generalReport.splitYears = {};
			angular.forEach($scope.generalReport.reportGeneral, function(value, key) {
				console.log(value);
				console.log(value.year);
				$scope.years.push(value.year);
				$scope.generalReport.splitYears[value.year] = value;
			});
			console.log($scope.years);
			console.log($scope.generalReport);
		};
}]);

//hol2eih3App.controller('DepartmentMonthMovementMySqlCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                            , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
hol2eih3App.controller('DepartmentMonthMovementMySqlCtrl', [ '$scope', '$http', '$filter', '$sce'
	, function ( $scope, $http, $filter, $sce) {
	console.log("DepartmentMonthMovementCtrl");
	initReport($scope, $http);
	
	$scope.bedDayHead = [
		{"title":"","name":"Відділення","key":""}
		,{"title":" (ліжка)","name":"Ліжок на штаті","key":"department_bed"}
		,{"title":"Поступило (люди)","name":"Поступило","key":"in_clinic"}
		,{"title":"Виписано (люди)","name":"Виписано","key":"out_clinic"}
		,{"title":"Переведено в інші відділення (люди)"
			,"name":"П. в інші від.","key":"in_dep"}
		,{"title":"Переведено з інші відділення (люди)"
			,"name":"П. з інші від.","key":"out_dep"}
		,{"title":"Померло (люди)","name":"Померло","key":"dead"}
		,{"title":"Лікарняна летальність (%)","name":"% Л-на летальність","key":"mortality"}
		,{"title":"Лікувалось хворих (люди)","name":"Лік. хворих","key":"TREAT"}
		,{"title":"Проведено ліжкоднів (дні)","name":"Проведено ліжкоднів","key":"bed_day"}
		,{"title":"План ліжкоднів (дні)","name":"Пл-н ліжкоднів","key":"bed_day_plan"}
		,{"title":"Процент виконання плана ліжкоднів (%)","name":"% вик. плана","key":"bed_day_fulfil"}
		,{"title":"Зайнятість ліжка (днів)","name":"З-ть ліжка","key":"bed_occupancy"}
		,{"title":"Оборот ліжка (раз)","name":"Оборот ліжка","key":"bed_turnover"}
		,{"title":"Оборот ліжка (раз)","name":"Оборот ліжка (альтернатів)","key":"bed_turnover2"}
		,{"title":"Ср. трив. лікуання (днів)","name":"Ср. трив. лікуання","key":"treat_avg"}
	];

	eqMonth = function(){
		var url1 = "/r/readBedDayMySql-";
		if($scope.param.department_id)
			url1 = "/r/readBedDayDepartmentMySql-";
		if(!$scope.paramYear)
			$scope.paramYear = new Date().getFullYear();

		var url = url1 + $scope.minMonth + "-" + $scope.maxMonth + "-" + $scope.paramYear;
		console.log(url);
		$http({ method : 'GET', url : url
		}).success(function(data, status, headers, config) {
			$scope.bedDay = data;
			console.log($scope.bedDay);
			console.log($scope.bedDay.duration);
			$scope.dbDuration = $scope.bedDay.duration;
			if($scope.param.department_id){
				for (var i = 0; i < $scope.bedDay.bedDayOfMonthMySql.length; i++) {
					if($scope.bedDay.bedDayOfMonthMySql[i].dp_id == $scope.param.department_id){
						$scope.bedDayOfMonthMySql = $scope.bedDay.bedDayOfMonthMySql[i];
						if($scope.bedDay.bedDayDepartmentMySql){
							$scope.bedDayOfMonthMySql.bedDayDepartmentMySql = [];
							for (var j = 0; j < $scope.bedDay.bedDayDepartmentMySql.length; j++) {
								var x = $scope.bedDay.bedDayDepartmentMySql[j][i];
								$scope.bedDayOfMonthMySql.bedDayDepartmentMySql.push(x);
							}
						}
						break;
					}
				}
				console.log($scope.bedDayOfMonthMySql);
				mmArray();
			}
//			initDateVariables();
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}

	eqMonth();

	mmArray();
}]);

//hol2eih3App.controller('MvPatientInWeekDayCtrl', ['$cookies', '$cookieStore', '$scope', '$http', '$filter', '$sce'
//                                                  , function ($cookies, $cookieStore, $scope, $http, $filter, $sce) {
//	console.log("/readMove-day-Patients");
//	initController($scope, $http, $filter, $cookies);
hol2eih3App.controller('MvPatientInWeekDayCtrl', ['$scope', '$http', '$filter', '$sce'
		, function ( $scope, $http, $filter, $sce) {
	console.log("/readMove-day-Patients");
	initController($scope, $http, $filter);
	// 1.1  Зчитування надходження/виписки хворих на сьогодні – readTodayMovePatients
	$scope.readMoveTodayPatients();
	$scope.isParamDate = function(month, day){
		if($scope.paramDateDate.getDate() == day){
			if(month == $scope.paramDateDate.getMonth() + 1){
				return true;
			}
		}
		return false;
	}
	$scope.monthDayDate = function(month, day){
//		var d2 = new Date($cookies.get('year'), month - 1, day);
		var d2 = new Date(2016, month - 1, day);
		return d2;
//		return new Date(new Date().getFullYear(), month - 1, day);
	}
	$scope.passedDays = function(m,d){
		return new Date() > $scope.monthDayDate(m,d);
	}
	$scope.satSanDay = function(m,d){
		return $scope.monthDayDate(m,d).getDay() == 6 || $scope.monthDayDate(m,d).getDay() == 0;
	}
	$scope.monthDay = function(m,d){
		var dt = new Date(new Date().getFullYear(), m, 0).getDate();
		if(d>dt){
			return;
		}
		return d;
	}
}]);

// 2  Показ кількості надходжень/виписки хворих за останні 7 днів – movePatients.html.
hol2eih3App.controller('MovePatientsCtrl', [ '$scope', '$http', '$filter', '$sce'
         , function ($scope, $http, $filter, $sce) {
	console.log("/readMovePatients");
	$scope.today = new Date();
	$scope.last7day = [$scope.today];
	for (var i = 0; i < 7; i++) {
		$scope.last7day.push(new Date($scope.last7day[i].getTime() - (24*60*60*1000)));
	}
	$scope.last7day.reverse();

	$scope.yyyyMMdd = function(d){
		return moment(d).format("YYYY-MM-DD");
	}

	// 2.1  Зчитування руху хворих за останні 7 днів – readMovePatients
	$scope.readMovePatients = function(){
		$http({ method : 'GET', url : "/readMovePatients"
		}).success(function(data, status, headers, config) {
			$scope.movePatients = data;
			console.log($scope.movePatients);
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
	$scope.readMovePatients();
	
}]);
// 3  План операцій – operationsPlan.html
hol2eih3App.controller('OperationsPlanCtrl', [ '$scope', '$http', '$filter', '$sce', function ($scope, $http, $filter, $sce) {
	// 3.2  Зчитування плану операцій на сьогодні – readTodayOperationsPlan
	$scope.readTodayOperationsPlan = function(){
		$http({ method : 'GET', url : "/readTodayOperationsPlan"
		}).success(function(data, status, headers, config) {
			$scope.movePatients = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
	$scope.readTodayOperationsPlan();
	// 3.1  Запис плану операцій на завтра – saveTomorrowOperationsPlan
	$scope.saveTomorrowOperationsPlan = function(){
		$http({ method : 'POST', data : $scope.moveTodayPatients, url : "/saveTomorrowOperationsPlan"
		}).success(function(data, status, headers, config){
			console.log(data);
			$scope.tomorrowOperationsPlan = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}

}]);
//4  Список операцій відділення по датах –  departmentOperationsList.html
hol2eih3App.controller('DepartmentOperationsListCtrl', [ '$scope', '$http', '$filter', '$sce', function ($scope, $http, $filter, $sce) {
	// 4.1  Зчитування списку операцій за останні 7 днів –  readListOperationsPlan
	$scope.readListOperationsPlan = function(){
		$http({ method : 'GET', url : "/readListOperationsPlan"
		}).success(function(data, status, headers, config) {
			$scope.movePatients = data;
		}).error(function(data, status, headers, config) {
			$scope.error = data;
		});
	}
	$scope.readListOperationsPlan();
}]);

