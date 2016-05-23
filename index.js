'use strict';

import React, {Component, PropTypes} from 'react';
import {
	StyleSheet,
	View,
	Text,
	Animated,
	Platform,
	Dimensions,
	PickerIOS
} from 'react-native';

import PickerAndroid from 'react-native-picker-android';

let Picker = Platform.OS === 'ios' ? PickerIOS : PickerAndroid;
let PickerItem = Picker.Item;
let {width, height} = Dimensions.get('window');

const longSide = width > height ? width : height;
const shortSide = width > height ? height : width;

export default class PickerAny extends Component {

	static propTypes = {
		style: View.propTypes.style,
		pickerElevation: PropTypes.number,
		pickerBtnText: PropTypes.string,
		pickerCancelBtnText: PropTypes.string,
		pickerBtnStyle: Text.propTypes.style,
		pickerTitle: PropTypes.string,
		pickerTitleStyle: Text.propTypes.style,
		pickerToolBarStyle: View.propTypes.style,
		showMask: PropTypes.bool,
		showDuration: PropTypes.number,
		pickerData: PropTypes.any.isRequired,
		selectedValue: PropTypes.any.isRequired,
		onPickerDone: PropTypes.func,
		onPickerCancel: PropTypes.func,
		onValueChange: PropTypes.func
	};

	static defaultProps = {
		style: {
			width: width
		},
		pickerBtnText: 'Done',
		pickerCancelBtnText: 'Cancel',
		showMask: false,
		showDuration: 300,
		onPickerDone: ()=>{},
		onPickerCancel: ()=>{},
		onValueChange: ()=>{}
	};

	constructor(props, context){
		super(props, context);
	}

	componentWillMount(){
		this.state = this._getStateFromProps(this.props);
	}

	componentWillReceiveProps(newProps){
		let newState = this._getStateFromProps(newProps);
		this.setState(newState);
	}

	shouldComponentUpdate(nextProps, nextState, context){
		return true;
	}

	_getStateFromProps(props){
		//the pickedValue must looks like [wheelone's, wheeltwo's, ...]
		//this.state.selectedValue may be the result of the first pickerWheel
		let {pickerData, selectedValue} = props;
		let pickerStyle = pickerData.constructor === Array ? 'parallel' : 'cascade';
		let firstWheelData;
		let firstPickedData;
		let secondPickedData;
		let secondWheelData;
		let secondPickedDataIndex;
		let thirdWheelData;
		let thirdPickedDataIndex;
		let cascadeData = {};
		let slideAnim = (this.state && this.state.slideAnim ? this.state.slideAnim : new Animated.Value(-height));

		if(pickerStyle === 'parallel'){
			//compatible single wheel sence
			if(selectedValue.constructor !== Array){
				selectedValue = [selectedValue];
			}
			if(pickerData[0].constructor !== Array){
				pickerData = [pickerData];
			}
		}
		else if(pickerStyle === 'cascade'){
			//only support three stage
			firstWheelData = Object.keys(pickerData);
			firstPickedData = props.selectedValue[0];
			secondPickedData = props.selectedValue[1];
			cascadeData = this._getCascadeData(pickerData, selectedValue, firstPickedData, secondPickedData, true);
		}
		//save picked data
		this.pickedValue = JSON.parse(JSON.stringify(selectedValue));
		this.pickerStyle = pickerStyle;
		return {
			...props,
			pickerData,
			selectedValue,
			//list of first wheel data
			firstWheelData,
			//first wheel selected value
			firstPickedData,
			slideAnim,
			//list of second wheel data and pickedDataIndex
			secondWheelData: cascadeData.secondWheelData,
			secondPickedDataIndex: cascadeData.secondPickedDataIndex,
			//third wheel selected value and pickedDataIndex
			thirdWheelData: cascadeData.thirdWheelData,
			thirdPickedDataIndex: cascadeData.thirdPickedDataIndex
		};
	}

	_slideUp(){
		this._isMoving = true;
		Animated.timing(
			this.state.slideAnim,
			{
				toValue: 0,
				duration: this.state.showDuration,
			}
		).start((evt) => {
			if(evt.finished) {
				this._isMoving = false;
				this._isPickerShow = true;
			}
		});
	}

	_slideDown(){
		this._isMoving = true;
		Animated.timing(
			this.state.slideAnim,
			{
				toValue: -height,
				duration: this.state.showDuration,
			}
		).start((evt) => {
			if(evt.finished) {
				this._isMoving = false;
				this._isPickerShow = false;
			}
		});
	}

	_toggle(){
		if(this._isMoving) {
			return;
		}
		if(this._isPickerShow) {
			this._slideDown();
		}
		else{
			this._slideUp();
		}
	}
	
	toggle(){
		this._toggle();
	}
	show(){
		if(!this._isPickerShow){
			this._slideUp();
		}
	}
	hide(){
		if(this._isPickerShow){
			this._slideDown();
		}
	}
	isPickerShow(){
		return this._isPickerShow;
	}

	_prePressHandle(callback){
		this.pickerWheel.moveUp();
	}

	_nextPressHandle(callback){
		this.pickerWheel.moveDown();
	}

	_pickerCancel(){
		this._toggle();
		this.state.onPickerCancel();
	}

	_pickerFinish(){
		this._toggle();
		this.state.onPickerDone(this.pickedValue);
	}

	_renderParallelWheel(pickerData){
		return pickerData.map((item, index) => {
			return (
				<View style={styles.pickerWheel} key={index}>
					<Picker
						selectedValue={this.state.selectedValue[index]}
						onValueChange={value => {
							this.pickedValue.splice(index, 1, value);
							//do not set state to another object!! why?
							// this.setState({
							// 	selectedValue: this.pickedValue
							// });
							this.setState({
								selectedValue: JSON.parse(JSON.stringify(this.pickedValue))
							});
							this.state.onValueChange(JSON.parse(JSON.stringify(this.pickedValue)), index);
						}} >
						{item.map((value, index) => (
							<PickerItem
								key={index}
								value={value}
								label={value.toString()}
							/>)
						)}
					</Picker>
				</View>
			);
		});
	}

	_getCascadeData(pickerData, pickedValue, firstPickedData, secondPickedData, onInit){
		let secondWheelData;
		let secondPickedDataIndex;
		let thirdWheelData;
		let thirdPickedDataIndex;
		//only support two and three stage
		for(let key in pickerData){
			//two stage
			if(pickerData[key].constructor === Array){
				secondWheelData = pickerData[firstPickedData];
				if(onInit){
					secondWheelData.forEach(function(v, k){
						if(v === pickedValue[1]){
							secondPickedDataIndex = k;
						}
					}.bind(this));
				}
				else{
					secondPickedDataIndex = 0;
				}
				break;
			}
			//three stage
			else{
				secondWheelData = Object.keys(pickerData[firstPickedData]);
				if(onInit){
					secondWheelData.forEach(function(v, k){
						if(v === pickedValue[1]){
							secondPickedDataIndex = k;
						}
					}.bind(this));
				}
				else{
					secondPickedDataIndex = 0;
				}
				thirdWheelData = pickerData[firstPickedData][secondPickedData];
				if(onInit){
					thirdWheelData.forEach(function(v, k){
						if(v === pickedValue[2]){
							thirdPickedDataIndex = k;
						}
					})
				}
				else{
					thirdPickedDataIndex = 0;
				}
				break;
			}
		}

		return {
			secondWheelData,
			secondPickedDataIndex,
			thirdWheelData,
			thirdPickedDataIndex
		}
	}

	_renderCascadeWheel(pickerData){
		let thirdWheel = this.state.thirdWheelData && (
			<View style={styles.pickerWheel}>
				<Picker
					ref={'thirdWheel'}
					selectedValue={this.state.thirdPickedDataIndex}
					onValueChange={(index) => {
						this.pickedValue.splice(2, 1, this.state.thirdWheelData[index]);
						this.setState({
							thirdPickedDataIndex: index,
							selectedValue: 'wheel3'+index
						});
						this.state.onValueChange(JSON.parse(JSON.stringify(this.pickedValue)), 2);
					}} >
					{this.state.thirdWheelData.map((value, index) => (
						<PickerItem
							key={index}
							value={index}
							label={value.toString()}
						/>)
					)}
				</Picker>
			</View>
		);

		return (
			<View style={[styles.pickerWrap, {width: this.state.style.width || width}]}>
				<View style={styles.pickerWheel}>
					<Picker
						ref={'firstWheel'}
						selectedValue={this.state.firstPickedData}
						onValueChange={value => {
							let secondWheelData = Object.keys(pickerData[value]);
							let cascadeData = this._getCascadeData(pickerData, this.pickedValue, value, secondWheelData[0]);
							//when onPicked, this.pickedValue will pass to the parent
							//when firstWheel changed, second and third will also change
							if(cascadeData.thirdWheelData){
								this.pickedValue.splice(0, 3, value, cascadeData.secondWheelData[0], cascadeData.thirdWheelData[0]);
							}
							else{
								this.pickedValue.splice(0, 2, value, cascadeData.secondWheelData[0]);
							}

							this.setState({
								selectedValue: 'wheel1'+value,
								firstPickedData: value,
								secondWheelData: cascadeData.secondWheelData,
								secondPickedDataIndex: 0,
								thirdWheelData: cascadeData.thirdWheelData,
								thirdPickedDataIndex: 0
							});
							this.state.onValueChange(JSON.parse(JSON.stringify(this.pickedValue)), 0);
							this.refs.secondWheel && this.refs.secondWheel.moveTo && this.refs.secondWheel.moveTo(0);
							this.refs.thirdWheel && this.refs.thirdWheel.moveTo && this.refs.thirdWheel.moveTo(0);
						}} >
						{this.state.firstWheelData.map((value, index) => (
							<PickerItem
								key={index}
								value={value}
								label={value.toString()}
							/>)
						)}
					</Picker>
				</View>
				<View style={styles.pickerWheel}>
					<Picker
						ref={'secondWheel'}
						selectedValue={this.state.secondPickedDataIndex}
						onValueChange={(index) => {
							let thirdWheelData = pickerData[this.state.firstPickedData][this.state.secondWheelData[index]];
							if(thirdWheelData){
								this.pickedValue.splice(1, 2, this.state.secondWheelData[index], thirdWheelData[0]);
							}
							else{
								this.pickedValue.splice(1, 1, this.state.secondWheelData[index]);
							}

							this.setState({
								secondPickedDataIndex: index,
								thirdWheelData,
								thirdPickedDataIndex: 0,
								selectedValue: 'wheel2'+index
							});
							this.state.onValueChange(JSON.parse(JSON.stringify(this.pickedValue)), 1);
							this.refs.thirdWheel && this.refs.thirdWheel.moveTo && this.refs.thirdWheel.moveTo(0);
						}} >
						{this.state.secondWheelData.map((value, index) => (
							<PickerItem
								key={index}
								value={index}
								label={value.toString()}
							/>)
						)}
					</Picker>
				</View>
				{thirdWheel}
			</View>
		);
	}

	_renderWheel(pickerData){
		let wheel = null;
		if(this.pickerStyle === 'parallel'){
			wheel = this._renderParallelWheel(pickerData);
		}
		else if(this.pickerStyle === 'cascade'){
			wheel = this._renderCascadeWheel(pickerData);
		}
		return wheel;
	}

	render(){

		let mask = this.state.showMask ? (
			<View style={styles.mask} >
				<Text style={{width: width, height: height}} onPress={this._pickerCancel.bind(this)}></Text>
			</View>
		) : null;

		return (
			<Animated.View style={[styles.picker, {
				elevation: this.state.pickerElevation,
				width: longSide,
				height: this.state.showMask ? height : this.state.style.height,
				bottom: this.state.slideAnim
			}]}>
				{mask}
				<View style={[styles.pickerBox, this.state.style]}>
					<View style={[styles.pickerToolbar, this.state.pickerToolBarStyle, {width: this.state.style.width || width}]}>
						<View style={styles.pickerCancelBtn}>
							<Text style={[styles.pickerFinishBtnText, this.state.pickerBtnStyle]}
								onPress={this._pickerCancel.bind(this)}>{this.state.pickerCancelBtnText}</Text>
						</View>
						<Text style={[styles.pickerTitle, this.state.pickerTitleStyle]} numberOfLines={1}>
							{this.state.pickerTitle}
						</Text>
						<View style={styles.pickerFinishBtn}>
							<Text style={[styles.pickerFinishBtnText, this.state.pickerBtnStyle]}
								onPress={this._pickerFinish.bind(this)}>{this.state.pickerBtnText}</Text>
						</View>
					</View>
					<View style={[styles.pickerWrap, {width: this.state.style.width || width}]}>
						{this._renderWheel(this.state.pickerData)}
					</View>
				</View>
			</Animated.View>
		);
	}
};

let styles = StyleSheet.create({
	picker: {
		position: 'absolute',
		bottom: 0,
		left: 0,
		backgroundColor: 'transparent',
	},
	pickerBox: {
		position: 'absolute',
		bottom: 0,
		left: 0,
		backgroundColor: '#bdc0c7'
	},
	mask: {
		position: 'absolute',
		top: 0,
		backgroundColor: 'transparent',
		height: height,
		width: width
	},
	pickerWrap: {
		flexDirection: 'row'
	},
	pickerWheel: {
		flex: 1
	},
	pickerToolbar: {
		height: 30,
		backgroundColor: '#e6e6e6',
		flexDirection: 'row',
		borderTopWidth: 1,
		borderBottomWidth: 1,
		borderColor: '#c3c3c3',
		alignItems: 'center'
	},
	pickerBtnView: {
		flex: 1,
		flexDirection: 'row',
		justifyContent: 'flex-start',
		alignItems: 'center'
	},
	pickerMoveBtn: {
		color: '#149be0',
		fontSize: 16,
		marginLeft: 20
	},
	pickerCancelBtn: {
		flex: 1,
		flexDirection: 'row',
		justifyContent: 'flex-start',
		alignItems: 'center',
		marginLeft: 20
	},
	pickerTitle: {
		flex: 4,
		color: 'black',
		textAlign: 'center'
	},
	pickerFinishBtn: {
		flex: 1,
		flexDirection: 'row',
		justifyContent: 'flex-end',
		alignItems: 'center',
		marginRight: 20
	},
	pickerFinishBtnText: {
		fontSize: 16,
		color: '#149be0'
	}
});
