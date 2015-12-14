'use strict';

import React, {
	StyleSheet, 
	PropTypes, 
	View, 
	Text,
	Animated,
	Platform,
	Dimensions
} from 'react-native';

import PickerAndroid from 'react-native-picker-android';

let Picker = Platform.OS === 'ios' ? PickerIOS : PickerAndroid;
let PickerItem = Picker.Item;
let width = Dimensions.get('window').width;
let height = Dimensions.get('window').height;

export default class PickerAny extends React.Component {

	constructor(props, context){
		super(props, context);
		this.state = {

		};
	};

	_prePressHandle(callback){
		this.picker.moveUp();
	};

	_nextPressHandle(callback){
		this.picker.moveDown();
	};
	
	render(){
		<Animated.View style={[styles.picker, {bottom: this.state.slideAnim}]}>
			<View style={styles.pickerToolbar}>
				<View style={styles.pickerBtnView}>
					<Text style={styles.pickerMoveBtn} onPress={this._prePressHandle}>上一个</Text>
					<Text style={styles.pickerMoveBtn} onPress={this._nextPressHandle}>下一个</Text>
				</View>
				<View style={styles.pickerFinishBtn}>
					<Text style={styles.pickerFinishBtnText} 
						onPress={() => {
							this.setState({course: this.state.courseData[this.index || 0]})
							this._pickerToggle();
						}}>完成</Text>
				</View>
			</View>
			<Picker
				ref={(picker) => { this.picker = picker }}
				selectedValue={this.props.selectedValue}
				onValueChange={(index) => this.index = index} >
				{this.props.pickerData.map((value, index) => (
					<PickerAndroid.Item
						key={index}
						value={value}
						label={value}
					/>)
				)}
			</Picker>
		</Animated.View>
	};
};

let styles = StyleSheet.create({
	picker: { 
		flex: 1,
		position: 'absolute',
		bottom: 0,
		left: 0,
		backgroundColor: 'rgb(189, 192, 199)',
		width: width,  
		height: height / 3, 
		overflow: 'hidden', 
	},
	pickerToolbar: {
		height: 30,
		width: width,
		backgroundColor: '#e6e6e6',
		flexDirection: 'row',
		borderTopWidth: 1,
		borderBottomWidth: 1,
		borderColor: '#c3c3c3'
	},
	pickerBtnView: {
		flex: 1,
		flexDirection: 'row',
		justifyContent: 'flex-start',
		alignItems: 'center',
		paddingLeft: 20
	},
	pickerMoveBtn: {
		paddingLeft: 20,
		color: '#149be0',
		fontSize: 16,
	},
	pickerFinishBtn: {
		flex: 1,
		flexDirection: 'row',
		justifyContent: 'flex-end',
		alignItems: 'center',
		paddingRight: 20,
	},
	pickerFinishBtnText: {
		fontSize: 16,
		color: '#149be0'
	}
});
