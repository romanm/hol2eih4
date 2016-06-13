var declareFileCtrl = function($scope){
	console.log("-----initFileCtrl-----op-----")
	$scope.viewTab = 'editOperation';
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

	$scope.openToEdit = function(fieldName){
		$scope.openedToEdit = fieldName;
		console.log($scope.openedToEdit);
		if($scope.openedToEdit == 'operation'){
			$scope.openChildProcedureDb($scope.procedureOperation);
		}else
		if($scope.openedToEdit == 'icd'){
			if($scope.operationCodeCtrl.seekIcd == null){
				if($scope.operationCodeCtrl.seekOperation != null){
					$scope.operationCodeCtrl.seekIcd =
						$scope.operationCodeCtrl.seekOperation;
				}
			}
		}
	}
	//------procedure------Operation-----------
	
	console.log($scope.objectKeys($scope.fieldsOperation));

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
	}

}