function() {

	/*
	dataProvider.getAllDataParamsString returns in this format:
	{"id":2,
	"values":[["1376420457713","out"]],
	"params":["timestamp","direction"],
	"type":"simple",
	"name":"CallData"}
	
	we return JSONArray (one object per day):
	[{"timestamp":<timestamp>,"in":<count>,"out":<count>},{...},...]
	*/
 
	var data = dataProvider.getAllDataParamsString('CallData', 1, 'timestamp,direction');
	//data = '{"id":"1","values":[["1382486400000","out"],["1382601600123","in"],["1382710800000","out"],["1382710800999","in"],["1382792317667","out"],["1382793856916","in"],["1382793861944","out"],["1382796864140","in"],["1382796866262","out"],["1382799847941","in"],["1382801159836","in"],["1382801180587","out"],["1382801246169","in"]],"params":["timestamp","mode"],"type":"simple","name":"CallData"}';
	
	if (data == undefined || data == null){
		document.write('no CallData');
		return;
	}
	document.write('From:<br/>');
	document.write(data + '<br/><br/>');
	
	var values = JSON.parse(data).values;
	var result = [];
	
	for (var j in values){
		var millis = +values[j][0];
		var date = new Date(millis);
		
		var startDay = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0);
		var startUTCmillis = startDay.getTime() - startDay.getTimezoneOffset()*60*1000;
		if (result.length == 0 || result[result.length-1].timestamp != startUTCmillis){
			var newObj = {};
			newObj.timestamp = startUTCmillis;
			newObj['out'] = 0;
			newObj['in'] = 0;
			if (values[j][1] == 'out'){
				newObj['out'] += 1;
			} else if (values[j][1] == 'in'){
				newObj['in'] += 1;
			}
			result.push(newObj);
		}
		else {
			var oldObj = result[result.length-1];
			if (values[j][1] == 'out'){
				oldObj['out'] += 1;
			} else if (values[j][1] == 'in'){
				oldObj['in'] += 1;
			}
		}
	}
	document.write('To:<br/>');
	document.write(JSON.stringify(result));
	dataProvider.setJSON(JSON.stringify(result));

}