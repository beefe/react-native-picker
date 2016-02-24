'use strict';
 
import React, {
	View,
	Text,
	TouchableOpacity,
	Dimensions
} from 'react-native';

import Picker from 'react-native-picker';

function createAreaData(area){
	let data = {};
	let len = area.length;
	for(let i=0;i<len;i++){
		let city = area[i]['city'];
		let cityLen = city.length;
		let ProvinceName = area[i]['name'];
		data[ProvinceName] = {};
		for(let j=0;j<cityLen;j++){
			let area = city[j]['area'];
			let cityName = city[j]['name'];
			data[ProvinceName][cityName] = area;
		}
	}
	return data;
};

export default class AreaPicker extends React.Component {

	constructor(props, context){
		super(props, context);
		this.state = {
			pickerData: [{
				'北京': {
					'北京': ['朝阳区']
				}
			}],
			selectedValue: ['北京', '北京', '朝阳区']
		};
		fetch('https://raw.githubusercontent.com/beefe/react-native-picker/master/demo/area.json').then(res => {
			res.json().then(data => {
				this.setState({
					pickerData: createAreaData(data)
				});
			});
		}, err => {
			console.log(err);
		});
	}

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
					pickerData={this.state.pickerData}
					selectedValue={this.state.selectedValue}
					onPickerDone={(pickedValue) => {
						console.log(pickedValue);
					}}
				/>
			</View>
		);
	}
};