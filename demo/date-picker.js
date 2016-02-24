'use strict';
 
import React, {
	View,
	Text,
	TouchableOpacity,
	Dimensions
} from 'react-native';

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

	_onPressHandle(){
		this.picker.toggle();
	}

	render(){
		return (
			<View style={{height: Dimensions.get('window').height}}>
				<TouchableOpacity style={{marginTop: 20}} onPress={this._onPressHandle.bind(this)}>
					<Text>点我</Text>
				</TouchableOpacity>
				<Picker
					ref={picker => this.picker = picker}
					style={{height: 320}}
					showDuration={300}
					pickerData={createDateData()}
					selectedValue={['2015年', '12月', '12日']}
					onPickerDone={(pickedValue) => {
						console.log(pickedValue);
					}}
				/>
			</View>
		);
	}
};