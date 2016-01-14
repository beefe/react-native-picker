'use strict';

import React from 'react-native';
import Picker from 'react-native-picker';

function createDateData(){
	let date = {};
	for(let i=1950;i<2050;i++){
		let month = {};
		for(let j = 1;j<13;j++){
			let day = [];
			if(j === 2){
				for(let k=1;k<29;k++){
					day.push(k+'日');
				}
			}
			else if(j in {1:1, 3:1, 5:1, 7:1, 8:1, 10:1, 12:1}){
				for(let k=1;k<32;k++){
					day.push(k+'日');
				}
			}
			else{
				for(let k=1;k<31;k++){
					day.push(k+'日');
				}
			}
			month[j+'月'] = day;
		}
		date[i+'年'] = month;
	}
	return date;
};

export default class DatePicker extends React.Component {

	toggle(){
		this.picker.toggle();
	}

	render(){
		return (
			<Picker
				ref={picker => this.picker = picker}
				pickerHeight={300}
				showDuration={300}
				pickerData={this.props.pickerData || createDateData()}
				selectedValue={this.props.selectedValue}
				onPickerDone={(pickedValue) => {
					this.props.onPickerDone && this.props.onPickerDone(pickedValue);
				}}
			/>
		);
	}
};