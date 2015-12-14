'use strict';

import React, {
	StyleSheet, 
	PropTypes, 
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
let width = Dimensions.get('window').width;
let height = Dimensions.get('window').height;

export default class PickerAny extends React.Component {

	static propTypes = {
		pickerHeight: PropTypes.number,
		showDuration: PropTypes.number,
		pickerData: PropTypes.array,
		onPickerDone: PropTypes.func
	}

	static defaultProps = {
		pickerHeight: height/3,
		showDuration: 300,
		onPickerDone: ()=>{}
	}

	constructor(props, context){
		super(props, context);
		this.state = {
			selectedValue: this.props.selectedValue,
			slideAnim: new Animated.Value(-this.props.pickerHeight)
		};
	}

	_slideUp(){
		this.isMoving = true;
		Animated.timing(
			this.state.slideAnim,
			{
				toValue: 0,
				duration: this.props.showDuration,
			}
		).start((evt) => {
			if(evt.finished) {
				this.isMoving = false;
				this.isPickerShow = true;
			}
		});
	}

	_slideDown(){
		this.isMoving = true;
		Animated.timing(
			this.state.slideAnim,
			{
				toValue: -this.props.pickerHeight,
				duration: this.props.showDuration,
			}
		).start((evt) => {
			if(evt.finished) {
				this.isMoving = false;
				this.isPickerShow = false;
			}
		});
	}

	_toggle(){
		if(this.isMoving) {
			return;
		}
		if(this.isPickerShow) {
			this._slideDown();
		}
		else{
			this._slideUp();
		}
	}
	//向父组件提供方法
	toggle(){
		this._toggle();
	}

	_prePressHandle(callback){
		//通知子组件往上滚
		this.pickerWheel.moveUp();
	}

	_nextPressHandle(callback){
		//通知子组件往下滚
		this.pickerWheel.moveDown();
	}

	_pickerFinish(){
		let pickedValue = this.pickedValue === undefined ? this.state.selectedValue : this.pickedValue;
		this._toggle();
		this.props.onPickerDone(pickedValue);
	}
	
	render(){
		let pickerBtn = Platform.OS === 'ios' ? null : (
			<View style={styles.pickerBtnView}>
				<Text style={styles.pickerMoveBtn} onPress={this._prePressHandle.bind(this)}>上一个</Text>
				<Text style={styles.pickerMoveBtn} onPress={this._nextPressHandle.bind(this)}>下一个</Text>
			</View>
		);
		return (
			<Animated.View style={[styles.picker, {
				height: this.props.pickerHeight,
				bottom: this.state.slideAnim
			}]}>
				<View style={styles.pickerToolbar}>
					{pickerBtn}
					<View style={styles.pickerFinishBtn}>
						<Text style={styles.pickerFinishBtnText} 
							onPress={this._pickerFinish.bind(this)}>完成</Text>
					</View>
				</View>
				<Picker
					ref={pickerWheel => this.pickerWheel = pickerWheel }
					selectedValue={this.state.selectedValue}
					onValueChange={value => {
						this.pickedValue = value;
						this.setState({
							selectedValue: value
						});
					}} >
					{this.props.pickerData.map((value, index) => (
						<PickerItem
							key={index}
							value={value}
							label={value}
						/>)
					)}
				</Picker>
			</Animated.View>
		);
	}
};

let styles = StyleSheet.create({
	picker: {
		flex: 1,
		position: 'absolute',
		bottom: 0,
		left: 0,
		backgroundColor: '#bdc0c7',
		width: width,
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
		alignItems: 'center'
	},
	pickerMoveBtn: {
		color: '#149be0',
		fontSize: 16,
		marginLeft: 20
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
